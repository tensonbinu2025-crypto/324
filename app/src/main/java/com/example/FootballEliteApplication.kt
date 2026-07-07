package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.AppRepository

class FootballEliteApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "football_elite_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    val repository by lazy { AppRepository(database.appDao()) }
}
