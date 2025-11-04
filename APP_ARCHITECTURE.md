# Luftr App Architecture

## Overview
Luftr follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                             │
│                    (Jetpack Compose)                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  HomeScreen     ActiveWorkoutScreen    AIPlannerScreen      │
│  HistoryScreen  WorkoutListScreen                           │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Navigation (NavGraph)                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Material You Theme (Dynamic Colors)           │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │ State & Events
                            │
┌─────────────────────────────────────────────────────────────┐
│                      ViewModel Layer                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│                    WorkoutViewModel                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ - StateFlow for UI state                             │  │
│  │ - Business logic                                      │  │
│  │ - Lifecycle aware                                     │  │
│  │ - Coroutines for async operations                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │ Data Operations
                            │
┌─────────────────────────────────────────────────────────────┐
│                     Repository Layer                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│                   WorkoutRepository                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ - Single source of truth                             │  │
│  │ - Abstracts data sources                             │  │
│  │ - Flow-based reactive data                           │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
│                  AIWorkoutGenerator                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ - Workout generation logic                           │  │
│  │ - Exercise database                                  │  │
│  │ - Algorithm for personalization                      │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │ Database Operations
                            │
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│                     (Room Database)                          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────┐   │
│  │ WorkoutDao  │  │ ExerciseDao │  │ ExerciseSetDao   │   │
│  └─────────────┘  └─────────────┘  └──────────────────┘   │
│         │                 │                  │              │
│         └─────────────────┴──────────────────┘              │
│                          │                                  │
│                 LuftrDatabase                               │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Entities:                                            │   │
│  │ - Workout (id, name, date, duration, isAiGenerated) │   │
│  │ - Exercise (id, workoutId, name, muscleGroup)       │   │
│  │ - ExerciseSet (id, exerciseId, reps, weight)        │   │
│  │                                                       │   │
│  │ Relationships:                                       │   │
│  │ - Workout 1:N Exercise (cascade delete)             │   │
│  │ - Exercise 1:N ExerciseSet (cascade delete)         │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Data Flow

### Reading Data (View → ViewModel → Repository → Database)
```
User opens screen
    → UI collects StateFlow
        → ViewModel exposes Flow
            → Repository queries Database
                → Room returns Flow<Data>
                    → ViewModel transforms data
                        → UI renders state
```

### Writing Data (User Action → ViewModel → Repository → Database)
```
User taps button
    → UI calls ViewModel function
        → ViewModel launches coroutine
            → Repository performs operation
                → Room inserts/updates/deletes
                    → Database triggers Flow update
                        → UI automatically updates
```

## Key Components

### UI Layer (Composables)
- **Screens**: Full-screen composables for each feature
- **Components**: Reusable UI elements (Cards, Dialogs, etc.)
- **Theme**: Material You color scheme and typography
- **Navigation**: Screen routing and deep linking

**Technologies**:
- Jetpack Compose
- Material Design 3
- Navigation Compose

### ViewModel Layer
- **WorkoutViewModel**: Central state management
  - Manages workout creation/editing
  - Handles exercise operations
  - Tracks current workout state
  - Exposes reactive streams to UI

**Technologies**:
- Android ViewModel
- Kotlin Coroutines
- StateFlow/Flow

### Repository Layer
- **WorkoutRepository**: Data operations abstraction
  - CRUD operations for workouts
  - CRUD operations for exercises
  - CRUD operations for sets
  - Reactive data streams

- **AIWorkoutGenerator**: Workout generation logic
  - Exercise database
  - Algorithm for personalization
  - Set/rep scheme calculation

**Technologies**:
- Kotlin Coroutines
- Flow

### Data Layer
- **DAOs**: Database access interfaces
  - WorkoutDao: Workout operations
  - ExerciseDao: Exercise operations
  - ExerciseSetDao: Set operations

- **Entities**: Database tables
  - @Entity annotations
  - Foreign key relationships
  - Type converters

- **Database**: Room database singleton
  - Database initialization
  - Migration strategy (if needed)

**Technologies**:
- Room Database
- SQLite
- KSP (annotation processing)

## Design Patterns

### 1. MVVM (Model-View-ViewModel)
- **View**: Compose UI (screens and components)
- **ViewModel**: Business logic and state management
- **Model**: Repository + Database

**Benefits**:
- Separation of concerns
- Testable business logic
- Lifecycle awareness
- Reactive UI updates

### 2. Repository Pattern
- Single source of truth for data
- Abstracts data sources
- Easy to test and mock
- Centralized data operations

### 3. Observer Pattern (via Flow)
- Reactive data streams
- Automatic UI updates
- Efficient updates (only what changed)
- Lifecycle-aware subscriptions

### 4. Singleton Pattern
- Database instance
- Repository instance
- Ensures single database connection

## Threading Model

```
┌────────────────────────────────────────────────┐
│                   Main Thread                   │
│              (UI Rendering Only)                │
└────────────────────────────────────────────────┘
                     ▲
                     │ UI Updates
                     │
┌────────────────────────────────────────────────┐
│              Coroutine Dispatchers              │
├────────────────────────────────────────────────┤
│                                                  │
│  Dispatchers.Main     → UI operations           │
│  Dispatchers.IO       → Database operations     │
│  Dispatchers.Default  → CPU-intensive work      │
│                                                  │
└────────────────────────────────────────────────┘
```

**Key Points**:
- All database operations on IO dispatcher
- UI updates on Main dispatcher
- Heavy computations on Default dispatcher
- Room handles threading automatically
- ViewModel scope for coroutines

## State Management

### UI State Flow
```kotlin
// ViewModel
private val _state = MutableStateFlow(initialValue)
val state: StateFlow = _state.asStateFlow()

// UI
val state by viewModel.state.collectAsState()
```

### Benefits
- Type-safe state
- Lifecycle-aware collection
- Automatic recomposition
- No memory leaks

## Dependency Injection

Currently using **Manual DI**:
```kotlin
Database → Repository → ViewModel → UI
```

**Advantages**:
- Simple and lightweight
- Easy to understand
- No additional library
- Perfect for small apps

**Future**: Could migrate to Hilt/Dagger for larger scale

## Testing Strategy

### Unit Tests (Future)
```
ViewModel Tests
├── Business logic validation
├── State transitions
└── Error handling

Repository Tests
├── Database operations
├── Data transformations
└── Flow emissions

AI Generator Tests
├── Workout generation logic
├── Exercise selection
└── Set/rep calculations
```

### Integration Tests (Future)
```
Database Tests
├── CRUD operations
├── Relationships
└── Cascade deletes

End-to-End Tests
├── Complete workflows
├── Data persistence
└── State consistency
```

### UI Tests (Future)
```
Screen Tests
├── Navigation flows
├── User interactions
└── State rendering

Component Tests
├── Dialog behavior
├── Input validation
└── Error states
```

## Performance Considerations

### Database
- ✅ Indexed foreign keys
- ✅ Efficient queries with Flow
- ✅ Cascade deletes (no orphaned data)
- ✅ Single database instance

### UI
- ✅ Lazy loading (LazyColumn)
- ✅ Efficient recomposition
- ✅ State hoisting
- ✅ Remember for expensive operations

### Memory
- ✅ Lifecycle-aware ViewModels
- ✅ Proper scope management
- ✅ Flow cancellation on lifecycle end
- ✅ No memory leaks

## Scalability

### Current Capacity
- ✅ Unlimited workouts
- ✅ Unlimited exercises per workout
- ✅ Unlimited sets per exercise
- ✅ Efficient at 1000+ workouts

### Future Enhancements
- Cloud sync capability
- Offline-first with sync
- Multi-user support
- Export/import features
- Analytics and insights

## Security

### Current
- ✅ Local data only
- ✅ No network permissions
- ✅ Secure Room database
- ✅ No user authentication needed

### Future Considerations
- Encrypted database
- Secure cloud sync
- User authentication
- Data privacy compliance

## Extensibility

### Easy to Add
- New screens (add to NavGraph)
- New database entities (add migration)
- New ViewModels (follow pattern)
- New AI algorithms (extend generator)

### Plugin Points
- Custom exercise sources
- External API integration
- Wearable device sync
- Cloud storage providers

---

**This architecture ensures Luftr is maintainable, testable, and scalable!** 🏗️
