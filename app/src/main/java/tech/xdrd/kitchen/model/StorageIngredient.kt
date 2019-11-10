package tech.xdrd.kitchen.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import tech.xdrd.kitchen.model.Ingredient.Unit
import tech.xdrd.kitchen.model.Ingredient.Unit.GRAM
import tech.xdrd.kitchen.model.Ingredient.Unit.values
import java.util.*

open class StorageIngredient(override var name: String = "") : Ingredient, RealmObject() {
    @PrimaryKey
    private var id: String = UUID.randomUUID().toString()
    var quantity: Double = 1.0
    var unit: Unit
        get() {
            return values()[unitOrdinal]
        }
        set(newUnit) {
            unitOrdinal = newUnit.ordinal
        }
    private var unitOrdinal: Int = GRAM.ordinal

    constructor(name: String, quantity: Double, unit: Unit) : this(name) {
        id = UUID.randomUUID().toString()
        this.quantity = quantity
        this.unit = unit
    }

    constructor(id: String, name: String, quantity: Double, unit: Unit) : this(name) {
        this.quantity = quantity
        this.unit = unit
    }
}