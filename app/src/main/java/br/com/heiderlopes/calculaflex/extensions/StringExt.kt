package br.com.heiderlopes.calculaflex.extensions

import android.util.Patterns
import java.util.regex.Pattern


fun String.isValidEmail() = run {
    val pattern: Pattern = Patterns.EMAIL_ADDRESS
    pattern.matcher(this).matches()
}