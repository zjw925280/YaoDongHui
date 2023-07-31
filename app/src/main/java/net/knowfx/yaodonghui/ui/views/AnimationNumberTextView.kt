package net.knowfx.yaodonghui.ui.views

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.utils.ToastUtils
import java.text.DecimalFormat

class AnimationNumberTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private val targetTime = 1000L
    private val timeTravel = 20L
    private var pattern = ""
    override fun onFinishInflate() {
        text = resources.getString(R.string.string_score_default)
        super.onFinishInflate()
    }

    fun autoIncreaseDouble(num: Double, digitCount: Int = 2) {
        val step = num * timeTravel
        var currentNum = 0.0
        initPattern(digitCount)
        object : CountDownTimer(targetTime, timeTravel) {
            override fun onTick(p0: Long) {
                currentNum += step
                text = formatDouble(currentNum / targetTime)
            }

            override fun onFinish() {
                text = formatDouble(num)
            }
        }.start()

    }

    fun setText(num: Double, digitCount: Int = 2){
        initPattern(digitCount)
        text = formatDouble(num)
    }

    private fun initPattern(count: Int) {
        pattern = "#####0."
        for (i in 1..count) {
            pattern = pattern.plus("0")
        }
    }

    private fun formatDouble(num: Double): String {
        val df = DecimalFormat(pattern)
        return df.format(num)
    }
}