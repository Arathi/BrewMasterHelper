package com.undsf.brew.models.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@TableName("ingredients")
class Ingredient(
    /**
     * 编号
     */
    @TableId
    var id: Int,

    /**
     * 名称
     */
    @TableField
    var name: String,

    // region 酒花特有
    /**
     * α-酸含量
     */
    @TableField("alpha_acid_content")
    @JsonProperty("alpha_acid_content")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var alphaAcidContent: Float? = null,

    /**
     * 原产地
     */
    @TableField
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var origin: String? = null,
    // endregion

    // region 酵母特有
    /**
     * 发酵率
     */
    @TableField
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var attenuation: Float? = null,

    /**
     * 酵母种类
     */
    @TableField("yeast_species")
    @JsonProperty("yeast_species")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var yeastSpecies: String? = null,

    /**
     * 理想温度下限
     */
    @TableField("optimal_temperature_low")
    @JsonProperty("optimal_temperature_low")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var optimalTemperatureLow: Int? = null,

    /**
     * 理想温度上限
     */
    @TableField("optimal_temperature_high")
    @JsonProperty("optimal_temperature_high")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var optimalTemperatureHigh: Int? = null,

    /**
     * 酒精耐受度
     */
    @TableField("alcohol_tolerance")
    @JsonProperty("alcohol_tolerance")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var alcoholTolerance: Float? = null,
    // endregion

    // region 谷物、浸泡物、提取物、其他
    /**
     * 效率
     */
    @TableField
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var efficiency: Float? = null,

    /**
     * 颜色影响
     */
    @TableField("color_influence")
    @JsonProperty("color_influence")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var colorInfluence: Float? = null,

    /**
     * 蛋白质添加物
     */
    @TableField("protein_addition")
    @JsonProperty("protein_addition")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var proteinAddition: String? = null,
    // endregion

    /**
     * 标准风味
     */
    @TableField(exist = false)
    @JsonProperty("standard_flavors")
    var standardFlavors: MutableList<Flavor> = mutableListOf(),

    /**
     * 风味描述
     */
    @TableField(exist = false)
    @JsonProperty("flavor_notes")
    var flavorNotes: MutableList<Flavor> = mutableListOf(),
) {
    val categoryId: Int get() = id / 100
    val mainCategoryId: Int get() = categoryId / 10
    val categoryFullName: String get() = Category.fullNames[categoryId]!!

    override fun toString(): String = "No.$id $name"

    fun addFlavor(type: Int, name: String, value: Int) : Flavor {
        val index = when (type) {
            0 -> standardFlavors.size + 1
            1 -> flavorNotes.size + 1
            else -> 0
        }

        val flavorId = id * 1000 + type * 100 + index
        val flavor = Flavor(flavorId, name, value)
        addFlavor(flavor)
        return flavor
    }

    fun addFlavor(flavor: Flavor) {
        if (flavor.isStandardFlavor) {
            standardFlavors.add(flavor)
        }
        if (flavor.isFlavorNote) {
            flavorNotes.add(flavor)
        }
    }

    val info: String get() {
        val builder = StringBuilder()
        builder.appendLine("No.$id $name")
        builder.appendLine("分类：\t\t\t$categoryFullName")

        if (mainCategoryId == Category.Hops) {
            builder.appendLine("α-酸含量：\t\t${String.format("%.01f%%", 100f * alphaAcidContent!!)}")
            builder.appendLine("原产地：\t\t\t$origin")
        }

        if (mainCategoryId == Category.Yeasts) {
            builder.appendLine("发酵度：\t\t\t${String.format("%.01f%%", 100f * attenuation!!)}")
            builder.appendLine("酵母菌种：\t\t$yeastSpecies")
            builder.appendLine("理想温度：\t\t$optimalTemperatureLow-$optimalTemperatureHigh℃")
            builder.appendLine("酒精耐受度：\t\t${String.format("%.01f%%", 100f * alcoholTolerance!!)}")
        }

        if (efficiency != null) {
            builder.appendLine("效率：\t\t\t${String.format("%.01f%%", 100f * efficiency!!)}")
        }
        if (colorInfluence != null) {
            val colorInfluenceFormat: String = if (colorInfluence!! < 10F) "%.01f SRM" else "%.0f SRM"
            builder.appendLine("颜色影响：\t\t${String.format(colorInfluenceFormat, colorInfluence)}")
        }
        if (proteinAddition != null) {
            builder.appendLine("蛋白质添加物：\t\t$proteinAddition")
        }

        builder.appendLine("标准风味：")
        for (flavor in standardFlavors) {
            builder.append("${flavor.tag} ")
        }
        builder.appendLine()

        builder.appendLine("风味描述：")
        for (flavor in flavorNotes) {
            builder.append("${flavor.tag} ")
        }
        builder.appendLine()

        return builder.toString()
    }
}
