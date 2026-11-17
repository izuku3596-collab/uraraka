package com.example

import com.example.model.DialogueBank
import com.example.model.HeroProfile
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDialogueBank_hasGreetingsAndCheers() {
        assertTrue(DialogueBank.incomingCallGreetings.isNotEmpty())
        assertTrue(DialogueBank.combatCheeringLines.isNotEmpty())
        assertTrue(DialogueBank.tipsAndMotivation.isNotEmpty())

        val firstGreeting = DialogueBank.incomingCallGreetings.first()
        assertTrue(firstGreeting.text.contains("Deku-kun"))
        assertTrue(firstGreeting.suggestedReplies.isNotEmpty())
    }

    @Test
    fun testHeroProfile_initialValues() {
        val profile = HeroProfile()
        assertEquals("Deku (Izuku Midoriya)", profile.heroName)
        assertEquals("Uravity (Ochako Uraraka)", profile.partnerName)
        assertEquals(14, profile.heroLevel)
        assertTrue(profile.trainingStreakDays > 0)
        assertTrue(profile.gravityControlMastery in 0..100)
    }
}
