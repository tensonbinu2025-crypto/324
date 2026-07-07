package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Daily Records
    @Query("SELECT * FROM daily_records WHERE date = :date LIMIT 1")
    fun getDailyRecordFlow(date: String): Flow<DailyRecord?>

    @Query("SELECT * FROM daily_records WHERE date = :date LIMIT 1")
    suspend fun getDailyRecord(date: String): DailyRecord?

    @Query("SELECT * FROM daily_records ORDER BY date ASC")
    fun getAllDailyRecordsFlow(): Flow<List<DailyRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyRecord(record: DailyRecord)

    // Goals
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoalsFlow(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    // Routine Items
    @Query("SELECT * FROM routine_items ORDER BY id ASC")
    fun getAllRoutineItemsFlow(): Flow<List<RoutineItem>>

    @Query("SELECT * FROM routine_items")
    suspend fun getAllRoutineItems(): List<RoutineItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineItem(item: RoutineItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineItems(items: List<RoutineItem>)

    @Update
    suspend fun updateRoutineItem(item: RoutineItem)

    // Workout Logs
    @Query("SELECT * FROM workout_logs WHERE date = :date")
    fun getWorkoutLogsFlow(date: String): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs ORDER BY completedAt DESC")
    fun getAllWorkoutLogsFlow(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)
}

@Database(
    entities = [
        UserProfile::class,
        DailyRecord::class,
        Goal::class,
        RoutineItem::class,
        WorkoutLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
