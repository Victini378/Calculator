package com.victi.calculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.sqrt
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer.layoutParams.width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        drawer.layoutParams.height = (resources.displayMetrics.heightPixels * 0.595).toInt()

        button_delete.setOnClickListener {
            memCalc.text = memCalc.text.dropLast(1)
            if (memCalc.text.isEmpty()) directCalc.text = ""
            else resolve()
        }

        button_delete.setOnLongClickListener {
            clearRippleEffect()
            true
        }

        button_enter.setOnClickListener {
            if (memCalc.text.isNotEmpty() && directCalc.text.isNotEmpty() && (!memCalc.text.contains("∞") || directCalc.text.contains("∞"))) {
                memCalc.text = directCalc.text
                directCalc.text = ""
            }
        }
    }

    private fun clearRippleEffect() {
        val startingColorFrame = ConstraintLayout(calcArea.context)
        val endingColorFrame = ConstraintLayout(calcArea.context)
        startingColorFrame.background = calcArea.foreground
        endingColorFrame.background = ContextCompat.getDrawable(applicationContext, R.drawable.calc_area_ripple)
        endingColorFrame.visibility = View.GONE
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
        // this is API 21 minimum. Add proper checks
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            endingColorFrame,
            sourceX,
            sourceY,
            0f,
            finalRadius.toFloat()
        )
        endingColorFrame.visibility = View.VISIBLE
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                CoroutineScope(IO).launch{
                    clear()
                }
            }
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                calcArea.removeView(startingColorFrame)
                calcArea.removeView(endingColorFrame)
            }
        })
        // customize the animation here
        circularReveal.duration = 700
        circularReveal.interpolator = AccelerateInterpolator()
        circularReveal.start()
    }

    private suspend fun clear() {
        delay(300)
        memCalc.text = ""
        directCalc.text = ""
    }

    private fun resolve(){
        try {
            val c = "÷×−+."
            if (memCalc.text.isEmpty()) return
            var expression = memCalc.text.toString()
            if(expression.last() == '√') return
            expression = expression
                .replace("π", "3.1415926536")
                .replace("e", "2.7182818285")
            if (c.contains(expression.last())) expression = expression.dropLast(1)
            val result = Value(expression).resolve()
            when {
                result == POSITIVE_INFINITY -> directCalc.text = "∞"
                result == NEGATIVE_INFINITY -> directCalc.text = "-∞"
                result % 1 == 0.0 -> directCalc.text = result.toInt().toString()
                else -> directCalc.text = result.toString()
            }
            println(memCalc.text.toString())
        } catch (e: IllegalArgumentException){
            directCalc.text = "Error"
        }
    }

    fun onClick(v : View){
        val btn = v as Button
        val c = "÷×−+."
        if (memCalc.text.isEmpty() && btn.text.first() == '−' || c.all { it != btn.text.first() }){
            if(btn.text.first() == '−') memCalc.append("-")
            else memCalc.append(btn.text.toString())
        }
        else if (memCalc.text.isNotEmpty() && (c.all { it != memCalc.text.last() } || c.all { it != btn.text.first() }))
            memCalc.append(btn.text.toString())
        resolve()
    }
}
