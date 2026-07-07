package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val streak: Int = 1,
    val lastActiveDate: String = "", // "YYYY-MM-DD"
    val isDarkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val selectedTheme: String = "Grass green",
    val footballRating: Int = 75
)

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val waterMl: Int = 0,
    val sleepMinutes: Int = 480, // 8 hours default
    val bedTime: String = "22:00",
    val wakeTime: String = "06:00",
    val sleepScore: Int = 85,
    val weightKg: Float = 70f,
    val sprintSpeedKmh: Float = 25f,
    val pushupsCount: Int = 20,
    val plankSeconds: Int = 60,
    val footballRating: Int = 75,
    val notes: String = ""
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "routine_items")
data class RoutineItem(
    @PrimaryKey val id: String, // "6:00 AM - Wake Up", etc.
    val time: String,
    val task: String,
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = false
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // "YYYY-MM-DD"
    val workoutName: String,
    val isFootball: Boolean, // true = Football drill, false = Home Workout
    val durationSeconds: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)
