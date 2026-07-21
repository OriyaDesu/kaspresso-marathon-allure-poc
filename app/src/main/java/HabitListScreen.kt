import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.habittracker.R
import com.example.habittracker.ui.components.EmptyHabitsState
import com.example.habittracker.ui.theme.CreamBg
import com.example.habittracker.ui.theme.Mantis
import com.example.habittracker.ui.theme.ProgressFill
import com.example.habittracker.ui.theme.SmokyAqua
import com.example.habittracker.ui.theme.StarColor
import com.example.habittracker.ui.theme.Tangerine
import com.example.habittracker.ui.viewModel.HabitViewModel
import kotlinx.coroutines.delay
const val NEW_TASK_DIALOG_TEXT_FIELD = "NEW_TASK_DIALOG_TEXT_FIELD"
@Composable
fun HabitListScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitViewModel,
    openAddDialog: Boolean,
    onDialogConsumed: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val filterCategories = listOf("Все") + categories
    var selectedCategory by remember { mutableStateOf("Все") }

    val visibleHabits = if (selectedCategory == "Все") {
        habits
    } else {
        habits.filter { it.category == selectedCategory }
    }
    val completedCount = habits.count { it.isDone }
    val totalCount = habits.size
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount

    var widthPx by remember { mutableStateOf(0) }
    var starScale by remember { mutableStateOf(1f) }
    var starRotation by remember { mutableStateOf(0f) }
    val animatedStarScale by animateFloatAsState(
        targetValue = starScale,
        animationSpec = tween(250),
        label = "star_scale"
    )
    val animatedStarRotation by animateFloatAsState(
        targetValue = starRotation,
        animationSpec = tween(300),
        label = "star_rotation"
    )
    var isAddDialogVisible by remember { mutableStateOf(false) }
    var newHabitText by remember { mutableStateOf("") }
    var selectedNewHabitCategory by remember { mutableStateOf("") }
    LaunchedEffect(categories) {
        if (selectedNewHabitCategory !in categories) {
            selectedNewHabitCategory = categories.firstOrNull().orEmpty()
        }
    }
    var isAddCategoryDialogVisible by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }

    LaunchedEffect(openAddDialog) {
        if (openAddDialog) {
            isAddDialogVisible = true
            onDialogConsumed()
        }
    }
    var previousProgress by remember { mutableStateOf(progress) }

    LaunchedEffect(progress) {
        if (progress != previousProgress) {
            starScale = 1.45f
            starRotation = 16f
            delay(160)
            starScale = 0.9f
            starRotation = -8f
            delay(120)
            starScale = 1f
            starRotation = 0f
        }

        previousProgress = progress
    }

    Column(modifier = modifier) {
        if (habits.isEmpty()) {
            EmptyHabitsState(
                modifier = Modifier.weight(1f),
                onAddClick = { isAddDialogVisible = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    ProgressCard(
                        completedCount = completedCount,
                        totalCount = totalCount,
                        progress = progress,
                        widthPx = widthPx,
                        onWidthChange = { widthPx = it },
                        animatedStarScale = animatedStarScale,
                        animatedStarRotation = animatedStarRotation
                    )
                }
                item {
                    CategoryChips(
                        categories = filterCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        onAddCategoryClick = {
                            isAddCategoryDialogVisible = true
                        }
                    )
                }

                items(
                    items = visibleHabits,
                    key = { it.id }
                ) { habit ->
                    HabitItem(
                        habit = habit,
                        onCheckedChange = { isChecked ->
                            viewModel.toggleHabit(habit.id, isChecked)
                        },
                        onDeleteClick = {
                            viewModel.deleteHabit(habit.id)
                        }
                    )
                }
            }
        }

        if (isAddDialogVisible) {
            AddHabitDialog(
                newHabitText = newHabitText,
                onTextChange = { newHabitText = it },
                categories = categories,
                selectedCategory = selectedNewHabitCategory,
                onCategorySelected = { selectedNewHabitCategory = it },
                onDismiss = {
                    isAddDialogVisible = false
                    newHabitText = ""
                },
                onAddClick = {
                    viewModel.addHabit(newHabitText, selectedNewHabitCategory)
                    selectedCategory = selectedNewHabitCategory
                    newHabitText = ""
                    isAddDialogVisible = false
                }
            )
        }
        if (isAddCategoryDialogVisible) {
            Dialog(
                onDismissRequest = {
                    isAddCategoryDialogVisible = false
                    newCategoryText = ""
                }
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("Новая категория")

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newCategoryText,
                            onValueChange = { newCategoryText = it },
                            placeholder = { Text("Например: Хобби") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isAddCategoryDialogVisible = false
                                    newCategoryText = ""
                                }
                            ) {
                                Text("Отмена")
                            }
                            Button(
                                onClick = {
                                    val category = newCategoryText.trim()
                                    viewModel.addCategory(category)
                                    selectedCategory = category
                                    selectedNewHabitCategory = category
                                    isAddCategoryDialogVisible = false
                                    newCategoryText = ""
                                },
                                enabled = newCategoryText.isNotBlank()
                            ) {
                                Text("Добавить")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    completedCount: Int,
    totalCount: Int,
    progress: Float,
    widthPx: Int,
    onWidthChange: (Int) -> Unit,
    animatedStarScale: Float,
    animatedStarRotation: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Твой прогресс",
                fontWeight = FontWeight.Bold,
                color = SmokyAqua
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$completedCount из $totalCount выполнено",
                color = SmokyAqua.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .onSizeChanged { onWidthChange(it.width) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE7F5F2))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(50))
                        .background(ProgressFill)
                )
                val safeProgress = progress.coerceIn(0f, 1f)
                Icon(
                    painter = painterResource(R.drawable.baseline_auto_awesome_24),
                    contentDescription = null,
                    tint = StarColor,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterStart)
                        .offset {
                            IntOffset(
                                x = if (widthPx > 0) {
                                    (safeProgress.coerceIn(0f, 1f) * (widthPx - 24)).toInt()
                                } else {
                                    0
                                },
                                y = -18
                            )
                        }
                        .graphicsLayer {
                            scaleX = animatedStarScale
                            scaleY = animatedStarScale
                            rotationZ = animatedStarRotation
                            alpha = 0.85f
                        }
                )
            }
        }
    }
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(category)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Mantis,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = SmokyAqua
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedCategory == category,
                    borderColor = SmokyAqua.copy(alpha = 0.25f),
                    selectedBorderColor = Mantis
                )
            )
        }
        item {
            AssistChip(
                onClick = onAddCategoryClick,
                label = { Text("+") }
            )
        }
    }
}

@Composable
private fun AddHabitDialog(
    newHabitText: String,
    onTextChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = CreamBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🌿", fontSize = 34.sp)

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Новая миссия",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmokyAqua
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedTextField(
                    value = newHabitText,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth().testTag(NEW_TASK_DIALOG_TEXT_FIELD),
                    placeholder = { Text("Например: выпить водички") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Mantis,
                        unfocusedBorderColor = SmokyAqua.copy(alpha = 0.55f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Tangerine
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Mantis,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = SmokyAqua
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SmokyAqua),
                        border = BorderStroke(1.dp, SmokyAqua.copy(alpha = 0.45f))
                    ) {
                        Text("Отмена")
                    }

                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.weight(1f),
                        enabled = newHabitText.isNotBlank() && selectedCategory.isNotBlank(),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Tangerine,
                            contentColor = Color.White,
                            disabledContainerColor = Tangerine.copy(alpha = 0.35f)
                        )
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}