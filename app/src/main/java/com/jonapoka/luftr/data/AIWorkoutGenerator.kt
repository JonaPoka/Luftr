package com.jonapoka.luftr.data

import com.jonapoka.luftr.data.api.ApiClient
import com.jonapoka.luftr.data.api.GroqMessage
import com.jonapoka.luftr.data.api.GroqRequest
import com.jonapoka.luftr.data.entities.Exercise
import com.jonapoka.luftr.data.entities.ExerciseSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class WorkoutPlan(
    val name: String,
    val exercises: List<PlannedExercise>
)

data class PlannedExercise(
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restTime: Int = 60,
    val imageUrl: String? = null,
    val gifUrl: String? = null,
    val instructions: String? = null
)

data class ExerciseGuidance(
    val formTips: List<String>,
    val why: String,
    val commonMistakes: List<String>
)

object AIWorkoutGenerator {
    
    private val exerciseDatabase = mapOf(
        "Chest" to listOf(
            "Bench Press", "Incline Dumbbell Press", "Cable Flyes", 
            "Push-ups", "Dips", "Pec Deck"
        ),
        "Back" to listOf(
            "Pull-ups", "Bent Over Rows", "Lat Pulldown", 
            "Seated Cable Row", "Deadlift", "T-Bar Row"
        ),
        "Legs" to listOf(
            "Squats", "Leg Press", "Lunges", "Leg Extensions", 
            "Leg Curls", "Calf Raises", "Romanian Deadlift"
        ),
        "Shoulders" to listOf(
            "Overhead Press", "Lateral Raises", "Front Raises", 
            "Rear Delt Flyes", "Arnold Press", "Upright Row"
        ),
        "Arms" to listOf(
            "Barbell Curls", "Tricep Dips", "Hammer Curls", 
            "Skull Crushers", "Cable Curls", "Tricep Pushdowns"
        ),
        "Core" to listOf(
            "Planks", "Crunches", "Russian Twists", 
            "Leg Raises", "Cable Crunches", "Mountain Climbers"
        )
    )

    suspend fun generateWorkout(
        goal: String,
        experienceLevel: String,
        duration: String,
        targetMuscles: List<String>
    ): WorkoutPlan = withContext(Dispatchers.IO) {
        try {
            // Try to use Groq API for AI-powered workout generation
            generateWorkoutWithGroq(goal, experienceLevel, duration, targetMuscles)
        } catch (e: Exception) {
            // Fallback to local generation if API fails
            generateWorkoutLocally(goal, experienceLevel, duration, targetMuscles)
        }
    }
    
    private suspend fun generateWorkoutWithGroq(
        goal: String,
        experienceLevel: String,
        duration: String,
        targetMuscles: List<String>
    ): WorkoutPlan {
        val musclesStr = if (targetMuscles.isEmpty()) "full body" else targetMuscles.joinToString(", ")
        
        val prompt = """
            Generate a workout plan with the following requirements:
            - Goal: $goal
            - Experience Level: $experienceLevel
            - Duration: $duration
            - Target Muscles: $musclesStr
            
            Return ONLY a valid JSON object (no markdown, no code blocks) with this exact structure:
            {
              "workoutName": "Workout Name",
              "exercises": [
                {
                  "name": "Exercise Name",
                  "muscleGroup": "Muscle Group",
                  "sets": 3,
                  "reps": 12,
                  "restTime": 60
                }
              ]
            }
            
            Select 4-10 exercises based on the duration. Use proper exercise names.
        """.trimIndent()
        
        val request = GroqRequest(
            messages = listOf(
                GroqMessage(role = "system", content = "You are a professional fitness trainer. Always respond with valid JSON only."),
                GroqMessage(role = "user", content = prompt)
            ),
            temperature = 0.7f,
            max_tokens = 2048
        )
        
        val response = ApiClient.groqApi.createChatCompletion(
            authorization = "Bearer ${ApiClient.GROQ_API_KEY}",
            request = request
        )
        
        val content = response.choices.firstOrNull()?.message?.content 
            ?: throw Exception("No response from Groq API")
        
        return parseWorkoutFromJson(content)
    }
    
    private suspend fun parseWorkoutFromJson(jsonString: String): WorkoutPlan {
        // Clean up the JSON string (remove markdown code blocks if present)
        val cleanJson = jsonString
            .replace("```json", "")
            .replace("```", "")
            .trim()
        
        val json = JSONObject(cleanJson)
        val workoutName = json.getString("workoutName")
        val exercisesArray = json.getJSONArray("exercises")
        
        val exercises = mutableListOf<PlannedExercise>()
        for (i in 0 until exercisesArray.length()) {
            val exerciseObj = exercisesArray.getJSONObject(i)
            val exerciseName = exerciseObj.getString("name")
            
            // Fetch exercise media
            val media = try {
                ExerciseMediaFetcher.fetchExerciseMedia(exerciseName)
            } catch (e: Exception) {
                ExerciseMedia(null, null, null)
            }
            
            exercises.add(
                PlannedExercise(
                    name = exerciseName,
                    muscleGroup = exerciseObj.getString("muscleGroup"),
                    sets = exerciseObj.getInt("sets"),
                    reps = exerciseObj.getInt("reps"),
                    restTime = exerciseObj.optInt("restTime", 60),
                    imageUrl = media.imageUrl,
                    gifUrl = media.gifUrl,
                    instructions = media.instructions
                )
            )
        }
        
        return WorkoutPlan(
            name = workoutName,
            exercises = exercises
        )
    }
    
    private suspend fun generateWorkoutLocally(
        goal: String,
        experienceLevel: String,
        duration: String,
        targetMuscles: List<String>
    ): WorkoutPlan {
        val exercises = mutableListOf<PlannedExercise>()
        
        // Determine number of exercises based on duration
        val exerciseCount = when (duration) {
            "30 minutes" -> 4
            "45 minutes" -> 6
            "60 minutes" -> 8
            else -> 10
        }
        
        // Determine sets and reps based on goal and experience
        val (sets, reps) = getSetRepScheme(goal, experienceLevel)
        
        // Select exercises from target muscles
        val selectedMuscles = if (targetMuscles.isEmpty()) {
            exerciseDatabase.keys.take(2).toList()
        } else {
            targetMuscles
        }
        
        val exercisesPerMuscle = exerciseCount / selectedMuscles.size.coerceAtLeast(1)
        
        selectedMuscles.forEach { muscle ->
            val muscleExercises = exerciseDatabase[muscle] ?: emptyList()
            muscleExercises.shuffled().take(exercisesPerMuscle).forEach { exerciseName ->
                // Fetch exercise media
                val media = try {
                    ExerciseMediaFetcher.fetchExerciseMedia(exerciseName)
                } catch (e: Exception) {
                    ExerciseMedia(null, null, null)
                }
                
                exercises.add(
                    PlannedExercise(
                        name = exerciseName,
                        muscleGroup = muscle,
                        sets = sets,
                        reps = reps,
                        restTime = getRestTime(goal, experienceLevel),
                        imageUrl = media.imageUrl,
                        gifUrl = media.gifUrl,
                        instructions = media.instructions
                    )
                )
            }
        }
        
        // Fill remaining slots if needed
        while (exercises.size < exerciseCount) {
            val randomMuscle = selectedMuscles.random()
            val randomExercise = exerciseDatabase[randomMuscle]?.random()
            if (randomExercise != null && exercises.none { it.name == randomExercise }) {
                // Fetch exercise media
                val media = try {
                    ExerciseMediaFetcher.fetchExerciseMedia(randomExercise)
                } catch (e: Exception) {
                    ExerciseMedia(null, null, null)
                }
                
                exercises.add(
                    PlannedExercise(
                        name = randomExercise,
                        muscleGroup = randomMuscle,
                        sets = sets,
                        reps = reps,
                        restTime = getRestTime(goal, experienceLevel),
                        imageUrl = media.imageUrl,
                        gifUrl = media.gifUrl,
                        instructions = media.instructions
                    )
                )
            }
        }
        
        val workoutName = generateWorkoutName(selectedMuscles, goal)
        
        return WorkoutPlan(
            name = workoutName,
            exercises = exercises.take(exerciseCount)
        )
    }
    
    private fun getSetRepScheme(goal: String, experienceLevel: String): Pair<Int, Int> {
        return when (goal) {
            "Build Muscle" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 12)
                "Intermediate" -> Pair(4, 10)
                else -> Pair(5, 8)
            }
            "Lose Weight" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 15)
                "Intermediate" -> Pair(4, 15)
                else -> Pair(4, 20)
            }
            "Get Stronger" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 8)
                "Intermediate" -> Pair(4, 6)
                else -> Pair(5, 5)
            }
            "Improve Endurance" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 20)
                "Intermediate" -> Pair(4, 20)
                else -> Pair(4, 25)
            }
            else -> Pair(3, 12)
        }
    }
    
    private fun getRestTime(goal: String, experienceLevel: String): Int {
        return when (goal) {
            "Build Muscle" -> 90
            "Lose Weight" -> 45
            "Get Stronger" -> 180
            "Improve Endurance" -> 30
            else -> 60
        }
    }
    
    private fun generateWorkoutName(muscles: List<String>, goal: String): String {
        val musclesPart = when {
            muscles.size == 1 -> muscles.first()
            muscles.size == 2 -> "${muscles[0]} & ${muscles[1]}"
            else -> "Full Body"
        }
        
        val goalPart = when (goal) {
            "Build Muscle" -> "Hypertrophy"
            "Get Stronger" -> "Strength"
            "Lose Weight" -> "Fat Burn"
            "Improve Endurance" -> "Endurance"
            else -> "Workout"
        }
        
        return "$musclesPart $goalPart"
    }
    
    suspend fun getExerciseGuidance(exerciseName: String, goal: String): ExerciseGuidance = withContext(Dispatchers.IO) {
        try {
            // Try to get AI-generated guidance
            getExerciseGuidanceFromAI(exerciseName, goal)
        } catch (e: Exception) {
            // Fallback to local guidance if API fails
            getExerciseGuidanceLocally(exerciseName, goal)
        }
    }
    
    private suspend fun getExerciseGuidanceFromAI(exerciseName: String, goal: String): ExerciseGuidance {
        val prompt = """
            You are a professional personal trainer. Provide detailed guidance for the exercise "$exerciseName" for someone whose goal is to "$goal".
            
            Return ONLY a valid JSON object (no markdown, no code blocks) with this exact structure:
            {
              "formTips": [
                "Tip 1 starting with an emoji like 🔹",
                "Tip 2 starting with an emoji like 🔹",
                "Tip 3 starting with an emoji like 🔹",
                "Tip 4 starting with an emoji like 🔹",
                "Tip 5 starting with an emoji like 🔹"
              ],
              "why": "A motivational 2-3 sentence explanation of why this exercise is great for their goal",
              "commonMistakes": [
                "Mistake 1 starting with ❌",
                "Mistake 2 starting with ❌",
                "Mistake 3 starting with ❌"
              ]
            }
            
            Be specific, motivational, and professional. Use simple, clear language.
        """.trimIndent()
        
        val request = GroqRequest(
            messages = listOf(
                GroqMessage(role = "system", content = "You are a professional personal trainer providing exercise form advice. Always respond with valid JSON only."),
                GroqMessage(role = "user", content = prompt)
            ),
            temperature = 0.7f,
            max_tokens = 1024
        )
        
        val response = ApiClient.groqApi.createChatCompletion(
            authorization = "Bearer ${ApiClient.GROQ_API_KEY}",
            request = request
        )
        
        val content = response.choices.firstOrNull()?.message?.content 
            ?: throw Exception("No response from Groq API")
        
        return parseGuidanceFromJson(content)
    }
    
    private fun parseGuidanceFromJson(jsonString: String): ExerciseGuidance {
        // Clean up the JSON string (remove markdown code blocks if present)
        val cleanJson = jsonString
            .replace("```json", "")
            .replace("```", "")
            .trim()
        
        val json = JSONObject(cleanJson)
        
        val formTipsArray = json.getJSONArray("formTips")
        val formTips = mutableListOf<String>()
        for (i in 0 until formTipsArray.length()) {
            formTips.add(formTipsArray.getString(i))
        }
        
        val why = json.getString("why")
        
        val mistakesArray = json.getJSONArray("commonMistakes")
        val mistakes = mutableListOf<String>()
        for (i in 0 until mistakesArray.length()) {
            mistakes.add(mistakesArray.getString(i))
        }
        
        return ExerciseGuidance(
            formTips = formTips,
            why = why,
            commonMistakes = mistakes
        )
    }
    
    private fun getExerciseGuidanceLocally(exerciseName: String, goal: String): ExerciseGuidance {
        val lowerName = exerciseName.lowercase()
        
        return when {
            // Chest exercises
            lowerName.contains("bench press") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your feet flat on the floor for stability",
                    "🔹 Retract and depress your shoulder blades",
                    "🔹 Lower the bar to your mid-chest with control",
                    "🔹 Press in a slight arc back to starting position",
                    "🔹 Keep your wrists straight and core tight"
                ),
                why = "Bench press is the king of chest exercises! It builds overall upper body strength and mass, working your chest, shoulders, and triceps together. Perfect for ${goal.lowercase()} because it allows progressive overload with heavy weight.",
                commonMistakes = listOf(
                    "❌ Bouncing the bar off your chest",
                    "❌ Flaring elbows too wide (45° angle is optimal)",
                    "❌ Lifting hips off the bench"
                )
            )
            
            lowerName.contains("push-up") || lowerName.contains("push up") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your body in a straight line from head to heels",
                    "🔹 Hands slightly wider than shoulder-width",
                    "🔹 Lower until chest nearly touches ground",
                    "🔹 Keep core engaged throughout",
                    "🔹 Breathe in down, breathe out up"
                ),
                why = "Push-ups are incredibly versatile! They build functional strength using your own bodyweight. Great for ${goal.lowercase()} as you can do them anywhere and easily adjust difficulty by changing hand position.",
                commonMistakes = listOf(
                    "❌ Sagging hips or raised butt",
                    "❌ Not going deep enough",
                    "❌ Head dropping or looking up"
                )
            )
            
            // Back exercises
            lowerName.contains("pull-up") || lowerName.contains("pull up") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Full hang at the bottom with arms extended",
                    "🔹 Pull your chest to the bar, not just your chin",
                    "🔹 Keep your core tight and avoid swinging",
                    "🔹 Lead with your chest, not your chin",
                    "🔹 Control the descent - don't just drop"
                ),
                why = "Pull-ups are the ultimate back builder! They develop width and thickness in your lats while building incredible grip strength. Essential for ${goal.lowercase()} as they work multiple muscle groups and build functional pulling strength.",
                commonMistakes = listOf(
                    "❌ Using momentum and swinging",
                    "❌ Only doing partial reps",
                    "❌ Shrugging shoulders at the top"
                )
            )
            
            lowerName.contains("row") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your back flat and core engaged",
                    "🔹 Pull to your lower chest/upper abs",
                    "🔹 Squeeze your shoulder blades together at the top",
                    "🔹 Keep elbows close to your body",
                    "🔹 Control both the pull and the release"
                ),
                why = "Rows build thick, strong back muscles! They're essential for balanced upper body development and posture. Perfect for ${goal.lowercase()} as they strengthen your entire posterior chain and prevent shoulder injuries.",
                commonMistakes = listOf(
                    "❌ Using too much momentum",
                    "❌ Rounding your lower back",
                    "❌ Not achieving full range of motion"
                )
            )
            
            // Leg exercises
            lowerName.contains("squat") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your chest up and core braced",
                    "🔹 Knees track over your toes, don't cave inward",
                    "🔹 Go at least to parallel (thighs parallel to ground)",
                    "🔹 Drive through your whole foot, not just toes",
                    "🔹 Keep your weight on mid-foot to heel"
                ),
                why = "Squats are the king of leg exercises! They build total body strength and mass, not just legs. Critical for ${goal.lowercase()} because they trigger massive hormone release and burn tons of calories while building functional strength.",
                commonMistakes = listOf(
                    "❌ Knees collapsing inward",
                    "❌ Leaning too far forward",
                    "❌ Not going deep enough"
                )
            )
            
            lowerName.contains("lunge") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Take a big step forward, not too short",
                    "🔹 Drop your back knee straight down",
                    "🔹 Keep your front knee aligned with your ankle",
                    "🔹 Keep your torso upright",
                    "🔹 Push through your front heel to return"
                ),
                why = "Lunges build single-leg strength and balance! They're amazing for ${goal.lowercase()} because they work each leg independently, preventing strength imbalances and improving stability.",
                commonMistakes = listOf(
                    "❌ Front knee going past toes",
                    "❌ Leaning forward too much",
                    "❌ Not stepping far enough"
                )
            )
            
            // Shoulder exercises
            lowerName.contains("press") && lowerName.contains("shoulder" ) || lowerName.contains("overhead") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your core tight and avoid arching your back",
                    "🔹 Press straight up, not forward",
                    "🔹 Keep your wrists stacked over elbows",
                    "🔹 Full lockout at the top without hyperextending",
                    "🔹 Control the weight down, don't drop it"
                ),
                why = "Overhead pressing builds powerful shoulders! It's essential for ${goal.lowercase()} as it develops overhead strength and stability while working your entire upper body.",
                commonMistakes = listOf(
                    "❌ Excessive back arching",
                    "❌ Pressing forward instead of up",
                    "❌ Not achieving full range of motion"
                )
            )
            
            lowerName.contains("lateral raise") || lowerName.contains("side raise") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep a slight bend in your elbows",
                    "🔹 Lead with your elbows, not your hands",
                    "🔹 Raise to shoulder height, not higher",
                    "🔹 Control the lowering phase",
                    "🔹 Avoid swinging or using momentum"
                ),
                why = "Lateral raises sculpt your side delts! They create that 3D shoulder look and width. Great for ${goal.lowercase()} because they isolate the middle deltoid for targeted development.",
                commonMistakes = listOf(
                    "❌ Using too much weight and swinging",
                    "❌ Raising arms too high",
                    "❌ Internally rotating shoulders"
                )
            )
            
            // Arm exercises
            lowerName.contains("curl") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your elbows stationary at your sides",
                    "🔹 Don't swing or use momentum",
                    "🔹 Fully extend arms at the bottom",
                    "🔹 Squeeze at the top for 1 second",
                    "🔹 Control the weight down, don't just drop it"
                ),
                why = "Curls build powerful biceps! They're essential for ${goal.lowercase()} as they develop arm size and strength while improving your pulling power.",
                commonMistakes = listOf(
                    "❌ Moving elbows forward or back",
                    "❌ Using momentum from your back",
                    "❌ Partial range of motion"
                )
            )
            
            lowerName.contains("tricep") || lowerName.contains("pushdown") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your elbows tucked at your sides",
                    "🔹 Fully extend at the bottom",
                    "🔹 Don't let the weight pull your elbows forward",
                    "🔹 Keep your upper arms stationary",
                    "🔹 Control the return phase"
                ),
                why = "Tricep work builds bigger arms! Your triceps make up 2/3 of your arm mass. Perfect for ${goal.lowercase()} as they're crucial for all pressing movements and overall arm development.",
                commonMistakes = listOf(
                    "❌ Elbows flaring out",
                    "❌ Leaning forward too much",
                    "❌ Not achieving full extension"
                )
            )
            
            // Core exercises
            lowerName.contains("plank") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your body in a straight line",
                    "🔹 Engage your core like someone's about to punch you",
                    "🔹 Don't let your hips sag or pike up",
                    "🔹 Squeeze your glutes",
                    "🔹 Breathe normally - don't hold your breath"
                ),
                why = "Planks build incredible core stability! They're fundamental for ${goal.lowercase()} as they strengthen your entire midsection and protect your lower back during other exercises.",
                commonMistakes = listOf(
                    "❌ Sagging hips",
                    "❌ Holding breath",
                    "❌ Looking up instead of down"
                )
            )
            
            lowerName.contains("crunch") -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Keep your lower back on the ground",
                    "🔹 Lift your shoulder blades off the floor",
                    "🔹 Hands behind head, don't pull on neck",
                    "🔹 Exhale as you crunch up",
                    "🔹 Focus on contracting your abs"
                ),
                why = "Crunches target your abs directly! They're great for ${goal.lowercase()} as they build definition and strength in your rectus abdominis.",
                commonMistakes = listOf(
                    "❌ Pulling on your neck",
                    "❌ Using momentum",
                    "❌ Going too fast"
                )
            )
            
            // Default for any other exercise
            else -> ExerciseGuidance(
                formTips = listOf(
                    "🔹 Start with lighter weight to master the form",
                    "🔹 Keep your core engaged throughout",
                    "🔹 Control both the lifting and lowering phases",
                    "🔹 Breathe properly - exhale on exertion",
                    "🔹 Focus on the muscle you're working"
                ),
                why = "This exercise is excellent for ${goal.lowercase()}! It targets specific muscle groups effectively and helps build balanced strength.",
                commonMistakes = listOf(
                    "❌ Using too much weight too soon",
                    "❌ Rushing through reps",
                    "❌ Poor breathing technique"
                )
            )
        }
    }
}
