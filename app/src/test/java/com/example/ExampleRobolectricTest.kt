package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AccountStatus
import com.example.data.model.TournamentMode
import com.example.data.model.TournamentStatus
import com.example.data.repository.ArenaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var repository: ArenaRepository

  @Before
  fun setup() {
    repository = ArenaRepository()
  }

  @Test
  fun `verify app name resource matches platform identity`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BGMI ARENA", appName)
  }

  @Test
  fun `verify default tournaments are loaded with rich esports metadata`() {
    val tournaments = repository.tournaments.value
    assertTrue("Tournaments list should not be empty", tournaments.isNotEmpty())
    
    val firstTournament = tournaments.first()
    assertNotNull(firstTournament.title)
    assertTrue(firstTournament.maxTeams > 0)
    assertTrue(firstTournament.rules.isNotEmpty())
  }

  @Test
  fun `verify user login and initial virtual coin balance`() {
    val result = repository.login("ahmed@example.com", "esports123")
    assertTrue(result.isSuccess)
    
    val user = repository.currentUser.value
    assertNotNull(user)
    assertEquals("Ahmed", user?.displayName)
    assertEquals("SNIPER_VIPER", user?.bgmiName)
    assertEquals(AccountStatus.ACTIVE, user?.accountStatus)
    assertEquals(5, user?.virtualCoins)
  }

  @Test
  fun `verify room credentials publish and status change`() {
    // Admin login required for room credential publishing
    val loginResult = repository.login("admin@bgmiarena.com", "admin123")
    assertTrue(loginResult.isSuccess)

    val matches = repository.matches.value
    assertTrue("Matches list should not be empty", matches.isNotEmpty())
    
    val matchId = matches.first().matchId
    val publishResult = repository.publishMatchRoom(matchId, "8829104", "BGMI99")
    assertTrue(publishResult.isSuccess)

    val updatedMatch = repository.matches.value.find { it.matchId == matchId }
    assertNotNull(updatedMatch)
    assertTrue(updatedMatch!!.isRoomPublished)
    assertEquals("8829104", updatedMatch.roomId)
    assertEquals("BGMI99", updatedMatch.roomPassword)
  }

  @Test
  fun `verify tournament registration updates team count`() {
    // Login as demo user
    repository.login("ahmed@example.com", "esports123")

    // Use solo tournament where user is not yet registered
    val openTournament = repository.tournaments.value.firstOrNull { it.id == "trn_solo_showdown" }
    assertNotNull("Should find solo tournament", openTournament)
    
    val initialTeams = openTournament!!.registeredTeams
    val regResult = repository.registerForTournament(
      tournamentId = openTournament.id,
      teamName = "Solo Sniper",
      players = listOf(
        com.example.data.model.TeamPlayer("Ahmed", "5829104729", true)
      )
    )
    assertTrue(regResult.isSuccess)

    val updated = repository.tournaments.value.find { it.id == openTournament.id }
    assertEquals(initialTeams + 1, updated?.registeredTeams)
  }
}
