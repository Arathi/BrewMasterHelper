package com.undsf.brew.models.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

@TableName("flavors")
class Flavor(
    @TableId
    var id: Int,

    @TableField
    var name: String,

    @TableField
    var value: Int
) {
    @get:JsonIgnore
    val ingredientId: Int get() = id / 1000

    @get:JsonIgnore
    val type: Int get() = (id / 100) % 10

    @get:JsonIgnore
    val isStandardFlavor: Boolean get() = type == StandardFlavor

    @get:JsonIgnore
    val isFlavorNote: Boolean get() = type == FlavorNote

    @get:JsonIgnore
    val tag: String get() = "$name-$value"

    override fun toString(): String = "No.$id $tag"

    companion object {
        const val StandardFlavor = 0
        const val FlavorNote = 1
    }
}