package com.example.data.network

import org.json.JSONObject

/**
 * Mail Chat High-Performance Secure WebSocket Protocol
 * Used for real-time bidirectional messaging, E2EE key exchange, and stealth ghost routing.
 */
object WebSocketProtocol {
  const val TYPE_HANDSHAKE_INIT = "HANDSHAKE_INIT"
  const val TYPE_HANDSHAKE_ACK = "HANDSHAKE_ACK"
  const val TYPE_E2EE_KEY_EXCHANGE = "E2EE_KEY_EXCHANGE"
  const val TYPE_ENCRYPTED_MESSAGE = "ENCRYPTED_MESSAGE"
  const val TYPE_MESSAGE_STATUS = "MESSAGE_STATUS"
  const val TYPE_TYPING_INDICATOR = "TYPING_INDICATOR"
  const val TYPE_REMOTE_DELETE = "REMOTE_DELETE"
  const val TYPE_PRESENCE_UPDATE = "PRESENCE_UPDATE"
  const val TYPE_ADMIN_COMMAND = "ADMIN_COMMAND"

  // Build JSON handshake packet
  fun createHandshakePacket(
    userId: String,
    publicKeyHex: String,
    clientVersion: String = "2.4.0-ghost",
    deviceDc: String = "DC2"
  ): String {
    val json = JSONObject()
    json.put("type", TYPE_HANDSHAKE_INIT)
    json.put("timestamp", System.currentTimeMillis())
    val payload = JSONObject().apply {
      put("user_id", userId)
      put("public_key", publicKeyHex)
      put("client_version", clientVersion)
      put("preferred_dc", deviceDc)
    }
    json.put("payload", payload)
    return json.toString()
  }

  // Build E2EE Encrypted Message JSON Packet with Ghost Mode headers
  fun createEncryptedMessagePacket(
    messageId: String,
    chatId: String,
    senderId: String,
    recipientId: String,
    ciphertextBase64: String,
    nonceHex: String,
    messageType: String,
    isGhostReadStealth: Boolean,
    isGhostTypingStealth: Boolean,
    voiceDurationSeconds: Int = 0,
    voiceWaveformCsv: String = ""
  ): String {
    val json = JSONObject()
    json.put("type", TYPE_ENCRYPTED_MESSAGE)
    json.put("message_id", messageId)
    json.put("chat_id", chatId)
    json.put("timestamp", System.currentTimeMillis())

    // Ghost headers allow the server relay to know routing flags without reading content
    val ghostHeaders = JSONObject().apply {
      put("stealth_read_receipt", isGhostReadStealth)
      put("stealth_typing", isGhostTypingStealth)
      put("preserve_anti_delete", true)
    }
    json.put("ghost_headers", ghostHeaders)

    val payload = JSONObject().apply {
      put("sender_id", senderId)
      put("recipient_id", recipientId)
      put("ciphertext", ciphertextBase64)
      put("nonce", nonceHex)
      put("algo", "AES-256-GCM")
      put("media_type", messageType)
      if (messageType == "VOICE") {
        put("voice_duration", voiceDurationSeconds)
        put("voice_waveform", voiceWaveformCsv)
      }
    }
    json.put("payload", payload)
    return json.toString()
  }

  // Build Typing Indicator packet respecting Ghost mode
  fun createTypingPacket(
    chatId: String,
    userId: String,
    isTyping: Boolean,
    isGhostSuppressed: Boolean
  ): String? {
    if (isGhostSuppressed) {
      // In Ghost mode, typing packet is completely dropped or withheld
      return null
    }
    val json = JSONObject()
    json.put("type", TYPE_TYPING_INDICATOR)
    json.put("chat_id", chatId)
    val payload = JSONObject().apply {
      put("user_id", userId)
      put("is_typing", isTyping)
      put("timestamp", System.currentTimeMillis())
    }
    json.put("payload", payload)
    return json.toString()
  }

  // Build Read Receipt packet respecting Ghost mode
  fun createReadReceiptPacket(
    messageId: String,
    chatId: String,
    readerId: String,
    isGhostStealth: Boolean
  ): String? {
    if (isGhostStealth) {
      // Ghost Mode: NEVER dispatch read receipts to sender
      return null
    }
    val json = JSONObject()
    json.put("type", TYPE_MESSAGE_STATUS)
    val payload = JSONObject().apply {
      put("message_id", messageId)
      put("chat_id", chatId)
      put("status", "READ")
      put("read_by", readerId)
      put("read_at", System.currentTimeMillis())
    }
    json.put("payload", payload)
    return json.toString()
  }
}
