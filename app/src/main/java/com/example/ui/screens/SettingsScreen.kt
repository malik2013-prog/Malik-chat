package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.theme.IconBattery
import com.example.ui.theme.IconBlue
import com.example.ui.theme.IconDataBlue
import com.example.ui.theme.IconFolderBlue
import com.example.ui.theme.IconGift
import com.example.ui.theme.IconGreen
import com.example.ui.theme.IconOrange
import com.example.ui.theme.IconPremium
import com.example.ui.theme.IconPurple
import com.example.ui.theme.IconRed
import com.example.ui.theme.IconStarGold
import com.example.ui.theme.IconTeal
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun SettingsScreen(
  viewModel: ChatViewModel,
  onOpenTelegramAuth: () -> Unit,
  onOpenGhostOverlay: () -> Unit
) {
  val user by viewModel.currentUser.collectAsState()
  var rekeyToast by remember { mutableStateOf<String?>(null) }

  Scaffold(
    containerColor = AyuDarkBackground,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AyuDarkSurface)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Настройки",
          color = AyuTextPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = {}) {
            Icon(Icons.Default.Search, contentDescription = "Поиск", tint = AyuTextPrimary)
          }
          IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = "Меню", tint = AyuTextPrimary)
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
        .padding(14.dp)
    ) {
      if (rekeyToast != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AyuGreenPrimary.copy(alpha = 0.2f))
            .padding(12.dp)
        ) {
          Text(text = rekeyToast ?: "", color = AyuGreenPrimary, fontSize = 12.5.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
      }

      // Group 1: General Settings (Matching Screenshot 2)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          SettingsItemRow(
            icon = Icons.Default.Person,
            iconBg = IconBlue,
            title = "Аккаунт",
            valueText = user.phoneMasked,
            onClick = onOpenTelegramAuth
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.AutoMirrored.Filled.Chat,
            iconBg = IconOrange,
            title = "Настройки чатов",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Lock,
            iconBg = IconGreen,
            title = "Конфиденциальность",
            subtitle = "Режим «Призрака» и E2EE ключи",
            onClick = onOpenGhostOverlay
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            iconBg = IconRed,
            title = "Уведомления",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.PieChart,
            iconBg = IconDataBlue,
            title = "Данные и память",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Folder,
            iconBg = IconFolderBlue,
            title = "Папки с чатами",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Computer,
            iconBg = IconTeal,
            title = "Устройства",
            valueText = "1",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.BatteryFull,
            iconBg = IconBattery,
            title = "Энергосбережение",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Language,
            iconBg = IconPurple,
            title = "Язык",
            valueText = "Русский",
            valueColor = AyuGreenPrimary,
            onClick = {}
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Special Go Key Regeneration Button ("Регенерация через Go")
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              val result = viewModel.regenerateSessionKeysViaGo()
              rekeyToast = "Ключи E2EE успешно регенерированы через Go! Сессия: ${result.newSessionId}"
            }
            .padding(horizontal = 14.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(AyuGreenPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Autorenew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Регенерация ключей через Go (Ratchet)", color = AyuGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Генерирует свежий X25519 ключ и обновляет сессию Go WebSocket", color = AyuTextSecondary, fontSize = 11.5.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Group 2: Premium & Stars (Matching Screenshot 2)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          SettingsItemRow(
            icon = Icons.Default.Star,
            iconBg = IconPremium,
            title = "Mail Chat Premium",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Star,
            iconBg = IconStarGold,
            title = "Звёзды Mail Chat",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Store,
            iconBg = IconRed,
            title = "Mail Chat для бизнеса",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.CardGiftcard,
            iconBg = IconGift,
            title = "Отправить подарок",
            onClick = {}
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Group 3: Help & About (Matching Screenshot 3)
      Text(
        text = "Помощь",
        color = AyuGreenPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 6.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          SettingsItemRow(
            icon = Icons.Default.QuestionAnswer,
            iconBg = IconOrange,
            title = "Задать вопрос",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.AutoMirrored.Filled.Help,
            iconBg = IconBlue,
            title = "Вопросы о Mail Chat",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.Lightbulb,
            iconBg = IconPurple,
            title = "Возможности Mail Chat",
            onClick = {}
          )

          DividerRow()

          SettingsItemRow(
            icon = Icons.Default.CheckCircle,
            iconBg = IconGreen,
            title = "Политика конфиденциальности",
            onClick = {}
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Footer from Screenshot 3
      Text(
        text = "AyuGram 20260329 (Mail Chat Core Edition)",
        color = AyuTextMuted,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun SettingsItemRow(
  icon: ImageVector,
  iconBg: Color,
  title: String,
  subtitle: String? = null,
  valueText: String? = null,
  valueColor: Color = AyuTextSecondary,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 11.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(30.dp)
        .clip(CircleShape)
        .background(iconBg),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, color = AyuTextPrimary, fontSize = 14.5.sp, fontWeight = FontWeight.Normal)
      if (subtitle != null) {
        Text(text = subtitle, color = AyuTextSecondary, fontSize = 11.5.sp)
      }
    }

    if (valueText != null) {
      Text(text = valueText, color = valueColor, fontSize = 13.5.sp)
    }
  }
}

@Composable
private fun DividerRow() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 58.dp)
      .height(0.5.dp)
      .background(AyuBorder)
  )
}
