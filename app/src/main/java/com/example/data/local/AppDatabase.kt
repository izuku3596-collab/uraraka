package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TrainingSessionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingSessionDao(): TrainingSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uravity_hero_training.db"
                ).addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialSessions(database.trainingSessionDao())
                    }
                }
            }
        }

        private suspend fun populateInitialSessions(dao: TrainingSessionDao) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            dao.insertSession(
                TrainingSessionEntity(
                    title = "Calentamiento y Flotación en Gimnasio Gamma",
                    trainingType = "Control de Gravedad",
                    dateString = "Hoy",
                    timestamp = now,
                    durationMinutes = 45,
                    heroPoints = 350,
                    rankGrade = "Rango S",
                    notes = "Práctica de despegue y aterrizaje suave con Izuku. ¡Logramos coordinar el impulso aéreo con el Full Cowling sin náuseas!",
                    partnerQuote = "¡Deku-kun, tu control aéreo es cada vez más fluido! ¡Vamos a seguir así!"
                )
            )

            dao.insertSession(
                TrainingSessionEntity(
                    title = "Reflejos de Rescate y Evasión Aérea",
                    trainingType = "Reflejos de Héroe",
                    dateString = "Ayer",
                    timestamp = now - dayMillis,
                    durationMinutes = 30,
                    heroPoints = 280,
                    rankGrade = "Rango A",
                    notes = "Simulación de rescate de escombros en caída libre. Se redujo el tiempo de toque a menos de 0.2 segundos.",
                    partnerQuote = "¡Estuviste increíble atrapando esos escombros a tiempo! ¡Un verdadero héroe!"
                )
            )

            dao.insertSession(
                TrainingSessionEntity(
                    title = "Sincronización de Comandos Vocales",
                    trainingType = "Comando de Voz",
                    dateString = "Hace 2 días",
                    timestamp = now - (dayMillis * 2),
                    durationMinutes = 20,
                    heroPoints = 200,
                    rankGrade = "Rango A",
                    notes = "Práctica del grito heroico PLUS ULTRA y comando RELEASE en sincronía táctica.",
                    partnerQuote = "¡Escuchar tu voz decidida me llena de energía positiva, Izuku!"
                )
            )
        }
    }
}
