package fr.barfou.socialnetwork.ui.utils

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import kotlin.math.roundToInt

fun View.dp(number: Number): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        number.toFloat(),
        this.resources.displayMetrics
    ).roundToInt()
}

fun Toolbar.changeToolbarFont(){
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (view is TextView && view.text == title) {
            view.typeface = Typeface.createFromAsset(view.context.assets, "fonts/russo_one_regular.ttf")
            break
        }
    }
}