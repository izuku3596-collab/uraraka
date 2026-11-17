package com.example.data.remote

import androidx.annotation.Keep
import com.example.model.HeroProfile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserHeroProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val heroName: String = "Deku (Izuku Midoriya)",
    val partnerName: String = "Uravity (Ochako Uraraka)",
    val heroRankTitle: String = "Héroe en Entrenamiento - Clase 1-A",
    val heroLevel: Long = 14L,
    val currentXp: Long = 2450L,
    val xpForNextLevel: Long = 3000L,
    val trainingStreakDays: Long = 8L,
    val gravityControlMastery: Long = 88L,
    val combatReflexesMastery: Long = 92L,
    val vocalPowerMastery: Long = 85L,
    val enduranceStamina: Long = 90L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toHeroProfile(): HeroProfile = HeroProfile(
        heroName = heroName.ifBlank { "Deku (Izuku Midoriya)" },
        partnerName = partnerName.ifBlank { "Uravity (Ochako Uraraka)" },
        heroRankTitle = heroRankTitle.ifBlank { "Héroe en Entrenamiento - Clase 1-A" },
        heroLevel = heroLevel.toInt().coerceIn(1, 999),
        currentXp = currentXp.toInt().coerceAtLeast(0),
        xpForNextLevel = xpForNextLevel.toInt().coerceAtLeast(100),
        trainingStreakDays = trainingStreakDays.toInt().coerceAtLeast(0),
        gravityControlMastery = gravityControlMastery.toInt().coerceIn(0, 100),
        combatReflexesMastery = combatReflexesMastery.toInt().coerceIn(0, 100),
        vocalPowerMastery = vocalPowerMastery.toInt().coerceIn(0, 100),
        enduranceStamina = enduranceStamina.toInt().coerceIn(0, 100)
    )
}

@Keep
@IgnoreExtraProperties
data class TrainingSessionDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val trainingType: String = "",
    val dateString: String = "",
    val durationMinutes: Long = 15L,
    val heroPoints: Long = 100L,
    val rankGrade: String = "Rango A",
    val notes: String = "",
    val partnerQuote: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
