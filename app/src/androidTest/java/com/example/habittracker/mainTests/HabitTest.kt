package com.example.habittracker.mainTests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habittracker.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addHabit_displaysInList() {
        composeTestRule
            .onNodeWithTag("AddHabitButton")
            .performClick()
        composeTestRule
            .onNodeWithTag("newHabitInput")
            .performTextInput("surf")
        composeTestRule
            .onNodeWithText("Добавить")
            .performClick()
        composeTestRule
            .onNodeWithText("surf")
            .assertIsDisplayed()
    }

    @Test
    fun toggleHabit_changesState() {
        // клик по чекбоксу первой привычки
        composeTestRule
            .onNodeWithTag("habitCheckbox")
            .onChildren().printToLog("лог")
//            .filterToOne(hasText("surf"))
//            .performClick().assertIsSelected()
    }
}