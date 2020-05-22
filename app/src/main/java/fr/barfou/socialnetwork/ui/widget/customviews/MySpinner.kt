package fr.barfou.socialnetwork.ui.widget.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.*
import androidx.core.content.ContextCompat
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.utils.dp

class MySpinner : LinearLayout {

    lateinit var spinner: Spinner

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        this.setBackgroundResource(R.drawable.spinner_background)
        this.orientation = HORIZONTAL

        // Spinner
        spinner = Spinner(context, Spinner.MODE_DIALOG)
        val layoutParams1 = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        )
        layoutParams1.setMargins(dp(10), 0, 0, 0)
        spinner.layoutParams = layoutParams1
        spinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_body1))
        this.addView(spinner)
    }

    fun selectedItem(): Any {
        return spinner.selectedItem
    }

    fun setAdapter(adapter: ArrayAdapter<String>) {
        spinner.adapter = adapter
    }

    fun setCustomAdapter(adapter: Any) {
        spinner.adapter = adapter as SpinnerAdapter
    }

    fun selectedItemPosition(): Int {
        return spinner.selectedItemPosition
    }
}