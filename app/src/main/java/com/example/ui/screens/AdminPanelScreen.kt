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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@Composable
fun AdminPanelScreen(
  viewModel: ChatViewModel,
  onBackClick: () -> Unit
) {
  val allChats by viewModel.allChats.collectAsState()
  var targetMessageIdToDelete by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    containerColor = CyberDarkBackground,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSurface)
          .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBackClick) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = CyberTextPrimary)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Панель администратора", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Эксклюзивный доступ для @zenox", color = CyberCyan, fontSize = 11.sp)
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      if (statusMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StealthGhostEmerald.copy(alpha = 0.2f))
            .border(1.dp, StealthGhostEmerald, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Text(text = statusMessage ?: "", color = StealthGhostEmerald, fontSize = 12.5.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Live Server Telemetry Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Speed, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Телеметрия Go WebSocket сервера", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text("DC2 Amsterdam", color = StealthGhostEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TelemetryTile(title = "WS Клиенты", value = "1,849", subtitle = "Активные сокеты")
            TelemetryTile(title = "Goroutines", value = "142", subtitle = "Конкурентные пулы")
            TelemetryTile(title = "Пинг", value = "24 ms", subtitle = "Latency к DC2")
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TelemetryTile(title = "Ghost Traps", value = "42,910", subtitle = "Anti-Delete событий")
            TelemetryTile(title = "Redis RAM", value = "48.2 MB", subtitle = "Кэш присутствия")
            TelemetryTile(title = "E2EE Coverage", value = "100%", subtitle = "Zero-plaintext")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Verification Badge Management (Grant/Revoke Blue Verified Checkmark)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedBadgeBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Выдача верификации пользователям", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Text("Вы можете присваивать или отзывать синюю галочку верификации в реальном времени:", color = CyberTextSecondary, fontSize = 12.sp)

          Spacer(modifier = Modifier.height(12.dp))

          allChats.forEach { chat ->
            val user = chat.recipientUser
            if (user != null) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.displayName, color = CyberTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                    if (user.isVerified) {
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedBadgeBlue, modifier = Modifier.size(14.dp))
                    }
                  }
                  Text(user.username, color = CyberTextMuted, fontSize = 11.5.sp)
                }

                Switch(
                  checked = user.isVerified,
                  onCheckedChange = { isChecked ->
                    viewModel.adminToggleUserVerification(user.id, isChecked)
                    statusMessage = "Статус верификации для ${user.username} обновлен: ${if (isChecked) "Верифицирован" else "Обычный"}"
                  },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = VerifiedBadgeBlue,
                    checkedTrackColor = VerifiedBadgeBlue.copy(alpha = 0.3f)
                  )
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Global Message Moderation (Hard Delete Any Message in Any Chat)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GhostDangerRed.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = GhostDangerRed, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Глобальная модерация сообщений", color = GhostDangerRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Text(
            text = "Удаление любого сообщения из базы данных и синхронизация принудительного удаления со всеми пирами:",
            color = CyberTextSecondary,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = targetMessageIdToDelete,
            onValueChange = { targetMessageIdToDelete = it },
            placeholder = { Text("Введите ID сообщения (например, msg_001)", color = CyberTextMuted, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GhostDangerRed,
              unfocusedBorderColor = CyberBorder,
              focusedContainerColor = CyberDarkCard,
              unfocusedContainerColor = CyberDarkCard,
              focusedTextColor = CyberTextPrimary,
              unfocusedTextColor = CyberTextPrimary
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              if (targetMessageIdToDelete.isNotBlank()) {
                viewModel.adminDeleteMessage(targetMessageIdToDelete.trim())
                statusMessage = "Сообщение ${targetMessageIdToDelete.trim()} безвозвратно удалено админом."
                targetMessageIdToDelete = ""
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GhostDangerRed)
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Принудительно удалить сообщение", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun TelemetryTile(title: String, value: String, subtitle: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(CyberDarkCard)
      .padding(10.dp)
  ) {
    Column {
      Text(text = title, fontSize = 11.sp, color = CyberTextMuted)
      Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
      Text(text = subtitle, fontSize = 9.5.sp, color = CyberTextSecondary)
    }
  }
}
