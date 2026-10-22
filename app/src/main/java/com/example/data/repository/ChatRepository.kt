package com.example.data.repository

import com.example.crypto.E2EECryptoEngine
import com.example.data.ai.GeminiChatAssistant
import com.example.data.local.ChatDao
import com.example.data.local.ChatEntity
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import com.example.data.network.ChatWebSocketManager
import com.example.data.network.WebSocketProtocol
import com.example.data.network.WsEvent
import com.example.model.ActivityAnalytics
import com.example.model.Chat
import com.example.model.Contact
import com.example.model.DeliveryStatus
import com.example.model.GhostSettings
import com.example.model.Message
import com.example.model.MessageType
import com.example.model.MusicTrack
import com.example.model.ProxyServer
import com.example.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class ChatRepository(
  private val chatDao: ChatDao,
  private val messageDao: MessageDao,
  private val wsManager: ChatWebSocketManager = ChatWebSocketManager(),
  private val aiAssistant: GeminiChatAssistant = GeminiChatAssistant(),
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
  // Current active user matching screenshot: ivk1488 / zenox
  private val _currentUser = MutableStateFlow(
    User(
      id = "8315588207",
      username = "@zenox",
      displayName = "zenox",
      avatarUrl = "",
      isVerified = true,
      isAdmin = true,
      phoneMasked = "+7 (962) 641-61-55",
      dcRegion = "DC2, Amsterdam, NL",
      dcId = "8315588207",
      accountAge = "28 мая 2013 (13 лет)",
      bio = "Mail Chat Founder & Lead Architect • AyuGram Core Contributor",
      publicKeyHex = "8f3e2b9c7a1d4f6e8a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f",
      statusText = "1 в сети"
    )
  )
  val currentUser: StateFlow<User> = _currentUser.asStateFlow()

  // Ghost Mode Global State
  private val _ghostSettings = MutableStateFlow(GhostSettings())
  val ghostSettings: StateFlow<GhostSettings> = _ghostSettings.asStateFlow()

  // Live WebSocket Connection Status
  val connectionStatus: StateFlow<String> = wsManager.connectionState

  // Music Player State (from Screenshot 1)
  private val _musicTrack = MutableStateFlow(MusicTrack())
  val musicTrack: StateFlow<MusicTrack> = _musicTrack.asStateFlow()

  // Telegram Login SMS / Code Notification Banner state
  private val _telegramOtpNotification = MutableStateFlow<String?>(null)
  val telegramOtpNotification: StateFlow<String?> = _telegramOtpNotification.asStateFlow()
  private var activeTelegramOtpCode: String = "48291"

  // Proxies list (from Screenshot 5 menu)
  private val _proxies = MutableStateFlow(
    listOf(
      ProxyServer("prx_1", "DC2 Amsterdam (Primary Relay)", "proxy-ams.mailchat.io", 443, 24, true, "MTProto"),
      ProxyServer("prx_2", "DC1 Frankfurt (Europe West)", "proxy-fra.mailchat.io", 443, 38, false, "MTProto"),
      ProxyServer("prx_3", "DC3 Helsinki (Nordic Relay)", "proxy-hel.mailchat.io", 1080, 45, false, "SOCKS5")
    )
  )
  val proxies: StateFlow<List<ProxyServer>> = _proxies.asStateFlow()

  // Contacts list
  private val _contacts = MutableStateFlow(
    listOf(
      Contact("cnt_support", "Поддержка Mail Chat", "@support", "в сети", iconEmoji = "🛡️"),
      Contact("cnt_saved", "Избранное", "@saved", "Облачное хранилище", iconEmoji = "⭐"),
      Contact("cnt_alex", "Алексей Смирнов", "@alex_smirnov", "в сети", iconEmoji = "👨‍💻"),
      Contact("cnt_elena", "Елена Соколова", "@elena_sok", "была 10 минут назад", iconEmoji = "🎨"),
      Contact("cnt_dmitry", "Дмитрий Волков", "@volkov_dev", "был(а) недавно", iconEmoji = "🚀")
    )
  )
  val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

  init {
    scope.launch {
      seedInitialDataIfNeeded()
      listenToWebSocketEvents()
    }
  }

  private fun listenToWebSocketEvents() {
    scope.launch {
      wsManager.eventFlow.collect { event ->
        when (event) {
          is WsEvent.InboundMessage -> {
            val key = E2EECryptoEngine.deriveDeterministicChatKey(_currentUser.value.id, event.senderId)
            val decryptedText = E2EECryptoEngine.decrypt(event.ciphertext, event.nonce, key)
            val messageEntity = MessageEntity(
              id = event.messageId,
              chatId = event.chatId,
              senderId = event.senderId,
              senderName = if (event.senderId == "usr_eva_002") "Eva Rostova" else "Server Core",
              senderAvatar = "",
              text = decryptedText,
              ciphertext = event.ciphertext,
              nonceHex = event.nonce,
              messageType = event.mediaType,
              timestamp = System.currentTimeMillis(),
              isOutgoing = false,
              deliveryStatus = DeliveryStatus.DELIVERED.name,
              isDeletedBySender = false,
              voiceDurationSeconds = event.voiceDuration,
              voiceWaveformCsv = event.voiceWaveform
            )
            messageDao.insertMessage(messageEntity)
            chatDao.updateLastMessage(event.chatId, decryptedText, System.currentTimeMillis())
          }
          is WsEvent.PeerDeletedMessage -> {
            // Anti-Delete Ghost Vault feature: intercept remote delete and preserve locally!
            messageDao.markAsDeletedBySender(event.messageId)
          }
          is WsEvent.DeliveryReceipt -> {
            messageDao.updateDeliveryStatus(event.messageId, event.status)
          }
          else -> {}
        }
      }
    }
  }

  fun updateGhostSettings(newSettings: GhostSettings) {
    _ghostSettings.value = newSettings
  }

  fun toggleGhostMaster() {
    val current = _ghostSettings.value
    _ghostSettings.value = current.copy(isGhostActive = !current.isGhostActive)
  }

  // Observable Chats
  fun getChats(): Flow<List<Chat>> {
    return chatDao.getAllChats().map { entities ->
      entities.map { it.toDomain() }
    }
  }

  // Observable Messages for a specific Chat
  fun getMessages(chatId: String): Flow<List<Message>> {
    return messageDao.getMessagesForChat(chatId).map { entities ->
      entities.map { it.toDomain() }
    }
  }

  // Observable Ghost Vault (intercepted deleted messages across all chats)
  fun getGhostVaultMessages(): Flow<List<Message>> {
    return messageDao.getAllDeletedMessages().map { entities ->
      entities.map { it.toDomain() }
    }
  }

  // Observable Starred Messages
  fun getStarredMessages(): Flow<List<Message>> {
    return messageDao.getAllStarredMessages().map { entities ->
      entities.map { it.toDomain() }
    }
  }

  // Search in messages
  fun searchMessages(query: String): Flow<List<Message>> {
    return messageDao.searchMessages(query).map { entities ->
      entities.map { it.toDomain() }
    }
  }

  // Send an E2EE encrypted text message
  suspend fun sendMessage(chatId: String, recipientId: String, text: String, replyToId: String? = null, replySnippet: String? = null) {
    val key = E2EECryptoEngine.deriveDeterministicChatKey(_currentUser.value.id, recipientId)
    val encrypted = E2EECryptoEngine.encrypt(text, key)
    val msgId = "msg_${UUID.randomUUID().toString().take(12)}"

    val entity = MessageEntity(
      id = msgId,
      chatId = chatId,
      senderId = _currentUser.value.id,
      senderName = _currentUser.value.displayName,
      senderAvatar = _currentUser.value.avatarUrl,
      text = text,
      ciphertext = encrypted.ciphertextBase64,
      nonceHex = encrypted.nonceHex,
      messageType = MessageType.TEXT.name,
      timestamp = System.currentTimeMillis(),
      isOutgoing = true,
      deliveryStatus = DeliveryStatus.SENT.name,
      isDeletedBySender = false,
      replyToId = replyToId,
      replyToSnippet = replySnippet
    )
    messageDao.insertMessage(entity)
    chatDao.updateLastMessage(chatId, text, System.currentTimeMillis())

    val packet = WebSocketProtocol.createEncryptedMessagePacket(
      messageId = msgId,
      chatId = chatId,
      senderId = _currentUser.value.id,
      recipientId = recipientId,
      ciphertextBase64 = encrypted.ciphertextBase64,
      nonceHex = encrypted.nonceHex,
      messageType = MessageType.TEXT.name,
      isGhostReadStealth = _ghostSettings.value.hideReadReceipts,
      isGhostTypingStealth = _ghostSettings.value.typingDelayEnabled
    )
    wsManager.sendPacket(packet)
  }

  // Send an encrypted Voice Message
  suspend fun sendVoiceMessage(chatId: String, recipientId: String, durationSeconds: Int, waveform: List<Float>) {
    val key = E2EECryptoEngine.deriveDeterministicChatKey(_currentUser.value.id, recipientId)
    val audioDescriptor = "🎙️ Голосовое сообщение ($durationSeconds сек)"
    val encrypted = E2EECryptoEngine.encrypt(audioDescriptor, key)
    val msgId = "voice_${UUID.randomUUID().toString().take(12)}"
    val waveformCsv = waveform.joinToString(",") { "%.2f".format(it) }

    val entity = MessageEntity(
      id = msgId,
      chatId = chatId,
      senderId = _currentUser.value.id,
      senderName = _currentUser.value.displayName,
      senderAvatar = _currentUser.value.avatarUrl,
      text = audioDescriptor,
      ciphertext = encrypted.ciphertextBase64,
      nonceHex = encrypted.nonceHex,
      messageType = MessageType.VOICE.name,
      timestamp = System.currentTimeMillis(),
      isOutgoing = true,
      deliveryStatus = DeliveryStatus.SENT.name,
      voiceDurationSeconds = durationSeconds,
      voiceWaveformCsv = waveformCsv
    )
    messageDao.insertMessage(entity)
    chatDao.updateLastMessage(chatId, audioDescriptor, System.currentTimeMillis())
  }

  // Peer Simulators for interactive demonstration in applet
  fun simulateIncomingPeerMessage(chatId: String, peerId: String, text: String) {
    val key = E2EECryptoEngine.deriveDeterministicChatKey(_currentUser.value.id, peerId)
    val encrypted = E2EECryptoEngine.encrypt(text, key)
    wsManager.simulatePeerReply(
      chatId = chatId,
      senderId = peerId,
      ciphertext = encrypted.ciphertextBase64,
      nonce = encrypted.nonceHex
    )
  }

  fun simulatePeerDeletingLastMessage(messageId: String, chatId: String) {
    wsManager.simulatePeerDeletedMessage(messageId, chatId)
  }

  // Star message
  suspend fun toggleStar(messageId: String, currentStarred: Boolean) {
    messageDao.toggleStar(messageId, !currentStarred)
  }

  // AI Transcribe Voice
  suspend fun transcribeVoice(messageId: String, duration: Int) {
    val transcript = aiAssistant.transcribeVoiceMessage(duration)
    messageDao.updateTranscription(messageId, transcript)
  }

  // AI Translate Message
  suspend fun translateMessage(messageId: String, text: String, targetLanguage: String = "English") {
    val translation = aiAssistant.translateMessage(text, targetLanguage)
    messageDao.updateTranslation(messageId, translation)
  }

  // AI Summarize Chat
  suspend fun getChatSummary(chatId: String, messages: List<String>): String {
    return aiAssistant.summarizeChat(messages)
  }

  // AI Quick Replies
  suspend fun getQuickReplies(lastMessage: String): List<String> {
    return aiAssistant.generateQuickReplies(lastMessage)
  }

  // Admin Actions for @zenox
  suspend fun adminToggleUserVerification(userId: String, isVerified: Boolean) {
    chatDao.updateVerification(userId, isVerified)
  }

  suspend fun adminDeleteMessagePermanently(messageId: String) {
    messageDao.deleteMessagePermanently(messageId)
  }

  fun getActivityAnalytics(): ActivityAnalytics {
    return ActivityAnalytics()
  }

  // --- TELEGRAM PHONE LOGIN & CODE FLOW ---
  fun requestTelegramAuthCode(phone: String): String {
    val randomCode = (10000 + Random.nextInt(90000)).toString()
    activeTelegramOtpCode = randomCode
    scope.launch {
      delay(800)
      _telegramOtpNotification.value = "Your login code: $randomCode. Do not give this code to anyone, even if they say they are from Telegram!"
    }
    return randomCode
  }

  fun verifyTelegramAuthCode(enteredCode: String, newPhone: String): Boolean {
    if (enteredCode.trim() == activeTelegramOtpCode.trim()) {
      _currentUser.value = _currentUser.value.copy(phoneMasked = newPhone)
      _telegramOtpNotification.value = null
      return true
    }
    return false
  }

  fun dismissTelegramNotification() {
    _telegramOtpNotification.value = null
  }

  // --- GO KEY REGENERATION / SESSION RATCHETING ---
  fun regenerateSessionKeysViaGo(): E2EECryptoEngine.GoRekeyResult {
    val result = E2EECryptoEngine.performGoRekeyingSession(_currentUser.value.id)
    _currentUser.value = _currentUser.value.copy(publicKeyHex = result.newPublicKeyHex)
    return result
  }

  // --- MUSIC PLAYER CONTROLS ---
  fun toggleMusicPlayback() {
    val current = _musicTrack.value
    _musicTrack.value = current.copy(isPlaying = !current.isPlaying)
  }

  // --- PROXY CONTROLS ---
  fun toggleProxy(proxyId: String) {
    val list = _proxies.value.map { proxy ->
      if (proxy.id == proxyId) {
        proxy.copy(isConnected = !proxy.isConnected)
      } else {
        proxy.copy(isConnected = false)
      }
    }
    _proxies.value = list
  }

  // Seed clean chats: Поддержка Mail Chat, Избранное, Алексей Смирнов
  private suspend fun seedInitialDataIfNeeded() {
    val validChatIds = listOf("chat_support", "chat_saved", "chat_alex")
    chatDao.deleteChatsNotIn(validChatIds)

    val chats = listOf(
      ChatEntity(
        id = "chat_support",
        title = "Поддержка Mail Chat",
        avatarUrl = "",
        isGroup = false,
        recipientUserId = "usr_mailchat_support",
        recipientUsername = "@support",
        recipientDisplayName = "Поддержка Mail Chat",
        recipientIsVerified = true,
        recipientPhoneMasked = "+7 (800) 555-35-35",
        recipientDcRegion = "DC2, Amsterdam, NL",
        recipientAccountAge = "5 лет",
        recipientBio = "Официальная служба безопасности и клиентской поддержки Mail Chat",
        recipientPublicKey = "f1e2d3c4b5a697887766554433221100aabbccddeeff00112233445566778899",
        lastMessageText = "Добро пожаловать в Mail Chat! Ваш аккаунт защищен сквозным шифрованием X25519 и режимом Призрака 👻",
        lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 2,
        unreadCount = 1,
        isEncrypted = true,
        isPinned = true
      ),
      ChatEntity(
        id = "chat_saved",
        title = "Избранное",
        avatarUrl = "",
        isGroup = false,
        recipientUserId = "usr_saved_vault",
        recipientUsername = "@saved",
        recipientDisplayName = "Избранное",
        recipientIsVerified = false,
        recipientPhoneMasked = "Личное облако",
        recipientDcRegion = "Локально + Облако",
        recipientAccountAge = "С момента создания",
        recipientBio = "Персональное безопасное облачное хранилище заметок, ключей и файлов",
        recipientPublicKey = "c1b2a3f4e5d6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2",
        lastMessageText = "🔑 Заметки, сохраненные сообщения и ключи сессий E2EE",
        lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
        unreadCount = 0,
        isEncrypted = true,
        isPinned = true
      ),
      ChatEntity(
        id = "chat_alex",
        title = "Алексей Смирнов",
        avatarUrl = "",
        isGroup = false,
        recipientUserId = "usr_alex_001",
        recipientUsername = "@alex_smirnov",
        recipientDisplayName = "Алексей Смирнов",
        recipientIsVerified = false,
        recipientPhoneMasked = "+7 (916) 123-45-67",
        recipientDcRegion = "DC2, Amsterdam, NL",
        recipientAccountAge = "4 года",
        recipientBio = "Разработчик • Always online 🚀",
        recipientPublicKey = "a1b2c3d4e5f60718293a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e",
        lastMessageText = "Привет! Проверил сквозное шифрование, работает мгновенно 🔥",
        lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 45,
        unreadCount = 2,
        isEncrypted = true,
        isPinned = false
      )
    )
    chatDao.insertChats(chats)

    // Seed messages for Поддержка Mail Chat
    val messagesSupport = listOf(
      MessageEntity(
        id = "msg_sup_01",
        chatId = "chat_support",
        senderId = "usr_mailchat_support",
        senderName = "Поддержка Mail Chat",
        senderAvatar = "",
        text = "Здравствуйте, @zenox! Рады приветствовать вас в защищенном мессенджере Mail Chat.",
        ciphertext = "U2FsdGVkX19zdXBwb3J0TVNHMQ==",
        nonceHex = "0102030405060708090a0b0c",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
        isOutgoing = false,
        deliveryStatus = DeliveryStatus.READ.name
      ),
      MessageEntity(
        id = "msg_sup_02",
        chatId = "chat_support",
        senderId = "usr_mailchat_support",
        senderName = "Поддержка Mail Chat",
        senderAvatar = "",
        text = "Все ваши диалоги защищены сквозным шифрованием (ECDH X25519 + AES-256-GCM). Режим Призрака (Ghost Mode) доступен в верхней панели.",
        ciphertext = "U2FsdGVkX19zdXBwb3J0TVNHMg==",
        nonceHex = "0c0b0a090807060504030201",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
        isOutgoing = false,
        deliveryStatus = DeliveryStatus.READ.name
      ),
      MessageEntity(
        id = "msg_sup_03",
        chatId = "chat_support",
        senderId = "usr_mailchat_support",
        senderName = "Поддержка Mail Chat",
        senderAvatar = "",
        text = "Добро пожаловать в Mail Chat! Ваш аккаунт защищен сквозным шифрованием X25519 и режимом Призрака 👻",
        ciphertext = "U2FsdGVkX19zdXBwb3J0TVNHMw==",
        nonceHex = "a1b2c3d4e5f60718293a4b5c",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 2,
        isOutgoing = false,
        deliveryStatus = DeliveryStatus.DELIVERED.name
      )
    )
    messageDao.insertMessages(messagesSupport)

    // Seed messages for Избранное
    val messagesSaved = listOf(
      MessageEntity(
        id = "msg_sav_01",
        chatId = "chat_saved",
        senderId = _currentUser.value.id,
        senderName = "zenox",
        senderAvatar = "",
        text = "🔒 Мои заметки и конфигурация E2EE сессий",
        ciphertext = "U2FsdGVkX19zYXZlZDAx",
        nonceHex = "11223344556677889900aabb",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60,
        isOutgoing = true,
        deliveryStatus = DeliveryStatus.READ.name,
        isStarred = true
      ),
      MessageEntity(
        id = "msg_sav_02",
        chatId = "chat_saved",
        senderId = _currentUser.value.id,
        senderName = "zenox",
        senderAvatar = "",
        text = "🔑 Заметки, сохраненные сообщения и ключи сессий E2EE",
        ciphertext = "U2FsdGVkX19zYXZlZDAy",
        nonceHex = "2233445566778899aabbccdd",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
        isOutgoing = true,
        deliveryStatus = DeliveryStatus.READ.name,
        isStarred = true
      )
    )
    messageDao.insertMessages(messagesSaved)

    // Seed messages for Алексей Смирнов
    val messagesAlex = listOf(
      MessageEntity(
        id = "msg_alx_01",
        chatId = "chat_alex",
        senderId = "usr_alex_001",
        senderName = "Алексей Смирнов",
        senderAvatar = "",
        text = "Привет @zenox! Оценил новый дизайн и анимации в приложении, прямо как на iOS! 🍏✨",
        ciphertext = "U2FsdGVkX19hbGV4MDE=",
        nonceHex = "3344556677889900aabbccdd",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 50,
        isOutgoing = false,
        deliveryStatus = DeliveryStatus.READ.name
      ),
      MessageEntity(
        id = "msg_alx_02",
        chatId = "chat_alex",
        senderId = _currentUser.value.id,
        senderName = "zenox",
        senderAvatar = "",
        text = "Да, добавили клавиатуру айфоновских смайликов и кучу плавных анимаций реакций 🔥🚀",
        ciphertext = "U2FsdGVkX19hbGV4MDI=",
        nonceHex = "445566778899aabbccddee00",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 48,
        isOutgoing = true,
        deliveryStatus = DeliveryStatus.READ.name
      ),
      MessageEntity(
        id = "msg_alx_03",
        chatId = "chat_alex",
        senderId = "usr_alex_001",
        senderName = "Алексей Смирнов",
        senderAvatar = "",
        text = "Привет! Проверил сквозное шифрование, работает мгновенно 🔥",
        ciphertext = "U2FsdGVkX19hbGV4MDM=",
        nonceHex = "5566778899aabbccddee0011",
        messageType = MessageType.TEXT.name,
        timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
        isOutgoing = false,
        deliveryStatus = DeliveryStatus.DELIVERED.name
      )
    )
    messageDao.insertMessages(messagesAlex)
  }

  private fun MessageEntity.toDomain(): Message {
    val waveform = if (voiceWaveformCsv.isNotBlank()) {
      voiceWaveformCsv.split(",").mapNotNull { it.toFloatOrNull() }
    } else emptyList()

    return Message(
      id = id,
      chatId = chatId,
      senderId = senderId,
      senderName = senderName,
      senderAvatar = senderAvatar,
      text = text,
      ciphertext = ciphertext,
      nonceHex = nonceHex,
      messageType = MessageType.valueOf(messageType),
      timestamp = timestamp,
      isOutgoing = isOutgoing,
      deliveryStatus = DeliveryStatus.valueOf(deliveryStatus),
      isDeletedBySender = isDeletedBySender,
      isStarred = isStarred,
      voiceDurationSeconds = voiceDurationSeconds,
      voiceWaveform = waveform,
      voiceTranscription = voiceTranscription,
      translatedText = translatedText,
      replyToId = replyToId,
      replyToSnippet = replyToSnippet
    )
  }

  private fun ChatEntity.toDomain(): Chat {
    val recipient = User(
      id = recipientUserId,
      username = recipientUsername,
      displayName = recipientDisplayName,
      avatarUrl = avatarUrl,
      isVerified = recipientIsVerified,
      isAdmin = recipientUsername == "@zenox",
      phoneMasked = recipientPhoneMasked,
      dcRegion = recipientDcRegion,
      accountAge = recipientAccountAge,
      bio = recipientBio,
      publicKeyHex = recipientPublicKey
    )

    return Chat(
      id = id,
      title = title,
      avatarUrl = avatarUrl,
      isGroup = isGroup,
      recipientUser = recipient,
      lastMessageText = lastMessageText,
      lastMessageTimestamp = lastMessageTimestamp,
      unreadCount = unreadCount,
      isEncrypted = isEncrypted,
      isPinned = isPinned,
      isMuted = isMuted
    )
  }
}
