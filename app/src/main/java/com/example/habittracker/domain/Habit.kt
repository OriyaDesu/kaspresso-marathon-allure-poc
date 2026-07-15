package com.example.habittracker.domain

data class Habit(
    val id: Int,
    val title: String,
    val isDone: Boolean,
    val category: String
)