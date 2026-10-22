package com.example.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuBubbleOutgoing
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedMessagesScreen(
  viewModel: ChatViewModel,
  onBackClick: () -> Unit
) {
  val starredMessages by viewModel.starredMessages.collectAsState()
  var noteInput by remember { mutableStateOf("") }
  val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

  Scaffold(
    containerColor = AyuDarkBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(AyuDarkSurface)
          .padding(horizontal = 8.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = AyuTextPrimary)
            }

            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AyuGreenPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text("Избранное", color = AyuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.5.sp)
              Text("Личное зашифрованное хранилище", color = AyuTextSecondary, fontSize = 11.sp)
            }
          }

          IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = AyuTextPrimary)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter pills matching Screenshot 6
        Row(modifier = Modifier.padding(start = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(AyuDarkCard)
              .border(1.dp, AyuGreenPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Text("Файлы", color = AyuGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(AyuDarkCard)
              .border(1.dp, AyuBorder, RoundedCornerShape(16.dp))
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Text("Все сообщения", color = AyuTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .imePadding()
    ) {
      Box(modifier = Modifier.weight(1f)) {
        if (starredMessages.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(AyuDarkCard),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = AyuGreenPrimary, modifier = Modifier.size(28.dp))
              }
              Spacer(modifier = Modifier.height(12.dp))
              Text("Ваше облачное хранилище", color = AyuTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "Пересылайте сюда сообщения из других чатов или сохраняйте важные ссылки, заметки и пароли. Всё защищено E2EE шифрованием.",
                color = AyuTextMuted,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(starredMessages, key = { it.id }) { msg ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AyuBubbleOutgoing)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(msg.senderName, color = AyuGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(timeFormat.format(Date(msg.timestamp)), color = AyuTextMuted, fontSize = 10.sp)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(msg.text, color = AyuTextPrimary, fontSize = 13.5.sp)
                }
              }
            }
          }
        }
      }

      // Input bar for self-notes
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AyuDarkSurface)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = noteInput,
          onValueChange = { noteInput = it },
          placeholder = { Text("Сохранить заметку в Избранное...", color = AyuTextMuted, fontSize = 13.sp) },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(20.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AyuGreenPrimary,
            unfocusedBorderColor = AyuBorder,
            focusedContainerColor = AyuDarkCard,
            unfocusedContainerColor = AyuDarkCard,
            focusedTextColor = AyuTextPrimary,
            unfocusedTextColor = AyuTextPrimary
          )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(AyuGreenPrimary)
            .clickable {
              if (noteInput.isNotBlank()) {
                viewModel.sendMessage(noteInput)
                noteInput = ""
              }
            },
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Сохранить", tint = Color.Black, modifier = Modifier.size(20.dp))
        }
      }
    }
  }
}
