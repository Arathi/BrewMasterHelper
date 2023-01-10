package com.undsf.brew.models

enum class IngredientCategory(val title: String, val idBase: Int) {
    Hops("啤酒花", 1000),
    Extracts("提取物", 2000),
    Yeasts("酵母", 3000),
    Grains("谷物", 4000),
    Steepables("可浸泡物", 5000),
    Others("其他", 6000)
}