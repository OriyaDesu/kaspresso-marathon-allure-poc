package tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.habittracker.MainActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.qameta.allure.kotlin.Allure.step
import org.junit.Rule
import org.junit.Test
import screen.MainScreen

class HabitTest : BaseTest() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun checkAddNewTaskIsSuccessTest() = run {
        step("Precondition") {
            onComposeScreen<MainScreen>(composeTestRule) {
                addNewTask("surf")
            }
        }

        step("Step 1. Поле <surf> отображается на экране") {
            composeTestRule
                .onNodeWithText("surf")
                .assertIsDisplayed()
        }
    }

    @Test
    fun checkFilterCheckboxIsSelectedTest() {
        step("Step 1. Выбрать фильтр") {
            onComposeScreen<MainScreen>(composeTestRule) {
                filterCheckboxCollection
                    .filterToOne(hasAnyChild(hasText("Спорт")))
                    .performClick()
                    .assertIsSelected()
            }
        }
    }

    @Test
    fun checkFilterCheckboxIsSelectedFailureTest() {
        step("Step 1. Выбрать фильтр") {
            onComposeScreen<MainScreen>(composeTestRule) {
                filterCheckboxCollection
                    .filterToOne(hasAnyChild(hasText("Нет такого")))
                    .performClick()
                    .assertIsSelected()
            }
        }
    }
}