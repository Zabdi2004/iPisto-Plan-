package com.finanzaspersonales.gt.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {
    private const val SALT_LENGTH = 16
    private const val HASH_ALGORITHM = "SHA-256"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    fun hashPassword(password: String): String {
        val salt = ByteArray(SALT_LENGTH).apply {
            SecureRandom().nextBytes(this)
        }
        val hash = hashWithSalt(password, salt)
        val saltString = Base64.getEncoder().encodeToString(salt)
        val hashString = Base64.getEncoder().encodeToString(hash)
        return "$saltString:$hashString"
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val parts = storedHash.split(":")
            if (parts.size != 2) return false
            val salt = Base64.getDecoder().decode(parts[0])
            val hash = Base64.getDecoder().decode(parts[1])
            val computedHash = hashWithSalt(password, salt)
            computedHash.contentEquals(hash)
        } catch (e: Exception) {
            false
        }
    }

    private fun hashWithSalt(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance(HASH_ALGORITHM)
        md.update(salt)
        var hash = md.digest(password.toByteArray())
        repeat(ITERATIONS - 1) {
            md.reset()
            hash = md.digest(hash)
        }
        return hash
    }
}
