# Luftr Testing Guide

## Manual Testing Checklist

Use this guide to verify all features work correctly after building the app.

### 1. Initial Launch
- [ ] App launches without crashing
- [ ] Home screen displays correctly
- [ ] Material You colors apply (check if they match your device theme)
- [ ] No workouts message appears
- [ ] Quick action cards are visible and styled correctly
- [ ] FAB (+ button) is visible in bottom right

### 2. Create New Workout
**Test creating a custom workout**:
- [ ] Tap FAB on home screen
- [ ] Dialog appears with text field
- [ ] Enter workout name (e.g., "Morning Workout")
- [ ] Confirm button works
- [ ] Navigate to Active Workout screen
- [ ] Screen shows empty state with "Add your first exercise" message

### 3. Add Exercises
**Test adding exercises to workout**:
- [ ] Tap FAB on Active Workout screen
- [ ] Add Exercise dialog appears
- [ ] Enter exercise name (e.g., "Bench Press")
- [ ] Open muscle group dropdown
- [ ] Select muscle group (e.g., "Chest")
- [ ] Confirm to add exercise
- [ ] Exercise card appears in list
- [ ] Exercise shows correct name and muscle group

**Repeat to add multiple exercises**:
- [ ] Add "Squats" (Legs)
- [ ] Add "Pull-ups" (Back)
- [ ] Verify exercises appear in order added

### 4. Add Sets
**Test set tracking**:
- [ ] Tap "Add Set" on first exercise
- [ ] Dialog appears with reps and weight fields
- [ ] Enter reps (e.g., 10)
- [ ] Enter weight (e.g., 60.5)
- [ ] Confirm
- [ ] Set appears in table with correct values
- [ ] Set number is 1

**Add multiple sets**:
- [ ] Add set 2: 8 reps, 65kg
- [ ] Add set 3: 6 reps, 70kg
- [ ] Verify set numbers increment correctly
- [ ] Verify all data displays correctly

**Test set deletion**:
- [ ] Tap delete icon on a set
- [ ] Set is removed immediately
- [ ] Other sets remain

### 5. Finish Workout
**Test workout completion**:
- [ ] Tap checkmark icon in top bar
- [ ] Confirmation dialog appears
- [ ] Confirm finish workout
- [ ] Returns to home screen
- [ ] Recent workouts section shows the completed workout
- [ ] Workout name matches what you entered
- [ ] Exercise count is correct

### 6. AI Workout Planner
**Test AI workout generation**:

**Step 1 - Fitness Goal**:
- [ ] Tap "Create AI Workout" card on home
- [ ] AI Planner screen loads
- [ ] Progress bar shows 25%
- [ ] Four goal options display
- [ ] Tap "Build Muscle"
- [ ] Card highlights in primary color
- [ ] Next button becomes enabled

**Step 2 - Experience Level**:
- [ ] Tap "Next"
- [ ] Progress bar shows 50%
- [ ] Three level options display
- [ ] Tap "Intermediate"
- [ ] Next button enabled

**Step 3 - Duration**:
- [ ] Tap "Next"
- [ ] Progress bar shows 75%
- [ ] Four duration options display
- [ ] Tap "60 minutes"
- [ ] Next button enabled

**Step 4 - Muscle Groups**:
- [ ] Tap "Next"
- [ ] Progress bar shows 100%
- [ ] Six muscle group options display
- [ ] Tap "Chest"
- [ ] Card highlights
- [ ] Tap "Back"
- [ ] Both cards highlighted
- [ ] Generate Workout button enabled

**Generation**:
- [ ] Tap "Generate Workout"
- [ ] Loading indicator appears
- [ ] Navigate to Active Workout screen
- [ ] Workout name is auto-generated (e.g., "Chest & Back Hypertrophy")
- [ ] Exercises are pre-populated
- [ ] Exercise count is appropriate for 60 minutes (≈8 exercises)
- [ ] Exercises match selected muscle groups
- [ ] All exercises have "Add Set" button

**Test Generated Workout**:
- [ ] Add sets to AI-generated exercises
- [ ] Verify sets save correctly
- [ ] Finish the workout
- [ ] Check home screen shows AI badge (✨) next to workout name

### 7. Workout History
**Test history view**:
- [ ] Tap "View History" card on home
- [ ] History screen displays
- [ ] Statistics card shows:
  - [ ] Total workouts count
  - [ ] This week count
- [ ] Workout cards display below stats
- [ ] Each card shows:
  - [ ] Workout name
  - [ ] Date (formatted nicely)
  - [ ] Exercise count
  - [ ] Total sets
  - [ ] AI badge if applicable
- [ ] Tap a workout card
- [ ] Navigate to Active Workout screen (read-only view)

### 8. Navigation
**Test app navigation**:
- [ ] Back button works from all screens
- [ ] Home button returns to home from all screens
- [ ] Navigation is smooth without lag
- [ ] No crashes during navigation
- [ ] Screen state persists when navigating back

### 9. Data Persistence
**Test data is saved**:
- [ ] Create and finish a workout
- [ ] Close the app completely (swipe from recent apps)
- [ ] Reopen the app
- [ ] Verify workout appears in recent workouts
- [ ] Tap the workout
- [ ] Verify all exercises are saved
- [ ] Verify all sets are saved with correct data

### 10. UI/UX Testing

**Material You Theme**:
- [ ] Colors match device theme
- [ ] Dark mode works (if device uses dark mode)
- [ ] Light mode works (if device uses light mode)
- [ ] Text is readable on all backgrounds
- [ ] Icons are clear and visible

**Responsiveness**:
- [ ] Test on different screen sizes if possible
- [ ] Rotate device (portrait/landscape)
- [ ] UI adapts correctly
- [ ] No text cutoff
- [ ] Cards scale appropriately

**Touch Targets**:
- [ ] All buttons are easy to tap
- [ ] Touch targets are large enough
- [ ] No accidental taps
- [ ] FABs don't overlap content

**Animations**:
- [ ] Smooth transitions between screens
- [ ] Dialog animations work
- [ ] List scrolling is smooth
- [ ] No janky animations

### 11. Edge Cases

**Empty States**:
- [ ] Empty recent workouts shows appropriate message
- [ ] Empty active workout shows appropriate message
- [ ] Empty history shows appropriate message

**Long Text**:
- [ ] Enter very long workout name (50+ characters)
- [ ] Enter very long exercise name
- [ ] Verify text doesn't break layout
- [ ] Text truncates or wraps appropriately

**Large Numbers**:
- [ ] Enter 999 reps
- [ ] Enter 999.99 kg weight
- [ ] Verify numbers display correctly

**Zero/Invalid Input**:
- [ ] Try entering 0 reps
- [ ] Try entering negative weight
- [ ] Try entering letters in number fields
- [ ] Verify validation prevents invalid data

**Multiple Workouts**:
- [ ] Create 10+ workouts
- [ ] Verify list scrolls smoothly
- [ ] Verify all workouts appear
- [ ] No performance degradation

### 12. Performance Testing

**Database Operations**:
- [ ] Adding exercises is instant
- [ ] Adding sets is instant
- [ ] Loading workouts is fast
- [ ] No lag when scrolling lists

**Memory**:
- [ ] App doesn't crash with many workouts
- [ ] App doesn't slow down over time
- [ ] Switching screens is smooth

**Battery**:
- [ ] App doesn't drain battery abnormally
- [ ] App suspends properly when backgrounded

## Automated Testing (Future)

### Unit Tests
- Database operations
- ViewModel logic
- AI workout generation algorithm
- Data validation

### Integration Tests
- Repository functions
- Database migrations
- End-to-end data flow

### UI Tests
- Screen navigation
- User interactions
- Dialog behavior
- List operations

## Bug Reporting

If you find a bug, please report it with:
1. Device model and Android version
2. Steps to reproduce
3. Expected behavior
4. Actual behavior
5. Screenshots if applicable
6. Logcat output if app crashes

## Test Results Template

```markdown
## Test Results

**Date**: [Date]
**Tester**: [Name]
**Device**: [Model]
**Android Version**: [Version]
**App Version**: [Version]

### Summary
- Total Tests: X
- Passed: X
- Failed: X
- Blocked: X

### Failed Tests
1. [Test Name]: [Description of failure]
2. [Test Name]: [Description of failure]

### Notes
[Any additional observations]
```

---

**Happy Testing! 🧪**
