package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  LaunchedEffect(Unit) {
    delay(1800L) // Quick professional splash
    onComplete()
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val scale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .clickable { onComplete() }
      .testTag("splash_screen_root"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      // Glowing esports shield icon / logo
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(scale)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(ArenaCyan.copy(alpha = 0.25f), ArenaPurple.copy(alpha = 0.05f), ArenaBgDark)
            )
          )
          .border(2.dp, Brush.linearGradient(listOf(ArenaPurpleBright, ArenaCyan)), CircleShape)
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_app_icon),
          contentDescription = "BGMI ARENA Logo",
          modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(18.dp))
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "BGMI ARENA",
        style = MaterialTheme.typography.headlineLarge,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "PLAY. COMPETE. CLIMB.",
        style = MaterialTheme.typography.labelLarge,
        color = ArenaCyan,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp
      )

      Spacer(modifier = Modifier.height(48.dp))

      CircularProgressIndicator(
        color = ArenaCyan,
        trackColor = ArenaPurple.copy(alpha = 0.3f),
        strokeWidth = 3.dp,
        modifier = Modifier.size(28.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Loading Arena Servers...",
        color = ArenaTextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
