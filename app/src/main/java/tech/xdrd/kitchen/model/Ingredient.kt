package tech.xdrd.kitchen.model

interface Ingredient {
    var name: String
    var quantity: Double
    var unit: Unit

    enum class Unit {
        ENTRY,
        POUND,
        GRAM,
        BOTTLE,
        MILLIMETRE,
        BOX
    }
}