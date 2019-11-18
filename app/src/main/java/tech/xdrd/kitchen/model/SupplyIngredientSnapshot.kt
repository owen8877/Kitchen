package tech.xdrd.kitchen.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class SupplyIngredientSnapshot(
    override var name: String,
    override var quantity: Double,
    unit: Ingredient.Unit,
    var recurrent: Boolean,
    var date: Date
) : RealmObject(), Ingredient {
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
    constructor() : this("", 1.0, Ingredient.Unit.GRAM, false, Date())

    constructor(ref: SupplyIngredient) : this(
        ref.name,
        ref.quantity,
        ref.unit,
        ref.recurrent,
        ref.date
    )
}