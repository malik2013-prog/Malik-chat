package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic AyuGram / Telegram Dark Palette
val AyuDarkBackground = Color(0xFF0F0F10)
val AyuDarkSurface = Color(0xFF18181A)
val AyuDarkCard = Color(0xFF212124)
val AyuDarkCardElevated = Color(0xFF28282C)
val AyuBorder = Color(0xFF2E2E32)

// AyuGram Emerald / Green Accent (from screenshots)
val AyuGreenPrimary = Color(0xFF60B86E)
val AyuGreenDark = Color(0xFF224E2A)
val AyuGreenLight = Color(0xFF84D891)
val AyuGreenPill = Color(0xFF2B4432)
val AyuGreenFab = Color(0xFF8CE077)
val AyuGreenBadge = Color(0xFF4CAF50)

// Text Colors
val AyuTextPrimary = Color(0xFFF5F5F7)
val AyuTextSecondary = Color(0xFF9E9EA5)
val AyuTextMuted = Color(0xFF6B6B73)
val AyuGreenText = Color(0xFF72D082)

// Settings Icon Background Colors (from screenshots)
val IconBlue = Color(0xFF2997FF)
val IconOrange = Color(0xFFFF9500)
val IconGreen = Color(0xFF34C759)
val IconRed = Color(0xFFFF3B30)
val IconDataBlue = Color(0xFF007AFF)
val IconFolderBlue = Color(0xFF5AC8FA)
val IconTeal = Color(0xFF30B0C7)
val IconBattery = Color(0xFFFF9500)
val IconPurple = Color(0xFFAF52DE)
val IconPremium = Color(0xFFA855F7)
val IconStarGold = Color(0xFFFFCC00)
val IconGift = Color(0xFFFF6482)

// Chat Bubbles
val AyuBubbleOutgoing = Color(0xFF233B2B)
val AyuBubbleIncoming = Color(0xFF212124)
val AyuBubbleGhostDeleted = Color(0xFF33161C)
val AyuGhostBadgeRed = Color(0xFFFF453A)

// Compatibility aliases for legacy sub-screens
val CyberDarkBackground = AyuDarkBackground
val CyberDarkSurface = AyuDarkSurface
val CyberDarkSurfaceVariant = AyuDarkCardElevated
val CyberDarkCard = AyuDarkCard
val CyberBorder = AyuBorder
val CyberCyan = AyuGreenPrimary
val CyberTextPrimary = AyuTextPrimary
val CyberTextSecondary = AyuTextSecondary
val CyberTextMuted = AyuTextMuted
val StealthGhostEmerald = AyuGreenPrimary
val GhostDangerRed = AyuGhostBadgeRed
val NeonViolet = Color(0xFFA855F7)
val NeonPink = Color(0xFFFF2D55)
val CyberBackground = AyuDarkBackground
val VerifiedBadgeBlue = Color(0xFF229ED9)

val BubbleGhostBorder = AyuGhostBadgeRed.copy(alpha = 0.4f)
val BubbleGhostDeleted = AyuBubbleGhostDeleted
val BubbleIncoming = AyuBubbleIncoming
val BubbleIncomingBorder = AyuBorder
val BubbleOutgoing = AyuBubbleOutgoing
val BubbleOutgoingBorder = AyuGreenDark
