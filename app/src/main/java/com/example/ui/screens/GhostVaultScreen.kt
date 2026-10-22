package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.GhostDangerRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StealthGhostEmerald
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GhostVaultScreen(viewModel: ChatViewModel) {
  val ghostMessages by viewModel.ghostVaultMessages.collectAsState()
  val timeFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

  Scaffold(
    containerColor = CyberDarkBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSurface)
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(listOf(GhostDangerRed.copy(alpha = 0.4f), Color.Transparent))
                )
                .border(1.dp, GhostDangerRed, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("👻", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "Ghost Vault",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
              )
              Text(
                text = "Локальный Anti-Delete архив сообщений",
                fontSize = 11.5.sp,
                color = GhostDangerRed
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(GhostDangerRed.copy(alpha = 0.15f))
              .border(0.8.dp, GhostDangerRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${ghostMessages.size} перехвачено",
              color = GhostDangerRed,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (ghostMessages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
          ) {
            Text("🛡️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "В Vault пока пусто",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Когда собеседник попытается удалить сообщение в чате, Mail Chat перехватит его и сохранит здесь навсегда.",
              fontSize = 12.5.sp,
              color = CyberTextMuted,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              lineHeight = 17.sp
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = CyberDarkCard),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = StealthGhostEmerald, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Сообщения хранятся локально в защищенном SQLite Room на вашем устройстве. Удаление с серверов Mail Chat не влияет на эти записи.",
                  fontSize = 11.5.sp,
                  color = CyberTextSecondary,
                  lineHeight = 15.sp
                )
              }
            }
          }

          items(ghostMessages, key = { it.id }) { msg ->
            GhostMessageCard(
              message = msg,
              formattedDate = timeFormat.format(Date(msg.timestamp))
            )
          }
        }
      }
    }
  }
}

@Composable
private fun GhostMessageCard(message: Message, formattedDate: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, GhostDangerRed.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(GhostDangerRed)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = message.senderName,
            color = CyberCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp
          )
        }

        Text(
          text = formattedDate,
          color = CyberTextMuted,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(GhostDangerRed.copy(alpha = 0.1f))
          .padding(8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("👻", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Отправитель удалил это сообщение",
            color = GhostDangerRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = message.text,
        color = CyberTextPrimary,
        fontSize = 13.5.sp,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ID: ${message.id.take(16)}...",
          color = CyberTextMuted,
          fontSize = 10.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = StealthGhostEmerald, modifier = Modifier.size(11.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "AES-256-GCM сохранен",
            color = StealthGhostEmerald,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
