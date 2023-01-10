package com.undsf.brew.models

class Flavor(
    var id: Int,
    var type: Int,
    var name: String,
    var value: Int) {
    override fun toString(): String {
        return "$id: $name-$value"
    }

    companion object {
        const val StandardFlavor = 0
        const val FlavorNote = 1
    }
}