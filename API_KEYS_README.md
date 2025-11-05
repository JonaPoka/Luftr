# API Keys Configuration

This application uses external APIs for enhanced functionality. To use the full features, you'll need to obtain and configure the following API keys:

## Required API Keys

### 1. Groq API Key (AI Workout Generation)

**Purpose**: Powers the AI workout generation using Llama 3 model via Groq Cloud

**How to obtain**:
1. Visit https://console.groq.com/
2. Sign up for a free account
3. Navigate to API Keys section
4. Create a new API key
5. Copy the key (starts with `gsk_...`)

**Where to add**:
- File: `app/src/main/java/com/jonapoka/luftr/data/api/ApiClient.kt`
- Line: `const val GROQ_API_KEY = "gsk_placeholder_key_will_be_updated"`
- Replace `"gsk_placeholder_key_will_be_updated"` with your actual Groq API key

**Example**:
```kotlin
const val GROQ_API_KEY = "gsk_YOUR_ACTUAL_GROQ_API_KEY_HERE"
```

### 2. ExerciseDB API Key (Exercise Images & GIFs)

**Purpose**: Provides exercise images, animated GIFs, and instructions

**How to obtain**:
1. Visit https://rapidapi.com/justin-WFnsXH_t6/api/exercisedb
2. Sign up for a RapidAPI account
3. Subscribe to the ExerciseDB API (there's a free tier)
4. Copy your RapidAPI key from the dashboard

**Where to add**:
- File: `app/src/main/java/com/jonapoka/luftr/data/api/ApiClient.kt`
- Line: `const val EXERCISEDB_API_KEY = "placeholder_rapidapi_key"`
- Replace `"placeholder_rapidapi_key"` with your actual RapidAPI key

**Example**:
```kotlin
const val EXERCISEDB_API_KEY = "YOUR_ACTUAL_RAPIDAPI_KEY_HERE"
```

## Current Status

⚠️ **WARNING**: The application currently has placeholder API keys. Features requiring API access will fall back to local/mock data:

- **AI Workout Generation**: Will use local algorithm-based generation
- **Exercise Media**: Will show placeholder images

## Security Notes

🔒 **For Production**:
- Never commit real API keys to version control
- Use environment variables or secure configuration management
- Consider using Android's `BuildConfig` for key management
- Implement proper key rotation policies

## Testing Without API Keys

The app is designed to work without API keys by falling back to:
- Local workout generation algorithm
- Placeholder exercise images with gradient backgrounds
- Pre-defined exercise instructions

This ensures the app remains functional even without API access.

## Future Improvements

- [ ] Move API keys to `local.properties` (excluded from version control)
- [ ] Use BuildConfig to inject keys at build time
- [ ] Implement proper key management with Android Keystore
- [ ] Add user-facing API key configuration in Settings
