package tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.habittracker.MainActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.qameta.allure.kotlin.Allure.step
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import screen.MainScreen

class HabitTest : BaseTest() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun ensureSurfTaskExists() {
        step("Precondition: задача <Surf> существует") {
            val surfExists = composeTestRule
                .onAllNodesWithText("Surf")
                .fetchSemanticsNodes()
                .isNotEmpty()

            if (!surfExists) {
                step("Precondition") {
                    onComposeScreen<MainScreen>(composeTestRule) {
                        addNewTask("Surf")
                    }
                }
            }
        }
    }

    @Test
    fun checkAddNewTaskIsSuccessTest() = run {
        step("Step 1. Поле <Surf> отображается на экране") {
            composeTestRule
                .onNodeWithText("Surf")
                .assertIsDisplayed()
        }
    }

    @Test
    fun checkFilterCheckboxIsSelectedTest() {
        step("Step 1. Выбранная категория IsSelected") {
            onComposeScreen<MainScreen>(composeTestRule) {
                categoryChip.sport.performClick().assertIsSelected()
            }
        }
    }

    @Test
    fun checkAddNewCategoryTest() {
        step("Step 1. Создать новую категорию") {
            onComposeScreen<MainScreen>(composeTestRule) {
                addNewCategory("Coffee")
            }
        }

        step("Step 2. Новая категория отображается на экране") {
            onComposeScreen<MainScreen>(composeTestRule) {
                categoryChip.custom("Coffee").assertIsDisplayed()
            }
        }
    }

}
