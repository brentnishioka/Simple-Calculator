package com.example.simplecalculator.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class CalculatorViewModel : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String>
        get() = _result

    private val _backSpaceButtonShown = MutableLiveData<Boolean>()
    val backSpaceButtonShown: LiveData<Boolean>
        get() = _backSpaceButtonShown

    init {
        onBackspaceButtonHidden()
    }

    fun calculateResult() {
        if (!result.value.isNullOrBlank()) {
            val lastChar = result.value!!.get(result.value!!.lastIndex)
            // Ensures that the expression has an operator
                if (checkLastCharacterIsSymbol(lastChar) && containsOperator(result.value.toString())) {
                val numbersAsString = result.value!!.split("+", "-", "*", "/").toMutableList()

                // Handles case where the first number is a negative (i.e. -1-2).
                if (numbersAsString[0].equals("")) {
                    var num = numbersAsString[1].toDouble()
                    // Checks error of mismatching lengths when first number is negative.
                    if (checkCanConvertToInt(num)) {
                        var numAsInt = num.toInt()
                        numAsInt = 0 - numAsInt
                        numbersAsString[1] = numAsInt.toString()
                        numbersAsString.removeAt(0)
                    } else {
                        num = 0 - num
                        numbersAsString[1] = num.toString()
                        numbersAsString.removeAt(0)
                    }
                }

                val numLength = numbersAsString[0].length
                val sign = result.value!!.substring(numLength, numLength + 1)
                val numbers = numbersAsString.map { it.toDouble() }.toTypedArray()

                var operationResult: Double
                when (sign) {
                    "+" -> operationResult = numbers[0] + numbers[1]
                    "-" -> operationResult = numbers[0] - numbers[1]
                    "*" -> operationResult = numbers[0] * numbers[1]
                    "/" -> operationResult = numbers[0] / numbers[1]
                    else -> operationResult = 0.0
                }

                // Converts the result to Int if does not contain a decimal point (i.e. 6.0)
                if (checkCanConvertToInt(operationResult)) {
                    val operationResultInt = operationResult.toInt()
                    _result.value = operationResultInt.toString()
                } else {
                    _result.value = operationResult.toString()
                }
            }
        }
    }

    fun parseEntryToString(entry: Char) {
        val enteredChar = entry.toString()
        try {
            val enteredNumber = parseInt(enteredChar)
            // At this point, user has entered a number 0-9
            if (!(enteredNumber == 0 && result.value.isNullOrEmpty())) {
                if (result.value.isNullOrBlank()) {
                    _result.value = enteredChar
                } else {
                    _result.value = result.value + enteredChar
                }
                if (backSpaceButtonShown.value == false) {
                    onBackspaceButtonShown()
                }
            }

        } catch (e: NumberFormatException) {
            // At this point, user has entered +, -, *, /, or .
            if (!result.value.isNullOrEmpty()) {
                val lastChar = result.value?.get(result.value!!.lastIndex)
                if (lastChar?.isDigit()!!) {
                    if (checkLastCharacterIsSymbol(lastChar)) {
                        // Length of 3 is the minimum number of characters and expression can be.
                        if (result.value!!.length >= 3) {
                            // Ensures that the user is not able to enter two decimal points in any number.
                            if (entry == '.') {
                                if (result.value?.get(result.value!!.lastIndex - 1) != '.') {
                                    _result.value = result.value + enteredChar
                                }
                                // Checks if a calculation can be performed, allows for continuous calculations.
                            } else if (entry == '+' || entry == '-' || entry == '*' || entry == '/') {
                                // Handles when the first number is a negative decimal.
                                if (containsOperator(result.value.toString()) && result.value!!.first() != '-') {
                                    calculateResult()
                                }
                                _result.value = result.value + enteredChar
                            }
                        } else {
                            _result.value = result.value + enteredChar
                        }
                    }
                }
                // Allows user to hit period to start typing a decimal.
            } else if (result.value.isNullOrEmpty() && entry == '.') {
                _result.value = "0$entry"
            }
        }
    }

    // Handles switching the sign of the input.
    fun posNegResult() {
        var currentResult: Double
        if (!result.value.isNullOrEmpty()) {
            // Checks to ensure there isn't an ongoing calculation if user presses posNegButton.
            if (result.value!!.length >= 3 && containsOperator(result.value.toString())) {
                calculateResult()
            }
            // Checks to ensure the user hasn't hit posNegButton after operational sign.
            val lastChar = result.value?.get(result.value!!.lastIndex)
            if (lastChar?.isDigit()!!) {
                // Number is negative.
                if (result.value?.first() == '-') {
                    currentResult = parseDouble(result.value)
                    currentResult += -(currentResult * 2)

                    // Converts the result to Int if does not contain a decimal point (i.e. 23.0)
                    if (checkCanConvertToInt(currentResult)) {
                        val operationResultInt = currentResult.toInt()
                        _result.value = operationResultInt.toString()
                    } else {
                        _result.value = currentResult.toString()
                    }
                }
                // Number is positive.
                else {
                    currentResult = parseDouble(result.value)
                    var zero = 0.0
                    zero -= currentResult

                    // Converts the result to Int if does not contain a decimal point (i.e. 1.0)
                    if (checkCanConvertToInt(zero)) {
                        val operationResultInt = zero.toInt()
                        _result.value = operationResultInt.toString()
                    } else {
                        _result.value = zero.toString()
                    }
                }
            }
        }
    }

    // Handles taking the percent out of 100 of the current input.
    fun percentOfResult() {
        if (!result.value.isNullOrEmpty()) {
            // Checks to ensure there isn't an ongoing calculation if user presses percentButton.
            if (result.value!!.length >= 3 && containsOperator(result.value.toString())) {
                calculateResult()
            }
            // Checks to ensure the user hasn't hit percentButton after operational sign.
            val lastChar = result.value?.get(result.value!!.lastIndex)
            if (lastChar?.isDigit()!!) {
                var currentResult = parseDouble(result.value)
                currentResult /= 100

                // Converts the result to Int if does not contain a decimal point (i.e. 4526.0)
                if (checkCanConvertToInt(currentResult)) {
                    val operationResultInt = currentResult.toInt()
                    _result.value = operationResultInt.toString()
                } else {
                    _result.value = currentResult.toString()
                }
            }
        }
    }

    // Clears result from the input
    fun clearResult() {
        _result.value = null
        onBackspaceButtonHidden()
    }

    fun checkCanConvertToInt(number: Double): Boolean {
        if (number % 1 == 0.0) {
            return true
        }
        return false
    }

    fun containsOperator(expression: String): Boolean {
        if (expression.contains('+') || expression.contains('-') ||
            expression.contains('*') || expression.contains('/')
        ) {
            return true
        }
        return false
    }

    fun checkLastCharacterIsSymbol(character: Char): Boolean {
        if (!(character == '+' || character == '-' || character == '*' ||
                    character == '/' || character == '.')
        ) {
            return true
        }
        return false
    }

    fun onBackspaceClicked() {
        if (!result.value.isNullOrEmpty()) {
            val newResult = result.value!!.substring(0, result.value!!.length - 1)
            _result.value = newResult
        } else {
            onBackspaceButtonHidden()
        }
    }

    fun onBackspaceButtonShown() {
        _backSpaceButtonShown.value = true
    }

    fun onBackspaceButtonHidden() {
        _backSpaceButtonShown.value = false
    }
}