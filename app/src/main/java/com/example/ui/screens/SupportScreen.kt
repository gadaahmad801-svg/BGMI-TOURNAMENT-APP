package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaDanger
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaSuccess
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupportScreen(
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val tickets by viewModel.supportTickets.collectAsState()

  var selectedCategory by remember { mutableStateOf("Match Dispute") }
  var subject by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }
  var formError by remember { mutableStateOf<String?>(null) }

  val categories = listOf("Match Dispute", "Room Issue", "Scoring / Points", "Bug Report", "General")

  val faqs = remember {
    listOf(
      Pair(
        "How do I register for a tournament?",
        "Select any tournament with 'REGISTRATION OPEN' on the Home screen. Fill in your squad details and BGMI character UIDs. All registrations are 100% free."
      ),
      Pair(
        "Where and when are Room ID and Password revealed?",
        "Head to the 'Matches' tab and tap your match. Room ID and Password are published exactly 15 minutes before the scheduled start time."
      ),
      Pair(
        "How is scoring calculated for the Leaderboard?",
        "Scores are calculated using official BGMI competitive placement points plus 1 kill point per elimination. Match winners get maximum placement points."
      ),
      Pair(
        "What are Virtual Coins?",
        "Virtual Coins are non-monetary tournament rewards and tokens of recognition. They have no real cash value and cannot be withdrawn or traded for real currency."
      )
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(ArenaCardBg)
          .border(BorderStroke(1.dp, ArenaBorder), CircleShape)
          .testTag("support_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ArenaTextPrimary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = "HELP & SUPPORT",
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // FAQs Accordion
    Text(
      text = "FREQUENTLY ASKED QUESTIONS",
      color = ArenaTextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      faqs.forEach { (question, answer) ->
        FaqItem(question = question, answer = answer)
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Submit Support Ticket Form
    Text(
      text = "SUBMIT A SUPPORT TICKET",
      color = ArenaTextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, ArenaBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        if (formError != null) {
          Text(formError!!, color = ArenaDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
        }

        Text("Select Issue Category", color = ArenaTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        // Category Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.take(3).forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) ArenaCyan else ArenaCardElevated)
                .clickable { selectedCategory = cat }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                color = if (isSelected) ArenaBgDark else ArenaTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = subject,
          onValueChange = { subject = it },
          label = { Text("Subject") },
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = message,
          onValueChange = { message = it },
          label = { Text("Describe the issue in detail...") },
          colors = arenaTextFieldColors(),
          minLines = 3,
          maxLines = 5,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            if (subject.isBlank() || message.isBlank()) {
              formError = "Please enter subject and message."
              return@Button
            }
            formError = null
            viewModel.submitSupportTicket(
              category = selectedCategory,
              subject = subject,
              message = message,
              onSuccess = {
                subject = ""
                message = ""
              },
              onError = { formError = it }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(ArenaPurple, ArenaCyan)))
            .testTag("submit_support_ticket_button")
        ) {
          Text("SUBMIT TICKET", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Submitted Tickets History
    if (tickets.isNotEmpty()) {
      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "YOUR SUPPORT TICKETS",
        color = ArenaTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tickets.forEach { ticket ->
          val dateFormatted = remember(ticket.createdAt) {
            SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(ticket.createdAt))
          }

          Card(
            colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ArenaBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = ticket.ticketId,
                  color = ArenaCyan,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )

                val statusColor = if (ticket.status == com.example.data.model.TicketStatus.RESOLVED) ArenaSuccess else ArenaPurple
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(ticket.status.name, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(ticket.subject, color = ArenaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              Text(ticket.message, color = ArenaTextSecondary, fontSize = 11.sp, maxLines = 2)

              Spacer(modifier = Modifier.height(6.dp))
              Text(dateFormatted, color = ArenaTextMuted, fontSize = 10.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun FaqItem(
  question: String,
  answer: String
) {
  var expanded by remember { mutableStateOf(false) }

  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { expanded = !expanded }
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = question,
          color = ArenaTextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.weight(1f)
        )
        Icon(
          imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          tint = ArenaCyan,
          modifier = Modifier.size(20.dp)
        )
      }

      AnimatedVisibility(visible = expanded) {
        Column {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = answer,
            color = ArenaTextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }
      }
    }
  }
}
