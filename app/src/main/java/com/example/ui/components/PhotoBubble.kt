package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Message
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberTextPrimary
import java.io.File

@Composable
fun PhotoBubble(
  message: Message,
  isOutgoing: Boolean
) {
  Column(
    modifier = Modifier
      .width(240.dp)
  ) {
    if (!message.mediaUri.isNullOrBlank() && File(message.mediaUri).exists()) {
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(File(message.mediaUri))
          .crossfade(true)
          .build(),
        contentDescription = "Фотография",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
      )
    }

    if (message.text.isNotBlank() && !message.text.startsWith("📷")) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = message.text,
        fontSize = 14.sp,
        color = CyberTextPrimary,
        modifier = Modifier.padding(horizontal = 4.dp)
      )
    }
  }
}
