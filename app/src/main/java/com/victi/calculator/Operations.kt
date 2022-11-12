package com.victi.calculator

import kotlin.math.*

enum class Operation {

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    MODULO,
    GT,
    LT,
    EQUALITY,
    AND,
    OR,
    ROOT,
    FACTORIAL,
    INFINITY,
    NEGATIVE,
    NEGATIVE_INFINITY_,
    SIN,
    ASIN,
    COS,
    ACOS,
    TAN,
    ATAN,
    LOG,
    LN;

    /**
     * @return Returns whether or not this operation requires a value to the left of it.
     */
    fun requireLeftValue(): Boolean {
        return when(this) {
            NEGATIVE -> false
            NEGATIVE_INFINITY_ -> false
            INFINITY -> false
            FACTORIAL -> true
            ROOT -> false
            TAN -> false
            ATAN -> false
            COS -> false
            ACOS -> false
            SIN -> false
            ASIN -> false
            LOG -> false
            LN -> false
            else -> true
        }
    }

    /**
     * @return Returns whether or not this operation requires a value to the right of it.
     */
    fun requireRightValue(): Boolean {
        return when (this) {
            NEGATIVE_INFINITY_ -> false
            INFINITY -> false
            FACTORIAL -> false
            else -> true
        }
    }

    /**
     * Performs this operation and returns the result.
     */
    fun execute(a: Double, b: Double): Double {
        when (this) {
            PLUS -> return a + b
            MINUS -> return (a.toBigDecimal() - b.toBigDecimal()).toDouble()
            MULTIPLY -> return a * b
            DIVIDE -> return a / b
            POWER -> return a.pow(b)
            MODULO -> return a % b
            GT -> return if(a > b) 1.0 else 0.0
            LT -> return if(a < b) 1.0 else 0.0
            EQUALITY -> return if(a == b) 1.0 else 0.0
            AND -> return if((a > 0.0) && (b > 0.0)) 1.0 else 0.0
            OR -> return if((a > 0.0) || (b > 0.0)) 1.0 else 0.0
            ROOT -> return sqrt(b)
            TAN -> return tan(b)
            ATAN -> return atan(b)
            SIN -> return sin(b)
            ASIN -> return asin(b)
            COS -> return cos(b)
            ACOS -> return acos(b)
            LOG -> return log10(b)
            LN -> return ln(b)
            FACTORIAL -> {
                if(a > 34f)
                    throw IllegalArgumentException("Factorial number too high: $a")
                return factorial(a.toInt())
            }
            INFINITY -> return Double.POSITIVE_INFINITY
            NEGATIVE_INFINITY_ -> return Double.NEGATIVE_INFINITY
            NEGATIVE -> return b * -1.0
            else -> throw IllegalArgumentException("Unknown operation: $name")
        }
    }

    private fun factorial(num: Int): Double {
        var result = 1.0
        for(i in 2..num)
            result *= i
        return result
    }
}

fun getOperationForChar(c: Char): Operation? {
    when (c) {
        '+' -> {
            return Operation.PLUS
        }
        '−' -> {
            return Operation.MINUS
        }
        '-' -> {
            return Operation.NEGATIVE
        }
        '/', '÷' -> {
            return Operation.DIVIDE
        }
        '*', '×' -> {
            return Operation.MULTIPLY
        }
        '^' -> {
            return Operation.POWER
        }
        '%' -> {
            return Operation.MODULO
        }
        '>' -> {
            return Operation.GT
        }
        '<' -> {
            return Operation.LT
        }
        '=' -> {
            return Operation.EQUALITY
        }
        '&' -> {
            return Operation.AND
        }
        '|' -> {
            return Operation.OR
        }
        '√' -> {
            return Operation.ROOT
        }
        '!' -> {
            return Operation.FACTORIAL
        }
        '∞' -> {
            return Operation.INFINITY
        }
        't' -> {
            return Operation.TAN
        }
        'y' -> {
            return Operation.ATAN
        }
        'c' -> {
            return Operation.COS
        }
        'v' -> {
            return Operation.ACOS
        }
        's' -> {
            return Operation.SIN
        }
        'a' -> {
            return Operation.ASIN
        }
        'l' -> {
            return Operation.LOG
        }
        'n' -> {
            return Operation.LN
        }
        else -> return null
    }

}

fun isOperator(c: Char) = getOperationForChar(c) != null