package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.StealthGhostEmerald

@Composable
fun SafetyNumberDialog(
  peerName: String,
  safetyNumber: String,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
      colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = StealthGhostEmerald,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Код безопасности E2EE",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = CyberTextPrimary
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = CyberTextMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated QR Code box
        Box(
          modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.QrCode2,
            contentDescription = "QR Code",
            tint = Color.Black,
            modifier = Modifier.size(116.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Код верификации ключей с $peerName:",
          fontSize = 13.sp,
          color = CyberTextSecondary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Safety number chunked display
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberDarkCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = safetyNumber,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = CyberCyan,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Если этот 30-значный код совпадает на обоих устройствах, ваш диалог защищен от прослушивания и атак Man-in-the-Middle (ECDH X25519).",
          fontSize = 11.5.sp,
          color = CyberTextMuted,
          textAlign = TextAlign.Center,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = StealthGhostEmerald)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Ключи проверены и подтверждены",
            color = Color.Black,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
