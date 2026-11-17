package com.example.model

data class HeroProfile(
    val heroName: String = "Deku (Izuku Midoriya)",
    val partnerName: String = "Uravity (Ochako Uraraka)",
    val heroRankTitle: String = "Héroe en Entrenamiento - Clase 1-A",
    val heroLevel: Int = 14,
    val currentXp: Int = 2450,
    val xpForNextLevel: Int = 3000,
    val trainingStreakDays: Int = 8,
    val gravityControlMastery: Int = 88, // 0 - 100%
    val combatReflexesMastery: Int = 92,
    val vocalPowerMastery: Int = 85,
    val enduranceStamina: Int = 90
)

enum class CallState {
    DISCONNECTED,
    INCOMING,
    CONNECTED
}

enum class MinigameType {
    ZERO_GRAVITY_CATCH,
    HERO_REFLEXES,
    VOCAL_POWER_DRILL
}

data class FloatingDebris(
    val id: Int,
    val xFraction: Float, // 0f to 1f
    val yFraction: Float, // 0f to 1f
    val sizeDp: Int,
    val isZeroGravityActive: Boolean = true,
    val points: Int = 50,
    val emoji: String = "🪨"
)
