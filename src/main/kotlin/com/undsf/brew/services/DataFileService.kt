package com.undsf.brew.services

import com.undsf.brew.models.po.Category
import com.undsf.brew.models.po.Flavor
import com.undsf.brew.models.po.Ingredient
import com.undsf.brew.models.vo.IngredientCategory
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
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
    val categoryNames = mutableMapOf<Int, String>()

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
                    "效率" -> {
                        val cell = row.getCell(column)
                        if (cell.cellType == CellType.NUMERIC) {
                            ingredient.efficiency = cell.numericCellValue
                        }
                    }
                    "颜色影响" -> {
                        val cell = row.getCell(column)
                        if (cell.cellType == CellType.NUMERIC) {
                            ingredient.colorInfluence = cell.numericCellValue
                        }
                    }
                    "蛋白质添加物（说明）" -> {
                        val cell = row.getCell(column)
                        if (cell.cellType == CellType.STRING) {
                            ingredient.proteinAddition = cell.stringCellValue
                        }
                    }
                    // endregion
                }
            }

            ingredients.add(ingredient)
        }

        return ingredients
    }

    fun save(fileName: String) {
        val workbook = XSSFWorkbook()

        val ingredientSheet = workbook.createSheet("原料")
        createHeaderRow(ingredientSheet, listOf(
            "序号", // 0
            "名称", // 1
            // ---
            "类别", // 2
            "子类别", // 3
            // ---
            "α-酸含量", // 4
            "原产地", // 5
            // ---
            "发酵度", // 6
            "酵母菌种", // 7
            "理想温度下限", // 8
            "理想温度上限", // 9
            "酒精耐受度", // 10
            // ---
            "效率", // 11
            "颜色影响", // 12
            "蛋白质添加物", // 13
            // ---
            "描述", // 14
        ))

        val flavorSheet = workbook.createSheet("风味")
        createHeaderRow(flavorSheet, listOf(
            "序号", // 0
            "原料名称", // 1
            "原料类别", // 2
            "风味", // 3
            "值", // 4
        ))

        val categories = categorySvc.fetchAll()
        categoryNames.clear()
        for (category in categories) {
            categoryNames[category.id] = category.name
        }

        val ingredients = ingredientSvc.getAll(true)
        var ingredientRowNum = 0
        var flavorRowNum = 0

        for (ingredient in ingredients) {
            val ingredientRow = ingredientSheet.createRow(++ingredientRowNum)
            writeIngredient(ingredientRow, ingredient)

            for (flavor in ingredient.standardFlavors) {
                val row = flavorSheet.createRow(++flavorRowNum)
                writeFlavor(row, ingredient, flavor)
            }

            for (flavor in ingredient.flavorNotes) {
                val row = flavorSheet.createRow(++flavorRowNum)
                writeFlavor(row, ingredient, flavor)
            }
        }

        val fs = FileOutputStream("$dir/$fileName")
        workbook.write(fs)
        workbook.close()
    }

    private fun createHeaderRow(sheet: Sheet, fields: List<String>) {
        val row = sheet.createRow(0)
        for (index in fields.indices) {
            val fieldName = fields[index]
            val cell = row.createCell(index, CellType.STRING)
            cell.setCellValue(fieldName)
        }
    }

    private fun writeIngredient(row: Row, ingredient: Ingredient) {
        // "序号", // 0
        // "名称", // 1
        val idCell = row.createCell(0, CellType.NUMERIC)
        idCell.setCellValue(ingredient.id.toDouble())

        val nameCell = row.createCell(1, CellType.STRING)
        nameCell.setCellValue(ingredient.name)

        // "类别", // 2
        // "子类别", // 3
        val categoryCell = row.createCell(2, CellType.STRING)
        categoryCell.setCellValue(categoryNames[ingredient.mainCategoryId])

        val subcategoryCell = row.createCell(3, CellType.STRING)
        subcategoryCell.setCellValue(categoryNames[ingredient.categoryId])

        // "α-酸含量", // 4
        // "原产地", // 5
        if (ingredient.mainCategoryId == Category.Hops) {
            val aacCell = row.createCell(4, CellType.NUMERIC)
            aacCell.setCellValue(ingredient.alphaAcidContent!!)

            val originCell = row.createCell(5, CellType.STRING)
            originCell.setCellValue(ingredient.origin)
        }

        // "发酵度", // 6
        // "酵母菌种", // 7
        // "理想温度下限", // 8
        // "理想温度上限", // 9
        // "酒精耐受度", // 10
        if (ingredient.mainCategoryId == Category.Yeasts) {
            val attenuationCell = row.createCell(6, CellType.NUMERIC)
            attenuationCell.setCellValue(ingredient.attenuation!!)

            val yeastSpeciesCell = row.createCell(7, CellType.STRING)
            yeastSpeciesCell.setCellValue(ingredient.yeastSpecies)

            val tempLowCell = row.createCell(8, CellType.NUMERIC)
            tempLowCell.setCellValue(ingredient.optimalTemperatureLow!!.toDouble())

            val tempHighCell = row.createCell(9, CellType.NUMERIC)
            tempHighCell.setCellValue(ingredient.optimalTemperatureLow!!.toDouble())

            val alcoholToleranceCell = row.createCell(10, CellType.NUMERIC)
            alcoholToleranceCell.setCellValue(ingredient.alcoholTolerance!!)
        }

        // "效率", // 11
        if (ingredient.efficiency != null) {
            val cell = row.createCell(11, CellType.NUMERIC)
            cell.setCellValue(ingredient.efficiency!!)
        }

        // "颜色影响", // 12
        if (ingredient.colorInfluence != null) {
            val cell = row.createCell(12, CellType.NUMERIC)
            cell.setCellValue(ingredient.colorInfluence!!)
        }

        // "蛋白质添加物", // 13
        if (ingredient.proteinAddition != null) {
            val cell = row.createCell(13, CellType.STRING)
            cell.setCellValue(ingredient.proteinAddition!!)
        }
    }

    fun writeFlavor(row: Row, ingredient: Ingredient, flavor: Flavor) {
        var cell = row.createCell(0, CellType.NUMERIC)
        cell.setCellValue(flavor.id.toDouble())

        cell = row.createCell(1, CellType.STRING)
        cell.setCellValue(ingredient.name)

        cell = row.createCell(2, CellType.STRING)
        cell.setCellValue(categoryNames[ingredient.mainCategoryId])

        cell = row.createCell(3, CellType.STRING)
        cell.setCellValue(flavor.name)

        cell = row.createCell(4, CellType.NUMERIC)
        cell.setCellValue(flavor.value.toDouble())
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