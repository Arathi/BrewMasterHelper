package com.undsf.brew.models.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore

@TableName("categories")
class Category(
    @TableId
    var id: Int,

    @TableField
    var name: String
) {
    @get:JsonIgnore
    val fullName: String get() {
        val mainCategoryId = id / 10
        if (mainCategoryId in 1 .. 6) {
            val mainCategory = mainCategoryNames[mainCategoryId]!!
            return "$mainCategory/$name"
        }
        return name
    }

    override fun toString(): String = "No.$id $name"

    companion object {
        const val Hops = 1
        const val Extracts = 2
        const val Yeasts = 3
        const val Grains = 4
        const val Steepables = 5
        const val Others = 6

        val mainCategoryNames: MutableMap<Int, String> = mutableMapOf(
            Hops to "酒花",
            Extracts to "提取物",
            Yeasts to "酵母",
            Grains to "谷物",
            Steepables to "浸泡物",
            Others to "其他",
        )

        val fullNames: MutableMap<Int, String> = mutableMapOf()
    }
}