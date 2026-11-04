# Daily Kharcha (दैनिक खर्चा) 💰

> **Apna Kharcha Track Karein** - A modern, fast, 100% offline native Android expense tracking application designed specifically for Indian users.

---

## 🌟 Key Features

- **100% Offline & Private:** Zero internet permissions declared. No login, no cloud sync, no tracking. All data is stored securely on the local device.
- **Accurate Financial Calculations:** Monetary values are strictly stored as 64-bit integer paise (1 Rupee = 100 Paise) to eliminate floating-point rounding errors.
- **Bilingual Interface (Hinglish):** Natural Hindi-English labels (*"Apna Kharcha Track Karein"*, *"Kharcha Jodein"*, *"Itihaas"*, *"Kul Kharcha"*).
- **Indian Rupee Formatting:** Proper Indian numbering format (e.g. `₹1,25,000.00`) with the `₹` symbol.
- **Interactive Donut / Pie Chart:**
  - Real-time animated category-wise breakdown.
  - Interactive slice selection showing category share (%) and rupee amounts.
  - Clean empty state illustration when starting fresh.
- **Expense Categories:**
  1. 🍛 **Khana (Food)** - Coral Red (`#EF4444`)
  2. ⛽ **Petrol** - Amber Orange (`#F59E0B`)
  3. 📱 **Recharge** - Royal Blue (`#2563EB`)
  4. 🏠 **Kiraya (Rent)** - Purple Violet (`#8B5CF6`)
  5. 🛍️ **Other (Anya)** - Emerald Teal (`#10B981`)
- **Dashboard & Monthly Summary:**
  - Month-by-month switcher (`< November 2026 >`).
  - Total monthly expenditure card with transaction count.
  - Recent transactions list with quick edit & delete actions.
- **Add & Edit Expense:**
  - Automatic selection of today's date with Material 3 Date Picker.
  - Input validation (prevents negative, zero, or non-numeric values).
  - Optional title/purpose and extra notes.
- **Expense History (Kharcha Itihaas):**
  - View all past transactions sorted newest first.
  - Filter by month or view all-time.
  - Category filter chips and fast search bar.
  - Confirmation alert dialog before deletion (*"Kya aap sach me ye kharcha hatana chahte hain?"*).

---

## 🛠️ Technology Stack & Architecture

- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose with Material Design 3
- **Database:** Room Database 2.6+ with SQLite
- **Architecture:** MVVM (Model - View - ViewModel) + Repository Pattern
- **Asynchronous Flow:** Kotlin Coroutines & StateFlow
- **Navigation:** Navigation Compose with animated screen transitions
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts` + `libs.versions.toml`)

---

## 📁 Project Architecture & Package Structure

```
app/src/main/java/com/dailykharcha/app/
├── DailyKharchaApplication.kt   # Application class initializing Room & Repository
├── MainActivity.kt              # Single Activity entry point hosting Compose NavHost
├── data/
│   ├── local/
│   │   ├── ExpenseEntity.kt     # Room entity with amountPaise: Long
│   │   ├── ExpenseDao.kt        # SQLite Data Access Object with reactive Flows
│   │   ├── ExpenseDatabase.kt   # Room Database definition & singleton builder
│   │   └── Converters.kt        # Room TypeConverters for ExpenseCategory
│   ├── model/
│   │   ├── ExpenseCategory.kt   # Category enum (Food, Petrol, Recharge, Rent, Other)
│   │   └── CategorySpending.kt  # Aggregated spending statistics for Pie Chart
│   └── repository/
│       ├── ExpenseRepository.kt     # Repository contract
│       └── ExpenseRepositoryImpl.kt # Repository implementation
├── ui/
│   ├── components/
│   │   ├── PieChart.kt              # Canvas-based interactive Donut / Pie Chart
│   │   ├── SummaryCard.kt           # Grand dashboard summary card
│   │   ├── ExpenseCard.kt           # Individual expense transaction card
│   │   ├── CategoryChip.kt          # Large touch-friendly category selector
│   │   └── ConfirmDeleteDialog.kt   # Material 3 deletion confirmation dialog
│   ├── navigation/
│   │   ├── Screen.kt                # Type-safe navigation routes
│   │   └── AppNavHost.kt            # Jetpack Compose animated navigation graph
│   ├── screens/
│   │   ├── dashboard/
│   │   │   └── DashboardScreen.kt   # Screen 1: Home Dashboard & Pie Chart
│   │   ├── add_edit/
│   │   │   └── AddEditExpenseScreen.kt # Screen 2: Add / Edit Expense Form
│   │   └── history/
│   │       └── HistoryScreen.kt     # Screen 3: Expense History with Filters
│   ├── theme/
│   │   ├── Color.kt                 # Fresh Green & Accent Blue color palette
│   │   ├── Theme.kt                 # Material 3 Light & Dark themes
│   │   └── Type.kt                  # Typography scale
│   └── viewmodel/
│       ├── ExpenseViewModel.kt      # Main ViewModel with StateFlows & CRUD
│       └── ExpenseViewModelFactory.kt # Factory for ViewModel injection
└── util/
    ├── CurrencyFormatter.kt         # Indian Rupee (₹) & Paise conversion utilities
    └── DateFormatter.kt             # Date formatting & friendly relative indicators
```

---

## 🚀 Building & Running the Application

### Option A: Using Android Studio (Recommended)

1. Open **Android Studio** (Koala / Ladybug / Iguana or later).
2. Click **Open** and select the `/Users/ankitpandey/Downloads/day_11_ai` folder.
3. Android Studio will automatically recognize the Gradle project and download the dependencies.
4. Connect an Android device (via USB with USB Debugging enabled) or start an Android Virtual Device (AVD).
5. Click the green **Run (▶)** button or press `Shift + F10`.

### Option B: Using Gradle Command Line

From the root project directory:

```bash
# 1. Run unit tests
./gradlew test

# 2. Build Debug APK
./gradlew assembleDebug
```

The generated installable APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

To install directly to a connected smartphone via ADB:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Unit Testing

Unit tests are included under `app/src/test/java/com/dailykharcha/app/`:
- **`CurrencyFormatterTest`**: Verifies paise-to-rupee formatting, input parsing with commas/symbols, and invalid amount rejections.
- **`DateFormatterTest`**: Verifies bilingual relative dates (*"Aaj"*, *"Kal"*), month names, and start-of-day calculations.
- **`ExpenseCategoryTest`**: Verifies all 5 standard categories, Hindi titles, and fallback behavior.

Run tests via command line:
```bash
./gradlew testDebugUnitTest
```
