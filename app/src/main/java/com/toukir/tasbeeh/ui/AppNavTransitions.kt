package com.toukir.tasbeeh.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun appNavTransitionSpec(): AnimatedContentTransitionScope<Triple<String, Int?, Int?>>.() -> ContentTransform = {
    val targetLevel = when {
        targetState.second != null -> 2
        targetState.third != null -> 1
        targetState.first == "statistics" || targetState.first == "history" -> 1
        else -> 0
    }
    val initialLevel = when {
        initialState.second != null -> 2
        initialState.third != null -> 1
        initialState.first == "statistics" || initialState.first == "history" -> 1
        else -> 0
    }

    if (targetLevel > initialLevel) {
        (slideInHorizontally { it } + fadeIn(tween(300))) togetherWith (slideOutHorizontally { -it } + fadeOut(tween(300)))
    } else if (targetLevel < initialLevel) {
        (slideInHorizontally { -it } + fadeIn(tween(300))) togetherWith (slideOutHorizontally { it } + fadeOut(tween(300)))
    } else {
        if (targetState.first != initialState.first) {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        } else {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        }
    }
}
