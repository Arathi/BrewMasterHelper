package com.undsf.brew.models.vo

import com.fasterxml.jackson.annotation.JsonValue

@Deprecated("废弃")
enum class IngredientCategory(
    @JsonValue
    val title: String,
    val idBase: Int) {
    Hops("酒花", 1000),
    Extracts("提取物", 2000),
    Yeasts("酵母", 3000),
    Grains("谷物", 4000),
    Steepables("浸泡物", 5000),
    Others("其他", 6000)
}
