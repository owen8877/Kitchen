package tech.xdrd.kitchen

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class DateTest {
    @Test
    fun `Using let block to construct a Date object at midnight`() {
        val date = Date().let { Date(it.year, it.month, it.date) }
        println(date)
        Assertions.assertEquals(date.minutes, 0)
    }
}