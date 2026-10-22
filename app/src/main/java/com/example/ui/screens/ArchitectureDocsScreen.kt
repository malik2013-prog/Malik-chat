package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StealthGhostEmerald

@Composable
fun ArchitectureDocsScreen(onBackClick: () -> Unit) {
  var activeTab by remember { mutableStateOf("GO_SERVER") }

  Scaffold(
    containerColor = CyberDarkBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSurface)
          .padding(horizontal = 8.dp, vertical = 10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = CyberTextPrimary)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Code, contentDescription = null, tint = StealthGhostEmerald, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Архитектура бэкенда и БД", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          DocTabButton("GO_SERVER", "Go WebSocket Сервер", activeTab == "GO_SERVER") { activeTab = "GO_SERVER" }
          DocTabButton("POSTGRES", "Схема PostgreSQL", activeTab == "POSTGRES") { activeTab = "POSTGRES" }
          DocTabButton("WEBSOCKET", "JSON Протокол", activeTab == "WEBSOCKET") { activeTab = "WEBSOCKET" }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      when (activeTab) {
        "GO_SERVER" -> GoServerDoc()
        "POSTGRES" -> PostgresSchemaDoc()
        "WEBSOCKET" -> WebSocketProtocolDoc()
      }
    }
  }
}

@Composable
private fun DocTabButton(tabKey: String, title: String, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberDarkCard)
      .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) CyberCyan else CyberTextSecondary,
      modifier = Modifier.align(Alignment.Center)
    )
  }
}

@Composable
private fun GoServerDoc() {
  val goCode = """
// package main: Mail Chat High-Performance Go Relay Hub
package main

import (
    "context"
    "encoding/json"
    "log"
    "net/http"
    "sync"
    "time"
    "github.com/gorilla/websocket"
    "github.com/redis/go-redis/v9"
)

type Client struct {
    UserID    string
    Conn      *websocket.Conn
    SendQueue chan []byte
    IsGhost   bool
}

type Hub struct {
    clients    sync.Map // map[string]*Client
    broadcast  chan []byte
    register   chan *Client
    unregister chan *Client
    rdb        *redis.Client
}

func (h *Hub) Run() {
    for {
        select {
        case client := <-h.register:
            h.clients.Store(client.UserID, client)
            // Update Redis presence (spoof if Ghost Mode)
            status := "ONLINE"
            if client.IsGhost {
                status = "OFFLINE_SPOOFED"
            }
            h.rdb.Set(context.Background(), "user:"+client.UserID+":presence", status, 24*time.Hour)

        case client := <-h.unregister:
            if _, ok := h.clients.Load(client.UserID); ok {
                h.clients.Delete(client.UserID)
                close(client.SendQueue)
                h.rdb.Set(context.Background(), "user:"+client.UserID+":presence", "OFFLINE", 24*time.Hour)
            }

        case message := <-h.broadcast:
            // Route encrypted packet without storing plaintext
            var packet InboundPacket
            if err := json.Unmarshal(message, &packet); err == nil {
                h.routeMessage(packet)
            }
        }
    }
}

func (h *Hub) routeMessage(p InboundPacket) {
    // Ghost Mode check: If sender or recipient is in Ghost Mode,
    // do NOT dispatch "READ" delivery status back to sender!
    if p.Type == "MESSAGE_READ" && p.GhostHeaders.StealthRead {
        log.Printf("[Ghost Mode] Suppressed READ receipt for message %s", p.MessageID)
        return
    }

    if targetClient, ok := h.clients.Load(p.RecipientID); ok {
        target := targetClient.(*Client)
        target.SendQueue <- p.RawJSON
    } else {
        // Enqueue to offline Redis queue for APNS/FCM delivery
// HandleKeyRegeneration processes client request for session rekeying
func (h *Hub) HandleKeyRegeneration(w http.ResponseWriter, r *http.Request) {
    var req struct {
        UserID    string `json:"user_id"`
        EphemeralKey string `json:"ephemeral_x25519"`
    }
    if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    // Generate ratcheted session key on server relay
    newSessionID := fmt.Sprintf("sess_%d_%s", time.Now().UnixNano(), req.UserID[:6])
    h.rdb.Set(r.Context(), "session:"+newSessionID+":ratchet", req.EphemeralKey, 72*time.Hour)

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(map[string]interface{}{
        "status":         "REKEYED_OK",
        "session_id":     newSessionID,
        "algorithm":      "X25519-AES256-GCM-HKDF-SHA256",
        "epoch":          time.Now().Unix(),
    })
}

// SendTelegramAuthCode dispatches realistic login verification code
func (h *Hub) SendTelegramAuthCode(phone string) (string, error) {
    // Generate 5-digit verification code
    code := fmt.Sprintf("%05d", rand.Intn(100000))
    msg := fmt.Sprintf("Your login code: %s. Do not give this code to anyone, even if they say they are from Telegram!", code)

    // Deliver via Telegram Bot API or TDLib notification
    log.Printf("[Telegram Service] Sent to %s: %s", phone, msg)
    return code, nil
}
  """.trimIndent()

  CodeBoxView(title = "Go WebSocket Server & Key Regeneration Router", code = goCode)
}

@Composable
private fun PostgresSchemaDoc() {
  val sqlSchema = """
-- PostgreSQL 16 DDL for Mail Chat Encrypted Messenger
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users Table
CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(32) UNIQUE NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    phone_hash VARCHAR(64) NOT NULL,
    public_key_x25519 TEXT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE,
    dc_region VARCHAR(16) DEFAULT 'DC2',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_phone_hash ON users(phone_hash);

-- Privacy & Ghost Settings Table
CREATE TABLE user_privacy_settings (
    user_id VARCHAR(64) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    ghost_mode_enabled BOOLEAN DEFAULT TRUE,
    hide_read_receipts BOOLEAN DEFAULT TRUE,
    hide_last_seen BOOLEAN DEFAULT TRUE,
    stealth_typing BOOLEAN DEFAULT TRUE,
    allow_anti_delete_sync BOOLEAN DEFAULT TRUE
);

-- Encrypted Messages Table (Zero-Plaintext)
CREATE TABLE encrypted_messages (
    id VARCHAR(64) PRIMARY KEY,
    chat_id VARCHAR(64) NOT NULL,
    sender_id VARCHAR(64) NOT NULL REFERENCES users(id),
    recipient_id VARCHAR(64) NOT NULL REFERENCES users(id),
    ciphertext_base64 TEXT NOT NULL,
    nonce_hex VARCHAR(32) NOT NULL,
    media_type VARCHAR(16) NOT NULL, -- TEXT, VOICE, MEDIA
    delivery_status VARCHAR(16) DEFAULT 'SENT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE -- Soft delete on server
);

CREATE INDEX idx_messages_chat_timestamp ON encrypted_messages(chat_id, created_at DESC);
CREATE INDEX idx_messages_recipient ON encrypted_messages(recipient_id) WHERE delivery_status != 'READ';
  """.trimIndent()

  CodeBoxView(title = "PostgreSQL Schema (DDL + Indexes)", code = sqlSchema)
}

@Composable
private fun WebSocketProtocolDoc() {
  val jsonSpec = """
// WebSocket Protocol Packet Structures

// 1. Handshake Packet (Client -> Server)
{
  "type": "HANDSHAKE_INIT",
  "timestamp": 1729609200000,
  "payload": {
    "user_id": "usr_zenox_001",
    "public_key": "8f3e2b9c7a1d4f6e...",
    "client_version": "2.4.0-ghost",
    "preferred_dc": "DC2"
  }
}

// 2. Encrypted Message Packet (Zero-Knowledge)
{
  "type": "ENCRYPTED_MESSAGE",
  "message_id": "msg_948210491823",
  "chat_id": "chat_eva_01",
  "timestamp": 1729609210000,
  "ghost_headers": {
    "stealth_read_receipt": true,
    "stealth_typing": true,
    "preserve_anti_delete": true
  },
  "payload": {
    "sender_id": "usr_zenox_001",
    "recipient_id": "usr_eva_002",
    "ciphertext": "U2FsdGVkX19sZWdlbmRhcnlQYXlsb2Fk...",
    "nonce": "a1b2c3d4e5f60718293a4b5c",
    "algo": "AES-256-GCM",
    "media_type": "VOICE",
    "voice_duration": 8,
    "voice_waveform": "0.2,0.5,0.9,0.7,0.3"
  }
}

// 3. Ghost Typing Status (Withheld when Ghost Mode is ON)
{
  "type": "TYPING_INDICATOR",
  "chat_id": "chat_eva_01",
  "payload": {
    "user_id": "usr_zenox_001",
    "is_typing": false
  }
}
  """.trimIndent()

  CodeBoxView(title = "WebSocket JSON Protocol Specification", code = jsonSpec)
}

@Composable
private fun CodeBoxView(title: String, code: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = title,
        color = CyberCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CyberDarkCard)
          .padding(12.dp)
      ) {
        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
          Text(
            text = code,
            color = CyberTextPrimary,
            fontSize = 11.5.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}
