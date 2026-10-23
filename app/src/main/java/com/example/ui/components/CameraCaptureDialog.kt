package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class CameraMode {
  CIRCULAR_VIDEO_NOTE,
  PHOTO_SNAP
}

@Composable
fun CameraCaptureDialog(
  initialMode: CameraMode = CameraMode.CIRCULAR_VIDEO_NOTE,
  onDismiss: () -> Unit,
  onCapturedPhoto: (fileUri: String) -> Unit,
  onCapturedVideoNote: (fileUri: String, durationSeconds: Int) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()

  var mode by remember { mutableStateOf(initialMode) }
  var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
  var isRecordingVideoNote by remember { mutableStateOf(false) }
  var videoNoteDuration by remember { mutableIntStateOf(0) }
  var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
  var isProcessing by remember { mutableStateOf(false) }

  // Video recording timer
  LaunchedEffect(isRecordingVideoNote) {
    if (isRecordingVideoNote) {
      videoNoteDuration = 0
      while (isRecordingVideoNote && videoNoteDuration < 60) {
        delay(1000)
        videoNoteDuration++
      }
      if (videoNoteDuration >= 60 && isRecordingVideoNote) {
        // Auto-finish after 60s
        isRecordingVideoNote = false
      }
    }
  }

  val infinitePulse = rememberInfiniteTransition(label = "pulse")
  val recScale by infinitePulse.animateFloat(
    initialValue = 1f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "recScale"
  )

  Dialog(
    onDismissRequest = {
      if (!isRecordingVideoNote) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.92f)),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // --- TOP BAR ---
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { if (!isRecordingVideoNote) onDismiss() },
            modifier = Modifier
              .size(44.dp)
              .background(CyberDarkCard, CircleShape)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = CyberTextPrimary)
          }

          // Mode Selector Pill
          Row(
            modifier = Modifier
              .background(CyberDarkCard, RoundedCornerShape(20.dp))
              .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (mode == CameraMode.CIRCULAR_VIDEO_NOTE) NeonViolet else Color.Transparent)
                .clickable { if (!isRecordingVideoNote) mode = CameraMode.CIRCULAR_VIDEO_NOTE }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                "Кружок",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (mode == CameraMode.CIRCULAR_VIDEO_NOTE) Color.White else CyberTextMuted
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (mode == CameraMode.PHOTO_SNAP) CyberCyan else Color.Transparent)
                .clickable { if (!isRecordingVideoNote) mode = CameraMode.PHOTO_SNAP }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                "Фото",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (mode == CameraMode.PHOTO_SNAP) Color.Black else CyberTextMuted
              )
            }
          }

          // Switch Lens (Front / Back)
          IconButton(
            onClick = {
              cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                CameraSelector.DEFAULT_BACK_CAMERA
              } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
              }
            },
            modifier = Modifier
              .size(44.dp)
              .background(CyberDarkCard, CircleShape)
          ) {
            Icon(
              Icons.Default.Cameraswitch,
              contentDescription = "Сменить камеру",
              tint = CyberCyan
            )
          }
        }

        // --- CAMERA VIEWPORT ---
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          if (mode == CameraMode.CIRCULAR_VIDEO_NOTE) {
            // TELEGRAM-STYLE CIRCULAR PREVIEW
            Box(
              modifier = Modifier
                .size(280.dp)
                .border(
                  width = if (isRecordingVideoNote) 4.dp else 2.dp,
                  brush = Brush.sweepGradient(
                    listOf(NeonPink, NeonViolet, CyberCyan, NeonPink)
                  ),
                  shape = CircleShape
                )
                .padding(4.dp)
                .clip(CircleShape),
              contentAlignment = Alignment.Center
            ) {
              CameraPreview(
                cameraSelector = cameraSelector,
                onImageCaptureReady = { imageCapture = it }
              )

              // Circular Recording Timer Overlay
              if (isRecordingVideoNote) {
                Box(
                  modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .scale(recScale)
                        .background(NeonPink, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "%02d:%02d / 01:00".format(videoNoteDuration / 60, videoNoteDuration % 60),
                      fontSize = 12.sp,
                      color = Color.White,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          } else {
            // PHOTO SNAP RECTANGULAR PREVIEW
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            ) {
              CameraPreview(
                cameraSelector = cameraSelector,
                onImageCaptureReady = { imageCapture = it }
              )
            }
          }
        }

        // --- BOTTOM CONTROLS ---
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (isProcessing) {
            CircularProgressIndicator(color = CyberCyan)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Обработка медиа...", fontSize = 13.sp, color = CyberTextSecondary)
          } else {
            if (mode == CameraMode.CIRCULAR_VIDEO_NOTE) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (isRecordingVideoNote) {
                  // Cancel button
                  IconButton(
                    onClick = {
                      isRecordingVideoNote = false
                      videoNoteDuration = 0
                    },
                    modifier = Modifier
                      .size(52.dp)
                      .background(CyberDarkCard, CircleShape)
                  ) {
                    Icon(Icons.Default.Close, contentDescription = "Отмена", tint = NeonPink)
                  }

                  // Finish and Send Video Note
                  IconButton(
                    onClick = {
                      val dur = videoNoteDuration.coerceAtLeast(1)
                      isRecordingVideoNote = false
                      isProcessing = true
                      scope.launch {
                        // Take snapshot of circle for representation and notify
                        val capture = imageCapture
                        val file = File(context.cacheDir, "vid_note_${System.currentTimeMillis()}.jpg")
                        if (capture != null) {
                          val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                          capture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                              override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                isProcessing = false
                                onCapturedVideoNote(file.absolutePath, dur)
                                onDismiss()
                              }
                              override fun onError(exception: ImageCaptureException) {
                                isProcessing = false
                                onCapturedVideoNote("", dur)
                                onDismiss()
                              }
                            }
                          )
                        } else {
                          isProcessing = false
                          onCapturedVideoNote("", dur)
                          onDismiss()
                        }
                      }
                    },
                    modifier = Modifier
                      .size(72.dp)
                      .background(
                        Brush.linearGradient(listOf(NeonViolet, CyberCyan)),
                        CircleShape
                      )
                  ) {
                    Icon(
                      Icons.Default.Send,
                      contentDescription = "Отправить кружок",
                      tint = Color.Black,
                      modifier = Modifier.size(32.dp)
                    )
                  }
                } else {
                  // Start Recording Button
                  Box(
                    modifier = Modifier
                      .size(76.dp)
                      .border(4.dp, NeonViolet, CircleShape)
                      .padding(6.dp)
                      .clip(CircleShape)
                      .background(NeonPink)
                      .clickable {
                        isRecordingVideoNote = true
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Default.Videocam,
                      contentDescription = "Записать видеосообщение",
                      tint = Color.White,
                      modifier = Modifier.size(36.dp)
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                if (isRecordingVideoNote) "Идет запись круглого видео... Нажмите кнопку отправки" else "Нажмите для старта видеозаписи",
                fontSize = 12.sp,
                color = CyberTextMuted
              )
            } else {
              // PHOTO SNAP TRIGGER
              Box(
                modifier = Modifier
                  .size(76.dp)
                  .border(4.dp, CyberCyan, CircleShape)
                  .padding(6.dp)
                  .clip(CircleShape)
                  .background(Color.White)
                  .clickable {
                    val capture = imageCapture
                    if (capture != null) {
                      isProcessing = true
                      val file = File(context.cacheDir, "snap_${System.currentTimeMillis()}.jpg")
                      val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                      capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                          override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            isProcessing = false
                            onCapturedPhoto(file.absolutePath)
                            onDismiss()
                          }
                          override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraCapture", "Photo capture error", exception)
                            isProcessing = false
                          }
                        }
                      )
                    }
                  },
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .background(Color.White, CircleShape)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text("Сделать моментальное фото", fontSize = 12.sp, color = CyberTextMuted)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CameraPreview(
  cameraSelector: CameraSelector,
  onImageCaptureReady: (ImageCapture) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  AndroidView(
    factory = { ctx ->
      val previewView = PreviewView(ctx).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        scaleType = PreviewView.ScaleType.FILL_CENTER
      }

      val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
      cameraProviderFuture.addListener({
        try {
          val cameraProvider = cameraProviderFuture.get()
          val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
          }

          val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
          onImageCaptureReady(imageCapture)

          cameraProvider.unbindAll()
          cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
          )
        } catch (e: Exception) {
          Log.e("CameraPreview", "Failed to bind camera use cases", e)
        }
      }, ContextCompat.getMainExecutor(ctx))

      previewView
    },
    update = { previewView ->
      val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
      cameraProviderFuture.addListener({
        try {
          val cameraProvider = cameraProviderFuture.get()
          val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
          }
          val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
          onImageCaptureReady(imageCapture)

          cameraProvider.unbindAll()
          cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
          )
        } catch (e: Exception) {
          Log.e("CameraPreview", "Failed to update camera preview", e)
        }
      }, ContextCompat.getMainExecutor(context))
    },
    modifier = Modifier.fillMaxSize()
  )
}
