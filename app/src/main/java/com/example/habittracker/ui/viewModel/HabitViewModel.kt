package com.example.habittracker.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.habittracker.domain.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.habittracker.data.HabitRepository
class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {
    private val _categories = MutableStateFlow(
        listOf("Спорт", "Здоровье", "Учёба", "Работа", "Без категории")
    )

    val categories: StateFlow<List<String>> = _categories
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    init {
        viewModelScope.launch {
            repository.getHabits().collect {
                _habits.value = it
            }
        }
    }

    fun addHabit(title: String, category: String) {
        if (title.isBlank()) return
        val newHabit = Habit(
            id = (_habits.value.maxOfOrNull { it.id } ?: 0) + 1,
            title = title.trim(),
            isDone = false,
            category = category
        )
        val updated = _habits.value + newHabit
        _habits.value = updated
        save(updated)
    }

    fun addCategory(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        if (_categories.value.contains(trimmedName)) return

        _categories.value = _categories.value + trimmedName
    }

    fun deleteCategory(name: String) {
        _categories.value = _categories.value.filter { it != name }

        val updatedHabits = _habits.value.map { habit ->
            if (habit.category == name) {
                habit.copy(category = "Без категории")
            } else {
                habit
            }
        }

        _habits.value = updatedHabits
        save(updatedHabits)
    }

    fun toggleHabit(id: Int, isDone: Boolean) {
        val updated = _habits.value.map {
            if (it.id == id) it.copy(isDone = isDone) else it
        }
        _habits.value = updated
        save(updated)
    }

    fun deleteHabit(id: Int) {
        val updated = _habits.value.filter { it.id != id }
        _habits.value = updated
        save(updated)
    }

    private fun save(habits: List<Habit>) {
        viewModelScope.launch {
            repository.saveHabits(habits)
        }
    }
}