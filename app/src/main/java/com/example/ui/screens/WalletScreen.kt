package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TransactionRowItem
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.ArenaWarning
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun WalletScreen(
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val transactions by viewModel.transactions.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(ArenaCardBg)
          .border(BorderStroke(1.dp, ArenaBorder), CircleShape)
          .testTag("wallet_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ArenaTextPrimary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = "VIRTUAL COIN VAULT",
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Balance Display Card
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.radialGradient(
                  colors = listOf(ArenaPurple.copy(alpha = 0.25f), ArenaBgDark.copy(alpha = 0.8f))
                )
              )
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000)))
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "C",
                color = ArenaBgDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "${currentUser?.virtualCoins ?: 5} COINS",
              color = ArenaTextPrimary,
              fontSize = 34.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            )

            Text(
              text = "NON-MONETARY TOURNAMENT REWARDS",
              color = ArenaCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ArenaCardElevated)
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Minimum Account Balance: 5 Coins",
                color = ArenaTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // 2. Clear Google Play & Platform Compliance Disclosure
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = ArenaCardElevated),
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, ArenaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Safe Play",
                tint = ArenaCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "FAIR PLAY & VIRTUAL COIN POLICY",
                color = ArenaTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "• 100% Free Entry: All tournaments are free to join with no entry fees.\n" +
                "• Virtual Coins have no real-world cash value and cannot be withdrawn, redeemed for money, or transferred.\n" +
                "• No payment gateways, cash deposits, or money-staking mechanisms exist within BGMI ARENA.\n" +
                "• Virtual coins are earned purely through in-game tournament placement, fair play, and sportsmanship.",
              color = ArenaTextSecondary,
              fontSize = 12.sp,
              lineHeight = 18.sp
            )
          }
        }
      }

      // 3. Transactions List Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TRANSACTION HISTORY",
            style = MaterialTheme.typography.titleMedium,
            color = ArenaTextPrimary,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = "${transactions.size} RECORDS",
            color = ArenaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 4. Transaction Items
      if (transactions.isEmpty()) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ArenaBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = ArenaTextMuted,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No Transactions Yet",
                color = ArenaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Earn virtual coins by placing on the podium in official tournaments.",
                color = ArenaTextMuted,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(transactions, key = { it.transactionId }) { txn ->
          TransactionRowItem(transaction = txn)
        }
      }
    }
  }
}
