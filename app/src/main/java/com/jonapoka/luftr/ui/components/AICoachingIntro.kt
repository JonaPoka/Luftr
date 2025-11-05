package com.jonapoka.luftr.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonapoka.luftr.data.entities.ExerciseWithSets
import kotlinx.coroutines.delay

/**
 * AI Coaching intro screen that appears when starting an AI workout
 * Shows welcome message, workout overview, and first exercise preview with fade-in video
 */
@Composable
fun AICoachingIntro(
    exercises: List<ExerciseWithSets>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) }
    var showVideo by remember { mutableStateOf(false) }
    
    val firstExercise = exercises.firstOrNull()
    
    // Auto-progress through steps
    LaunchedEffect(currentStep) {
        when (currentStep) {
            0 -> {
                delay(2500) // Welcome message
                currentStep = 1
            }
            1 -> {
                delay(3000) // Workout overview
                currentStep = 2
            }
            2 -> {
                delay(2000) // Show first exercise
                showVideo = true
                delay(3500) // Show video
                // Auto-dismiss after showing everything
            }
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // AI Coach Icon
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(40.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "AI Coach",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Messages with fade-in animations
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(animationSpec = tween(700)) with
                            fadeOut(animationSpec = tween(700))
                },
                label = "coaching_steps"
            ) { step ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (step) {
                        0 -> {
                            // Welcome
                            Text(
                                text = "Welcome to your AI workout! 💪",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "I'm your personal trainer for today. Let me guide you through this workout!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        1 -> {
                            // Workout Overview
                            Text(
                                text = "Today's Workout",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "We've got ${exercises.size} exercises planned for you. Each one is carefully selected to match your goals.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Take your time, focus on form, and let's make this count!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        2 -> {
                            // First Exercise Preview
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "First Exercise",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                if (firstExercise != null) {
                                    Text(
                                        text = firstExercise.exercise.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Target: ${firstExercise.exercise.muscleGroup}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // Fade in exercise video/image
                                    AnimatedVisibility(
                                        visible = showVideo,
                                        enter = fadeIn(animationSpec = tween(1000))
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Watch the form:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(bottom = 12.dp)
                                            )
                                            
                                            // Show exercise video/image
                                            ExerciseImageCard(
                                                imageUrl = firstExercise.exercise.imageUrl,
                                                gifUrl = firstExercise.exercise.gifUrl,
                                                exerciseName = firstExercise.exercise.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Skip/Continue button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (currentStep < 2) "Skip Intro" else "Start Workout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
