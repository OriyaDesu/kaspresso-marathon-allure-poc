package com.example.habittracker.mainTests

import NEW_TASK_DIALOG_TEXT_FIELD
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.example.habittracker.MainActivity
import com.example.habittracker.ui.components.ADD_TASK_BUTTON
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.components.composesupport.config.addComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.qameta.allure.kotlin.Allure.step
import org.junit.Rule
import org.junit.Test

class HabitTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withForcedAllureSupport(shouldRecordVideo = false).addComposeSupport()) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addHabit_displaydInList() = run {
        step("Step 1. Нажать на кнопку <Добавить дело>") {
            composeTestRule
                .onNodeWithTag(ADD_TASK_BUTTON)
                .assertIsDisplayed()
                .performClick()
        }

        step("Step 2. Ввести <surf> в поле ввода") {
            composeTestRule
                .onNodeWithTag(NEW_TASK_DIALOG_TEXT_FIELD)
                .assertIsDisplayed()
                .performTextInput("surf")
        }

        step("Step 3. Нажать на кнопку <Добавить>") {
            composeTestRule
                .onNodeWithText("Добавить")
                .performClick()
        }

        step("Step 4. Поле <surf> отображается на экране") {
            composeTestRule
                .onNodeWithText("surf")
                .assertIsDisplayed()
        }
    }

    @Test
    fun toggleHabit_changesState() {
        // клик по чекбоксу первой привычки
        step("Step 1. Выбрать фильтр") {
            composeTestRule
                .onNodeWithTag("habitCheckbox")
                .onChildren().printToLog("лог")
        }
//            .filterToOne(hasText("surf"))
//            .performClick().assertIsSelected()
    }
}