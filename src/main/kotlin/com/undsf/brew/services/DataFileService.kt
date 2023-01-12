package com.undsf.brew.services

import com.undsf.brew.models.po.Category
import com.undsf.brew.models.po.Flavor
import com.undsf.brew.models.po.Ingredient
import com.undsf.brew.models.vo.IngredientCategory
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private val logger = KotlinLogging.logger {}

@Service
class DataFileService {
    @Value("\${brew-master-helper.data.dir}")
    lateinit var dir: String

    @Autowired
    lateinit var categorySvc: CategoryService

    @Autowired
    lateinit var flavorSvc: FlavorService

    @Autowired
    lateinit var ingredientSvc: IngredientService

    lateinit var workbook: Workbook
    val ingredients = mutableListOf<Ingredient>()

    val standardFlavorSheet: Sheet
        get() {
        return workbook.getSheet("标准味道")
    }

    val flavorNoteSheet: Sheet
        get() {
        return workbook.getSheet("味道描述")
    }

    fun open(fileName: String) : Boolean {
        val path = "$dir/$fileName"
        logger.info { "正在打开$path" }

        val file = File(path)
        if (!file.exists()) {
            logger.warn { "${path}不存在！" }
            return false
        }

        workbook = XSSFWorkbook(file)
        return true
    }

    fun load() : List<Ingredient> {
        ingredients.clear()

        val hops = loadSheet("1-啤酒花（Hop）", Category.Hops)
        ingredients.addAll(hops)

        val extracts = loadSheet("2-提取物（Extract）", Category.Extracts)
        ingredients.addAll(extracts)

        val yeasts = loadSheet("3-酵母（Yeast）", Category.Yeasts)
        ingredients.addAll(yeasts)

        val grains = loadSheet("4-谷物（Grain）", Category.Grains)
        ingredients.addAll(grains)

        val steepables = loadSheet("5-可浸泡物（Steepable）", Category.Steepables)
        ingredients.addAll(steepables)

        val others = loadSheet("6-其他（Other）", Category.Others)
        ingredients.addAll(others)

        addFlavors(ingredients)
        return ingredients
    }

    private fun addFlavors(ingredients: List<Ingredient>) {
        val ingredientMap = mutableMapOf<String, Ingredient>()

        for (ingredient in ingredients) {
            val mainCategoryName = Category.mainCategoryNames[ingredient.mainCategoryId]!!
            val index = "$mainCategoryName/${ingredient.name}"
            ingredientMap[index] = ingredient
        }

        for (rowNum in 1 .. standardFlavorSheet.lastRowNum) {
            val row = standardFlavorSheet.getRow(rowNum) ?: continue
            if (row.lastCellNum < 0) continue

            val ingredientName = row.getCell(0).stringCellValue
            var ingredientCategory = row.getCell(1).stringCellValue
            if (ingredientCategory == "麦芽提取物") {
                ingredientCategory = "提取物"
            }

            val index = "$ingredientCategory/$ingredientName"
            if (!ingredientMap.containsKey(index)) {
                logger.warn { "未找到原料：$index！" }
                continue
            }
            val ingredient = ingredientMap[index] ?: continue

            val name = row.getCell(2).stringCellValue
            val value = row.getCell(3).numericCellValue.toInt()

            ingredient.addFlavor(Flavor.StandardFlavor, name, value)
        }

        for (rowNum in 1 .. flavorNoteSheet.lastRowNum) {
            val row = flavorNoteSheet.getRow(rowNum) ?: continue
            if (row.lastCellNum < 0) continue

            val ingredientName = row.getCell(0).stringCellValue
            var ingredientCategory = row.getCell(1).stringCellValue
            if (ingredientCategory == "麦芽提取物") {
                ingredientCategory = "提取物"
            }

            val index = "$ingredientCategory/$ingredientName"
            if (!ingredientMap.containsKey(index)) {
                logger.warn { "未找到原料：$index！" }
                continue
            }
            val ingredient = ingredientMap[index] ?: continue

            val name = row.getCell(2).stringCellValue
            val value = row.getCell(3).numericCellValue.toInt()

            ingredient.addFlavor(Flavor.FlavorNote, name, value)
        }
    }

    private fun loadSheet(sheetName: String, categoryId: Int) : List<Ingredient> {
        logger.info { "开始加载表格${sheetName}" }
        val ingredientSheet = workbook.getSheet(sheetName)
        val ingredients = mutableListOf<Ingredient>()
        val header = ingredientSheet.getRow(0)
        val fields = mutableMapOf<String, Int>()
        for (column in 0 until header.lastCellNum) {
            val cell = header.getCell(column)
            fields[cell.stringCellValue] = column
        }
        val scIndex: Int = fields["子类别"]!!
        val nameIndex: Int = fields["名称"]!!

        var idBase = categoryId * 1000
        var index = 1
        var lastSubcategory: String? = null

        for (rowNum in 1 .. ingredientSheet.lastRowNum) {
            val row = ingredientSheet.getRow(rowNum)

            if (row == null) {
                logger.warn { "获取到第${rowNum}行为null" }
                continue
            }
            if (row.lastCellNum < 0) {
                logger.warn { "获取到第${rowNum}行为null" }
                continue
            }

            val subcategory = row.getCell(scIndex).stringCellValue
            val name = row.getCell(nameIndex).stringCellValue

            if (lastSubcategory != subcategory) {
                idBase += 100
                index = 0
            }
            lastSubcategory = subcategory

            index++
            val ingredient = Ingredient(
                idBase + index,
                name
            )

            for (column in 0 until header.lastCellNum) {
                val fieldName = header.getCell(column).stringCellValue
                when (fieldName) {
                    // region 啤酒花
                    "α-酸含量" -> ingredient.alphaAcidContent = row.getCell(column).numericCellValue
                    "原产地" -> ingredient.origin = row.getCell(column).stringCellValue
                    // endregion

                    // region 酵母
                    "发酵度" -> ingredient.attenuation = row.getCell(column).numericCellValue
                    "酵母菌种" -> ingredient.yeastSpecies = row.getCell(column).stringCellValue
                    "最佳温度下限" -> ingredient.optimalTemperatureLow = row.getCell(column).numericCellValue.toInt()
                    "最佳温度上限" -> ingredient.optimalTemperatureHigh = row.getCell(column).numericCellValue.toInt()
                    "酒精耐受度" -> ingredient.alcoholTolerance = row.getCell(column).numericCellValue
                    // endregion

                    // region 提取物、谷物、可浸泡物、其他
                    "效率" -> ingredient.efficiency = row.getCell(column).numericCellValue
                    "颜色影响" -> ingredient.colorInfluence = row.getCell(column).numericCellValue
                    "蛋白质添加物（说明）" -> ingredient.proteinAddition = row.getCell(column).stringCellValue
                    // endregion
                }
            }

            ingredients.add(ingredient)
        }

        return ingredients
    }

    fun saveSheet() {

    }

    fun saveToDatabase() {
        val ingredientInserted = AtomicLong()
        val standardFlavorInserted = AtomicLong()
        val flavorNoteInserted = AtomicLong()

        for (ingredient in ingredients) {
            ingredientSvc.save(ingredient)
            ingredientInserted.incrementAndGet()

            for (flavor in ingredient.standardFlavors) {
                flavorSvc.save(flavor)
                standardFlavorInserted.incrementAndGet()
            }

            for (flavor in ingredient.flavorNotes) {
                flavorSvc.save(flavor)
                flavorNoteInserted.incrementAndGet()
            }
        }

        logger.info { "导入原料${ingredientInserted.get()}条" }
        logger.info { "导入标准风味${standardFlavorInserted.get()}条" }
        logger.info { "导入风味描述${flavorNoteInserted.get()}条" }
    }
}