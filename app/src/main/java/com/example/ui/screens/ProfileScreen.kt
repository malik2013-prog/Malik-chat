package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenDark
import com.example.ui.theme.AyuGreenFab
import com.example.ui.theme.AyuGreenLight
import com.example.ui.theme.AyuGreenPill
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun ProfileScreen(
  viewModel: ChatViewModel,
  onNavigateToAdmin: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onOpenTelegramAuth: () -> Unit
) {
  val user by viewModel.currentUser.collectAsState()
  val musicTrack by viewModel.musicTrack.collectAsState()
  var selectedTab by remember { mutableStateOf("PUBLICATIONS") }

  Scaffold(
    containerColor = AyuDarkBackground
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
    ) {
      // Top Green Header Section (Matching Screenshot 1)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xFF265834),
                Color(0xFF1E4629),
                AyuDarkBackground
              )
            )
          )
          .padding(bottom = 16.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Header Top Bar: 4-squares grid on left, 3-dots on right
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = {}) {
              Icon(Icons.Default.GridView, contentDescription = "Меню", tint = Color.White)
            }
            IconButton(onClick = {}) {
              Icon(Icons.Default.MoreVert, contentDescription = "Опции", tint = Color.White)
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Big Circle Avatar with generated bird image from screenshot
          Box(
            modifier = Modifier
              .size(110.dp)
              .clip(CircleShape)
              .background(Color(0xFF2D5C38))
              .border(2.dp, AyuGreenLight.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.avatar_ayugram_bird_1792692909781),
              contentDescription = "Аватар профиля",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Display Name + Green Star (from Screenshot 1)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = user.displayName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Star",
              tint = AyuGreenLight,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Status Badge: 1 в сети
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text("1", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = user.statusText,
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Three Quick Action Pill Buttons (from Screenshot 1)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            ProfileActionButton(
              icon = Icons.Default.CameraAlt,
              label = "Выбрать фото",
              onClick = {}
            )
            ProfileActionButton(
              icon = Icons.Default.Edit,
              label = "Изменить",
              onClick = onOpenTelegramAuth
            )
            ProfileActionButton(
              icon = Icons.Default.Settings,
              label = "Настройки",
              onClick = onNavigateToSettings
            )
          }
        }
      }

      // Music Player Widget Card (from Screenshot 1)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.toggleMusic() }
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Album thumbnail
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color.DarkGray),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.album_italian_magician_1792692927615),
              contentDescription = "Обложка альбома",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            // Play/Pause overlay indicator
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (musicTrack.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = musicTrack.title,
              color = AyuTextPrimary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = musicTrack.artist,
              color = AyuTextSecondary,
              fontSize = 12.5.sp
            )
            Text(
              text = musicTrack.album,
              color = AyuTextMuted,
              fontSize = 11.sp
            )
          }

          Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = if (musicTrack.isPlaying) AyuGreenPrimary else AyuTextMuted,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // User Information Card (Matching Screenshot 1 exactly)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
      ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          // Phone (Clickable to trigger Telegram Auth Code!)
          ProfileDetailRow(
            title = user.phoneMasked,
            subtitle = "Телефон",
            onClick = onOpenTelegramAuth
          )

          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(0.5.dp).background(AyuBorder))

          // Username
          ProfileDetailRow(
            title = user.username,
            subtitle = "Имя пользователя",
            onClick = {}
          )

          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(0.5.dp).background(AyuBorder))

          // DC info with Calendar icon
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {}
              .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = user.dcId,
                color = AyuTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = user.dcRegion,
                color = AyuTextSecondary,
                fontSize = 12.sp
              )
            }

            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = "DC Calendar",
              tint = AyuGreenLight,
              modifier = Modifier.size(22.dp)
            )
          }

          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(0.5.dp).background(AyuBorder))

          // Birthday & Account Age
          ProfileDetailRow(
            title = user.accountAge,
            subtitle = "День рождения",
            onClick = {}
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Admin button for @zenox
      if (user.isAdmin) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToAdmin() }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AyuGreenPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Админ-панель (@zenox)", color = AyuGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Управление статусами, модерация и сервер Go", color = AyuTextSecondary, fontSize = 11.5.sp)
            }
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Segmented Tabs: Публикации, Архив публикаций, Подарки (from Screenshot 1)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ProfileTabPill(
          title = "Публикации",
          isSelected = selectedTab == "PUBLICATIONS",
          onClick = { selectedTab = "PUBLICATIONS" }
        )
        ProfileTabPill(
          title = "Архив публикаций",
          isSelected = selectedTab == "ARCHIVE",
          onClick = { selectedTab = "ARCHIVE" }
        )
        ProfileTabPill(
          title = "Подарки",
          isSelected = selectedTab == "GIFTS",
          onClick = { selectedTab = "GIFTS" }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Публикаций пока нет",
          color = AyuTextMuted,
          fontSize = 13.5.sp
        )
      }

      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun ProfileActionButton(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(AyuGreenPill)
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
private fun ProfileDetailRow(
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    Text(text = title, color = AyuTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.height(2.dp))
    Text(text = subtitle, color = AyuTextSecondary, fontSize = 12.sp)
  }
}

@Composable
private fun ProfileTabPill(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(if (isSelected) AyuGreenPill else AyuDarkCard)
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Text(
      text = title,
      color = if (isSelected) AyuGreenLight else AyuTextSecondary,
      fontSize = 12.5.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
  }
}
