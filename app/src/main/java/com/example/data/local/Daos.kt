package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
  @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
  fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE isDeletedBySender = 1 ORDER BY timestamp DESC")
  fun getAllDeletedMessages(): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE isStarred = 1 ORDER BY timestamp DESC")
  fun getAllStarredMessages(): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE text LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
  fun searchMessages(query: String): Flow<List<MessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessages(messages: List<MessageEntity>)

  // Ghost Vault Anti-Delete: remote deletion request intercepted and marked instead of deleted!
  @Query("UPDATE messages SET isDeletedBySender = 1 WHERE id = :messageId")
  suspend fun markAsDeletedBySender(messageId: String)

  // Hard deletion (for admin message moderation or manual user local purge)
  @Query("DELETE FROM messages WHERE id = :messageId")
  suspend fun deleteMessagePermanently(messageId: String)

  @Query("DELETE FROM messages WHERE chatId = :chatId")
  suspend fun deleteChatMessages(chatId: String)

  @Query("UPDATE messages SET deliveryStatus = :status WHERE id = :messageId")
  suspend fun updateDeliveryStatus(messageId: String, status: String)

  @Query("UPDATE messages SET isStarred = :isStarred WHERE id = :messageId")
  suspend fun toggleStar(messageId: String, isStarred: Boolean)

  @Query("UPDATE messages SET voiceTranscription = :transcription WHERE id = :messageId")
  suspend fun updateTranscription(messageId: String, transcription: String)

  @Query("UPDATE messages SET translatedText = :translatedText WHERE id = :messageId")
  suspend fun updateTranslation(messageId: String, translatedText: String)

  @Query("SELECT COUNT(*) FROM messages")
  fun getTotalMessageCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM messages WHERE isDeletedBySender = 1")
  fun getDeletedMessageCount(): Flow<Int>
}

@Dao
interface ChatDao {
  @Query("SELECT * FROM chats ORDER BY isPinned DESC, lastMessageTimestamp DESC")
  fun getAllChats(): Flow<List<ChatEntity>>

  @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
  fun getChatById(chatId: String): Flow<ChatEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChat(chat: ChatEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChats(chats: List<ChatEntity>)

  @Query("UPDATE chats SET lastMessageText = :text, lastMessageTimestamp = :timestamp WHERE id = :chatId")
  suspend fun updateLastMessage(chatId: String, text: String, timestamp: Long)

  @Query("UPDATE chats SET recipientIsVerified = :isVerified WHERE recipientUserId = :userId")
  suspend fun updateVerification(userId: String, isVerified: Boolean)

  @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
  suspend fun clearUnreadCount(chatId: String)

  @Query("UPDATE chats SET isMuted = NOT isMuted WHERE id = :chatId")
  suspend fun toggleMute(chatId: String)

  @Query("UPDATE chats SET isPinned = NOT isPinned WHERE id = :chatId")
  suspend fun togglePin(chatId: String)

  @Query("DELETE FROM chats WHERE id NOT IN (:validIds)")
  suspend fun deleteChatsNotIn(validIds: List<String>)

  @Query("DELETE FROM chats")
  suspend fun deleteAllChats()
}
