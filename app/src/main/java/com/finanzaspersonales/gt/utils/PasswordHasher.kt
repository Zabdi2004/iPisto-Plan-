package com.finanzaspersonales.gt.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Password hashing with a per-user salt. Legacy SHA-256 hashes remain verifiable and
 * are upgraded to PBKDF2 when the user next authenticates. */
object PasswordHasher {
    private const val SALT_LENGTH = 16
    private const val ITERATIONS = 210_000
    private const val KEY_LENGTH_BITS = 256
    private const val PREFIX = "pbkdf2"

    fun hashPassword(password: String): String {
        val salt = ByteArray(SALT_LENGTH).also(SecureRandom()::nextBytes)
        val (algorithm, hash) = derive(password, salt, ITERATIONS)
        return "$PREFIX\$$algorithm\$$ITERATIONS\$${encode(salt)}\$${encode(hash)}"
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
        if (storedHash.startsWith("$PREFIX\$")) {
            val parts = storedHash.split('$')
            if (parts.size != 5) return false
            val algorithm = parts[1].takeIf { it == "sha256" || it == "sha1" } ?: return false
            val iterations = parts[2].toIntOrNull()?.takeIf { it in 100_000..1_000_000 } ?: return false
            val salt = decode(parts[3])
            val expected = decode(parts[4])
            salt.size >= 16 && expected.size == KEY_LENGTH_BITS / 8 &&
                MessageDigest.isEqual(derive(password, salt, iterations, algorithm).second, expected)
        } else {
            verifyLegacy(password, storedHash)
        }
        } catch (_: Exception) {
            false
        }
    }

    fun needsUpgrade(storedHash: String): Boolean = !storedHash.startsWith("$PREFIX\$sha256\$")

    private fun derive(password: String, salt: ByteArray, iterations: Int, preferred: String? = null): Pair<String, ByteArray> {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        return try {
            val algorithm = preferred ?: try {
                "sha256".also { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256") }
            } catch (_: java.security.NoSuchAlgorithmException) { "sha1" }
            val factoryName = if (algorithm == "sha256") "PBKDF2WithHmacSHA256" else "PBKDF2WithHmacSHA1"
            algorithm to SecretKeyFactory.getInstance(factoryName).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun verifyLegacy(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(':')
        if (parts.size != 2) return false
        val salt = decode(parts[0])
        val expected = decode(parts[1])
        var actual = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8).let { salt + it })
        repeat(9_999) { actual = MessageDigest.getInstance("SHA-256").digest(actual) }
        return MessageDigest.isEqual(actual, expected)
    }

    private fun encode(value: ByteArray): String = Base64.encodeToString(value, Base64.NO_WRAP)
    private fun decode(value: String): ByteArray = Base64.decode(value, Base64.DEFAULT)
}
