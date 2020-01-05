package com.victi.calculator

import kotlin.math.pow
import kotlin.math.sqrt

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
    _NEGATIVE_INFINITY;

    /**
     * @return Returns whether or not this operation requires a value to the left of it.
     */
    fun requireLeftValue(): Boolean {
        return when(this) {
            NEGATIVE -> false
            _NEGATIVE_INFINITY -> false
            INFINITY -> false
            FACTORIAL -> true
            ROOT -> false
            else -> true
        }
    }

    /**
     * @return Returns whether or not this operation requires a value to the right of it.
     */
    fun requireRightValue(): Boolean {
        return when (this) {
            _NEGATIVE_INFINITY -> false
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
            FACTORIAL -> {
                if(a > 34f) {
                    throw IllegalArgumentException("Factorial number too high: $a")
                }

                return factorial(a.toInt())
            }
            INFINITY -> return Double.POSITIVE_INFINITY
            _NEGATIVE_INFINITY -> return Double.NEGATIVE_INFINITY
            NEGATIVE -> return b * -1.0
            else -> throw IllegalArgumentException("Unknown operation: $name")
        }
    }

    private fun factorial(i: Int): Double {
        var result = 1.0
        for(j in 1..i) {
            result *= j
        }

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
    }

    return null
}

fun isOperator(c: Char) = getOperationForChar(c) != null