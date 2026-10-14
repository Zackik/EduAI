package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cached_materials")
data class LocalMaterialEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subject: String,
    val category: String,
    val summary: String,
    val content: String,
    val isSavedOffline: Boolean = true,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_sessions")
data class LocalSessionEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val durationMinutes: Int,
    val date: String,
    val notes: String,
    val isSynced: Boolean = false,
    val loggedAt: Long = System.currentTimeMillis()
)

@Dao
interface MaterialDao {
    @Query("SELECT * FROM cached_materials ORDER BY cachedAt DESC")
    fun getAllMaterials(): Flow<List<LocalMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materials: List<LocalMaterialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: LocalMaterialEntity)

    @Query("SELECT * FROM cached_materials WHERE id = :id LIMIT 1")
    suspend fun getMaterialById(id: String): LocalMaterialEntity?

    @Query("DELETE FROM cached_materials WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM offline_sessions ORDER BY loggedAt DESC")
    fun getAllSessions(): Flow<List<LocalSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: LocalSessionEntity)

    @Query("SELECT * FROM offline_sessions WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<LocalSessionEntity>

    @Query("UPDATE offline_sessions SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}

@Database(
    entities = [LocalMaterialEntity::class, LocalSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edu_ai_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
