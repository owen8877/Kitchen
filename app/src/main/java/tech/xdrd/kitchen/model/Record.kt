package tech.xdrd.kitchen.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Record(var quantity: Double, var date: Date, var observation: Boolean) :
    RealmObject() {
    @PrimaryKey
    private var id: String = UUID.randomUUID().toString()

    @Deprecated("Only used by Realm")
    constructor() : this(0.0, Date(), true)
}