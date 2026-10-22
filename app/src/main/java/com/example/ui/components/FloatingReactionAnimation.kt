package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class ParticleState(
  val id: Int,
  val startXOffset: Float,
  val targetXOffset: Float,
  val targetYOffset: Float,
  val rotation: Float,
  val maxScale: Float,
  val delayMs: Long
)

@Composable
fun FloatingReactionBurst(
  emoji: String,
  onAnimationEnd: () -> Unit
) {
  val particleCount = 12
  val particles = remember {
    List(particleCount) { index ->
      ParticleState(
        id = index,
        startXOffset = Random.nextInt(-40, 40).toFloat(),
        targetXOffset = Random.nextInt(-160, 160).toFloat(),
        targetYOffset = -Random.nextInt(280, 560).toFloat(),
        rotation = Random.nextInt(-45, 45).toFloat(),
        maxScale = Random.nextDouble(1.0, 1.8).toFloat(),
        delayMs = Random.nextLong(0, 150)
      )
    }
  }

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    particles.forEach { p ->
      SingleAnimatedParticle(emoji = emoji, particle = p)
    }
  }

  LaunchedEffect(Unit) {
    delay(1300)
    onAnimationEnd()
  }
}

@Composable
private fun SingleAnimatedParticle(
  emoji: String,
  particle: ParticleState
) {
  val progress = remember { Animatable(0f) }

  LaunchedEffect(particle.id) {
    delay(particle.delayMs)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
    )
  }

  val t = progress.value
  val currentY = (particle.targetYOffset * t).roundToInt()
  val currentX = ((particle.startXOffset + (particle.targetXOffset - particle.startXOffset) * t)).roundToInt()
  val currentScale = if (t < 0.25f) (t / 0.25f) * particle.maxScale else (particle.maxScale * (1f - (t - 0.25f) * 0.4f))
  val currentAlpha = if (t > 0.6f) 1f - ((t - 0.6f) / 0.4f) else 1f
  val currentRotation = particle.rotation * t

  Box(
    modifier = Modifier
      .offset { IntOffset(currentX, currentY) }
      .scale(currentScale.coerceAtLeast(0.1f))
      .alpha(currentAlpha.coerceIn(0f, 1f))
      .rotate(currentRotation)
  ) {
    Text(
      text = emoji,
      fontSize = 32.sp
    )
  }
}
