package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun TasbeehsListScreen(
    goals: List<TasbeehGoal>,
    onGoalClick: (TasbeehGoal) -> Unit,
    onEditGoal: (TasbeehGoal) -> Unit,
    onAddToGoal: (TasbeehGoal) -> Unit,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val libraryItems = remember(goals, searchQuery, language) {
        val distinctGoals = goals.distinctBy { it.name }
        if (searchQuery.isBlank()) {
            distinctGoals
        } else {
            distinctGoals.filter { goal ->
                val localizedName = AdhkarLibrary.getLocalizedName(context, goal.name)
                goal.name.contains(searchQuery, ignoreCase = true) ||
                localizedName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(modifier = modifier) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LibrarySearchBar(searchQuery = searchQuery, onQueryChange = { searchQuery = it })
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(libraryItems, key = { it.id }) { goal ->
                    val localizedName = remember(goal.name, language, context) {
                        AdhkarLibrary.getLocalizedName(context, goal.name)
                    }
                    TasbeehListCard(
                        goal = goal,
                        displayName = localizedName,
                        allGoalsForThisName = goals.filter { it.name == goal.name },
                        language = language,
                        onClick = { onGoalClick(goal) },
                        onEditClick = { onEditGoal(goal) },
                        onAddToGoal = { onAddToGoal(goal) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibrarySearchBar(searchQuery: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_adhkar)) },
        leadingIcon = { StudioIcon(StudioIcons.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true
    )
}
