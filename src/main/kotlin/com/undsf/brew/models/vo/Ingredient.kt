package com.undsf.brew.models.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 原料
 */
@Deprecated("废弃")
class Ingredient(
    /**
     * 编号
     */
    var id: Int,

    /**
     * 分类
     */
    var category: IngredientCategory,

    /**
     * 子分类
     */
    var subcategory: String,

    /**
     * 名称
     */
    var name: String,

    // region 酒花特有
    /**
     * α-酸含量
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var alphaAcidContent: Float? = null,

    /**
     * 原产地
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var origin: String? = null,
    // endregion

    // region 酵母特有
    /**
     * 发酵率
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var attenuation: Float? = null,

    /**
     * 酵母种类
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var yeastSpecies: String? = null,

    /**
     * 理想温度下限
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var optimalTemperatureLow: Int? = null,

    /**
     * 理想温度上限
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var optimalTemperatureHigh: Int? = null,

    /**
     * 酒精耐受度
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var alcoholTolerance: Float? = null,
    // endregion

    // region 谷物、浸泡物、提取物、其他
    /**
     * 效率
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var efficiency: Float? = null,

    /**
     * 颜色影响
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var colorInfluence: Float? = null,

    /**
     * 蛋白质添加物
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var proteinAddition: String? = null,
    // endregion

    /**
     * 标准风味
     */
    val standardFlavors: MutableList<Flavor> = mutableListOf(),

    /**
     * 风味描述
     */
    val flavorNotes: MutableList<Flavor> = mutableListOf()
) {
    fun addFlavor(type: Int, name: String, value: Int) : Flavor {
        var flavorId = id * 1000 + type * 100
        flavorId += when (type) {
            Flavor.StandardFlavor -> standardFlavors.size + 1
            Flavor.FlavorNote -> flavorNotes.size + 1
            else -> 0
        }
        val flavor = Flavor(
            flavorId,
            type,
            name,
            value
        )
        return addFlavor(flavor)
    }

    fun addFlavor(flavor: Flavor) : Flavor {
        when (flavor.type) {
            Flavor.StandardFlavor -> standardFlavors.add(flavor)
            Flavor.FlavorNote -> flavorNotes.add(flavor)
        }
        return flavor
    }

    override fun toString() : String {
        return when (category) {
            IngredientCategory.Hops -> toHopString()
            IngredientCategory.Extracts -> toGrainsString()
            IngredientCategory.Yeasts -> toYeastString()
            IngredientCategory.Grains -> toGrainsString()
            IngredientCategory.Steepables -> toGrainsString()
            else -> "其他"
        }
    }

    private fun toHopString() : String {
        val builder = StringBuilder()
        // builder.appendLine("编号：\t\t\t$id")
        builder.appendLine("No.$id $name")
        builder.appendLine("-".repeat(40))
        builder.appendLine("分类：\t\t\t${category.title}")
        builder.appendLine("子分类：\t\t\t$subcategory")
        builder.appendLine("α-酸含量：\t\t${String.format("%.01f%%", 100f * alphaAcidContent!!)}")
        builder.appendLine("原产地：\t\t\t$origin")

        builder.appendLine("标准风味：")
        for (flavor in standardFlavors) {
            builder.append("${flavor.name}-${flavor.value} ")
        }
        builder.appendLine()

        builder.appendLine("风味描述：")
        for (flavor in flavorNotes) {
            builder.append("${flavor.name}-${flavor.value} ")
        }
        builder.appendLine()

        return builder.toString()
    }

    private fun toYeastString() : String {
        val builder = StringBuilder()
        builder.appendLine("No.$id $name")
        builder.appendLine("-".repeat(40))
        builder.appendLine("分类：\t\t\t${category.title}")
        builder.appendLine("子分类：\t\t\t$subcategory")
        builder.appendLine("发酵度：\t\t\t${String.format("%.01f%%", 100f * attenuation!!)}")
        builder.appendLine("酵母菌种：\t\t$yeastSpecies")
        builder.appendLine("理想温度：\t\t$optimalTemperatureLow-$optimalTemperatureHigh℃")
        builder.appendLine("酒精耐受度：\t\t${String.format("%.01f%%", 100f * alcoholTolerance!!)}")

        builder.appendLine("标准风味：")
        for (flavor in standardFlavors) {
            builder.append("${flavor.name}-${flavor.value} ")
        }
        builder.appendLine()

        builder.appendLine("风味描述：")
        for (flavor in flavorNotes) {
            builder.append("${flavor.name}-${flavor.value} ")
        }
        builder.appendLine()

        return builder.toString()
    }

    private fun toGrainsString() : String {
        val colorInfluenceStr: String = if (colorInfluence!! < 10F) {
            String.format("%.01f SRM", colorInfluence)
        }
        else {
            String.format("%.0f SRM", colorInfluence)
        }

        val builder = StringBuilder()
        builder.appendLine("No.$id $name")
        builder.appendLine("-".repeat(40))
        builder.appendLine("分类：\t\t\t${category.title}")
        builder.appendLine("子分类：\t\t\t$subcategory")
        builder.appendLine("效率：\t\t\t${String.format("%.01f%%", 100f * efficiency!!)}")
        builder.appendLine("颜色影响：\t\t$colorInfluenceStr")
        builder.appendLine("蛋白质添加物：\t\t$proteinAddition")

        builder.appendLine("标准风味：")
        for (flavor in standardFlavors) {
            builder.append("${flavor.name}-${flavor.value} ")
        }
        builder.appendLine()

        builder.appendLine("风味描述：")
        for (flavor in flavorNotes) {
            builder.append("${flavor.name}-${flavor.value} ")
        }
        builder.appendLine()

        return builder.toString()
    }

    companion object {
        // region of
        fun hopOf(id: Int,
                  subcategory: String,
                  name: String,
                  alphaAcidContent: Float,
                  origin: String,
                  flavors: List<Flavor>?
        ) : Ingredient {
            val hop = Ingredient(
                id,
                IngredientCategory.Hops,
                subcategory,
                name,
                alphaAcidContent = alphaAcidContent,
                origin = origin
            )
            if (flavors != null) {
                for (flavor in flavors) {
                    hop.addFlavor(flavor)
                }
            }
            return hop
        }

        fun yeastOf(id: Int,
                    subcategory: String,
                    name: String,
                    attenuation: Float,
                    species: String,
                    tempLow: Int,
                    tempHigh: Int,
                    alcoholTolerance: Float,
                    flavors: List<Flavor>? = null
        ) : Ingredient {
            val yeast = Ingredient(
                id,
                IngredientCategory.Yeasts,
                subcategory,
                name,
                attenuation = attenuation,
                yeastSpecies = species,
                optimalTemperatureLow = tempLow,
                optimalTemperatureHigh = tempHigh,
                alcoholTolerance = alcoholTolerance
            )
            if (flavors != null) {
                for (flavor in flavors) {
                    yeast.addFlavor(flavor)
                }
            }
            return yeast
        }

        fun extractOf(
            id: Int,
            subcategory: String,
            name: String,
            efficiency: Float,
            colorInfluence: Float,
            proteinAddition: String,
            flavors: List<Flavor>? = null
        ) : Ingredient {
            return of(
                id,
                IngredientCategory.Extracts,
                subcategory,
                name,
                efficiency,
                colorInfluence,
                proteinAddition,
                flavors
            )
        }

        fun grainOf(
            id: Int,
            subcategory: String,
            name: String,
            efficiency: Float,
            colorInfluence: Float,
            proteinAddition: String,
            flavors: List<Flavor>? = null
        ) : Ingredient {
            return of(
                id,
                IngredientCategory.Grains,
                subcategory,
                name,
                efficiency,
                colorInfluence,
                proteinAddition,
                flavors
            )
        }

        fun steepableOf(
            id: Int,
            subcategory: String,
            name: String,
            efficiency: Float,
            colorInfluence: Float,
            proteinAddition: String,
            flavors: List<Flavor>? = null
        ) : Ingredient {
            return of(
                id,
                IngredientCategory.Steepables,
                subcategory,
                name,
                efficiency,
                colorInfluence,
                proteinAddition,
                flavors
            )
        }

        fun otherOf(
            id: Int,
            subcategory: String,
            name: String,
            efficiency: Float,
            colorInfluence: Float? = null,
            proteinAddition: String? = null,
            flavors: List<Flavor>? = null
        ) : Ingredient {
            return of(
                id,
                IngredientCategory.Others,
                subcategory,
                name,
                efficiency,
                colorInfluence,
                proteinAddition,
                flavors
            )
        }

        private fun of(
            id: Int,
            category: IngredientCategory,
            subcategory: String,
            name: String,
            efficiency: Float,
            colorInfluence: Float? = null,
            proteinAddition: String? = null,
            flavors: List<Flavor>? = null
        ) : Ingredient {
            val ingredient = Ingredient(
                id,
                category,
                subcategory,
                name,
                efficiency = efficiency,
                colorInfluence = colorInfluence,
                proteinAddition = proteinAddition
            )
            if (flavors != null) {
                for (flavor in flavors) {
                    ingredient.addFlavor(flavor)
                }
            }
            return ingredient
        }
        // endregion
    }
}