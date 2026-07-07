package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // User Profile
    val userProfile: Flow<UserProfile?> = appDao.getUserProfileFlow()
    
    suspend fun getProfile(): UserProfile {
        return appDao.getUserProfile() ?: UserProfile().also {
            appDao.insertUserProfile(it)
        }
    }

    suspend fun saveProfile(profile: UserProfile) {
        appDao.insertUserProfile(profile)
    }

    // Daily Records
    fun getDailyRecord(date: String): Flow<DailyRecord?> = appDao.getDailyRecordFlow(date)
    
    val allDailyRecords: Flow<List<DailyRecord>> = appDao.getAllDailyRecordsFlow()

    suspend fun getOrInitDailyRecord(date: String): DailyRecord {
        return appDao.getDailyRecord(date) ?: DailyRecord(date = date).also {
            appDao.insertDailyRecord(it)
        }
    }

    suspend fun saveDailyRecord(record: DailyRecord) {
        appDao.insertDailyRecord(record)
    }

    // Goals
    val allGoals: Flow<List<Goal>> = appDao.getAllGoalsFlow()

    suspend fun addGoal(goal: Goal) {
        appDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        appDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        appDao.deleteGoal(goal)
    }

    // Routine Items
    val allRoutineItems: Flow<List<RoutineItem>> = appDao.getAllRoutineItemsFlow()

    suspend fun initializeRoutineIfEmpty() {
        val current = appDao.getAllRoutineItems()
        if (current.isEmpty()) {
            val defaults = listOf(
                RoutineItem("1_0600", "6:00 AM", "Wake Up"),
                RoutineItem("2_0610", "6:10 AM", "Drink Water"),
                RoutineItem("3_0620", "6:20 AM", "Stretch"),
                RoutineItem("4_0640", "6:40 AM", "Breakfast"),
                RoutineItem("5_0800", "8:00 AM", "School"),
                RoutineItem("6_1600", "4:00 PM", "Snack"),
                RoutineItem("7_1630", "4:30 PM", "Football Training"),
                RoutineItem("8_1800", "6:00 PM", "Home Workout"),
                RoutineItem("9_1900", "7:00 PM", "Stretch"),
                RoutineItem("10_1930", "7:30 PM", "Dinner"),
                RoutineItem("11_2030", "8:30 PM", "Study"),
                RoutineItem("12_2200", "10:00 PM", "Sleep")
            )
            appDao.insertRoutineItems(defaults)
        }
    }

    suspend fun updateRoutineItem(item: RoutineItem) {
        appDao.updateRoutineItem(item)
    }

    // Workout Logs
    fun getWorkoutLogsForDate(date: String): Flow<List<WorkoutLog>> = appDao.getWorkoutLogsFlow(date)
    val allWorkoutLogs: Flow<List<WorkoutLog>> = appDao.getAllWorkoutLogsFlow()

    suspend fun addWorkoutLog(log: WorkoutLog) {
        appDao.insertWorkoutLog(log)
    }
}
