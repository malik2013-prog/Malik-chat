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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.GhostDangerRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StealthGhostEmerald
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun AnalyticsScreen(
  viewModel: ChatViewModel,
  onBackClick: () -> Unit
) {
  val analytics = viewModel.analytics

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
          Icon(Icons.Default.Analytics, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Аналитика и телеметрия активности", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
      // Highlights Row
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricHighlightCard(
          modifier = Modifier.weight(1f),
          title = "Отправлено",
          value = analytics.totalMessagesSent.toString(),
          subtitle = "100% зашифровано",
          color = CyberCyan
        )
        MetricHighlightCard(
          modifier = Modifier.weight(1f),
          title = "Ghost Vault",
          value = analytics.ghostSavedCount.toString(),
          subtitle = "Перехвачено удалений",
          color = GhostDangerRed
        )
        MetricHighlightCard(
          modifier = Modifier.weight(1f),
          title = "E2EE Защита",
          value = "${analytics.encryptionPercentage}%",
          subtitle = "Zero-plaintext",
          color = StealthGhostEmerald
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Weekly Activity Chart Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Активность сообщений по дням недели",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
          )
          Text(
            text = "Анонимизированная статистика локального трафика",
            fontSize = 11.5.sp,
            color = CyberTextSecondary
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Bar Chart Representation
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            val maxCount = (analytics.dailyStats.maxOfOrNull { it.messageCount } ?: 130).toFloat()
            analytics.dailyStats.forEach { metric ->
              val barFraction = metric.messageCount / maxCount
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(36.dp)
              ) {
                Text(
                  text = metric.messageCount.toString(),
                  fontSize = 10.sp,
                  color = CyberCyan,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                  modifier = Modifier
                    .width(18.dp)
                    .height((barFraction * 90).coerceAtLeast(8f).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                      if (metric.messageCount > 100) NeonViolet else CyberCyan
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = metric.day,
                  fontSize = 11.sp,
                  color = CyberTextMuted
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Network & Relay Info
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Параметры маршрутизации и безопасности",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
          )

          Spacer(modifier = Modifier.height(12.dp))

          AnalyticsRow(
            icon = Icons.Default.Speed,
            title = "Пиковые часы активности",
            value = analytics.peakActiveHour,
            accent = CyberCyan
          )

          AnalyticsRow(
            icon = Icons.Default.Security,
            title = "Дата-центр маршрутизации",
            value = analytics.serverRegion,
            accent = StealthGhostEmerald
          )

          AnalyticsRow(
            icon = Icons.Default.Lock,
            title = "Протокол сквозного шифрования",
            value = "X25519 (ECDH) + HKDF + AES-256-GCM",
            accent = NeonViolet
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun MetricHighlightCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  subtitle: String,
  color: Color
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = title, fontSize = 11.sp, color = CyberTextMuted)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, fontSize = 9.sp, color = CyberTextSecondary)
    }
  }
}

@Composable
private fun AnalyticsRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  value: String,
  accent: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(accent.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(text = title, fontSize = 11.sp, color = CyberTextMuted)
      Text(text = value, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
    }
  }
}
