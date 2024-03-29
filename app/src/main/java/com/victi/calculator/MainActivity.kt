package com.victi.calculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.View.*
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.roundToInt
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private var pointFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        hideBars()
    }

    private fun clearRippleEffect() {
        val startingColorFrame = ConstraintLayout(calcArea.context)
        val endingColorFrame = ConstraintLayout(calcArea.context)
        startingColorFrame.background = calcArea.foreground
        endingColorFrame.background = ContextCompat.getDrawable(applicationContext, R.drawable.calc_area_ripple)
        endingColorFrame.visibility = GONE
        calcArea.addView(
            endingColorFrame,
            0,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        calcArea.addView(
            startingColorFrame,
            0,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        val finalRadius = sqrt((calcArea.width * calcArea.width + calcArea.height * calcArea.height).toDouble())
        val sourceX: Int = calcArea.width - button_delete.width/2
        val sourceY: Int = calcArea.height
        // this is API 21 minimum. It's better to add proper checks
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            endingColorFrame,
            sourceX,
            sourceY,
            0f,
            finalRadius.toFloat()
        )
        endingColorFrame.visibility = VISIBLE
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                CoroutineScope(IO).launch {
                    clear()
                }
            }
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                endingColorFrame.animate().alpha(0f).duration = 400
                calcArea.removeView(startingColorFrame)
                //calcArea.removeView(endingColorFrame)
            }
        })
        // customize the animation here
        circularReveal.setDuration(600).interpolator = AccelerateInterpolator()
        circularReveal.start()
    }

    private suspend fun clear() {
        delay(280)
        runOnUiThread { memCalc.text = "" }
        directCalc.text = ""
    }

    private fun resolve(){
        try {
            val c = "÷×−+."
            if (memCalc.text.isEmpty())
                return
            var expression = memCalc.text.toString()
            expression = expression
                .replace("π", Math.PI.toString())
                .replace("e", Math.E.toString())
                .replace("atan", "y")
                .replace("acos", "v")
                .replace("asin", "a")
                .replace("tan", "t")
                .replace("cos", "c")
                .replace("sin", "s")
                .replace("log", "l")
                .replace("ln", "n")
            if (c.contains(expression.last()))
                expression = expression.dropLast(1)
            val result = Value(expression).resolve()
            when {
                result == POSITIVE_INFINITY -> directCalc.text = "∞"
                result == NEGATIVE_INFINITY -> directCalc.text = "-∞"
                result % 1 == 0.0 -> directCalc.text = result.toInt().toString()
                else -> directCalc.text = result.toString()
            }
            println(memCalc.text.toString())
        } catch (e: IllegalArgumentException) {
            directCalc.text = getString(R.string.error)
        }
    }

    fun onClick(v : View){
        val btn = v as Button
        val c = "÷×−+.-"
        val operator = "÷×−+"

        if (operator.contains(btn.text.first()))
            pointFlag = false
        if (!pointFlag && memCalc.text.isEmpty() && btn.text.first() == '−' || c.all { it != btn.text.first() }){
            if(btn.text.first() == '−')
                memCalc.append("-")
            else memCalc.append(btn.text.toString())
        }
        else if (!pointFlag && memCalc.text.isNotEmpty()) {
            if (memCalc.text.last() != '-' && c.contains(memCalc.text.last()) && btn.text.first() == '−')
                memCalc.append("-")
            else if (c.all { it != memCalc.text.last() } || c.all { it != btn.text.first() })
                memCalc.append(btn.text.toString())
            if(memCalc.text.last() == '.')
                pointFlag = true
        }

        resolve()
    }

    private fun hideBars(){
        window.decorView.systemUiVisibility = (
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or SYSTEM_UI_FLAG_FULLSCREEN
                        or SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    private fun initializeUI(){
        hideBars()

        val operator = "÷×−+"

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        val ratio = if (height > width) height/width else width/height

        drawer.layoutParams.width = (width * 0.75).roundToInt()
        drawer.layoutParams.height = (height * 0.665).roundToInt()
        if (ratio != 16/9) {
            numbers.layoutParams.height = (height * 0.68).roundToInt()
            operators.layoutParams.height = (height * 0.68).roundToInt()
        }

        val n = 3
        val m = n+4+1
        var span = SpannableString("cos\nacos")
        span.setSpan(RelativeSizeSpan(0.6f), n, m, SPAN_EXCLUSIVE_EXCLUSIVE)
        button_cos.text = span
        span = SpannableString("sin\nasin")
        span.setSpan(RelativeSizeSpan(0.6f), n, m, SPAN_EXCLUSIVE_EXCLUSIVE)
        button_sin.text = span
        span = SpannableString("tan\natan")
        span.setSpan(RelativeSizeSpan(0.6f), n, m, SPAN_EXCLUSIVE_EXCLUSIVE)
        button_tan.text = span
        /*span = SpannableString("cot\nacot")
        span.setSpan(RelativeSizeSpan(0.6f), n, m, SPAN_EXCLUSIVE_EXCLUSIVE)
        button_cot.text = span*/

        button_tan.setOnClickListener {
            memCalc.append("tan")
            resolve()
        }
        button_tan.setOnLongClickListener {
            memCalc.append("atan")
            resolve()
            true
        }
        button_cos.setOnClickListener {
            memCalc.append("cos")
            resolve()
        }
        button_cos.setOnLongClickListener {
            memCalc.append("acos")
            resolve()
            true
        }
        button_sin.setOnClickListener {
            memCalc.append("sin")
            resolve()
        }
        button_sin.setOnLongClickListener {
            memCalc.append("asin")
            resolve()
            true
        }

        button_delete.setOnClickListener {
            if (memCalc.text.isNotEmpty() && memCalc.text.last() == '.')
                pointFlag = false
            memCalc.text = memCalc.text.dropLast(1)
            if (memCalc.text.isEmpty())
                directCalc.text = ""
            if (memCalc.text.isNotEmpty() && operator.contains(memCalc.text.last()))
                pointFlag = false
            else resolve()
        }

        button_delete.setOnLongClickListener {
            clearRippleEffect()
            pointFlag = false
            true
        }

        button_enter.setOnClickListener {
            if (memCalc.text.isNotEmpty() && directCalc.text.isNotEmpty() && (!memCalc.text.contains("∞") || directCalc.text.contains("∞"))) {
                if (directCalc.text.contains("Error"))
                    memCalc.text = ""
                else
                    memCalc.text = directCalc.text
                if(!directCalc.text.contains("."))
                    pointFlag = false
                directCalc.text = ""
            }
        }
    }
}
