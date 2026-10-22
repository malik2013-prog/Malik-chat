package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chat
import com.example.model.DeliveryStatus
import com.example.model.Message
import com.example.model.MessageType
import com.example.ui.components.AppleEmojiPicker
import com.example.ui.components.FloatingReactionBurst
import com.example.ui.components.VoiceMessageBubble
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.BubbleGhostBorder
import com.example.ui.theme.BubbleGhostDeleted
import com.example.ui.theme.BubbleIncoming
import com.example.ui.theme.BubbleIncomingBorder
import com.example.ui.theme.BubbleOutgoing
import com.example.ui.theme.BubbleOutgoingBorder
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.GhostDangerRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StealthGhostEmerald
import com.example.ui.theme.VerifiedBadgeBlue
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationScreen(
  viewModel: ChatViewModel,
  chatId: String,
  onBackClick: () -> Unit,
  onOpenGhostOverlay: () -> Unit
) {
  val allChats by viewModel.allChats.collectAsState()
  val messages by viewModel.activeMessages.collectAsState()
  val ghostSettings by viewModel.ghostSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val smartReplies by viewModel.smartReplies.collectAsState()
  val aiSummary by viewModel.aiSummary.collectAsState()
  val isAiLoading by viewModel.isAiLoading.collectAsState()
  val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
  val recordingDuration by viewModel.recordingDuration.collectAsState()
  val replyingTo by viewModel.replyingTo.collectAsState()

  val currentChat = allChats.find { it.id == chatId }
  val peer = currentChat?.recipientUser

  var inputText by remember { mutableStateOf("") }
  var isMenuExpanded by remember { mutableStateOf(false) }
  var isEmojiPickerOpen by remember { mutableStateOf(false) }
  var activeReactionEmoji by remember { mutableStateOf<String?>(null) }
  val listState = rememberLazyListState()
  val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
  val clipboardManager = LocalClipboardManager.current

  // Auto-scroll on new message
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    containerColor = CyberDarkBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSurface)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            IconButton(onClick = onBackClick) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = CyberTextPrimary
              )
            }

            // Peer avatar
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    if (peer?.isVerified == true) listOf(CyberCyan, VerifiedBadgeBlue) else listOf(NeonViolet, Color(0xFF4C1D95))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = currentChat?.title?.take(1)?.uppercase() ?: "M",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentChat?.title ?: "Диалог",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.5.sp,
                  color = CyberTextPrimary
                )
                if (peer?.isVerified == true) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified",
                    tint = VerifiedBadgeBlue,
                    modifier = Modifier.size(15.dp)
                  )
                }
              }

              Text(
                text = if (ghostSettings.isGhostActive && ghostSettings.hideOnlineStatus) {
                  "был(а) недавно (Ghost)"
                } else if (peer?.isOnline == true) {
                  "в сети • E2EE"
                } else {
                  "был(а) недавно"
                },
                fontSize = 11.sp,
                color = if (ghostSettings.isGhostActive) StealthGhostEmerald else CyberCyan
              )
            }
          }

          // Top Action Buttons
          Row(verticalAlignment = Alignment.CenterVertically) {
            // AyuGram Ghost overlay trigger
            IconButton(onClick = onOpenGhostOverlay) {
              Text("👻", fontSize = 18.sp)
            }

            // AI Summary button
            IconButton(
              onClick = { viewModel.requestChatSummary() },
              enabled = !isAiLoading
            ) {
              if (isAiLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NeonViolet, strokeWidth = 2.dp)
              } else {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = "Сводка чата",
                  tint = NeonViolet
                )
              }
            }

            // More Options (Safety Number & Simulation)
            Box {
              IconButton(onClick = { isMenuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Меню", tint = CyberTextPrimary)
              }

              DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.background(CyberDarkSurfaceVariant)
              ) {
                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Код безопасности E2EE", color = CyberTextPrimary, fontSize = 13.sp)
                    }
                  },
                  onClick = {
                    isMenuExpanded = false
                    if (peer != null) viewModel.showSafetyNumber(peer)
                  }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Science, contentDescription = null, tint = StealthGhostEmerald, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Симуляция: Сообщение от собеседника", color = CyberTextPrimary, fontSize = 13.sp)
                    }
                  },
                  onClick = {
                    isMenuExpanded = false
                    viewModel.simulatePeerIncomingMessage()
                  }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Delete, contentDescription = null, tint = GhostDangerRed, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Симуляция: Собеседник удалил сообщение (Ghost Vault)", color = GhostDangerRed, fontSize = 13.sp)
                    }
                  },
                  onClick = {
                    isMenuExpanded = false
                    viewModel.simulatePeerDeletingLastMessage()
                  }
                )
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .imePadding()
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
      // AI Summary Notification Card
      AnimatedVisibility(
        visible = aiSummary != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
      ) {
        if (aiSummary != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(NeonViolet.copy(alpha = 0.12f))
              .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("AI Сводка диалога", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonViolet)
                }
                IconButton(
                  onClick = { viewModel.dismissAiSummary() },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = CyberTextMuted, modifier = Modifier.size(16.dp))
                }
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = aiSummary ?: "",
                color = CyberTextPrimary,
                fontSize = 12.5.sp,
                lineHeight = 17.sp
              )
            }
          }
        }
      }

      // Messages Feed
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // E2EE Banner
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CyberDarkCard)
                .border(0.5.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Сквозное E2EE шифрование (ECDH X25519 • AES-256-GCM)",
                fontSize = 10.5.sp,
                color = CyberTextSecondary
              )
            }
          }
        }

        items(messages, key = { it.id }) { message ->
          MessageBubbleRow(
            message = message,
            isCurrentUserAdmin = currentUser.isAdmin,
            formattedTime = timeFormat.format(Date(message.timestamp)),
            onReply = { viewModel.setReply(message) },
            onStar = { viewModel.toggleStar(message) },
            onTranslate = { viewModel.translateMessage(message) },
            onTranscribeVoice = { viewModel.transcribeVoice(message) },
            onAdminDelete = { viewModel.adminDeleteMessage(message.id) },
            onCopy = { clipboardManager.setText(AnnotatedString(message.text)) },
            onReaction = { emoji ->
              activeReactionEmoji = emoji
              viewModel.sendMessage(emoji)
            }
          )
        }
      }

      // Replying Banner
      if (replyingTo != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(CyberDarkSurfaceVariant)
            .padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Ответ для: ${replyingTo?.senderName}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CyberCyan
            )
            Text(
              text = replyingTo?.text?.take(45) ?: "",
              fontSize = 11.sp,
              color = CyberTextSecondary,
              maxLines = 1
            )
          }
          IconButton(onClick = { viewModel.setReply(null) }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Отмена", tint = CyberTextMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Smart Quick Replies
      if (smartReplies.isNotEmpty() && !isRecordingVoice) {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .background(CyberDarkSurface)
            .padding(horizontal = 10.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(smartReplies) { reply ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(NeonViolet.copy(alpha = 0.15f))
                .border(0.8.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .clickable {
                  viewModel.sendMessage(reply)
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = reply,
                  fontSize = 12.sp,
                  color = CyberTextPrimary,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }

      // Bottom Input Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSurface)
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isRecordingVoice) {
          // Voice Recording in Progress UI
          Row(
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(GhostDangerRed.copy(alpha = 0.15f))
              .border(1.dp, GhostDangerRed.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
              .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(GhostDangerRed)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Запись: %02d:%02d".format(recordingDuration / 60, recordingDuration % 60),
                color = GhostDangerRed,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }

            Text(
              text = "Отпустите для отправки",
              color = CyberTextSecondary,
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Stop / Send voice button
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(GhostDangerRed)
              .clickable { viewModel.finishVoiceRecordingAndSend() },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Stop, contentDescription = "Отправить голос", tint = Color.White)
          }
        } else {
          // Apple Emoji toggle button
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (isEmojiPickerOpen) AyuGreenPrimary.copy(alpha = 0.25f) else CyberDarkCard)
              .clickable { isEmojiPickerOpen = !isEmojiPickerOpen },
            contentAlignment = Alignment.Center
          ) {
            Text(text = "😀", fontSize = 21.sp)
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Standard text message bar
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Зашифрованное сообщение...", color = CyberTextMuted, fontSize = 13.5.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyberCyan,
              unfocusedBorderColor = CyberBorder,
              focusedContainerColor = CyberDarkCard,
              unfocusedContainerColor = CyberDarkCard,
              focusedTextColor = CyberTextPrimary,
              unfocusedTextColor = CyberTextPrimary
            )
          )

          Spacer(modifier = Modifier.width(8.dp))

          if (inputText.isNotBlank()) {
            // Send Text
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(CyberCyan, NeonViolet)))
                .clickable {
                  viewModel.sendMessage(inputText)
                  inputText = ""
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Отправить",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
          } else {
            // Record Voice button
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(NeonViolet)
                .clickable { viewModel.startVoiceRecording() },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Голосовое сообщение",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }

      // Apple iOS Emoji Keyboard Drawer
      AnimatedVisibility(
        visible = isEmojiPickerOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        AppleEmojiPicker(
          onEmojiSelected = { emoji ->
            inputText += emoji
          },
          onBackspace = {
            if (inputText.isNotEmpty()) {
              inputText = inputText.dropLast(1)
            }
          },
          onQuickReaction = { emoji ->
            activeReactionEmoji = emoji
            viewModel.sendMessage(emoji)
          },
          onClose = { isEmojiPickerOpen = false }
        )
      }
    }

    // Floating Reaction Particle Burst Animation
    activeReactionEmoji?.let { emoji ->
      FloatingReactionBurst(
        emoji = emoji,
        onAnimationEnd = { activeReactionEmoji = null }
      )
    }
  }
}
}

@Composable
private fun MessageBubbleRow(
  message: Message,
  isCurrentUserAdmin: Boolean,
  formattedTime: String,
  onReply: () -> Unit,
  onStar: () -> Unit,
  onTranslate: () -> Unit,
  onTranscribeVoice: () -> Unit,
  onAdminDelete: () -> Unit,
  onCopy: () -> Unit,
  onReaction: (String) -> Unit
) {
  var showMenu by remember { mutableStateOf(false) }

  val alignment = if (message.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
  val bubbleBackground = when {
    message.isDeletedBySender -> BubbleGhostDeleted
    message.isOutgoing -> BubbleOutgoing
    else -> BubbleIncoming
  }
  val bubbleBorder = when {
    message.isDeletedBySender -> BubbleGhostBorder
    message.isOutgoing -> BubbleOutgoingBorder
    else -> BubbleIncomingBorder
  }

  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = alignment
  ) {
    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (message.isOutgoing) 18.dp else 4.dp,
            bottomEnd = if (message.isOutgoing) 4.dp else 18.dp
          )
        )
        .background(bubbleBackground)
        .border(
          width = 1.dp,
          color = bubbleBorder,
          shape = RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (message.isOutgoing) 18.dp else 4.dp,
            bottomEnd = if (message.isOutgoing) 4.dp else 18.dp
          )
        )
        .clickable { showMenu = true }
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Column {
        // Sender Name if incoming
        if (!message.isOutgoing) {
          Text(
            text = message.senderName,
            color = CyberCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
        }

        // Anti-Delete Ghost Banner (Key AyuGram / Ghost requirement!)
        if (message.isDeletedBySender) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(GhostDangerRed.copy(alpha = 0.2f))
              .border(0.5.dp, GhostDangerRed, RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("👻", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Удалено собеседником • Сохранено в Ghost Vault",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = GhostDangerRed
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
        }

        // Reply snippet
        if (message.replyToSnippet != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CyberDarkCard.copy(alpha = 0.7f))
              .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "↩ ${message.replyToSnippet}",
              fontSize = 11.sp,
              color = CyberTextSecondary,
              maxLines = 1
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
        }

        // Message Body
        when (message.messageType) {
          MessageType.VOICE -> {
            VoiceMessageBubble(
              message = message,
              isOutgoing = message.isOutgoing,
              onTranscribeClick = onTranscribeVoice
            )
          }
          else -> {
            Text(
              text = message.text,
              color = CyberTextPrimary,
              fontSize = 14.sp,
              lineHeight = 19.sp
            )
          }
        }

        // Translated Text
        if (message.translatedText != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(CyberDarkCard.copy(alpha = 0.8f))
              .padding(6.dp)
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Перевод AI", fontSize = 9.5.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
              }
              Text(
                text = message.translatedText,
                color = CyberTextSecondary,
                fontSize = 12.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Info: Timestamp, Star, Delivery Status
        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (message.isStarred) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Избранное",
              tint = Color(0xFFFBBF24),
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }

          Text(
            text = formattedTime,
            fontSize = 10.sp,
            color = CyberTextMuted
          )

          if (message.isOutgoing) {
            Spacer(modifier = Modifier.width(4.dp))
            val (icon, tint) = when (message.deliveryStatus) {
              DeliveryStatus.READ -> Icons.Default.DoneAll to CyberCyan
              DeliveryStatus.DELIVERED -> Icons.Default.DoneAll to CyberTextMuted
              DeliveryStatus.SENT -> Icons.Default.Check to CyberTextMuted
              DeliveryStatus.SENDING -> Icons.Default.Check to CyberTextMuted.copy(alpha = 0.5f)
            }
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = tint,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }

      // Message Action Context Dropdown
      DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        modifier = Modifier.background(CyberDarkSurfaceVariant)
      ) {
        // Quick Apple Reactions Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("❤️", "🔥", "👍", "😂", "😮", "🚀").forEach { r ->
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(CyberDarkCard)
                .clickable {
                  showMenu = false
                  onReaction(r)
                },
              contentAlignment = Alignment.Center
            ) {
              Text(r, fontSize = 17.sp)
            }
          }
        }

        DropdownMenuItem(
          text = { Text("Ответить", color = CyberTextPrimary, fontSize = 13.sp) },
          onClick = {
            showMenu = false
            onReply()
          }
        )
        DropdownMenuItem(
          text = { Text("Скопировать текст", color = CyberTextPrimary, fontSize = 13.sp) },
          onClick = {
            showMenu = false
            onCopy()
          }
        )
        DropdownMenuItem(
          text = { Text(if (message.isStarred) "Убрать из избранного" else "В избранное ⭐", color = CyberTextPrimary, fontSize = 13.sp) },
          onClick = {
            showMenu = false
            onStar()
          }
        )
        DropdownMenuItem(
          text = { Text("Перевести на английский (AI) 🌐", color = CyberCyan, fontSize = 13.sp) },
          onClick = {
            showMenu = false
            onTranslate()
          }
        )

        // Admin privilege: delete any message globally
        if (isCurrentUserAdmin) {
          DropdownMenuItem(
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = GhostDangerRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Удалить навсегда (Админ)", color = GhostDangerRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            },
            onClick = {
              showMenu = false
              onAdminDelete()
            }
          )
        }
      }
    }
  }
}
