package tech.xdrd.kitchen.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Dish(@PrimaryKey var name: String = "") : RealmObject() {
}