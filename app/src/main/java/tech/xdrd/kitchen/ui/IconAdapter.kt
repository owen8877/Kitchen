package tech.xdrd.kitchen.ui

import androidx.annotation.DrawableRes
import tech.xdrd.kitchen.R

// <a href="https://www.freepik.com/free-photos-vectors/brochure">Brochure vector created by freepik - www.freepik.com</a>
// https://github.com/berteh/svg-objects-export
object IconAdapter {
    val DefaultValue = R.drawable.ima_ingredient_null
    val mMap = mapOf(
        "apple" to R.drawable.img_fruit_apple,
        "banana" to R.drawable.img_fruit_banana,
        "grape" to R.drawable.img_fruit_grape,
        null to DefaultValue
    )

    @DrawableRes
    fun getImage(name: String): Int {
        return mMap[name] ?: DefaultValue
    }
}