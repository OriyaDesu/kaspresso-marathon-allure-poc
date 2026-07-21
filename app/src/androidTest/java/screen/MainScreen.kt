package screen

import FILTER_CHECKBOX
import NEW_TASK_DIALOG_TEXT_FIELD
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.habittracker.ui.components.ADD_TASK_BUTTON
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.qameta.allure.kotlin.Allure.step

class MainScreen(
    private val provider: SemanticsNodeInteractionsProvider
) : ComposeScreen<MainScreen>(
    semanticsProvider = provider
) {

    val addTaskButton: SemanticsNodeInteraction = provider.onNodeWithTag(ADD_TASK_BUTTON)
    val newTaskTextField: SemanticsNodeInteraction =
        provider.onNodeWithTag(NEW_TASK_DIALOG_TEXT_FIELD)
    val addButton: SemanticsNodeInteraction = provider.onNodeWithText("Добавить")
    val filterCheckboxCollection: SemanticsNodeInteractionCollection = provider.onAllNodesWithTag(FILTER_CHECKBOX)

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

        step("Задача <$taskName> отображается на экране") {
            provider
                .onNodeWithText(taskName)
                .assertIsDisplayed()
        }
    }
}