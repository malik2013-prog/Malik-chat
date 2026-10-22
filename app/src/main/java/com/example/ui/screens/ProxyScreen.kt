package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProxyServer
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun ProxyScreen(
  viewModel: ChatViewModel,
  onBackClick: () -> Unit
) {
  val proxies by viewModel.proxies.collectAsState()
  var isProxyEnabled by remember { mutableStateOf(true) }

  Scaffold(
    containerColor = AyuDarkBackground,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AyuDarkSurface)
          .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBackClick) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = AyuTextPrimary)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Настройки прокси",
          color = AyuTextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
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
      // Master toggle card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Использовать прокси", color = AyuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
              text = "Шифрует и обфусцирует трафик через MTProto/SOCKS5",
              color = AyuTextSecondary,
              fontSize = 12.sp
            )
          }

          Switch(
            checked = isProxyEnabled,
            onCheckedChange = { isProxyEnabled = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = AyuGreenPrimary,
              checkedTrackColor = AyuGreenPrimary.copy(alpha = 0.3f)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "ПОДКЛЮЧЕНИЯ",
        color = AyuGreenPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 6.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Proxies list
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          proxies.forEachIndexed { index, proxy ->
            ProxyItemRow(
              proxy = proxy,
              isEnabled = isProxyEnabled,
              onSelect = { viewModel.toggleProxy(proxy.id) }
            )
            if (index < proxies.size - 1) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(start = 48.dp)
                  .height(0.5.dp)
                  .background(AyuBorder)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = { /* Add proxy */ },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AyuDarkCard)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = AyuGreenPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Добавить прокси", color = AyuGreenPrimary, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Прокси-серверы MTProto передают зашифрованный трафик через альтернативные сетевые реле Mail Chat / AyuGram, скрывая реальный IP-адрес клиента и предотвращая блокировки провайдеров.",
        color = AyuTextMuted,
        fontSize = 11.5.sp,
        lineHeight = 16.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
    }
  }
}

@Composable
private fun ProxyItemRow(
  proxy: ProxyServer,
  isEnabled: Boolean,
  onSelect: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(enabled = isEnabled) { onSelect() }
      .padding(horizontal = 8.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(
      selected = proxy.isConnected && isEnabled,
      onClick = onSelect,
      enabled = isEnabled,
      colors = RadioButtonDefaults.colors(
        selectedColor = AyuGreenPrimary,
        unselectedColor = AyuTextMuted
      )
    )

    Spacer(modifier = Modifier.width(8.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = proxy.title,
        color = if (isEnabled) AyuTextPrimary else AyuTextMuted,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
      )
      Text(
        text = "${proxy.proxyType} • ${proxy.host}:${proxy.port}",
        color = AyuTextSecondary,
        fontSize = 11.5.sp
      )
    }

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(
          if (proxy.isConnected && isEnabled) AyuGreenPrimary.copy(alpha = 0.2f) else AyuDarkSurface
        )
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Text(
        text = if (proxy.isConnected && isEnabled) "Подключен • ${proxy.pingMs} ms" else "${proxy.pingMs} ms",
        color = if (proxy.isConnected && isEnabled) AyuGreenPrimary else AyuTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
