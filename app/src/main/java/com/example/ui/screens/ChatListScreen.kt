package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chat
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenBadge
import com.example.ui.theme.AyuGreenLight
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(
  viewModel: ChatViewModel,
  onChatClick: (String) -> Unit,
  onOpenGhostOverlay: () -> Unit,
  onOpenSavedMessages: () -> Unit,
  onOpenProxyScreen: () -> Unit
) {
  val chats by viewModel.filteredChats.collectAsState()
  val ghostSettings by viewModel.ghostSettings.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val timeFormat = remember { SimpleDateFormat("dd MMM", Locale("ru")) }

  var isMenuOpen by remember { mutableStateOf(false) }
  var isSearchExpanded by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = AyuDarkBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(AyuDarkSurface)
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Top title: user name + star (matching Screenshot 5: "ivk1488 ⭐")
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = user.displayName,
              color = AyuTextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Star",
              tint = AyuGreenLight,
              modifier = Modifier.size(18.dp)
            )
          }

          // Top right actions: Search and 3-dots Menu
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
              Icon(
                imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                contentDescription = "Поиск",
                tint = AyuTextPrimary
              )
            }

            Box {
              IconButton(onClick = { isMenuOpen = true }) {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "Меню",
                  tint = AyuTextPrimary
                )
              }

              // Authentic AyuGram Dropdown Menu from Screenshot 5!
              DropdownMenu(
                expanded = isMenuOpen,
                onDismissRequest = { isMenuOpen = false },
                modifier = Modifier
                  .width(220.dp)
                  .background(Color(0xFF232326))
                  .border(0.5.dp, AyuBorder, RoundedCornerShape(12.dp))
              ) {
                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.WbSunny, contentDescription = null, tint = AyuTextPrimary, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(14.dp))
                      Text("Дневной режим", color = AyuTextPrimary, fontSize = 14.sp)
                    }
                  },
                  onClick = { isMenuOpen = false }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text("👻", fontSize = 18.sp)
                      Spacer(modifier = Modifier.width(14.dp))
                      Text(
                        text = if (ghostSettings.isGhostActive) "Отключить призрака" else "Включить призрака",
                        color = AyuTextPrimary,
                        fontSize = 14.sp
                      )
                    }
                  },
                  onClick = {
                    isMenuOpen = false
                    viewModel.toggleGhostMaster()
                  }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Archive, contentDescription = null, tint = AyuTextPrimary, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(14.dp))
                      Text("Архив чатов", color = AyuTextPrimary, fontSize = 14.sp)
                    }
                  },
                  onClick = { isMenuOpen = false }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.GroupAdd, contentDescription = null, tint = AyuTextPrimary, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(14.dp))
                      Text("Создать группу", color = AyuTextPrimary, fontSize = 14.sp)
                    }
                  },
                  onClick = { isMenuOpen = false }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Bookmark, contentDescription = null, tint = AyuTextPrimary, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(14.dp))
                      Text("Избранное", color = AyuTextPrimary, fontSize = 14.sp)
                    }
                  },
                  onClick = {
                    isMenuOpen = false
                    onOpenSavedMessages()
                  }
                )

                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Shield, contentDescription = null, tint = AyuTextPrimary, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(14.dp))
                      Text("Прокси", color = AyuTextPrimary, fontSize = 14.sp)
                    }
                  },
                  onClick = {
                    isMenuOpen = false
                    onOpenProxyScreen()
                  }
                )
              }
            }
          }
        }

        // Expandable Search
        AnimatedVisibility(visible = isSearchExpanded) {
          Column {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { viewModel.updateSearchQuery(it) },
              modifier = Modifier.fillMaxWidth(),
              placeholder = { Text("Поиск чатов и сообщений", color = AyuTextMuted, fontSize = 13.5.sp) },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AyuGreenPrimary,
                unfocusedBorderColor = AyuBorder,
                focusedContainerColor = AyuDarkCard,
                unfocusedContainerColor = AyuDarkCard,
                focusedTextColor = AyuTextPrimary,
                unfocusedTextColor = AyuTextPrimary
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      items(chats, key = { it.id }) { chat ->
        AyuChatItemRow(
          chat = chat,
          formattedTime = timeFormat.format(Date(chat.lastMessageTimestamp)),
          onClick = { onChatClick(chat.id) }
        )
      }
    }
  }
}

@Composable
private fun AyuChatItemRow(
  chat: Chat,
  formattedTime: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Circle avatar matching Screenshot 5
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .background(
          when (chat.id) {
            "chat_lumina" -> Brush.radialGradient(listOf(Color(0xFF381E72), Color(0xFF1D0C3A)))
            "chat_strike" -> Brush.radialGradient(listOf(Color(0xFF1B4D24), Color(0xFF0F2B14)))
            "chat_pixel" -> Brush.radialGradient(listOf(Color(0xFF2E6930), Color(0xFF123814)))
            "chat_user_info" -> Brush.radialGradient(listOf(Color(0xFF1D5A6C), Color(0xFF0C2C35)))
            else -> Brush.radialGradient(listOf(Color(0xFF3A3A3C), Color(0xFF242426)))
          }
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = chat.title.take(1).uppercase(),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = chat.title,
            color = AyuTextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (chat.id != "chat_lumina") {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = AyuGreenLight,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Text(
            text = formattedTime,
            color = AyuTextMuted,
            fontSize = 11.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(3.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (chat.id == "chat_lumina") {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color(0xFFFFD60A),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Text(
            text = chat.lastMessageText,
            color = if (chat.id == "chat_lumina") Color(0xFFFFD60A) else AyuTextSecondary,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (chat.unreadCount > 0) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(AyuGreenBadge)
              .padding(horizontal = 7.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = chat.unreadCount.toString(),
              color = Color.Black,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}
