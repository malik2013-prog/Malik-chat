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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contact
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkCard
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenFab
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuGreenText
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextPrimary
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.theme.IconBlue
import com.example.ui.theme.IconGreen
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun ContactsScreen(
  viewModel: ChatViewModel,
  onContactClick: (Contact) -> Unit
) {
  val contacts by viewModel.contacts.collectAsState()
  var searchQuery by remember { mutableStateOf("") }

  val filteredContacts = contacts.filter {
    searchQuery.isBlank() ||
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.username.contains(searchQuery, ignoreCase = true)
  }

  Scaffold(
    containerColor = AyuDarkBackground,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AyuDarkSurface)
          .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = {}) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = AyuTextPrimary)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Новое сообщение",
            color = AyuTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = {}) {
            Icon(Icons.Default.Sort, contentDescription = "Сортировка", tint = AyuTextPrimary)
          }
          IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = "Меню", tint = AyuTextPrimary)
          }
        }
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {},
        containerColor = AyuGreenFab,
        contentColor = Color.Black,
        shape = CircleShape,
        modifier = Modifier.size(56.dp)
      ) {
        Icon(Icons.Default.PersonAdd, contentDescription = "Добавить контакт", modifier = Modifier.size(24.dp))
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 14.dp)
    ) {
      // Search Bar
      item {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Поиск контактов", color = AyuTextMuted, fontSize = 14.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AyuGreenPrimary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = AyuDarkCard,
            unfocusedContainerColor = AyuDarkCard,
            focusedTextColor = AyuTextPrimary,
            unfocusedTextColor = AyuTextPrimary
          ),
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = AyuTextMuted, modifier = Modifier.size(20.dp))
          }
        )
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Group & Channel Actions Card (from Screenshot 4)
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = AyuDarkCard)
        ) {
          Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {}
                .padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(IconBlue),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(14.dp))
              Text("Создать группу", color = AyuTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Box(modifier = Modifier.fillMaxWidth().padding(start = 66.dp).height(0.5.dp).background(AyuBorder))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {}
                .padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(IconGreen),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
              }
              Spacer(modifier = Modifier.width(14.dp))
              Text("Создать канал", color = AyuTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section Title matching Screenshot 4
        Text(
          text = "Сортировка по времени захода",
          color = AyuGreenText,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(start = 6.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
      }

      // Contacts list
      items(filteredContacts, key = { it.id }) { contact ->
        ContactRow(
          contact = contact,
          onClick = { onContactClick(contact) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
private fun ContactRow(
  contact: Contact,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 6.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Circle avatar
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(listOf(Color(0xFF3A3A3C), Color(0xFF2C2C2E)))
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = contact.name.take(1).uppercase(),
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = contact.name,
          color = AyuTextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
        if (contact.iconEmoji != null) {
          Spacer(modifier = Modifier.width(4.dp))
          Text(contact.iconEmoji, fontSize = 14.sp)
        }
      }
      Text(
        text = contact.lastSeenText,
        color = AyuTextSecondary,
        fontSize = 12.5.sp
      )
    }

    // Small dual-arrow icon from Screenshot 4
    Icon(
      imageVector = Icons.AutoMirrored.Filled.CompareArrows,
      contentDescription = null,
      tint = AyuTextMuted,
      modifier = Modifier.size(18.dp)
    )
  }
}
