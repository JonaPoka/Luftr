package com.jonapoka.luftr.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonapoka.luftr.data.AIWorkoutGenerator
import com.jonapoka.luftr.data.ExerciseGuidance
import kotlinx.coroutines.launch

@Composable
fun ExerciseGuidanceCard(
    exerciseName: String,
    goal: String,
    modifier: Modifier = Modifier
) {
    var showFormTips by remember { mutableStateOf(false) }
    var showWhy by remember { mutableStateOf(false) }
    var showMistakes by remember { mutableStateOf(false) }
    var guidance by remember { mutableStateOf<ExerciseGuidance?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(exerciseName, goal) {
        isLoading = true
        guidance = AIWorkoutGenerator.getExerciseGuidance(exerciseName, goal)
        isLoading = false
    }
    
    if (isLoading || guidance == null) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        return
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "💪 Personal Trainer Tips",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Form Tips Section
            GuidanceSection(
                icon = Icons.Default.CheckCircle,
                title = "Form Tips",
                expanded = showFormTips,
                onToggle = { showFormTips = !showFormTips }
            ) {
                AnimatedAITextList(
                    items = guidance!!.formTips,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    delayBetweenItems = 80L
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Why Section
            GuidanceSection(
                icon = Icons.Default.Psychology,
                title = "Why This Exercise?",
                expanded = showWhy,
                onToggle = { showWhy = !showWhy }
            ) {
                AnimatedAIText(
                    text = guidance!!.why,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    delayBetweenLines = 100L
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Common Mistakes Section
            GuidanceSection(
                icon = Icons.Default.Warning,
                title = "Avoid These Mistakes",
                expanded = showMistakes,
                onToggle = { showMistakes = !showMistakes }
            ) {
                AnimatedAITextList(
                    items = guidance!!.commonMistakes,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    delayBetweenItems = 80L
                )
            }
        }
    }
}

@Composable
private fun GuidanceSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    content()
                }
            }
        }
    }
}
