package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Message
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import java.io.File

@Composable
fun CircularVideoBubble(
  message: Message,
  isOutgoing: Boolean
) {
  var isPlaying by remember { mutableStateOf(false) }
  val progress = remember { Animatable(0f) }

  val durationSec = if (message.voiceDurationSeconds > 0) message.voiceDurationSeconds else 8

  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      progress.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = durationSec * 1000, easing = LinearEasing)
      )
      isPlaying = false
      progress.snapTo(0f)
    }
  }

  Column(horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start) {
    Box(
      modifier = Modifier
        .size(200.dp)
        .border(
          width = 3.dp,
          brush = Brush.sweepGradient(listOf(NeonViolet, CyberCyan, NeonPink, NeonViolet)),
          shape = CircleShape
        )
        .padding(3.dp)
        .clip(CircleShape)
        .background(CyberDarkCard)
        .clickable {
          isPlaying = !isPlaying
        },
      contentAlignment = Alignment.Center
    ) {
      if (!message.mediaUri.isNullOrBlank() && File(message.mediaUri).exists()) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(File(message.mediaUri))
            .crossfade(true)
            .build(),
          contentDescription = "Круглое видео",
          contentScale = ContentScale.Crop,
          modifier = Modifier.size(200.dp)
        )
      } else {
        // Fallback video placeholder visual
        Box(
          modifier = Modifier
            .size(200.dp)
            .background(
              Brush.radialGradient(
                listOf(NeonViolet.copy(alpha = 0.5f), CyberDarkCard)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = null,
            tint = CyberCyan.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
          )
        }
      }

      // Progress Ring around circle when playing
      if (isPlaying) {
        CircularProgressIndicator(
          progress = { progress.value },
          modifier = Modifier.size(194.dp),
          color = CyberCyan,
          strokeWidth = 4.dp,
          trackColor = Color.Transparent,
        )
      }

      // Play / Pause Icon Button in center
      Box(
        modifier = Modifier
          .size(46.dp)
          .background(Color.Black.copy(alpha = 0.65f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }

      // Duration Tag
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 12.dp)
          .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Text(
          text = "%02d:%02d".format(durationSec / 60, durationSec % 60),
          fontSize = 11.sp,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
