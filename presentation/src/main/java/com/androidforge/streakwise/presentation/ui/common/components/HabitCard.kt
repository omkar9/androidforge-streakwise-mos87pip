package com.androidforge.streakwise.presentation.ui.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.streakwise.R
import com.androidforge.streakwise.domain.model.HabitWithCompletionStatus
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    habitWithStatus: HabitWithCompletionStatus,
    onToggleComplete: (Long, LocalDate, Boolean) -> Unit,
    onCardClick: (Long) -> Unit,
    onCardLongPress: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = habitWithStatus.habit
    val isCompletedToday = habitWithStatus.isCompletedToday
    val currentStreak = habitWithStatus.currentStreak

    val cardBackgroundColor by animateColorAsState(
        targetValue = if (isCompletedToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "habitCardBackgroundColor"
    )

    val checkIconScale by animateFloatAsState(
        targetValue = if (isCompletedToday) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "checkIconScale"
    )

    val checkIconColor by animateColorAsState(
        targetValue = if (isCompletedToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "checkIconColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onCardClick(habit.id) },
                onLongClick = { onCardLongPress(habit.id) }
            )
            .semantics { role = Role.Button },
        shape = MaterialTheme.shapes.large, // 20dp rounded
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (habit.description.isNotBlank()) habit.description else stringResource(R.string.no_description_provided),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.streak_info),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.current_streak_label, currentStreak),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            IconButton(
                onClick = { onToggleComplete(habit.id, LocalDate.now(), !isCompletedToday) },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .semantics { contentDescription = if (isCompletedToday) stringResource(R.string.mark_habit_incomplete) else stringResource(R.string.mark_habit_complete) }
            ) {
                Icon(
                    imageVector = if (isCompletedToday) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = checkIconColor,
                    modifier = Modifier.scale(checkIconScale)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    StreakWiseTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitCard(
                habitWithStatus = HabitWithCompletionStatus(
                    habit = com.androidforge.streakwise.domain.model.Habit(
                        id = 1L,
                        name = "Drink Water",
                        description = "Drink 8 glasses of water every day",
                        frequencyType = com.androidforge.streakwise.domain.model.Habit.FrequencyType.DAILY,
                        frequencyValue = listOf(),
                        reminderTime = null,
                        creationDate = LocalDate.now(),
                        archived = false
                    ),
                    isCompletedToday = true,
                    currentStreak = 15,
                    longestStreak = 20
                ),
                onToggleComplete = { _, _, _ -> },
                onCardClick = { _ -> },
                onCardLongPress = { _ -> }
            )
            HabitCard(
                habitWithStatus = HabitWithCompletionStatus(
                    habit = com.androidforge.streakwise.domain.model.Habit(
                        id = 2L,
                        name = "Workout",
                        description = "30 minutes of strength training",
                        frequencyType = com.androidforge.streakwise.domain.model.Habit.FrequencyType.WEEKLY,
                        frequencyValue = listOf(DayOfWeek.MONDAY.value, DayOfWeek.WEDNESDAY.value, DayOfWeek.FRIDAY.value),
                        reminderTime = null,
                        creationDate = LocalDate.now(),
                        archived = false
                    ),
                    isCompletedToday = false,
                    currentStreak = 0,
                    longestStreak = 5
                ),
                onToggleComplete = { _, _, _ -> },
                onCardClick = { _ -> },
                onCardLongPress = { _ -> }
            )
            HabitCard(
                habitWithStatus = HabitWithCompletionStatus(
                    habit = com.androidforge.streakwise.domain.model.Habit(
                        id = 3L,
                        name = "Read Book",
                        description = "",
                        frequencyType = com.androidforge.streakwise.domain.model.Habit.FrequencyType.DAILY,
                        frequencyValue = listOf(),
                        reminderTime = null,
                        creationDate = LocalDate.now(),
                        archived = false
                    ),
                    isCompletedToday = false,
                    currentStreak = 7,
                    longestStreak = 10
                ),
                onToggleComplete = { _, _, _ -> },
                onCardClick = { _ -> },
                onCardLongPress = { _ -> }
            )
        }
    }
}