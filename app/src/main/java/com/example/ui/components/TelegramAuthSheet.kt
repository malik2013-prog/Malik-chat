package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenFab
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import kotlinx.coroutines.delay

@Composable
fun TelegramAuthSheet(
  initialPhone: String = "+7 (962) 641-61-55",
  onDismiss: () -> Unit,
  onRequestCode: (String) -> String,
  onVerifyCode: (String, String) -> Boolean
) {
  var phone by remember { mutableStateOf(initialPhone) }
  var codeInput by remember { mutableStateOf("") }
  var isCodeSent by remember { mutableStateOf(false) }
  var isVerified by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var countdown by remember { mutableIntStateOf(50) }

  LaunchedEffect(isCodeSent) {
    if (isCodeSent) {
      countdown = 50
      while (countdown > 0) {
        delay(1000)
        countdown--
      }
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, AyuGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
      colors = CardDefaults.cardColors(containerColor = AyuDarkSurface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF229ED9)), // Telegram Blue
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Вход через Telegram",
                color = AyuTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "Код подтверждения в Telegram",
                color = AyuTextSecondary,
                fontSize = 11.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = AyuTextMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isVerified) {
          // Success State
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(AyuGreenPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AyuGreenPrimary, modifier = Modifier.size(34.dp))
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text("Номер подтвержден!", color = AyuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Сессия Go WebSocket успешно авторизована для номера $phone",
            color = AyuTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(18.dp))
          Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AyuGreenPrimary)
          ) {
            Text("Готово", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        } else if (!isCodeSent) {
          // Phone Input Step
          Text(
            text = "Введите номер телефона. Мы отправим служебное сообщение в Telegram с одноразовым кодом подтверждения (Your login code).",
            color = AyuTextSecondary,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Номер телефона", color = AyuTextMuted) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AyuGreenPrimary,
              unfocusedBorderColor = AyuBorder,
              focusedContainerColor = AyuDarkCard,
              unfocusedContainerColor = AyuDarkCard,
              focusedTextColor = AyuTextPrimary,
              unfocusedTextColor = AyuTextPrimary
            ),
            leadingIcon = {
              Icon(Icons.Default.Phone, contentDescription = null, tint = AyuGreenPrimary, modifier = Modifier.size(20.dp))
            }
          )

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              if (phone.isNotBlank()) {
                onRequestCode(phone)
                isCodeSent = true
                errorMessage = null
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9))
          ) {
            Text("Отправить код в Telegram", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        } else {
          // Code Verification Step
          Text(
            text = "Код отправлен в ваше приложение Telegram на номер:\n$phone",
            color = AyuTextSecondary,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // 5-digit code field
          OutlinedTextField(
            value = codeInput,
            onValueChange = {
              if (it.length <= 5) {
                codeInput = it
                if (it.length == 5) {
                  val success = onVerifyCode(it, phone)
                  if (success) {
                    isVerified = true
                    errorMessage = null
                  } else {
                    errorMessage = "Неверный код. Попробуйте еще раз."
                  }
                }
              }
            },
            placeholder = { Text("• • • • •", color = AyuTextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.7f),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AyuGreenPrimary,
              unfocusedBorderColor = AyuBorder,
              focusedContainerColor = AyuDarkCard,
              unfocusedContainerColor = AyuDarkCard,
              focusedTextColor = AyuGreenPrimary,
              unfocusedTextColor = AyuGreenPrimary
            )
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(errorMessage ?: "", color = Color(0xFFFF453A), fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Paste button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(AyuDarkCard)
              .border(1.dp, AyuBorder, RoundedCornerShape(8.dp))
              .clickable {
                // Auto-fill test code
                codeInput = "48291"
                val success = onVerifyCode("48291", phone)
                if (success) isVerified = true
              }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Вставить код из уведомления (48291)", fontSize = 11.5.sp, color = AyuGreenPrimary)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = if (countdown > 0) "Отправить код повторно через $countdown с" else "Отправить код повторно",
            color = if (countdown > 0) AyuTextMuted else Color(0xFF229ED9),
            fontSize = 12.sp,
            modifier = Modifier.clickable(enabled = countdown == 0) {
              onRequestCode(phone)
              countdown = 50
            }
          )
        }
      }
    }
  }
}
