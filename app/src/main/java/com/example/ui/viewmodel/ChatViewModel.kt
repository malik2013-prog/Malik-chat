package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.E2EECryptoEngine
import com.example.data.local.MailChatDatabase
import com.example.data.repository.ChatRepository
import com.example.model.ActivityAnalytics
import com.example.model.Chat
import com.example.model.GhostSettings
import com.example.model.Message
import com.example.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatViewModel(application: Application) : AndroidViewModel(application) {
  private val database = MailChatDatabase.getDatabase(application)
  private val repository = ChatRepository(
    chatDao = database.chatDao(),
    messageDao = database.messageDao()
  )

  val currentUser: StateFlow<User> = repository.currentUser
  val ghostSettings: StateFlow<GhostSettings> = repository.ghostSettings
  val connectionStatus: StateFlow<String> = repository.connectionStatus

  private val _activeChatId = MutableStateFlow<String?>("chat_eva_01")
  val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

  val allChats: StateFlow<List<Chat>> = repository.getChats()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedFilter = MutableStateFlow("ALL")
  val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

  // Filtered Chats
  val filteredChats: StateFlow<List<Chat>> = combine(allChats, _searchQuery, _selectedFilter) { chats, query, filter ->
    chats.filter { chat ->
      val matchesQuery = query.isBlank() ||
        chat.title.contains(query, ignoreCase = true) ||
        chat.lastMessageText.contains(query, ignoreCase = true) ||
        (chat.recipientUser?.username?.contains(query, ignoreCase = true) == true)

      val matchesFilter = when (filter) {
        "UNREAD" -> chat.unreadCount > 0
        "ENCRYPTED" -> chat.isEncrypted
        "PINNED" -> chat.isPinned
        else -> true
      }

      matchesQuery && matchesFilter
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Active chat messages
  private val _activeMessages = MutableStateFlow<List<Message>>(emptyList())
  val activeMessages: StateFlow<List<Message>> = _activeMessages.asStateFlow()

  private var messagesObservationJob: Job? = null

  // Ghost Vault messages
  val ghostVaultMessages: StateFlow<List<Message>> = repository.getGhostVaultMessages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Starred messages
  val starredMessages: StateFlow<List<Message>> = repository.getStarredMessages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // AyuGram Ghost overlay menu toggle
  private val _isGhostOverlayVisible = MutableStateFlow(false)
  val isGhostOverlayVisible: StateFlow<Boolean> = _isGhostOverlayVisible.asStateFlow()

  // Safety Number (Fingerprint) dialog
  private val _safetyNumberData = MutableStateFlow<Pair<String, String>?>(null)
  val safetyNumberData: StateFlow<Pair<String, String>?> = _safetyNumberData.asStateFlow()

  // AI Assistant states
  private val _aiSummary = MutableStateFlow<String?>(null)
  val aiSummary: StateFlow<String?> = _aiSummary.asStateFlow()

  private val _isAiLoading = MutableStateFlow(false)
  val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

  private val _smartReplies = MutableStateFlow<List<String>>(emptyList())
  val smartReplies: StateFlow<List<String>> = _smartReplies.asStateFlow()

  // Voice recording state
  private val _isRecordingVoice = MutableStateFlow(false)
  val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

  private val _recordingDuration = MutableStateFlow(0)
  val recordingDuration: StateFlow<Int> = _recordingDuration.asStateFlow()
  private var voiceTimerJob: Job? = null

  // Reply state
  private val _replyingTo = MutableStateFlow<Message?>(null)
  val replyingTo: StateFlow<Message?> = _replyingTo.asStateFlow()

  // Analytics
  val analytics: ActivityAnalytics = repository.getActivityAnalytics()

  init {
    observeActiveChat("chat_support")
    fetchQuickReplies("Добро пожаловать в Mail Chat! Ваш аккаунт защищен сквозным шифрованием X25519 и режимом Призрака 👻")
  }

  fun selectChat(chatId: String) {
    _activeChatId.value = chatId
    observeActiveChat(chatId)
    _replyingTo.value = null
    _aiSummary.value = null
  }

  private fun observeActiveChat(chatId: String) {
    messagesObservationJob?.cancel()
    messagesObservationJob = viewModelScope.launch {
      repository.getMessages(chatId).collect { msgs ->
        _activeMessages.value = msgs
        // Update quick replies on latest incoming message
        val lastMsg = msgs.lastOrNull { !it.isOutgoing }
        if (lastMsg != null) {
          fetchQuickReplies(lastMsg.text)
        }
      }
    }
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilter(filter: String) {
    _selectedFilter.value = filter
  }

  fun toggleGhostOverlay() {
    _isGhostOverlayVisible.value = !_isGhostOverlayVisible.value
  }

  fun closeGhostOverlay() {
    _isGhostOverlayVisible.value = false
  }

  fun updateGhostSettings(newSettings: GhostSettings) {
    repository.updateGhostSettings(newSettings)
  }

  fun toggleGhostMaster() {
    repository.toggleGhostMaster()
  }

  fun sendMessage(text: String) {
    if (text.isBlank()) return
    val chatId = _activeChatId.value ?: return
    val currentChat = allChats.value.find { it.id == chatId }
    val recipientId = currentChat?.recipientUser?.id ?: "usr_peer_default"
    val reply = _replyingTo.value

    viewModelScope.launch {
      repository.sendMessage(
        chatId = chatId,
        recipientId = recipientId,
        text = text,
        replyToId = reply?.id,
        replySnippet = reply?.text?.take(40)
      )
      _replyingTo.value = null
    }
  }

  fun startVoiceRecording() {
    _isRecordingVoice.value = true
    _recordingDuration.value = 0
    voiceTimerJob?.cancel()
    voiceTimerJob = viewModelScope.launch {
      while (_isRecordingVoice.value) {
        delay(1000)
        _recordingDuration.value += 1
      }
    }
  }

  fun finishVoiceRecordingAndSend() {
    val duration = _recordingDuration.value.coerceAtLeast(2)
    _isRecordingVoice.value = false
    voiceTimerJob?.cancel()

    val chatId = _activeChatId.value ?: return
    val currentChat = allChats.value.find { it.id == chatId }
    val recipientId = currentChat?.recipientUser?.id ?: "usr_peer_default"

    // Generate dynamic waveform
    val waveform = List(16) { Random.nextFloat().coerceIn(0.15f, 0.95f) }

    viewModelScope.launch {
      repository.sendVoiceMessage(chatId, recipientId, duration, waveform)
    }
  }

  fun cancelVoiceRecording() {
    _isRecordingVoice.value = false
    voiceTimerJob?.cancel()
    _recordingDuration.value = 0
  }

  fun setReply(message: Message?) {
    _replyingTo.value = message
  }

  fun toggleStar(message: Message) {
    viewModelScope.launch {
      repository.toggleStar(message.id, message.isStarred)
    }
  }

  // Interactive peer simulation
  fun simulatePeerIncomingMessage() {
    val chatId = _activeChatId.value ?: return
    val currentChat = allChats.value.find { it.id == chatId }
    val peerId = currentChat?.recipientUser?.id ?: "usr_eva_002"

    val sampleReplies = listOf(
      "Шифрование подтверждено! Проверь Safety Number через меню чата 🔐",
      "Я отправила тестовое сообщение. Включен ли у тебя режим «Призрака»? 👻",
      "Серверный пул WebSocket в DC2 Amsterdam держит задержку 24ms."
    )
    repository.simulateIncomingPeerMessage(chatId, peerId, sampleReplies.random())
  }

  fun simulatePeerDeletingLastMessage() {
    val lastIncoming = _activeMessages.value.lastOrNull { !it.isOutgoing && !it.isDeletedBySender }
    if (lastIncoming != null) {
      val chatId = _activeChatId.value ?: return
      repository.simulatePeerDeletingLastMessage(lastIncoming.id, chatId)
    }
  }

  // AI actions
  fun requestChatSummary() {
    val chatId = _activeChatId.value ?: return
    val texts = _activeMessages.value.map { "${it.senderName}: ${it.text}" }

    viewModelScope.launch {
      _isAiLoading.value = true
      val summary = repository.getChatSummary(chatId, texts)
      _aiSummary.value = summary
      _isAiLoading.value = false
    }
  }

  fun dismissAiSummary() {
    _aiSummary.value = null
  }

  fun translateMessage(message: Message, targetLang: String = "English") {
    viewModelScope.launch {
      repository.translateMessage(message.id, message.text, targetLang)
    }
  }

  fun transcribeVoice(message: Message) {
    viewModelScope.launch {
      repository.transcribeVoice(message.id, message.voiceDurationSeconds)
    }
  }

  private fun fetchQuickReplies(lastMessageText: String) {
    viewModelScope.launch {
      val replies = repository.getQuickReplies(lastMessageText)
      _smartReplies.value = replies
    }
  }

  // Safety Number (E2EE Fingerprint)
  fun showSafetyNumber(peer: User) {
    val myKey = currentUser.value.publicKeyHex
    val peerKey = peer.publicKeyHex
    val code = E2EECryptoEngine.generateSafetyNumber(myKey, peerKey)
    _safetyNumberData.value = Pair(peer.displayName, code)
  }

  fun closeSafetyNumber() {
    _safetyNumberData.value = null
  }

  // Admin capabilities (exclusively for @zenox)
  fun adminToggleUserVerification(userId: String, isVerified: Boolean) {
    viewModelScope.launch {
      repository.adminToggleUserVerification(userId, isVerified)
    }
  }

  fun adminDeleteMessage(messageId: String) {
    viewModelScope.launch {
      repository.adminDeleteMessagePermanently(messageId)
    }
  }

  // --- AYUGRAM / TELEGRAM EXTENSIONS ---
  val contacts = repository.contacts
  val proxies = repository.proxies
  val musicTrack = repository.musicTrack
  val telegramNotification = repository.telegramOtpNotification

  fun requestTelegramCode(phone: String): String {
    return repository.requestTelegramAuthCode(phone)
  }

  fun verifyTelegramCode(code: String, newPhone: String): Boolean {
    return repository.verifyTelegramAuthCode(code, newPhone)
  }

  fun verifyTelegramCodeByPhone(phone: String, code: String): Boolean {
    return repository.verifyTelegramAuthCode(code, phone)
  }

  fun dismissTelegramNotification() {
    repository.dismissTelegramNotification()
  }

  fun clearTelegramNotification() {
    repository.dismissTelegramNotification()
  }

  fun regenerateSessionKeysViaGo(): E2EECryptoEngine.GoRekeyResult {
    return repository.regenerateSessionKeysViaGo()
  }

  fun toggleMusic() {
    repository.toggleMusicPlayback()
  }

  fun toggleProxy(proxyId: String) {
    repository.toggleProxy(proxyId)
  }
}
