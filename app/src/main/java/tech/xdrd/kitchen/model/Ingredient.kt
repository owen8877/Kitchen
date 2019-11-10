package tech.xdrd.kitchen.model

interface Ingredient {
    var name: String

    enum class Unit {
        ENTRY,
        POUND,
        GRAM,
        BOTTLE,
        MILLIMETRE,
        BOX
    }
}