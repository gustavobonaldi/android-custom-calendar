package br.com.bonaldi.customcalendar.helpers

object StringHelper {
    fun String?.orEmpty() = this ?: ""
}