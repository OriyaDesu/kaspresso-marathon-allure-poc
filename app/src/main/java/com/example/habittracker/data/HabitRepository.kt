package com.example.habittracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.habittracker.domain.Habit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "habit_storage")

class HabitRepository(
    private val context: Context
) {
    private val gson = Gson()

    private val habitsKey = stringPreferencesKey("habits")

    fun getHabits(): Flow<List<Habit>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[habitsKey]

            if (json.isNullOrBlank()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson(json, type)
            }
        }
    }

    suspend fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)

        context.dataStore.edit { preferences ->
            preferences[habitsKey] = json
        }
    }
}