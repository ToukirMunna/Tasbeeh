package com.toukir.tasbeeh.ui

import com.toukir.tasbeeh.ui.dialogs.*

import androidx.compose.runtime.Composable
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.TasbeehGoal

@Composable
fun GoalDialogHost(
    savedGoals: List<TasbeehGoal>,
    currentScreen: String,
    showAddGoalDialog: Boolean,
    onDismissAddGoal: () -> Unit,
    tasbeehToAddToGoal: TasbeehGoal?,
    onDismissAddToGoal: () -> Unit,
    goalToEdit: TasbeehGoal?,
    onDismissEditGoal: () -> Unit,
    managingGoalsType: Boolean?,
    onDismissManageGoals: () -> Unit,
    viewModel: MainViewModel
) {
    if (showAddGoalDialog) {
        AddGoalDialogContainer(
            savedGoals = savedGoals,
            viewModel = viewModel,
            onDismiss = onDismissAddGoal
        )
    }

    if (tasbeehToAddToGoal != null) {
        AddToGoalDialogContainer(
            tasbeeh = tasbeehToAddToGoal,
            savedGoals = savedGoals,
            viewModel = viewModel,
            onDismiss = onDismissAddToGoal
        )
    }

    if (goalToEdit != null) {
        EditGoalDialogContainer(
            goal = goalToEdit,
            currentScreen = currentScreen,
            savedGoals = savedGoals,
            viewModel = viewModel,
            onDismiss = onDismissEditGoal
        )
    }

    if (managingGoalsType != null && currentScreen == "home") {
        ManageGoalsDialogContainer(
            managingGoalsType = managingGoalsType,
            savedGoals = savedGoals,
            viewModel = viewModel,
            onDismiss = onDismissManageGoals
        )
    }
}

@Composable
private fun AddGoalDialogContainer(
    savedGoals: List<TasbeehGoal>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    AddGoalDialog(
        onDismiss = onDismiss,
        onAddNew = { name ->
            val currentList = savedGoals.toMutableList()
            val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
            currentList.add(TasbeehGoal(newId, name, 100, 0, 0, isGoal = false, duration = GoalDuration.DAILY))
            viewModel.saveGoals(currentList)
            onDismiss()
        }
    )
}

@Composable
private fun AddToGoalDialogContainer(
    tasbeeh: TasbeehGoal,
    savedGoals: List<TasbeehGoal>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    AddToGoalDialog(
        tasbeehName = tasbeeh.name,
        onDismiss = onDismiss,
        onConfirm = { duration, target ->
            val currentList = savedGoals.toMutableList()
            val existingIndex = currentList.indexOfFirst { it.name == tasbeeh.name && it.duration == duration && it.isGoal }
            if (existingIndex != -1) {
                currentList[existingIndex] = currentList[existingIndex].copy(targetCount = target)
            } else {
                val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
                currentList.add(
                    TasbeehGoal(
                        id = newId,
                        name = tasbeeh.name,
                        targetCount = target,
                        currentCount = tasbeeh.currentCount,
                        dailyCount = tasbeeh.dailyCount,
                        totalCount = tasbeeh.totalCount,
                        isGoal = true,
                        duration = duration,
                        lastResetDate = System.currentTimeMillis()
                    )
                )
            }
            viewModel.saveGoals(currentList)
            onDismiss()
        }
    )
}

@Composable
private fun EditGoalDialogContainer(
    goal: TasbeehGoal,
    currentScreen: String,
    savedGoals: List<TasbeehGoal>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    EditGoalDialog(
        goal = goal,
        isHomeSource = currentScreen == "home",
        onDismiss = onDismiss,
        onDelete = {
            val currentList = savedGoals.toMutableList()
            val index = currentList.indexOfFirst { it.id == goal.id }
            if (index != -1) {
                if (currentScreen == "home") {
                    currentList[index] = currentList[index].copy(isGoal = false)
                } else {
                    currentList.removeAt(index)
                }
                viewModel.saveGoals(currentList)
            }
            onDismiss()
        },
        onRename = { newName ->
            val currentList = savedGoals.toMutableList()
            val index = currentList.indexOfFirst { it.id == goal.id }
            if (index != -1) {
                currentList[index] = currentList[index].copy(name = newName)
                viewModel.saveGoals(currentList)
            }
            onDismiss()
        }
    )
}

@Composable
private fun ManageGoalsDialogContainer(
    managingGoalsType: Boolean,
    savedGoals: List<TasbeehGoal>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val isCustom = managingGoalsType
    val filteredGoals = if (isCustom) {
        savedGoals.filter { it.isGoal && it.duration != GoalDuration.DAILY }
    } else {
        savedGoals.filter { it.isGoal && it.duration == GoalDuration.DAILY }
    }

    ManageGoalsDialog(
        goals = filteredGoals,
        onDismiss = onDismiss,
        onUpdateGoals = { updatedFilteredGoals ->
            val originalFilteredIds = filteredGoals.map { it.id }.toSet()
            val newFilteredIds = updatedFilteredGoals.map { it.id }.toSet()
            val removedIds = originalFilteredIds - newFilteredIds

            val finalGoals = ArrayList<TasbeehGoal>()
            finalGoals.addAll(updatedFilteredGoals)

            savedGoals.forEach { goal ->
                if (goal.id in removedIds) {
                    finalGoals.add(goal.copy(isGoal = false))
                } else if (goal.id !in originalFilteredIds) {
                    finalGoals.add(goal)
                }
            }
            viewModel.saveGoals(finalGoals)
            onDismiss()
        }
    )
}
