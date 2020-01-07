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
            MINUS -> return a - b
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
                if(a > 34f) {
                    throw IllegalArgumentException("Factorial number too high: $a")
                }

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
        for(i in 2..num) result *= i
        return result
    }
}

fun getOperationForChar(c: Char): Operation? {
    if(c == '+') {
        return Operation.PLUS
    } else if(c == '−') {
        return Operation.MINUS
    } else if(c == '-') {
        return Operation.NEGATIVE
    } else if(c == '/' || c == '÷') {
        return Operation.DIVIDE
    } else if(c == '*' || c == '×') {
        return Operation.MULTIPLY
    } else if(c == '^') {
        return Operation.POWER
    } else if(c == '%') {
        return Operation.MODULO
    } else if(c == '>') {
        return Operation.GT
    } else if(c == '<') {
        return Operation.LT
    } else if(c == '=') {
        return Operation.EQUALITY
    } else if(c == '&') {
        return Operation.AND
    } else if(c == '|') {
        return Operation.OR
    } else if(c == '√') {
        return Operation.ROOT
    } else if(c == '!') {
        return Operation.FACTORIAL
    } else if(c == '∞'){
        return Operation.INFINITY
    } else if(c == 't'){
        return Operation.TAN
    } else if(c == 'y'){
        return Operation.ATAN
    } else if(c == 'c'){
        return Operation.COS
    } else if(c == 'v'){
        return Operation.ACOS
    } else if(c == 's'){
        return Operation.SIN
    } else if(c == 'a'){
        return Operation.ASIN
    } else if(c == 'l'){
        return Operation.LOG
    } else if(c == 'n'){
        return Operation.LN
    }

    return null
}

fun isOperator(c: Char) = getOperationForChar(c) != null