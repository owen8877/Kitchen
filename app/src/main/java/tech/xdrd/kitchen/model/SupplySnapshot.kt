package tech.xdrd.kitchen.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class SupplySnapshot(
    var date: Date,
    var list: RealmList<SupplyIngredientSnapshot>
) : RealmObject() {
    @PrimaryKey
    private var id: String = UUID.randomUUID().toString()

    @Deprecated("Only used by Realm")
    constructor() : this(Date(), RealmList<SupplyIngredientSnapshot>())
}