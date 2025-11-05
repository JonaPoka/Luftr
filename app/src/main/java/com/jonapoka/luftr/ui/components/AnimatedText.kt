package com.jonapoka.luftr.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Animated text that fades in from left to right, line by line
 */
@Composable
fun AnimatedAIText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    delayBetweenLines: Long = 100L, // Delay between each line appearing
    enabled: Boolean = true
) {
    if (!enabled) {
        // If animation disabled, just show the text
        Text(
            text = text,
            style = style,
            modifier = modifier
        )
        return
    }
    
    val lines = remember(text) { text.split("\n") }
    
    Column(modifier = modifier) {
        lines.forEachIndexed { index, line ->
            AnimatedTextLine(
                text = line,
                style = style,
                delay = index * delayBetweenLines
            )
        }
    }
}

@Composable
private fun AnimatedTextLine(
    text: String,
    style: TextStyle,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )
    
    val offsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -30f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "offsetX"
    )
    
    Text(
        text = text,
        style = style,
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha
                translationX = offsetX
            }
            .fillMaxWidth()
    )
}

/**
 * Animated list of text items (e.g., for bullet points)
 */
@Composable
fun AnimatedAITextList(
    items: List<String>,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    delayBetweenItems: Long = 80L,
    enabled: Boolean = true
) {
    if (!enabled) {
        // If animation disabled, just show all items
        Column(modifier = modifier) {
            items.forEach { item ->
                Text(
                    text = item,
                    style = style,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
        return
    }
    
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            AnimatedTextLine(
                text = item,
                style = style,
                delay = index * delayBetweenItems
            )
            if (index < items.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * Typing animation effect for AI responses
 */
@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    typingSpeed: Long = 30L, // Milliseconds per character
    enabled: Boolean = true
) {
    if (!enabled) {
        Text(
            text = text,
            style = style,
            modifier = modifier
        )
        return
    }
    
    var visibleText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        visibleText = ""
        text.forEachIndexed { index, char ->
            delay(typingSpeed)
            visibleText = text.substring(0, index + 1)
        }
    }
    
    Text(
        text = visibleText,
        style = style,
        modifier = modifier
    )
}
