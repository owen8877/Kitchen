package tech.xdrd.kitchen

import java.util.*

object Util {
    fun getToday() = Date().let { Date(it.year, it.month, it.date) }
}