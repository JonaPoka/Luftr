@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.jonapoka.luftr.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jonapoka.luftr.data.AIWorkoutGenerator
import com.jonapoka.luftr.data.entities.Exercise
import com.jonapoka.luftr.ui.components.ChatMessage
import com.jonapoka.luftr.ui.components.ChatMessageBubble
import com.jonapoka.luftr.ui.components.ChatOptionButton
import com.jonapoka.luftr.ui.components.TypingIndicator
import com.jonapoka.luftr.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ChatStep {
    WELCOME,
    GOAL,
    EXPERIENCE,
    DURATION,
    TARGET_MUSCLES,
    GENERATING,
    COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAIPlannerScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit,
    onWorkoutGenerated: (Long) -> Unit
) {
    var currentStep by remember { mutableStateOf(ChatStep.WELCOME) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var showTyping by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(true) }
    
    // User selections
    var selectedGoal by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf("") }
    var selectedMuscles by remember { mutableStateOf(setOf<String>()) }
    
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Initial welcome message
    LaunchedEffect(Unit) {
        delay(500)
        messages = messages + ChatMessage(
            text = "Hey there! 💪 I'm your personal AI trainer, and I'm here to create the perfect workout just for you!\n\nLet's get started - what's your main fitness goal?",
            isFromTrainer = true
        )
    }

    // Auto-scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    val addTrainerMessage: (String) -> Unit = { text ->
        scope.launch {
            showOptions = false
            showTyping = true
            delay(1000)
            showTyping = false
            messages = messages + ChatMessage(text, isFromTrainer = true)
            delay(300)
            showOptions = true
        }
    }

    val addUserMessage: (String) -> Unit = { text ->
        showOptions = false
        messages = messages + ChatMessage(text, isFromTrainer = false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("AI Personal Trainer")
                        Text(
                            "Let's build your workout",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatMessageBubble(message = message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (showTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Options section
            AnimatedVisibility(
                visible = showOptions && currentStep != ChatStep.GENERATING && currentStep != ChatStep.COMPLETE,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        when (currentStep) {
                            ChatStep.WELCOME, ChatStep.GOAL -> {
                                GoalOptions(
                                    onGoalSelected = { goal ->
                                        addUserMessage(goal)
                                        selectedGoal = goal
                                        currentStep = ChatStep.EXPERIENCE
                                        addTrainerMessage("Great choice! Now, what's your experience level with weight training?\n\nThis helps me set the right intensity for you.")
                                    }
                                )
                            }
                            ChatStep.EXPERIENCE -> {
                                ExperienceOptions(
                                    onLevelSelected = { level ->
                                        addUserMessage(level)
                                        selectedLevel = level
                                        currentStep = ChatStep.DURATION
                                        addTrainerMessage("Perfect! How much time do you have for today's workout?\n\nI'll make sure every minute counts!")
                                    }
                                )
                            }
                            ChatStep.DURATION -> {
                                DurationOptions(
                                    onDurationSelected = { duration ->
                                        addUserMessage(duration)
                                        selectedDuration = duration
                                        currentStep = ChatStep.TARGET_MUSCLES
                                        addTrainerMessage("Awesome! Which muscle groups do you want to focus on today?\n\nYou can select multiple areas - I'll balance the workout perfectly!")
                                    }
                                )
                            }
                            ChatStep.TARGET_MUSCLES -> {
                                MuscleGroupOptions(
                                    selectedMuscles = selectedMuscles,
                                    onMusclesChanged = { muscles ->
                                        selectedMuscles = muscles
                                    },
                                    onConfirm = {
                                        if (selectedMuscles.isNotEmpty()) {
                                            addUserMessage(selectedMuscles.joinToString(", "))
                                            currentStep = ChatStep.GENERATING
                                            addTrainerMessage("Excellent! Let me create the perfect workout for you...\n\nI'm selecting exercises that target your goals, match your experience level, and fit your time. Each exercise will come with form tips and explanations!")
                                            
                                            scope.launch {
                                                delay(2000)
                                                try {
                                                    val workoutPlan = AIWorkoutGenerator.generateWorkout(
                                                        goal = selectedGoal,
                                                        experienceLevel = selectedLevel,
                                                        duration = selectedDuration,
                                                        targetMuscles = selectedMuscles.toList()
                                                    )

                                                    viewModel.createWorkout(workoutPlan.name, isAiGenerated = true) { workoutId ->
                                                        val exercises = workoutPlan.exercises.map { planned ->
                                                            Exercise(
                                                                workoutId = workoutId,
                                                                name = planned.name,
                                                                muscleGroup = planned.muscleGroup,
                                                                order = workoutPlan.exercises.indexOf(planned),
                                                                imageUrl = planned.imageUrl,
                                                                gifUrl = planned.gifUrl,
                                                                instructions = planned.instructions
                                                            )
                                                        }
                                                        viewModel.addExercises(workoutId, exercises)
                                                        
                                                        currentStep = ChatStep.COMPLETE
                                                        addTrainerMessage("Your personalized workout is ready! 🎉\n\nI've created ${exercises.size} exercises targeting ${selectedMuscles.joinToString(", ")}.\n\nYou'll see each exercise with proper form guidance, and if any machine is taken, you can easily swap it out. Let's crush this workout!")
                                                        
                                                        scope.launch {
                                                            delay(1500)
                                                            onWorkoutGenerated(workoutId)
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    addTrainerMessage("Oops! Something went wrong. Let me try that again...")
                                                    currentStep = ChatStep.TARGET_MUSCLES
                                                    showOptions = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalOptions(onGoalSelected: (String) -> Unit) {
    Column {
        ChatOptionButton("💪 Build Muscle", onClick = { onGoalSelected("Build Muscle") })
        ChatOptionButton("🔥 Lose Weight", onClick = { onGoalSelected("Lose Weight") })
        ChatOptionButton("⚡ Get Stronger", onClick = { onGoalSelected("Get Stronger") })
        ChatOptionButton("🏃 Improve Endurance", onClick = { onGoalSelected("Improve Endurance") })
    }
}

@Composable
fun ExperienceOptions(onLevelSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        ChatOptionButton("🌱 Beginner - Just starting out", onClick = { onLevelSelected("Beginner") })
        ChatOptionButton("🎯 Intermediate - Training regularly", onClick = { onLevelSelected("Intermediate") })
        ChatOptionButton("🔥 Advanced - Experienced lifter", onClick = { onLevelSelected("Advanced") })
    }
}

@Composable
fun DurationOptions(onDurationSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        ChatOptionButton("⚡ 30 minutes - Quick session", onClick = { onDurationSelected("30 minutes") })
        ChatOptionButton("🎯 45 minutes - Standard workout", onClick = { onDurationSelected("45 minutes") })
        ChatOptionButton("💪 60 minutes - Full session", onClick = { onDurationSelected("60 minutes") })
        ChatOptionButton("🔥 90+ minutes - Extended training", onClick = { onDurationSelected("90+ minutes") })
    }
}

@Composable
fun MuscleGroupOptions(
    selectedMuscles: Set<String>,
    onMusclesChanged: (Set<String>) -> Unit,
    onConfirm: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        val muscles = listOf(
            "💪 Chest" to "Chest",
            "🦾 Back" to "Back",
            "🦵 Legs" to "Legs",
            "🏋️ Shoulders" to "Shoulders",
            "💪 Arms" to "Arms",
            "🎯 Core" to "Core"
        )
        
        muscles.forEach { (display, value) ->
            val isSelected = selectedMuscles.contains(value)
            OutlinedButton(
                onClick = {
                    val newSet = if (isSelected) {
                        selectedMuscles - value
                    } else {
                        selectedMuscles + value
                    }
                    onMusclesChanged(newSet)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    } else {
                        androidx.compose.ui.graphics.Color.Transparent
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = display,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onConfirm,
            enabled = selectedMuscles.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (selectedMuscles.isEmpty()) "Select at least one muscle group" else "Create My Workout 🚀",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
