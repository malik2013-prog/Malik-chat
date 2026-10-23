package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
  @PrimaryKey val id: String,
  val chatId: String,
  val senderId: String,
  val senderName: String,
  val senderAvatar: String,
  val text: String,
  val ciphertext: String,
  val nonceHex: String,
  val messageType: String,
  val timestamp: Long,
  val isOutgoing: Boolean,
  val deliveryStatus: String,
  val isDeletedBySender: Boolean = false, // Ghost Vault Anti-Delete
  val isStarred: Boolean = false,
  val voiceDurationSeconds: Int = 0,
  val voiceWaveformCsv: String = "",
  val voiceTranscription: String? = null,
  val mediaUri: String? = null,
  val isRoundVideo: Boolean = false,
  val translatedText: String? = null,
  val replyToId: String? = null,
  val replyToSnippet: String? = null
)

@Entity(tableName = "chats")
data class ChatEntity(
  @PrimaryKey val id: String,
  val title: String,
  val avatarUrl: String,
  val isGroup: Boolean,
  val recipientUserId: String,
  val recipientUsername: String,
  val recipientDisplayName: String,
  val recipientIsVerified: Boolean,
  val recipientPhoneMasked: String,
  val recipientDcRegion: String,
  val recipientAccountAge: String,
  val recipientBio: String,
  val recipientPublicKey: String,
  val lastMessageText: String,
  val lastMessageTimestamp: Long,
  val unreadCount: Int,
  val isEncrypted: Boolean = true,
  val isPinned: Boolean = false,
  val isMuted: Boolean = false
)
