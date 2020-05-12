package com.example.general

import java.security.MessageDigest
import java.security.SecureRandom

class SaltHash {
    companion object{
        @ExperimentalStdlibApi
        fun generateHash(data: String, salt: ByteArray, algorithm: String= "SHA-256"): String {
            var digest: MessageDigest= MessageDigest.getInstance(algorithm);
            digest.reset()
            digest.update(salt)
            var hash: ByteArray= digest.digest(data.toByteArray());
            return hash.toHexString();
        }

        fun createSalt(): ByteArray{
            var bytes: ByteArray = ByteArray(5)
            var random: SecureRandom= SecureRandom()
            random.nextBytes(bytes)
            return bytes
        }
    }

}

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun String.hexStringToByteArray() = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }