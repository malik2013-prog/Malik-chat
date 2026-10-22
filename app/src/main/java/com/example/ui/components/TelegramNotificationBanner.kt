package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AyuDarkCardElevated
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary

@Composable
fun TelegramNotificationBanner(
  notificationText: String?,
  onDismiss: () -> Unit,
  onClick: () -> Unit
) {
  AnimatedVisibility(
    visible = notificationText != null,
    enter = slideInVertically(initialOffsetY = { -it }),
    exit = slideOutVertically(targetOffsetY = { -it })
  ) {
    if (notificationText != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(AyuDarkCardElevated)
          .border(1.dp, Color(0xFF229ED9).copy(alpha = 0.6f), RoundedCornerShape(18.dp))
          .clickable { onClick() }
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF229ED9)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Telegram",
                  color = Color(0xFF229ED9),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "сейчас",
                  color = AyuTextMuted,
                  fontSize = 10.5.sp
                )
              }
              Text(
                text = notificationText,
                color = AyuTextPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Скрыть",
              tint = AyuTextMuted,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
