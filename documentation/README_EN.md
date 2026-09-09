# 📱 Android Application Documentation (LEVEL 10/10)

---

## 🧾 General Information

- **Project Name:** Understand The Answer
- **Author(s):** Zeev Fraiman
- **Date:** May 2026
- **Language:** Java
- **Development Environment:** Android Studio
- **Android Version (minSdk / targetSdk):** minSdk 28 / targetSdk 37

---

## 🎯 Project Goal

- **Problem Solved:** Traditional math learning forces students to mechanically plug numbers into formulas and compute output passively. "Understand The Answer" reverses this process: students are given a rendered graph or output and must reconstruct the initial parameters/conditions using interactive tools.
- **Importance:** Promotes active mathematical intuition, spatial reasoning, critical thinking, and trial-and-hypothesis verification over mechanical rote memorization.
- **Target Audience:** School students (middle and high school), math teachers, tutors, and self-learners practicing coordinate geometry and algebra.

---

## 📌 Application Requirements

### Functional Requirements
- Multi-topic math trainer ("Point in Coordinate System", "Linear Function", "Quadratic Function / Parabola").
- Interactive graphical rendering on custom Canvas views (`CoordinatePlaneView`, `LinearFunctionView`, `ParabolaFunctionView`).
- Interactive hint tools (Drag & drop hint points, Parallel line shifts, 2-point line constructions, Axis of symmetry, Points-on-graph incidence).
- Anti-cheat protection mechanism (forbidden zone radius $R \le 2.0$ preventing users from placing hints directly onto answer targets).
- Real-time arithmetic expression parser (`MathExpressionEvaluator`) allowing mathematical expressions in all input fields.
- Full bilingual localization (English / Russian) with runtime language switching and `SharedPreferences` persistence (defaults to English on first launch).
- Contextual help dialogs providing detailed rules and step-by-step mathematical solutions.

### Non-Functional Requirements
- **Performance:** Smooth 60 FPS Canvas rendering, pre-allocated Paint/Path objects, zero UI thread blocking.
- **Usability:** Intuitive Material 3 design, touch-friendly drag targets, responsive layout across various screen sizes.
- **Reliability:** Strict input validation, zero crash guarantee on invalid syntax or division by zero, robust state management.

---

## 🧠 System Architecture

- **Chosen Approach:** Component-Based Modular View-Fragment Architecture (MVC / Hybrid View-Driven Pattern).
- **Rationale:** Fits Android's native View lifecycle seamlessly, allows light overhead, direct Canvas manipulation, and reusable Fragment-based screen management without third-party framework overhead.
- **Core Components:**
  - `StartActivity`: App launcher, branding logo, language selector, and entry point.
  - `TopicSelectionActivity`: Material card list of math topics.
  - `MainActivity`: Host Activity managing `FragmentContainerView` and top app bar with dynamic help dialogs.
  - `CoordinatePointFragment`, `LinearFunctionFragment`, `ParabolaFunctionFragment`: Interactive lesson logic handlers.
  - `CoordinatePlaneView`, `LinearFunctionView`, `ParabolaFunctionView`: High-performance custom Canvas views.
  - `MathExpressionEvaluator`: Recursive-descent arithmetic expression engine.
  - `LanguageManager`: Locale manager and preference persistence.

---

## 🧩 UML Class Diagram

```
[StartActivity] ──> [LanguageManager] ──> [SharedPreferences]
       │
       ▼
[TopicSelectionActivity]
       │
       ▼
[MainActivity] ──> [HelpDialog / MaterialAlertDialog]
       │
       ├──> [CoordinatePointFragment] ──> [CoordinatePlaneView]
       ├──> [LinearFunctionFragment]  ──> [LinearFunctionView] ──> [MathExpressionEvaluator]
       └──> [ParabolaFunctionFragment] ──> [ParabolaFunctionView] ──> [MathExpressionEvaluator]
```

- **Package Structure:** All classes reside in package `zeev.fraiman.understandtheanswer`, grouping views, fragments, utilities, and activities cleanly.
- **Scalability:** Adding new math topics (e.g., Trigonometry, Hyperbola, Logarithms) requires only creating a new `Fragment` and custom `View`, without touching existing topics.

---

## 🧩 Detailed Description of Classes

### 📌 Class: MainActivity
- **Role:** Main container Activity hosting topic fragments and global app toolbar.
- **Responsibility:** Receives intent extras from `TopicSelectionActivity`, loads the corresponding Fragment, and handles the top action bar Help icon.
- **Key Methods:**
  - `onCreate(Bundle savedInstanceState)` — Initializes layout, sets up EdgeToEdge, configures toolbar and inflates initial fragment.
  - `showHelpDialog()` — Displays a scrollable Material AlertDialog with HTML-formatted topic rules.
- **Interaction with other classes:** Communicates with `TopicSelectionActivity`, embeds `CoordinatePointFragment`, `LinearFunctionFragment`, or `ParabolaFunctionFragment`.

### 📌 Class: StartActivity
- **Role:** Entry point screen.
- **Responsibility:** Displays logo, app title, reverse math methodology description, language toggle group (English / Russian), and "Start" button.
- **Key Methods:**
  - `onCreate(Bundle savedInstanceState)` — Initializes saved language via `LanguageManager`, sets up layout and click listeners.

### 📌 Class: TopicSelectionActivity
- **Role:** Topic selector screen.
- **Responsibility:** Displays available topics in MaterialCards and launches `MainActivity` with selected topic ID.

### 📌 Class: CoordinatePointFragment / LinearFunctionFragment / ParabolaFunctionFragment
- **Role:** Controllers for individual math topics.
- **Responsibility:** Handles random problem generation, binds custom Views, processes input validation, computes step-by-step mathematical solutions, and displays user feedback.

### 📌 Class: CoordinatePlaneView / LinearFunctionView / ParabolaFunctionView
- **Role:** Custom View components for Canvas graphics.
- **Responsibility:** Draws $[-15..+15]$ coordinate grid, axes, arrows, numbers, function graphs, interactive draggable hint points/lines, and anti-cheat forbidden warning zones.

### 📌 Class: MathExpressionEvaluator
- **Role:** Expression parser utility.
- **Responsibility:** Safely evaluates arithmetic expressions (e.g. `(7 - 3) / (4 - 2)`) using a recursive descent parser.

### 📌 Class: LanguageManager
- **Role:** Locale persistence and management.
- **Responsibility:** Reads/writes language choice to `SharedPreferences` (defaults to `"en"`) and applies locale via `AppCompatDelegate`.

---

## 🔄 App Execution Workflow

1. App launches -> `StartActivity` loads saved language (defaults to English on first run).
2. User selects language if desired -> Locale updates on the fly.
3. User taps "Start" -> `TopicSelectionActivity` opens.
4. User picks a topic -> `MainActivity` opens and inflates topic fragment (e.g., `ParabolaFunctionFragment`).
5. App generates random mathematical target conditions.
6. User interacts with custom View (drags hint points, shifts parallel lines, inspects symmetry axis, or reveals points on graph).
7. Anti-cheat prevents user from dragging hints directly onto answer targets ($R \le 2.0$).
8. User inputs parameter expressions -> Real-time evaluator checks answer -> User receives instant feedback and step-by-step calculation dialogs if needed.

---

## 🎨 UI/UX Analysis

- **Design Philosophy:** Clean Material 3 design, minimal clutter.
- **Principles Used:**
  - *Simplicity:* Uncluttered main screen, bulky instructions moved into top-bar Help dialogs.
  - *Logical Flow:* Top-down hierarchy (Canvas -> Switches/Tools -> Input Fields -> Primary Actions).
  - *Accessibility:* Large touch targets ($28\text{dp}+$ for draggable points), high-contrast grid lines, clear feedback toasts and status cards.
- **Future Improvements:** Add dark mode theme customization, animated graph drawing transitions, and voice accessibility.

---

## ⚙️ Thread & State Management

- **Threading Model:** Android Main/UI Thread for lightweight View drawing and UI state updates.
- **Performance Optimization:** High-frequency operations in custom Views avoid object allocations inside `onDraw()` (Paints and Paths pre-allocated in `init()`).
- **ANR & Memory Leak Prevention:** Uses weak/short-lived Context references, avoids static View references, uses `Runnable` callbacks managed via `removeCallbacks()`.

---

## 💾 Data Persistence

- **Storage:** `SharedPreferences` (`app_language_prefs`).
- **Data Saved:** Selected language code (`"en"` or `"ru"`).
- **Reliability:** Asynchronous `apply()` write ensures zero UI jank and instant persistence across app restarts.

---

## 🌐 Network Operations

- **Network Scope:** Offline-first application. No external API calls required.
- **Reliability:** Works 100% offline without internet connection, ensuring privacy and instant performance anywhere.

---

## 🔐 Security (Basic Level)

- **Sensitive Data:** None. No personal data collected or transmitted.
- **Protection:** Internal `SharedPreferences` isolated to application sandbox.

---

## 🧪 Testing

- **Testing Approach:**
  - Automated Gradle compilation and static code analysis (`analyze_file`).
  - Unit evaluation testing for `MathExpressionEvaluator` edge cases (parentheses, division by zero, floating point operations).
  - Manual UI & Gesture testing across different screen aspect ratios.

---

## 🐞 Error Handling

- **Scenarios Handled:**
  - Invalid math syntax -> Friendly warning toast and inline label.
  - Division by zero -> Arithmetic Exception caught and reported as "Division by zero".
  - Empty input -> Guidance message asking for all parameters.
  - Anti-cheat breach -> Translucent red warning zone flash and friendly toast.

---

## ⚡ Performance

- **Optimizations:**
  - Canvas pre-allocation of `Paint`, `Path`, and `Color` objects.
  - `requestDisallowInterceptTouchEvent` for smooth drag gestures inside `ScrollView`.
  - Zero third-party library overhead.

---

## 🚀 Future Extensions

- Adding new math topics: Trigonometry ($y = A \sin(Bx + C)$), Hyperbola ($y = \frac{k}{x}$), Logarithmic/Exponential functions.
- Achievements and gamification system (streaks, stars, score tracking).
- Teacher mode for creating custom assignments and sharing via QR code.
