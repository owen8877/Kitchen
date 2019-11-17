package tech.xdrd.kitchen.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class SupplyIngredient(
    override var name: String,
    override var quantity: Double,
    unit: Ingredient.Unit,
    var bought: Boolean,
    var recurrent: Boolean,
    var date: Date
) : Ingredient, RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    override var unit: Ingredient.Unit
        get() {
            return Ingredient.Unit.values()[unitOrdinal]
        }
        set(newUnit) {
            unitOrdinal = newUnit.ordinal
        }
    private var unitOrdinal: Int = unit.ordinal

    @Deprecated("Only used by Realm")
    constructor() : this("", 1.0, Ingredient.Unit.GRAM, false, false, Date())

    constructor(
        name: String,
        quantity: Double,
        unit: Ingredient.Unit,
        recurrent: Boolean,
        date: Date
    ) : this(name, quantity, unit, false, recurrent, date)

    override fun toString(): String {
        return "SupplyIngredient(name='$name', quantity=$quantity, bought=$bought, recurrent=$recurrent, date=$date, id='$id', unitOrdinal=$unitOrdinal)"
    }
}