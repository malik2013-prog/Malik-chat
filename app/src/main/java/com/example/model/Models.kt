package com.example.model

enum class MessageType {
  TEXT,
  VOICE,
  MEDIA,
  SYSTEM
}

enum class DeliveryStatus {
  SENDING,
  SENT,
  DELIVERED,
  READ
}

data class User(
  val id: String,
  val username: String,
  val displayName: String,
  val avatarUrl: String = "",
  val isVerified: Boolean = false,
  val isAdmin: Boolean = false,
  val phoneMasked: String = "+7 (962) 641-61-55",
  val dcRegion: String = "DC2, Amsterdam, NL",
  val dcId: String = "8315588207",
  val accountAge: String = "28 мая 2013 (13 лет)",
  val bio: String = "Zero-knowledge security enthusiast. Encrypted by default.",
  val publicKeyHex: String = "4a8f9c1b7e3d2a5c8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d",
  val statusText: String = "1 в сети",
  val isOnline: Boolean = true
)

data class Message(
  val id: String,
  val chatId: String,
  val senderId: String,
  val senderName: String,
  val senderAvatar: String = "",
  val text: String,
  val ciphertext: String = "",
  val nonceHex: String = "",
  val messageType: MessageType = MessageType.TEXT,
  val timestamp: Long = System.currentTimeMillis(),
  val isOutgoing: Boolean = false,
  val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,
  val isDeletedBySender: Boolean = false, // Ghost Anti-Delete feature!
  val isStarred: Boolean = false,
  val voiceDurationSeconds: Int = 0,
  val voiceWaveform: List<Float> = emptyList(),
  val voiceTranscription: String? = null,
  val mediaUri: String? = null,
  val isRoundVideo: Boolean = false,
  val translatedText: String? = null,
  val replyToId: String? = null,
  val replyToSnippet: String? = null
)

data class Chat(
  val id: String,
  val title: String,
  val avatarUrl: String = "",
  val isGroup: Boolean = false,
  val recipientUser: User? = null,
  val lastMessageText: String = "",
  val lastMessageTimestamp: Long = System.currentTimeMillis(),
  val unreadCount: Int = 0,
  val isEncrypted: Boolean = true,
  val isPinned: Boolean = false,
  val isMuted: Boolean = false,
  val warningNote: String? = null // e.g. "⚠️ Не удалось..." for Lumina AI
)

data class Contact(
  val id: String,
  val name: String,
  val username: String,
  val lastSeenText: String = "был(а) недавно",
  val isOnline: Boolean = false,
  val phone: String = "+7 (999) 000-00-00",
  val iconEmoji: String? = null
)

data class ProxyServer(
  val id: String,
  val title: String,
  val host: String,
  val port: Int,
  val pingMs: Int,
  val isConnected: Boolean = false,
  val proxyType: String = "MTProto"
)

data class MusicTrack(
  val title: String = "An Italian Magician Be Like",
  val artist: String = "Project Dumb",
  val album: String = "An Italian Magician Be Like - Single",
  val durationSeconds: Int = 186,
  val isPlaying: Boolean = false
)

data class GhostSettings(
  val isGhostActive: Boolean = true,
  val hideReadReceipts: Boolean = true,
  val hideOnlineStatus: Boolean = true,
  val typingDelayEnabled: Boolean = true,
  val antiDeleteVaultEnabled: Boolean = true,
  val autoDecryptOnArrival: Boolean = true
)

data class ActivityAnalytics(
  val totalMessagesSent: Int = 142,
  val totalMessagesReceived: Int = 389,
  val ghostSavedCount: Int = 18,
  val encryptionPercentage: Int = 100,
  val peakActiveHour: String = "22:00 - 02:00 (MSK)",
  val currentServerPingMs: Int = 24,
  val serverRegion: String = "DC2 • Amsterdam Cloud Relay",
  val dailyStats: List<DailyMetric> = listOf(
    DailyMetric("Пн", 48),
    DailyMetric("Вт", 72),
    DailyMetric("Ср", 95),
    DailyMetric("Чт", 64),
    DailyMetric("Пт", 130),
    DailyMetric("Сб", 112),
    DailyMetric("Вс", 88)
  )
)

data class DailyMetric(
  val day: String,
  val messageCount: Int
)
