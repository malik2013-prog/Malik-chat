package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GhostSettings
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StealthGhostEmerald

@Composable
fun AyuGramGhostOverlay(
  isVisible: Boolean,
  settings: GhostSettings,
  onDismiss: () -> Unit,
  onSettingsChange: (GhostSettings) -> Unit
) {
  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)),
    exit = fadeOut(spring(stiffness = Spring.StiffnessMediumLow))
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.72f))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onDismiss
        ),
      contentAlignment = Alignment.Center
    ) {
      AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(initialScale = 0.85f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
        exit = scaleOut(targetScale = 0.85f) + fadeOut()
      ) {
        Card(
          modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(28.dp))
            .border(
              width = 1.5.dp,
              brush = Brush.linearGradient(
                listOf(StealthGhostEmerald.copy(alpha = 0.8f), NeonViolet.copy(alpha = 0.6f), CyberCyan.copy(alpha = 0.3f))
              ),
              shape = RoundedCornerShape(28.dp)
            )
            .clickable(enabled = false) {},
          colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
          shape = RoundedCornerShape(28.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp)
          ) {
            // Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                      Brush.radialGradient(
                        listOf(StealthGhostEmerald.copy(alpha = 0.3f), Color.Transparent)
                      )
                    )
                    .border(1.dp, StealthGhostEmerald, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text("👻", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "AyuGram Ghost Mode",
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                  )
                  Text(
                    text = if (settings.isGhostActive) "Режим скрытности АКТИВЕН" else "Режим скрытности ОТКЛЮЧЕН",
                    color = if (settings.isGhostActive) StealthGhostEmerald else CyberTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              IconButton(onClick = onDismiss) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Закрыть",
                  tint = CyberTextSecondary
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Master Switch Card
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (settings.isGhostActive) StealthGhostEmerald.copy(alpha = 0.12f) else CyberDarkCard
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (settings.isGhostActive) StealthGhostEmerald.copy(alpha = 0.4f) else CyberBorder
              )
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Главный переключатель Ghost",
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                  )
                  Text(
                    text = "Скрыть ваше присутствие и активность во всех чатах",
                    color = CyberTextSecondary,
                    fontSize = 12.sp
                  )
                }
                Switch(
                  checked = settings.isGhostActive,
                  onCheckedChange = { onSettingsChange(settings.copy(isGhostActive = it)) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = StealthGhostEmerald,
                    checkedTrackColor = StealthGhostEmerald.copy(alpha = 0.3f)
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Granular Controls
            GhostToggleItem(
              icon = Icons.Default.VisibilityOff,
              title = "Скрывать статус «Прочитано»",
              subtitle = "Собеседник никогда не увидит двойную синюю галочку",
              checked = settings.hideReadReceipts,
              enabled = settings.isGhostActive,
              accentColor = CyberCyan,
              onToggle = { onSettingsChange(settings.copy(hideReadReceipts = it)) }
            )

            GhostToggleItem(
              icon = Icons.Default.RemoveRedEye,
              title = "Скрывать «В сети» (Last Seen)",
              subtitle = "Сервер всегда транслирует статус «Был(а) недавно»",
              checked = settings.hideOnlineStatus,
              enabled = settings.isGhostActive,
              accentColor = NeonViolet,
              onToggle = { onSettingsChange(settings.copy(hideOnlineStatus = it)) }
            )

            GhostToggleItem(
              icon = Icons.Default.Edit,
              title = "Скрывать «Печатает...»",
              subtitle = "Блокирует отправку typing-событий в WebSocket",
              checked = settings.typingDelayEnabled,
              enabled = settings.isGhostActive,
              accentColor = StealthGhostEmerald,
              onToggle = { onSettingsChange(settings.copy(typingDelayEnabled = it)) }
            )

            GhostToggleItem(
              icon = Icons.Default.Security,
              title = "Ghost Vault: Anti-Delete архив",
              subtitle = "Сохранять удаленные собеседником сообщения навсегда",
              checked = settings.antiDeleteVaultEnabled,
              enabled = settings.isGhostActive,
              accentColor = CyberCyan,
              onToggle = { onSettingsChange(settings.copy(antiDeleteVaultEnabled = it)) }
            )

            GhostToggleItem(
              icon = Icons.Default.Lock,
              title = "E2EE сквозное шифрование (X25519)",
              subtitle = "Дешифрование локально, нулевое знание сервера",
              checked = settings.autoDecryptOnArrival,
              enabled = true,
              accentColor = NeonViolet,
              onToggle = { onSettingsChange(settings.copy(autoDecryptOnArrival = it)) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun GhostToggleItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  enabled: Boolean,
  accentColor: Color,
  onToggle: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
      .clickable(enabled = enabled) { onToggle(!checked) },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(if (checked && enabled) accentColor.copy(alpha = 0.15f) else CyberDarkCard)
        .border(1.dp, if (checked && enabled) accentColor.copy(alpha = 0.4f) else CyberBorder, RoundedCornerShape(10.dp)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (checked && enabled) accentColor else CyberTextMuted,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = if (enabled) CyberTextPrimary else CyberTextMuted,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = subtitle,
        color = CyberTextSecondary,
        fontSize = 11.5.sp,
        lineHeight = 14.sp
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    Switch(
      checked = checked && enabled,
      enabled = enabled,
      onCheckedChange = onToggle,
      colors = SwitchDefaults.colors(
        checkedThumbColor = accentColor,
        checkedTrackColor = accentColor.copy(alpha = 0.3f),
        uncheckedThumbColor = CyberTextMuted,
        uncheckedTrackColor = CyberDarkCard
      )
    )
  }
}
