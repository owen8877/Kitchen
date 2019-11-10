package tech.xdrd.kitchen.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import tech.xdrd.kitchen.model.Plan.Type.Breakfast
import tech.xdrd.kitchen.model.Plan.Type.values
import java.util.*

open class Plan() : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var date: Date = Date()
    var type: Type
        get() {
            return values()[typeOrdinal]
        }
        set(newType) {
            typeOrdinal = newType.ordinal
        }
    private var typeOrdinal: Int = Breakfast.ordinal
    var dishes = RealmList<Dish>()

    constructor(date: Date, type: Type, dishes: RealmList<Dish>) : this() {
        this.date = date
        this.type = type
        this.dishes = dishes
    }

    fun getDescription(): String {
        if (dishes.isEmpty()) return ""
        return dishes.map { dish -> dish.name }.reduce { acc, s -> acc + '\n' + s }
    }

    enum class Type { Breakfast, Lunch, Dinner }

    fun contentEqual(other: Plan?): Boolean {
        if (other == null) return false
        return date == other.date && type == other.type // TODO("Add compare dishes!")
    }
}