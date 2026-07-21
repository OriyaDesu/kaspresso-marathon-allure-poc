package screen.component

import ADD_CATEGORY_CHIP
import FILTER_CHIP
import FILTER_CHIPS_LAZY_ROW
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode

class CategoryChips(private val provider: SemanticsNodeInteractionsProvider) {
    val lazyRow: SemanticsNodeInteraction = provider.onNodeWithTag(FILTER_CHIPS_LAZY_ROW, true)
    val all get() = categoryChip("Все")
    val sport get() = categoryChip("Спорт")
    val health get() = categoryChip("Здоровье")
    val study get() = categoryChip("Учёба")
    val work get() = categoryChip("Работа")
    val addChip: SemanticsNodeInteraction
        get() {
            lazyRow.performScrollToNode(hasTestTag(ADD_CATEGORY_CHIP))
            return provider.onNodeWithTag(ADD_CATEGORY_CHIP)
        }

    fun custom(categoryName: String): SemanticsNodeInteraction = categoryChip(categoryName)

    private fun categoryChip(categoryName: String): SemanticsNodeInteraction {
        val chip = provider.onNodeWithTag("$FILTER_CHIP-$categoryName")
        if (chip.isNotDisplayed()) {
            lazyRow.performScrollToNode(hasText(categoryName))
        }
        return chip
    }
}