package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // Current Date Selection ("YYYY-MM-DD")
    private val _currentDate = MutableStateFlow(getTodayDateString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Reactive database objects
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val dailyRecord: StateFlow<DailyRecord> = _currentDate
        .flatMapLatest { date ->
            repository.getDailyRecord(date).map { it ?: DailyRecord(date = date) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyRecord(date = getTodayDateString()))

    val allDailyRecords: StateFlow<List<DailyRecord>> = repository.allDailyRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoutineItems: StateFlow<List<RoutineItem>> = repository.allRoutineItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWorkoutLogs: StateFlow<List<WorkoutLog>> = _currentDate
        .flatMapLatest { date -> repository.getWorkoutLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkoutLogs: StateFlow<List<WorkoutLog>> = repository.allWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Curated soccer motivational quotes
    val quotes = listOf(
        Quote("Success is no accident. It is hard work, perseverance, learning, studying, sacrifice and most of all, love of what you are doing.", "Pelé"),
        Quote("I start early and I stay late, day after day, year after year, it took me 17 years and 114 days to become an overnight success.", "Lionel Messi"),
        Quote("Your love makes me strong, your hate makes me unstoppable.", "Cristiano Ronaldo"),
        Quote("You have to fight to reach your dream. You have to sacrifice and work hard for it.", "Lionel Messi"),
        Quote("I once cried because I had no shoes to play soccer, but one day, I met a man who had no feet.", "Zinedine Zidane"),
        Quote("Talent without working hard is nothing.", "Cristiano Ronaldo"),
        Quote("Every time I went away I was deceiving my mother. I would say to her I was going to school but I would be out on the street playing football.", "Ronaldinho"),
        Quote("If you are first, you are first. If you are second, you are nothing.", "Bill Shankly"),
        Quote("Everything is practice.", "Pelé"),
        Quote("I don't need to show anyone anything. There is nothing to prove.", "Cristiano Ronaldo"),
        Quote("You can overcome anything, if and only if you love something enough.", "Lionel Messi"),
        Quote("To be a football elite, you must train when others sleep, and eat like a champion.", "Elite Coach")
    )

    private val _currentQuote = MutableStateFlow(quotes.first())
    val currentQuote: StateFlow<Quote> = _currentQuote.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeRoutineIfEmpty()
            updateStreak()
            rotateQuote()
        }
    }

    // Date navigation
    fun selectDate(date: String) {
        _currentDate.value = date
    }

    fun selectToday() {
        _currentDate.value = getTodayDateString()
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // Water tracker operations (Goal is 4L / 4000ml)
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val record = repository.getOrInitDailyRecord(_currentDate.value)
            val updatedWater = (record.waterMl + amountMl).coerceAtLeast(0)
            repository.saveDailyRecord(record.copy(waterMl = updatedWater))
        }
    }

    fun resetWater() {
        viewModelScope.launch {
            val record = repository.getOrInitDailyRecord(_currentDate.value)
            repository.saveDailyRecord(record.copy(waterMl = 0))
        }
    }

    // Routine checklist operations
    fun toggleRoutineItem(item: RoutineItem) {
        viewModelScope.launch {
            repository.updateRoutineItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun toggleRoutineItemReminder(item: RoutineItem) {
        viewModelScope.launch {
            repository.updateRoutineItem(item.copy(reminderEnabled = !item.reminderEnabled))
        }
    }

    // Goals operations
    fun addGoal(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addGoal(Goal(title = title))
        }
    }

    fun toggleGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isCompleted = !goal.isCompleted))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Workout logs operations
    fun logWorkout(workoutName: String, isFootball: Boolean, durationSeconds: Int) {
        viewModelScope.launch {
            repository.addWorkoutLog(
                WorkoutLog(
                    date = _currentDate.value,
                    workoutName = workoutName,
                    isFootball = isFootball,
                    durationSeconds = durationSeconds
                )
            )
            // If the user completes a workout today, we can check streak or other metrics
            updateStreak()
        }
    }

    // Sleep tracking operations
    fun saveSleepDetails(bedTime: String, wakeTime: String, sleepScore: Int) {
        viewModelScope.launch {
            val record = repository.getOrInitDailyRecord(_currentDate.value)
            
            // Calculate total sleep minutes
            val minutes = calculateSleepMinutes(bedTime, wakeTime)
            
            repository.saveDailyRecord(
                record.copy(
                    bedTime = bedTime,
                    wakeTime = wakeTime,
                    sleepMinutes = minutes,
                    sleepScore = sleepScore
                )
            )
        }
    }

    private fun calculateSleepMinutes(bedTime: String, wakeTime: String): Int {
        try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val bedDate = sdf.parse(bedTime) ?: return 480
            val wakeDate = sdf.parse(wakeTime) ?: return 480
            
            var diffMs = wakeDate.time - bedDate.time
            if (diffMs <= 0) {
                // Sleep spans over midnight
                diffMs += 24 * 60 * 60 * 1000
            }
            return (diffMs / (1000 * 60)).toInt()
        } catch (e: Exception) {
            return 480 // 8 hours default fallback
        }
    }

    // Notes persistence
    fun saveNotes(notes: String) {
        viewModelScope.launch {
            val record = repository.getOrInitDailyRecord(_currentDate.value)
            repository.saveDailyRecord(record.copy(notes = notes))
        }
    }

    // Progress metrics
    fun saveProgressMetrics(
        weight: Float,
        sprintSpeed: Float,
        pushups: Int,
        plank: Int,
        rating: Int
    ) {
        viewModelScope.launch {
            val record = repository.getOrInitDailyRecord(_currentDate.value)
            val updatedRecord = record.copy(
                weightKg = weight,
                sprintSpeedKmh = sprintSpeed,
                pushupsCount = pushups,
                plankSeconds = plank,
                footballRating = rating
            )
            repository.saveDailyRecord(updatedRecord)

            // Update rating in profile too
            val profile = repository.getProfile()
            repository.saveProfile(profile.copy(footballRating = rating))
        }
    }

    // Settings
    fun updateTheme(themeName: String) {
        viewModelScope.launch {
            val profile = repository.getProfile()
            repository.saveProfile(profile.copy(selectedTheme = themeName))
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val profile = repository.getProfile()
            repository.saveProfile(profile.copy(isDarkMode = enabled))
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val profile = repository.getProfile()
            repository.saveProfile(profile.copy(notificationsEnabled = enabled))
        }
    }

    // Streak logic
    private suspend fun updateStreak() {
        val today = getTodayDateString()
        val profile = repository.getProfile()
        
        if (profile.lastActiveDate == today) {
            return // Streak already verified for today
        }
        
        val yesterday = getYesterdayDateString()
        val isLastActiveYesterday = profile.lastActiveDate == yesterday
        
        val newStreak = if (profile.lastActiveDate.isEmpty()) {
            1
        } else if (isLastActiveYesterday) {
            profile.streak + 1
        } else {
            1 // Streak broken
        }
        
        repository.saveProfile(
            profile.copy(
                streak = newStreak,
                lastActiveDate = today
            )
        )
    }

    private fun getYesterdayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return sdf.format(cal.time)
    }

    // Rotate motivational quote
    fun rotateQuote() {
        val randomIndex = (quotes.indices).random()
        _currentQuote.value = quotes[randomIndex]
    }
}

data class Quote(val text: String, val author: String)

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
