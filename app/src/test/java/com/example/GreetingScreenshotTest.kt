package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Tournament
import com.example.data.model.TournamentMode
import com.example.data.model.TournamentStatus
import com.example.ui.components.TournamentCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tournament_card_screenshot() {
    val sampleTournament = Tournament(
      id = "tourney-sample",
      title = "BGMI BATTLE ROYALE SHOWDOWN",
      description = "Epic squad battleground championship with official competitive rules.",
      mode = TournamentMode.SQUAD,
      map = "Erangel",
      status = TournamentStatus.REGISTRATION_OPEN,
      maxTeams = 25,
      registeredTeams = 18,
      startAt = System.currentTimeMillis() + 3600000L,
      registrationDeadline = System.currentTimeMillis() + 1800000L,
      rules = listOf("Fair play strictly enforced", "No emulators"),
      rewardCoins = 150
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        TournamentCard(
          tournament = sampleTournament,
          onClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/tournament_card.png")
  }
}
