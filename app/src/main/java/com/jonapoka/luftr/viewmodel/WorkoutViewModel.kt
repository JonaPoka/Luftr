package com.jonapoka.luftr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonapoka.luftr.data.entities.*
import com.jonapoka.luftr.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    val allWorkouts: Flow<List<Workout>> = repository.getAllWorkouts()
    val recentWorkouts: Flow<List<WorkoutWithExercises>> = repository.getRecentWorkouts()

    private val _currentWorkoutId = MutableStateFlow<Long?>(null)
    val currentWorkoutId: StateFlow<Long?> = _currentWorkoutId.asStateFlow()

    private val _currentExercises = MutableStateFlow<List<ExerciseWithSets>>(emptyList())
    val currentExercises: StateFlow<List<ExerciseWithSets>> = _currentExercises.asStateFlow()

    fun createWorkout(name: String, isAiGenerated: Boolean = false, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val workout = Workout(
                name = name,
                isAiGenerated = isAiGenerated
            )
            val id = repository.insertWorkout(workout)
            _currentWorkoutId.value = id
            onSuccess(id)
        }
    }

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            _currentWorkoutId.value = workoutId
            repository.getExercisesWithSets(workoutId).collect { exercises ->
                _currentExercises.value = exercises
            }
        }
    }

    fun addExercise(workoutId: Long, name: String, muscleGroup: String) {
        viewModelScope.launch {
            val order = _currentExercises.value.size
            val exercise = Exercise(
                workoutId = workoutId,
                name = name,
                muscleGroup = muscleGroup,
                order = order
            )
            repository.insertExercise(exercise)
        }
    }

    fun addExercises(workoutId: Long, exercises: List<Exercise>) {
        viewModelScope.launch {
            repository.insertExercises(exercises)
        }
    }

    fun addSet(exerciseId: Long, reps: Int, weight: Float) {
        viewModelScope.launch {
            val currentSets = _currentExercises.value
                .find { it.exercise.id == exerciseId }
                ?.sets ?: emptyList()
            
            val setNumber = currentSets.size + 1
            val set = ExerciseSet(
                exerciseId = exerciseId,
                setNumber = setNumber,
                reps = reps,
                weight = weight
            )
            repository.insertSet(set)
        }
    }

    fun updateSet(set: ExerciseSet) {
        viewModelScope.launch {
            repository.updateSet(set)
        }
    }

    fun deleteSet(set: ExerciseSet) {
        viewModelScope.launch {
            repository.deleteSet(set)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun finishWorkout(workoutId: Long, duration: Int) {
        viewModelScope.launch {
            repository.getWorkoutWithExercises(workoutId)?.let { workoutWithExercises ->
                val updatedWorkout = workoutWithExercises.workout.copy(
                    duration = duration
                )
                repository.updateWorkout(updatedWorkout)
            }
        }
    }
}
