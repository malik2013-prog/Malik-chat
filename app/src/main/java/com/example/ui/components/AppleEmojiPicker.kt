package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary

val EMOJI_QUICK_REACTIONS = listOf("❤️", "🔥", "👍", "😂", "😮", "😢", "🚀", "💯", "👻", "🔒")

val EMOJI_FREQUENT = listOf(
  "❤️", "🔥", "😂", "👍", "😍", "🥳", "🥹", "💀", "🚀", "✨", "💯", "🤫",
  "👻", "🔒", "⚡", "💎", "🤩", "🎉", "🫶", "🤤", "🤝", "👀", "🫡", "😎"
)

val EMOJI_SMILEYS = listOf(
  "😀", "😃", "😄", "😁", "😆", "🥹", "😅", "😂", "🤣", "🥲", "☺️", "😊",
  "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋",
  "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏",
  "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺",
  "😢", "😭", "😮‍💨", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱",
  "😨", "😰", "😥", "😓", "🫣", "🤗", "🫡", "🤔", "🫢", "🤭", "🤫", "🤥",
  "😶", "😐", "😑", "😬", "🫠", "🙄", "😯", "😦", "😧", "😮", "😲", "🥱",
  "😴", "🤤", "😪", "😵", "😵‍💫", "🤐", "🥴", "🤢", "🤮", "🤧", "😷", "🤒",
  "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩", "👻", "💀", "☠️",
  "👽", "👾", "🤖", "🎃"
)

val EMOJI_GESTURES = listOf(
  "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞", "🫰", "🤟",
  "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "🫵", "👍", "👎", "✊",
  "👊", "🤛", "🤜", "👏", "🙌", "🫶", "👐", "🤲", "🤝", "🙏", "✍️", "💅",
  "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻", "👃", "🧠", "🫀", "🫁",
  "🦷", "🦴", "👀", "👁️", "👅", "👄", "🫦"
)

val EMOJI_HEARTS = listOf(
  "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹",
  "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️", "✝️", "☪️",
  "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐", "⛎", "♈", "♉", "♊",
  "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓", "🆔"
)

val EMOJI_SYMBOLS = listOf(
  "🔥", "✨", "🌟", "💫", "💥", "⚡", "☄️", "🌈", "💎", "🔮", "🧿", "🚀",
  "🛸", "🎮", "🎯", "🎲", "🏆", "🥇", "🥈", "🥉", "👑", "🕶️", "🍿", "☕",
  "🥂", "🍻", "🍀", "🌸", "🌺", "☀️", "🌙", "🪐", "⭐️", "⚡️", "🛡️", "🔒",
  "🗝️", "👁️‍🗨️", "📱", "💻", "💡", "💰", "💳", "💸", "🧨", "💣", "🦾", "🤖",
  "💯", "💢", "💬", "🗯️", "💭", "💤", "🎵", "🎶", "🔔", "🔕"
)

enum class EmojiCategory(val title: String, val icon: ImageVector, val emojis: List<String>) {
  FREQUENT("Частые", Icons.Default.Schedule, EMOJI_FREQUENT),
  SMILEYS("Смайлики", Icons.Default.Mood, EMOJI_SMILEYS),
  GESTURES("Жесты", Icons.Default.WavingHand, EMOJI_GESTURES),
  HEARTS("Сердца", Icons.Default.Favorite, EMOJI_HEARTS),
  SYMBOLS("Символы", Icons.Default.RocketLaunch, EMOJI_SYMBOLS)
}

@Composable
fun AppleEmojiPicker(
  onEmojiSelected: (String) -> Unit,
  onBackspace: () -> Unit,
  onQuickReaction: ((String) -> Unit)? = null,
  onClose: () -> Unit
) {
  var selectedCategoryIndex by remember { mutableIntStateOf(1) } // Default to Smileys
  val categories = EmojiCategory.values()
  val currentCategory = categories[selectedCategoryIndex]

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(310.dp)
      .background(Color(0xFF18181A))
      .border(0.5.dp, AyuBorder)
  ) {
    // 1. Quick Reaction Bar (Apple / Telegram Style floating pill)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(AyuDarkSurface)
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        EMOJI_QUICK_REACTIONS.take(7).forEach { reaction ->
          AppleReactionPill(
            emoji = reaction,
            onClick = {
              if (onQuickReaction != null) {
                onQuickReaction(reaction)
              } else {
                onEmojiSelected(reaction)
              }
            }
          )
        }
      }

      IconButton(
        onClick = onClose,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Закрыть",
          tint = AyuTextMuted,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // 2. Emoji Grid (Scrollable, 7 columns like Apple Keyboard)
    LazyVerticalGrid(
      columns = GridCells.Fixed(7),
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      items(currentCategory.emojis) { emoji ->
        AppleEmojiCell(emoji = emoji, onClick = { onEmojiSelected(emoji) })
      }
    }

    // 3. Bottom iOS-Style Segmented Navigation Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF141416))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Category Icons
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        categories.forEachIndexed { index, cat ->
          val isSelected = index == selectedCategoryIndex
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isSelected) AyuGreenPrimary.copy(alpha = 0.2f) else Color.Transparent)
              .clickable { selectedCategoryIndex = index },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = cat.icon,
              contentDescription = cat.title,
              tint = if (isSelected) AyuGreenPrimary else AyuTextMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // Backspace Button (iOS keyboard style)
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(AyuDarkCard)
          .clickable { onBackspace() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Backspace,
          contentDescription = "Удалить",
          tint = AyuTextPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun AppleEmojiCell(
  emoji: String,
  onClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 1.35f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    label = "emoji_scale"
  )

  Box(
    modifier = Modifier
      .size(44.dp)
      .clip(RoundedCornerShape(8.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = emoji,
      fontSize = 26.sp,
      modifier = Modifier.scale(scale)
    )
  }
}

@Composable
fun AppleReactionPill(
  emoji: String,
  onClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 1.4f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
    label = "pill_scale"
  )

  Box(
    modifier = Modifier
      .size(34.dp)
      .clip(CircleShape)
      .background(AyuDarkCard)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = emoji,
      fontSize = 19.sp,
      modifier = Modifier.scale(scale)
    )
  }
}
