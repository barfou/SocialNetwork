package fr.barfou.socialnetwork.ui.widget.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.utils.dp

class MySpinner @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
        this.height = dp(40)
    }
}