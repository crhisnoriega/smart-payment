package br.com.trybu.ui.widget.text

fun String.capitalizeWords() = lowercase()
    .split(" ")
    .joinToString(" ") { word -> word.replaceFirstChar { char -> char.uppercase() } }

fun String.hasMultipleWords() =
    isNotEmpty() && split(' ').size > 1