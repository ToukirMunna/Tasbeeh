package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun TasbeehBottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        HorizontalDivider(thickness = 1.dp, color = colorScheme.outlineVariant)
        NavigationBar(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                selectedTextColor = colorScheme.primary,
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                indicatorColor = colorScheme.primaryContainer
            )

            TasbeehNavItem(
                icon = StudioIcons.Home,
                label = stringResource(R.string.nav_home),
                selected = currentScreen == "home",
                colors = navItemColors,
                onClick = { onNavigate("home") }
            )
            TasbeehNavItem(
                icon = StudioIcons.Checklist,
                label = stringResource(R.string.nav_tasbeehs),
                selected = currentScreen == "tasbeehs",
                colors = navItemColors,
                onClick = { onNavigate("tasbeehs") }
            )
            TasbeehNavItem(
                icon = StudioIcons.Analytics,
                label = stringResource(R.string.nav_profile),
                selected = currentScreen == "dashboard",
                colors = navItemColors,
                onClick = { onNavigate("dashboard") }
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TasbeehNavItem(
    icon: Int,
    label: String,
    selected: Boolean,
    colors: NavigationBarItemColors,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { StudioIcon(icon, contentDescription = null) },
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = selected,
        colors = colors,
        onClick = onClick
    )
}

@Composable
fun TasbeehFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        StudioIcon(StudioIcons.Add, contentDescription = stringResource(R.string.cd_add_tasbeeh))
    }
}
