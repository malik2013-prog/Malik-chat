package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AyuGramGhostOverlay
import com.example.ui.components.SafetyNumberDialog
import com.example.ui.components.TelegramAuthSheet
import com.example.ui.components.TelegramNotificationBanner
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ArchitectureDocsScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.ConversationScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProxyScreen
import com.example.ui.screens.SavedMessagesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AyuBorder
import com.example.ui.theme.AyuDarkBackground
import com.example.ui.theme.AyuDarkSurface
import com.example.ui.theme.AyuGreenBadge
import com.example.ui.theme.AyuGreenPrimary
import com.example.ui.theme.AyuTextMuted
import com.example.ui.theme.AyuTextSecondary
import com.example.ui.theme.MailChatTheme
import com.example.ui.viewmodel.ChatViewModel

enum class MainScreenTab {
  CHATS,
  CONTACTS,
  SETTINGS,
  PROFILE
}

enum class SubScreen {
  NONE,
  CONVERSATION,
  ADMIN_PANEL,
  ARCHITECTURE_DOCS,
  SAVED_MESSAGES,
  PROXY
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MailChatTheme {
        val viewModel: ChatViewModel = viewModel()
        MailChatApp(viewModel)
      }
    }
  }
}

@Composable
fun MailChatApp(viewModel: ChatViewModel) {
  var currentTab by remember { mutableStateOf(MainScreenTab.CHATS) }
  var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }
  var openedChatId by remember { mutableStateOf("chat_support") }
  var showTelegramAuthSheet by remember { mutableStateOf(false) }

  val isGhostOverlayOpen by viewModel.isGhostOverlayVisible.collectAsState()
  val ghostSettings by viewModel.ghostSettings.collectAsState()
  val safetyNumberData by viewModel.safetyNumberData.collectAsState()
  val telegramNotification by viewModel.telegramNotification.collectAsState()

  // Handle system back button for nested screens
  BackHandler(enabled = currentSubScreen != SubScreen.NONE) {
    currentSubScreen = SubScreen.NONE
  }

  Scaffold(
    containerColor = AyuDarkBackground,
    bottomBar = {
      // Hide bottom bar when inside a subscreen like conversation
      if (currentSubScreen == SubScreen.NONE) {
        NavigationBar(
          containerColor = AyuDarkSurface,
          modifier = Modifier.navigationBarsPadding(),
          tonalElevation = 8.dp
        ) {
          // Tab 1: Чаты (with green badge 21)
          NavigationBarItem(
            selected = currentTab == MainScreenTab.CHATS,
            onClick = { currentTab = MainScreenTab.CHATS },
            icon = {
              BadgedBox(
                badge = {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(9.dp))
                      .background(AyuGreenBadge)
                      .padding(horizontal = 5.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "21",
                      color = Color.Black,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              ) {
                Icon(
                  imageVector = if (currentTab == MainScreenTab.CHATS) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                  contentDescription = "Чаты"
                )
              }
            },
            label = { Text("Чаты", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AyuGreenPrimary,
              selectedTextColor = AyuGreenPrimary,
              unselectedIconColor = AyuTextMuted,
              unselectedTextColor = AyuTextMuted,
              indicatorColor = AyuGreenPrimary.copy(alpha = 0.15f)
            )
          )

          // Tab 2: Контакты
          NavigationBarItem(
            selected = currentTab == MainScreenTab.CONTACTS,
            onClick = { currentTab = MainScreenTab.CONTACTS },
            icon = {
              Icon(
                imageVector = if (currentTab == MainScreenTab.CONTACTS) Icons.Filled.People else Icons.Outlined.PeopleOutline,
                contentDescription = "Контакты"
              )
            },
            label = { Text("Контакты", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AyuGreenPrimary,
              selectedTextColor = AyuGreenPrimary,
              unselectedIconColor = AyuTextMuted,
              unselectedTextColor = AyuTextMuted,
              indicatorColor = AyuGreenPrimary.copy(alpha = 0.15f)
            )
          )

          // Tab 3: Настройки
          NavigationBarItem(
            selected = currentTab == MainScreenTab.SETTINGS,
            onClick = { currentTab = MainScreenTab.SETTINGS },
            icon = {
              Icon(
                imageVector = if (currentTab == MainScreenTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                contentDescription = "Настройки"
              )
            },
            label = { Text("Настройки", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AyuGreenPrimary,
              selectedTextColor = AyuGreenPrimary,
              unselectedIconColor = AyuTextMuted,
              unselectedTextColor = AyuTextMuted,
              indicatorColor = AyuGreenPrimary.copy(alpha = 0.15f)
            )
          )

          // Tab 4: Профиль (with round bird avatar)
          NavigationBarItem(
            selected = currentTab == MainScreenTab.PROFILE,
            onClick = { currentTab = MainScreenTab.PROFILE },
            icon = {
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .border(
                    width = if (currentTab == MainScreenTab.PROFILE) 2.dp else 1.dp,
                    color = if (currentTab == MainScreenTab.PROFILE) AyuGreenPrimary else AyuBorder,
                    shape = CircleShape
                  )
              ) {
                Image(
                  painter = painterResource(id = R.drawable.avatar_ayugram_bird_1792692909781),
                  contentDescription = "Профиль",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
            },
            label = { Text("Профиль", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = AyuGreenPrimary,
              selectedTextColor = AyuGreenPrimary,
              unselectedIconColor = AyuTextMuted,
              unselectedTextColor = AyuTextMuted,
              indicatorColor = AyuGreenPrimary.copy(alpha = 0.15f)
            )
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentSubScreen,
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "subscreen_transition"
      ) { subScreen ->
        when (subScreen) {
          SubScreen.CONVERSATION -> {
            ConversationScreen(
              viewModel = viewModel,
              chatId = openedChatId,
              onBackClick = { currentSubScreen = SubScreen.NONE },
              onOpenGhostOverlay = { viewModel.toggleGhostOverlay() }
            )
          }
          SubScreen.ADMIN_PANEL -> {
            AdminPanelScreen(
              viewModel = viewModel,
              onBackClick = { currentSubScreen = SubScreen.NONE }
            )
          }
          SubScreen.ARCHITECTURE_DOCS -> {
            ArchitectureDocsScreen(
              onBackClick = { currentSubScreen = SubScreen.NONE }
            )
          }
          SubScreen.SAVED_MESSAGES -> {
            SavedMessagesScreen(
              viewModel = viewModel,
              onBackClick = { currentSubScreen = SubScreen.NONE }
            )
          }
          SubScreen.PROXY -> {
            ProxyScreen(
              viewModel = viewModel,
              onBackClick = { currentSubScreen = SubScreen.NONE }
            )
          }
          SubScreen.NONE -> {
            AnimatedContent(
              targetState = currentTab,
              transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(160))
              },
              label = "tab_transition"
            ) { tab ->
              when (tab) {
                MainScreenTab.CHATS -> {
                  ChatListScreen(
                    viewModel = viewModel,
                    onChatClick = { chatId ->
                      openedChatId = chatId
                      viewModel.selectChat(chatId)
                      currentSubScreen = SubScreen.CONVERSATION
                    },
                    onOpenGhostOverlay = { viewModel.toggleGhostOverlay() },
                    onOpenSavedMessages = { currentSubScreen = SubScreen.SAVED_MESSAGES },
                    onOpenProxyScreen = { currentSubScreen = SubScreen.PROXY }
                  )
                }
                MainScreenTab.CONTACTS -> {
                  ContactsScreen(
                    viewModel = viewModel,
                    onContactClick = { contact ->
                      val targetId = when (contact.id) {
                        "cnt_support" -> "chat_support"
                        "cnt_saved" -> "chat_saved"
                        else -> "chat_alex"
                      }
                      openedChatId = targetId
                      viewModel.selectChat(targetId)
                      currentSubScreen = SubScreen.CONVERSATION
                    }
                  )
                }
                MainScreenTab.SETTINGS -> {
                  SettingsScreen(
                    viewModel = viewModel,
                    onOpenTelegramAuth = { showTelegramAuthSheet = true },
                    onOpenGhostOverlay = { viewModel.toggleGhostOverlay() }
                  )
                }
                MainScreenTab.PROFILE -> {
                  ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAdmin = { currentSubScreen = SubScreen.ADMIN_PANEL },
                    onNavigateToSettings = { currentTab = MainScreenTab.SETTINGS },
                    onOpenTelegramAuth = { showTelegramAuthSheet = true }
                  )
                }
              }
            }
          }
        }
      }

      // Telegram System Dropdown Notification (simulating realistic OTP dispatch banner)
      TelegramNotificationBanner(
        notificationText = telegramNotification,
        onDismiss = { viewModel.clearTelegramNotification() },
        onClick = {
          showTelegramAuthSheet = true
          viewModel.clearTelegramNotification()
        }
      )

      // AyuGram Floating Ghost Overlay Modal
      AyuGramGhostOverlay(
        isVisible = isGhostOverlayOpen,
        settings = ghostSettings,
        onDismiss = { viewModel.closeGhostOverlay() },
        onSettingsChange = { viewModel.updateGhostSettings(it) }
      )

      // E2EE Safety Number Fingerprint Verification Modal
      safetyNumberData?.let { (peerName, number) ->
        SafetyNumberDialog(
          peerName = peerName,
          safetyNumber = number,
          onDismiss = { viewModel.closeSafetyNumber() }
        )
      }

      // Telegram Phone OTP Verification Sheet
      if (showTelegramAuthSheet) {
        TelegramAuthSheet(
          onDismiss = { showTelegramAuthSheet = false },
          onRequestCode = { phone ->
            viewModel.requestTelegramCode(phone)
          },
          onVerifyCode = { phone, code ->
            viewModel.verifyTelegramCodeByPhone(phone, code)
          }
        )
      }
    }
  }
}
