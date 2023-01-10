package com.undsf.brew.models

/**
 * 原料
 */
open class Ingredient(
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
    var alphaAcidContent: Float? = null,

    /**
     * 原产地
     */
    var origin: String? = null,
    // endregion

    // region 酵母特有
    /**
     * 发酵率
     */
    var attenuation: Float? = null,

    /**
     * 酵母种类
     */
    var yeastSpecies: String? = null,

    /**
     * 理想温度下限
     */
    var optimalTemperatureLow: Int? = null,

    /**
     * 理想温度上限
     */
    var optimalTemperatureHigh: Int? = null,

    /**
     * 酒精耐受度
     */
    var alcoholTolerance: Float? = null,
    // endregion

    // region 谷物、浸泡物、提取物、其他
    /**
     * 效率
     */
    var efficiency: Float? = null,

    /**
     * 颜色影响
     */
    var colorInfluence: Float? = null,

    /**
     * 蛋白质添加物
     */
    var proteinAddition: String? = null,
    // endregion

    /**
     * 风味
     */
    var flavors: MutableList<Flavor> = mutableListOf()
) {
    val standardFlavors: List<Flavor>
        get() {
            val list = mutableListOf<Flavor>()
            for (flavor in flavors) {
                if (flavor.type == Flavor.StandardFlavor) {
                    list.add(flavor)
                }
            }
            return list
        }

    val flavorNotes: List<Flavor>
        get() {
            val list = mutableListOf<Flavor>()
            for (flavor in flavors) {
                if (flavor.type == Flavor.FlavorNote) {
                    list.add(flavor)
                }
            }
            return list
        }

    fun addFlavor(type: Int, name: String, value: Int) : Flavor {
        val flavorId = id * 100 + flavors.size + 1
        return addFlavor(Flavor(flavorId, type, name, value))
    }

    fun addFlavor(flavor: Flavor) : Flavor {
        flavors.add(flavor)
        return flavor
    }

    override fun toString() : String {
        return when (category) {
            IngredientCategory.Hops -> toHopString()
            IngredientCategory.Extracts -> toGrainsString()
            IngredientCategory.Grains -> toGrainsString()
            IngredientCategory.Steepables -> toGrainsString()
            else -> "其他"
        }
    }

    private fun toHopString() : String {
        val builder = StringBuilder()
        builder.appendLine("编号：\t\t\t$id")
        builder.appendLine("分类：\t\t\t${category.title}")
        builder.appendLine("子分类：\t\t\t$subcategory")
        builder.appendLine("α-酸含量：\t\t${String.format("%.01f%%", 100f * alphaAcidContent!!)}")
        builder.appendLine("原产地：\t\t\t$origin")
        return builder.toString()
    }

    private fun toGrainsString() : String {
        val builder = StringBuilder()
        builder.appendLine("编号：\t\t\t$id")
        builder.appendLine("分类：\t\t\t${category.title}")
        builder.appendLine("子分类：\t\t\t$subcategory")
        builder.appendLine("效率：\t\t\t${String.format("%.01f%%", 100f * efficiency!!)}")
        builder.appendLine("颜色影响：\t\t$colorInfluence SRM")
        builder.appendLine("蛋白质添加物：\t\t$proteinAddition")
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