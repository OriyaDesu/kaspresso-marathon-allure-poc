package screen

import NEW_CATEGORY_CHIP_TEXT_FIELD
import NEW_TASK_DIALOG_TEXT_FIELD
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.habittracker.ui.components.ADD_TASK_BUTTON
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.qameta.allure.kotlin.Allure.step
import screen.component.CategoryChips

class MainScreen(
    private val provider: SemanticsNodeInteractionsProvider
) : ComposeScreen<MainScreen>(
    semanticsProvider = provider
) {

    val addTaskButton: SemanticsNodeInteraction = provider.onNodeWithTag(ADD_TASK_BUTTON)
    val newTaskTextField: SemanticsNodeInteraction =
        provider.onNodeWithTag(NEW_TASK_DIALOG_TEXT_FIELD)
    val newCategoryChipTextField: SemanticsNodeInteraction =
        provider.onNodeWithTag(NEW_CATEGORY_CHIP_TEXT_FIELD)
    val addButton: SemanticsNodeInteraction = provider.onNodeWithText("Добавить")

    /**
     * Components
     */
    val categoryChip = CategoryChips(provider)

    fun addNewTask(taskName: String) {
        step("Нажать на кнопку <Добавить дело>") {
            addTaskButton
                .assertIsDisplayed()
                .performClick()
        }

        step("Ввести <$taskName> в поле ввода") {
            newTaskTextField
                .assertIsDisplayed()
                .performTextInput(taskName)
        }

        step("Нажать на кнопку <Добавить>") {
            addButton
                .assertIsDisplayed()
                .performClick()
        }
    }

    fun addNewCategory(categoryName: String) {
        step("Нажать на кнопку <+>") {
            categoryChip.addChip.assertIsDisplayed().performClick()
        }

        step("Ввести <$categoryName> в поле ввода") {
            newCategoryChipTextField
                .assertIsDisplayed()
                .performTextInput(categoryName)
        }

        step("Нажать на кнопку <Добавить>") {
            addButton
                .assertIsDisplayed()
                .performClick()
        }
    }
}