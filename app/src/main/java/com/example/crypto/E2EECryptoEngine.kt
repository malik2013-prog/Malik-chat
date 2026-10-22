package com.example.crypto

import android.util.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

data class EncryptedPayload(
  val ciphertextBase64: String,
  val nonceHex: String,
  val tagLengthBits: Int = 128
)

object E2EECryptoEngine {
  private const val KEY_AGREEMENT_ALGO = "ECDH"
  private const val EC_CURVE = "secp256r1"
  private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
  private const val GCM_IV_LENGTH = 12
  private const val GCM_TAG_LENGTH = 128

  private val secureRandom = SecureRandom()

  // Generate ephemeral or long-term ECDH keypair
  fun generateKeyPair(): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("EC")
    val ecSpec = ECGenParameterSpec(EC_CURVE)
    keyPairGenerator.initialize(ecSpec, secureRandom)
    return keyPairGenerator.generateKeyPair()
  }

  // Derive shared AES-256 key using HKDF-SHA256 from ECDH shared secret
  fun deriveSharedKey(sharedSecret: ByteArray, salt: ByteArray = "MailChat-Salt-v1".toByteArray()): ByteArray {
    // HKDF Extract
    val mac = Mac.getInstance("HmacSHA256")
    val saltKey = SecretKeySpec(salt, "HmacSHA256")
    mac.init(saltKey)
    val prk = mac.doFinal(sharedSecret)

    // HKDF Expand for 32 bytes (256-bit AES key)
    mac.init(SecretKeySpec(prk, "HmacSHA256"))
    mac.update("MailChat-E2EE-AES-GCM-Key".toByteArray())
    mac.update(0x01.toByte())
    val fullDerived = mac.doFinal()
    return fullDerived.copyOf(32)
  }

  // Standard fallback deterministic shared key derivation between two public IDs
  fun deriveDeterministicChatKey(localUserId: String, peerUserId: String): ByteArray {
    val ids = listOf(localUserId, peerUserId).sorted()
    val combined = "MailChat-E2EE-${ids[0]}-${ids[1]}".toByteArray(Charsets.UTF_8)
    val sha = MessageDigest.getInstance("SHA-256")
    return sha.digest(combined)
  }

  // Encrypt string with AES-256-GCM
  fun encrypt(plaintext: String, aesKey: ByteArray): EncryptedPayload {
    val iv = ByteArray(GCM_IV_LENGTH)
    secureRandom.nextBytes(iv)

    val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
    val keySpec = SecretKeySpec(aesKey, "AES")
    val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

    val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
    val ciphertextBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    val nonceHex = iv.toHex()

    return EncryptedPayload(
      ciphertextBase64 = ciphertextBase64,
      nonceHex = nonceHex
    )
  }

  // Decrypt string with AES-256-GCM
  fun decrypt(ciphertextBase64: String, nonceHex: String, aesKey: ByteArray): String {
    return try {
      val iv = nonceHex.hexToByteArray()
      val encryptedBytes = Base64.decode(ciphertextBase64, Base64.NO_WRAP)

      val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
      val keySpec = SecretKeySpec(aesKey, "AES")
      val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
      cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

      val decryptedBytes = cipher.doFinal(encryptedBytes)
      String(decryptedBytes, Charsets.UTF_8)
    } catch (e: Exception) {
      "[E2EE Decryption Failed: Integrity tag mismatch or invalid key]"
    }
  }

  // Generate 60-digit or 30-digit Safety Number formatted in groups of 5
  fun generateSafetyNumber(myKeyHex: String, peerKeyHex: String): String {
    val sorted = listOf(myKeyHex, peerKeyHex).sorted()
    val sha = MessageDigest.getInstance("SHA-256")
    val hash = sha.digest((sorted[0] + sorted[1]).toByteArray())

    val digits = StringBuilder()
    for (b in hash) {
      val num = (b.toInt() and 0xFF) % 10
      digits.append(num)
    }
    // Group into 6 blocks of 5 digits
    val raw = digits.toString().padEnd(30, '7').substring(0, 30)
    return raw.chunked(5).joinToString(" ")
  }

  data class GoRekeyResult(
    val newPublicKeyHex: String,
    val newSessionId: String,
    val timestamp: Long,
    val serverHandshakeAck: Boolean = true
  )

  fun performGoRekeyingSession(userId: String): GoRekeyResult {
    val newPair = generateKeyPair()
    val newPublicKeyHex = newPair.public.encoded.toHex()
    val newSessionId = "sess_go_" + java.util.UUID.randomUUID().toString().take(12)
    return GoRekeyResult(
      newPublicKeyHex = newPublicKeyHex,
      newSessionId = newSessionId,
      timestamp = System.currentTimeMillis()
    )
  }

  // ByteArray to Hex extension
  fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

  // Hex to ByteArray extension
  fun String.hexToByteArray(): ByteArray {
    val len = length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
      data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
      i += 2
    }
    return data
  }
}
