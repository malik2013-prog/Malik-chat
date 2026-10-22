package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonViolet
import kotlinx.coroutines.launch

@Composable
fun VoiceMessageBubble(
  message: Message,
  isOutgoing: Boolean,
  onTranscribeClick: () -> Unit
) {
  var isPlaying by remember { mutableStateOf(false) }
  var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
  val progress = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()

  // Animate playback progress
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      val durationMs = ((message.voiceDurationSeconds * 1000) / playbackSpeed).toInt().coerceAtLeast(1000)
      val remainingFraction = 1f - progress.value
      val animTime = (durationMs * remainingFraction).toInt()
      progress.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = animTime, easing = LinearEasing)
      )
      isPlaying = false
      progress.snapTo(0f)
    }
  }

  val bars = if (message.voiceWaveform.isNotEmpty()) {
    message.voiceWaveform
  } else {
    listOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 0.9f, 0.7f, 0.5f, 0.8f, 0.3f, 0.6f, 0.9f, 0.4f)
  }

  Column(modifier = Modifier.width(260.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Play/Pause circular button
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isOutgoing) CyberCyan else NeonViolet)
          .clickable {
            if (isPlaying) {
              isPlaying = false
            } else {
              if (progress.value >= 1f) {
                scope.launch { progress.snapTo(0f) }
              }
              isPlaying = true
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Пауза" else "Слушать",
          tint = Color.Black,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Waveform display
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          bars.forEachIndexed { index, amp ->
            val fraction = index.toFloat() / bars.size.toFloat()
            val isPassed = fraction <= progress.value
            val barColor by animateColorAsState(
              targetValue = if (isPassed) {
                if (isOutgoing) CyberCyan else NeonViolet
              } else {
                CyberTextMuted.copy(alpha = 0.5f)
              },
              label = "waveformColor"
            )

            Box(
              modifier = Modifier
                .weight(1f)
                .height((amp * 26).coerceAtLeast(4f).dp)
                .clip(RoundedCornerShape(2.dp))
                .background(barColor)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Time and Speed toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val seconds = message.voiceDurationSeconds
          val minutes = seconds / 60
          val remSec = seconds % 60
          Text(
            text = "%02d:%02d".format(minutes, remSec),
            fontSize = 11.sp,
            color = CyberTextSecondary
          )

          // Speed switch (1x -> 1.5x -> 2x)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CyberDarkCard)
              .clickable {
                playbackSpeed = when (playbackSpeed) {
                  1.0f -> 1.5f
                  1.5f -> 2.0f
                  else -> 1.0f
                }
              }
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${playbackSpeed}x",
              fontSize = 10.sp,
              color = CyberTextPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // AI Transcription Button & Result
    if (message.voiceTranscription != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CyberDarkCard.copy(alpha = 0.8f))
          .padding(8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = NeonViolet,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "AI Транскрипция",
              fontSize = 10.sp,
              color = NeonViolet,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = message.voiceTranscription,
            fontSize = 12.sp,
            color = CyberTextPrimary,
            lineHeight = 16.sp
          )
        }
      }
    } else {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(NeonViolet.copy(alpha = 0.15f))
          .clickable { onTranscribeClick() }
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = NeonViolet,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Транскрибировать (AI)",
          fontSize = 11.sp,
          color = NeonViolet,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
