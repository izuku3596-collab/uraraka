package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.model.HeroProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class HeroTrainingRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUid: String? = null): String {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            throw IllegalStateException("El usuario debe iniciar sesión con Google antes de acceder a Firestore.")
        }
        if (expectedUid != null && uid != expectedUid) {
            throw IllegalStateException("La sesión activa cambió; operación cancelada.")
        }
        return uid
    }

    fun observeHeroProfile(userId: String): Flow<HeroProfile> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        UserHeroProfileDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )?.toHeroProfile() ?: HeroProfile()
                } else {
                    HeroProfile()
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                if (auth.currentUser?.uid == userId) {
                    handleFirestoreError(firestoreError ?: error, OperationType.GET, path)
                }
                throw error
            }
    }

    fun observeTrainingSessions(userId: String): Flow<List<TrainingSessionDoc>> {
        val path = "users/$userId/training_sessions"
        return db.collection("users").document(userId)
            .collection("training_sessions")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    TrainingSessionDoc::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                if (auth.currentUser?.uid == userId) {
                    handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun ensureInitialHeroData(userId: String, displayName: String?): Result<Unit> {
        val userDocPath = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            val userRef = db.collection("users").document(uid)
            val existingProfile = userRef.get().await()
            if (!existingProfile.exists()) {
                val safeHeroName = displayName
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { "$it (Deku)" }
                    ?.take(80)
                    ?: "Deku (Izuku Midoriya)"

                val profilePayload = mapOf(
                    "userId" to uid,
                    "heroName" to safeHeroName,
                    "partnerName" to "Uravity (Ochako Uraraka)",
                    "heroRankTitle" to "Héroe en Entrenamiento - Clase 1-A",
                    "heroLevel" to 14L,
                    "currentXp" to 2450L,
                    "xpForNextLevel" to 3000L,
                    "trainingStreakDays" to 8L,
                    "gravityControlMastery" to 88L,
                    "combatReflexesMastery" to 92L,
                    "vocalPowerMastery" to 85L,
                    "enduranceStamina" to 90L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userRef.set(profilePayload).await()

                val sessionsRef = userRef.collection("training_sessions")
                val existingSessions = sessionsRef.limit(1).get().await()
                if (existingSessions.isEmpty) {
                    val starterSessions = listOf(
                        mapOf(
                            "userId" to uid,
                            "title" to "Control de Gravedad Cero",
                            "trainingType" to "Quirk Sync",
                            "dateString" to "Hoy, 09:30",
                            "durationMinutes" to 45L,
                            "heroPoints" to 350L,
                            "rankGrade" to "Rango S",
                            "notes" to "Práctica de evasión y manejo de peso flotante en el Gimnasio Gamma.",
                            "partnerQuote" to "¡Lo lograste, Deku-kun! Tu control del aire mejora cada día.",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        ),
                        mapOf(
                            "userId" to uid,
                            "title" to "Reflejos Full Cowling 20%",
                            "trainingType" to "Combate Conjunto",
                            "dateString" to "Ayer, 17:15",
                            "durationMinutes" to 30L,
                            "heroPoints" to 280L,
                            "rankGrade" to "Rango A",
                            "notes" to "Sesión de reflejos rápidos combinando Shoot Style con Zero Gravity.",
                            "partnerQuote" to "¡Vamos a dar lo mejor de nosotros! ¡Confío en ti!",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        ),
                        mapOf(
                            "userId" to uid,
                            "title" to "Entrenamiento de Voz Heroica",
                            "trainingType" to "Resistencia Vocal",
                            "dateString" to "Hace 2 días",
                            "durationMinutes" to 15L,
                            "heroPoints" to 200L,
                            "rankGrade" to "Rango A",
                            "notes" to "Gritos de motivación y comandos de rescate en entornos ruidosos.",
                            "partnerQuote" to "¡Tu voz suena como la de un verdadero Símbolo de la Paz!",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    starterSessions.forEach { payload ->
                        sessionsRef.document().set(payload).await()
                    }
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userDocPath)
            Result.failure(e)
        }
    }

    suspend fun addTrainingSession(
        userId: String,
        title: String,
        trainingType: String,
        dateString: String,
        durationMinutes: Int,
        heroPoints: Int,
        rankGrade: String,
        notes: String,
        partnerQuote: String
    ): Result<String> {
        var docPath = "users/$userId/training_sessions"
        return try {
            val uid = requireUserId(userId)
            val safeTitle = title.trim().ifBlank { "Entrenamiento Heroico" }.take(120)
            val safeType = trainingType.trim().ifBlank { "Entrenamiento" }.take(80)
            val safeDate = dateString.trim().ifBlank { "Hoy" }.take(60)
            val safeDuration = durationMinutes.coerceIn(1, 1440).toLong()
            val safePoints = heroPoints.coerceIn(0, 100000).toLong()
            val safeRank = if (rankGrade in setOf("Rango S", "Rango A", "Rango B", "Rango C")) {
                rankGrade
            } else {
                "Rango B"
            }
            val safeNotes = notes.trim().take(500)
            val safeQuote = partnerQuote.trim().take(400)

            val docRef = db.collection("users").document(uid)
                .collection("training_sessions")
                .document()
            docPath = docRef.path

            val payload = mapOf(
                "userId" to uid,
                "title" to safeTitle,
                "trainingType" to safeType,
                "dateString" to safeDate,
                "durationMinutes" to safeDuration,
                "heroPoints" to safePoints,
                "rankGrade" to safeRank,
                "notes" to safeNotes,
                "partnerQuote" to safeQuote,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteTrainingSession(userId: String, sessionId: String): Result<Unit> {
        val docPath = "users/$userId/training_sessions/$sessionId"
        return try {
            val uid = requireUserId(userId)
            require(sessionId.isNotBlank()) { "El ID de sesión no puede estar vacío." }
            db.collection("users").document(uid)
                .collection("training_sessions").document(sessionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateHeroProgress(userId: String, profile: HeroProfile): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            db.collection("users").document(uid)
                .update(
                    mapOf(
                        "heroLevel" to profile.heroLevel.coerceIn(1, 999).toLong(),
                        "currentXp" to profile.currentXp.coerceIn(0, 1000000).toLong(),
                        "xpForNextLevel" to profile.xpForNextLevel.coerceIn(100, 10000000).toLong(),
                        "trainingStreakDays" to profile.trainingStreakDays.coerceIn(0, 10000).toLong(),
                        "gravityControlMastery" to profile.gravityControlMastery.coerceIn(0, 100).toLong(),
                        "combatReflexesMastery" to profile.combatReflexesMastery.coerceIn(0, 100).toLong(),
                        "vocalPowerMastery" to profile.vocalPowerMastery.coerceIn(0, 100).toLong(),
                        "enduranceStamina" to profile.enduranceStamina.coerceIn(0, 100).toLong(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }
}
