package com.example

import com.example.crypto.E2EECryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class E2EECryptoEngineTest {

  @Test
  fun testE2EESymmetricEncryptionAndDecryption() {
    val key = E2EECryptoEngine.deriveDeterministicChatKey("usr_zenox_001", "usr_eva_002")
    assertEquals(32, key.size)

    val secretMessage = "Top Secret Mail Chat Message с режимом Призрака 👻"
    val encrypted = E2EECryptoEngine.encrypt(secretMessage, key)

    assertNotNull(encrypted.ciphertextBase64)
    assertEquals(24, encrypted.nonceHex.length) // 12 bytes = 24 hex chars

    val decrypted = E2EECryptoEngine.decrypt(encrypted.ciphertextBase64, encrypted.nonceHex, key)
    assertEquals(secretMessage, decrypted)
  }

  @Test
  fun testSafetyNumberGeneration() {
    val myKey = "8f3e2b9c7a1d4f6e8a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f"
    val peerKey = "c1b2a3f4e5d6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2"

    val safetyNumber = E2EECryptoEngine.generateSafetyNumber(myKey, peerKey)
    val chunks = safetyNumber.split(" ")

    assertEquals(6, chunks.size)
    chunks.forEach { chunk ->
      assertEquals(5, chunk.length)
      assertTrue(chunk.all { it.isDigit() })
    }
  }
}
