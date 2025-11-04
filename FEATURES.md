# Luftr Features Documentation

## Overview
Luftr is a comprehensive gym workout tracker with AI-powered workout planning capabilities. This document details all the features implemented in the app.

## Core Features

### 1. Home Screen
**Purpose**: Main dashboard for quick access to all app features

**Features**:
- Welcome message with Material You styling
- Quick action cards for:
  - Create AI Workout (with AI icon)
  - View History (with history icon)
- Recent workouts list showing:
  - Workout name
  - Date
  - Number of exercises
  - AI-generated badge for AI workouts
- Floating Action Button (FAB) to start a new workout
- Material You dynamic colors

**User Actions**:
- Tap quick action cards to navigate
- Tap recent workouts to view/edit
- Tap FAB to create a new workout with custom name

---

### 2. AI Workout Planner
**Purpose**: Generate personalized workout plans through an intelligent quiz system

**Features**:
- **Multi-Step Quiz System**:
  
  **Step 1 - Fitness Goal**:
  - Build Muscle (Hypertrophy focus)
  - Lose Weight (High volume, shorter rest)
  - Get Stronger (Strength focus, lower reps)
  - Improve Endurance (High reps, short rest)

  **Step 2 - Experience Level**:
  - Beginner (3-4 sets, higher reps)
  - Intermediate (4-5 sets, moderate reps)
  - Advanced (5+ sets, varied intensity)

  **Step 3 - Workout Duration**:
  - 30 minutes (4-5 exercises)
  - 45 minutes (6-7 exercises)
  - 60 minutes (8-9 exercises)
  - 90+ minutes (10+ exercises)

  **Step 4 - Target Muscle Groups**:
  - Chest
  - Back
  - Legs
  - Shoulders
  - Arms
  - Core
  - Multiple selection supported

- **Smart Exercise Selection**:
  - Extensive exercise database per muscle group
  - Random selection for variety
  - No duplicate exercises in same workout
  - Automatic set/rep scheme based on goals

- **Adaptive Programming**:
  - Rest times adjusted by goal
  - Rep ranges optimized for experience level
  - Exercise count matches duration preference
  - Intelligent workout naming

**Exercise Database**:
- **Chest**: Bench Press, Incline Dumbbell Press, Cable Flyes, Push-ups, Dips, Pec Deck
- **Back**: Pull-ups, Bent Over Rows, Lat Pulldown, Seated Cable Row, Deadlift, T-Bar Row
- **Legs**: Squats, Leg Press, Lunges, Leg Extensions, Leg Curls, Calf Raises, Romanian Deadlift
- **Shoulders**: Overhead Press, Lateral Raises, Front Raises, Rear Delt Flyes, Arnold Press, Upright Row
- **Arms**: Barbell Curls, Tricep Dips, Hammer Curls, Skull Crushers, Cable Curls, Tricep Pushdowns
- **Core**: Planks, Crunches, Russian Twists, Leg Raises, Cable Crunches, Mountain Climbers

**User Actions**:
- Navigate through quiz steps
- Select preferences for each question
- Review and modify selections
- Generate personalized workout
- Immediately start generated workout

---

### 3. Active Workout Screen
**Purpose**: Real-time workout tracking interface

**Features**:
- Exercise cards showing:
  - Exercise name
  - Target muscle group
  - All sets with details
- Set tracking table:
  - Set number
  - Reps performed
  - Weight used (kg)
  - Delete option per set
- Add new sets with:
  - Rep count input
  - Weight input (decimal support)
  - Input validation
- Add new exercises:
  - Exercise name input
  - Muscle group dropdown
  - Maintains order
- Finish workout:
  - Duration calculation
  - Confirmation dialog
  - Data persistence

**User Actions**:
- Add/remove exercises
- Add/remove sets for each exercise
- Input reps and weight for sets
- Finish and save workout
- Navigate back to discard (with warning)

---

### 4. History Screen
**Purpose**: View past workouts and track progress

**Features**:
- Statistics card showing:
  - Total workouts completed
  - Workouts this week
- Workout cards displaying:
  - Workout name
  - Date (formatted: "Wednesday, Nov 04")
  - AI-generated badge if applicable
  - Number of exercises
  - Total sets completed
  - Duration (if tracked)
- Clean, scrollable list
- Material You cards
- Empty state with icon when no history

**User Actions**:
- View workout statistics
- Tap workout to view details
- Scroll through history
- Navigate back to home

---

### 5. Workout List Screen
**Purpose**: Browse all saved workouts

**Features**:
- List of all workouts
- Workout cards with name
- Empty state when no workouts
- Quick access to any workout

**User Actions**:
- Browse all workouts
- Tap to open workout
- Navigate back

---

## Technical Features

### Database Architecture
**Room Database with 3 main entities**:

1. **Workout Entity**:
   - ID (auto-generated)
   - Name
   - Date (timestamp)
   - Duration (minutes)
   - Notes
   - isAiGenerated flag

2. **Exercise Entity**:
   - ID (auto-generated)
   - Workout ID (foreign key)
   - Name
   - Muscle group
   - Order in workout
   - Cascade delete with workout

3. **ExerciseSet Entity**:
   - ID (auto-generated)
   - Exercise ID (foreign key)
   - Set number
   - Reps
   - Weight
   - Completed flag
   - Cascade delete with exercise

**Relationships**:
- One workout → Many exercises
- One exercise → Many sets
- Automatic cascade deletion

### Data Flow
- **Repository Pattern**: Single source of truth
- **Flow-based Updates**: Reactive UI updates
- **ViewModel Management**: Lifecycle-aware state
- **Coroutines**: Non-blocking database operations

### UI/UX Features
- **Material You Dynamic Colors**: Adapts to system theme
- **Dark/Light Theme**: Automatic switching
- **Smooth Animations**: Material motion
- **Input Validation**: All user inputs validated
- **Error Handling**: Graceful error states
- **Loading States**: Progress indicators where needed
- **Empty States**: Helpful messages and icons
- **Confirmation Dialogs**: Prevent accidental actions

### Performance
- **Offline First**: All data stored locally
- **Fast Queries**: Optimized database access
- **Lazy Loading**: Efficient list rendering
- **Memory Efficient**: Proper lifecycle management

## AI Workout Generation Algorithm

### Input Parameters
1. Fitness Goal (affects rep range and rest time)
2. Experience Level (affects set count)
3. Workout Duration (affects exercise count)
4. Target Muscles (affects exercise selection)

### Generation Logic

1. **Calculate Exercise Count**:
   - 30 min → 4 exercises
   - 45 min → 6 exercises
   - 60 min → 8 exercises
   - 90+ min → 10 exercises

2. **Determine Set/Rep Scheme**:
   ```
   Build Muscle:
     Beginner: 3x12
     Intermediate: 4x10
     Advanced: 5x8
   
   Lose Weight:
     Beginner: 3x15
     Intermediate: 4x15
     Advanced: 4x20
   
   Get Stronger:
     Beginner: 3x8
     Intermediate: 4x6
     Advanced: 5x5
   
   Improve Endurance:
     Beginner: 3x20
     Intermediate: 4x20
     Advanced: 4x25
   ```

3. **Select Exercises**:
   - Distribute evenly across selected muscle groups
   - Random selection for variety
   - No duplicates in same workout
   - Fill remaining slots if needed

4. **Set Rest Times**:
   - Build Muscle: 90 seconds
   - Lose Weight: 45 seconds
   - Get Stronger: 180 seconds
   - Improve Endurance: 30 seconds

5. **Generate Workout Name**:
   - Format: "{Muscle Groups} {Goal Type}"
   - Examples:
     - "Chest & Back Hypertrophy"
     - "Legs Strength"
     - "Full Body Fat Burn"

## Future Feature Ideas

### Planned Enhancements
- [ ] Exercise images and animations
- [ ] Form tips and guides
- [ ] Progressive overload tracking
- [ ] Workout templates
- [ ] Rest timer with notifications
- [ ] 1RM calculator
- [ ] Volume/intensity tracking
- [ ] Body measurements tracking
- [ ] Progress photos
- [ ] Personal records (PRs)
- [ ] Workout calendar view
- [ ] Exercise substitution suggestions
- [ ] Superset support
- [ ] Dropset tracking
- [ ] Warm-up set tracking
- [ ] Notes per set
- [ ] Workout sharing
- [ ] Cloud backup
- [ ] Export to CSV/PDF
- [ ] Weekly/monthly reports
- [ ] Gamification (badges, streaks)
- [ ] Integration with wearables
- [ ] Voice commands during workout
- [ ] Custom exercise creation
- [ ] Video recording of sets
- [ ] Social features
- [ ] Workout reminders

### Community Requests
- Multi-language support
- Imperial/Metric unit toggle
- Multiple workout programs
- Meal planning integration
- Water intake tracking
- Sleep tracking correlation
- Injury tracking and prevention
- Flexibility/mobility routines
- Cardio integration
- Custom muscle groups
- Workout difficulty rating
- Energy level tracking
- Music integration
- Apple Health / Google Fit sync

---

**Note**: This document will be updated as new features are added to Luftr.
