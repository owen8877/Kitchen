package tech.xdrd.kitchen

import java.util.*

object Util {
    fun getToday(): Date {
        return Date().let { Date(it.year, it.month, it.date) }
    }
}