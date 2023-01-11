package com.undsf.brew.models.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("flavors")
class Flavor(
    @TableId
    var id: Int,

    @TableField
    var name: String,

    @TableField
    var value: Int
) {
    val ingredientId: Int get() = id / 1000
    val type: Int get() = (id / 100) % 10
    val isStandardFlavor: Boolean get() = type == StandardFlavor
    val isFlavorNote: Boolean get() = type == FlavorNote
    val tag: String get() = "$name-$value"

    override fun toString(): String = "No.$id $tag"

    companion object {
        const val StandardFlavor = 0
        const val FlavorNote = 1
    }
}