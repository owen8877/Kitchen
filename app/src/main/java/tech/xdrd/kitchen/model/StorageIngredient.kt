package tech.xdrd.kitchen.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import tech.xdrd.kitchen.model.Ingredient.Unit
import tech.xdrd.kitchen.model.Ingredient.Unit.GRAM
import tech.xdrd.kitchen.model.Ingredient.Unit.values
import java.util.*

open class StorageIngredient(
    override var name: String,
    override var quantity: Double,
    unit: Unit
) : Ingredient, RealmObject() {
    @PrimaryKey
    private var id: String = UUID.randomUUID().toString()
    override var unit: Unit
        get() {
            return values()[unitOrdinal]
        }
        set(newUnit) {
            unitOrdinal = newUnit.ordinal
        }
    private var unitOrdinal: Int = unit.ordinal
    var records = RealmList<Record>()

    @Deprecated("Only used by Realm")
    constructor() : this("", 1.0, GRAM)

    fun addRecord(date: Date, observation: Boolean) {
        records.add(Record(quantity, date, observation))
    }
}