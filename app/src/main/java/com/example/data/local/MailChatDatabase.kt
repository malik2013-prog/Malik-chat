package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [MessageEntity::class, ChatEntity::class],
  version = 2,
  exportSchema = false
)
abstract class MailChatDatabase : RoomDatabase() {
  abstract fun messageDao(): MessageDao
  abstract fun chatDao(): ChatDao

  companion object {
    @Volatile
    private var INSTANCE: MailChatDatabase? = null

    fun getDatabase(context: Context): MailChatDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MailChatDatabase::class.java,
          "mailchat_encrypted_vault.db"
        )
        .fallbackToDestructiveMigration(true)
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
