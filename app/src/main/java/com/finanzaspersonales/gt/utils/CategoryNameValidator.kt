package com.finanzaspersonales.gt.utils

object CategoryNameValidator {
    enum class Error { EMPTY, DUPLICATE }

    fun validate(name: String, existingNames: Collection<String>): Error? {
        val candidate = name.trim()
        if (candidate.isEmpty()) return Error.EMPTY
        if (existingNames.any { it.trim().equals(candidate, ignoreCase = true) }) return Error.DUPLICATE
        return null
    }
}
