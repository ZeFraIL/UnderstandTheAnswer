# Class Documentation: StartActivity

---

## 1. General Information
- **Class Name:** `StartActivity`
- **Type:** Activity (Android Entry Point / Launcher Screen)
- **Class Assignment:** `StartActivity` serves as the initial landing screen of the application. It displays the branding logo, application title, reverse-math methodology explanation, language selection toggle buttons (English / Russian), and a "Start" button to launch topic selection.
- **Interaction with Components:** Initializes `LanguageManager` before layout inflation, reads and applies locale settings, and launches `TopicSelectionActivity` via an `Intent`.

---

## 2. Variables (Class Fields)
*(This class does not declare member fields; it uses local variables within `onCreate`).*

---

## 3. Class Methods

### Method: `onCreate(Bundle savedInstanceState)`
- **Method Type:** `protected`
- **Return Value:** `void` (does not return a value).
- **Parameters:**
  | Name | Type | Description |
  | :--- | :--- | :--- |
  | `savedInstanceState` | `Bundle` | Previously saved instance state bundle when activity is recreated. |
- **What the method does (in detail):**
  1. Calls `LanguageManager.initLanguage(this)` before layout inflation to apply the saved application locale.
  2. Calls `super.onCreate(savedInstanceState)` and `EdgeToEdge.enable(this)`.
  3. Inflates the layout XML `R.layout.activity_start`.
  4. Configures `WindowInsetsListener` on `@+id/start_main` to apply system bar padding.
  5. Finds `toggleGroupLanguage` and checks the button corresponding to `LanguageManager.getLanguage(this)`.
  6. Attaches `addOnButtonCheckedListener` to `toggleGroupLanguage` to switch app locale via `LanguageManager.setLanguage(...)` when a user selects English or Russian.
  7. Attaches `setOnClickListener` to `btnStart` to launch `TopicSelectionActivity`.
- **When called:** Automatically invoked by Android OS when `StartActivity` is created.
- **What is important to understand:** `LanguageManager.initLanguage(this)` MUST be called before `super.onCreate()` and `setContentView()` so the layout inflates using the correct language resources.

---

## 4. Lifecycle (Activity)
- **`onCreate()`:**
  - *When called:* When the activity is first created.
  - *What happens:* Initializes language, enables Edge-to-Edge display, inflates UI, wires language toggle group and start button listeners.

---

## 5. Interface Interaction (UI)
- **UI Elements Used:**
  - `ImageView` (`@+id/ivAppIcon`): Displays `app_icon.png`.
  - `TextView` (`@+id/tvAppTitle`, `@+id/tvAppSubtitle`): Displays app title and subtitle.
  - `MaterialCardView` (`@+id/cardDescription`): Displays app methodology description.
  - `MaterialButtonToggleGroup` (`@+id/toggleGroupLanguage`): Language selector group containing `btnLangEn` and `btnLangRu`.
  - `MaterialButton` (`@+id/btnStart`): "Start" button.
- **Handled Events:** Button check events on language toggle and click event on "Start" button.

---

## 6. Interaction with Other Components
- **Intents:** Creates an `Intent` targeting `TopicSelectionActivity.class` and calls `startActivity(intent)`.
- **Data Transfer:** Applies global app locale via `LanguageManager`.

---

## 7. General Logic of the Class
1. `StartActivity` opens as the launcher screen.
2. It checks saved language preference (defaults to English on first run).
3. If the user clicks "Русский", the locale updates on the fly to Russian.
4. Tapping "Start" moves the user to `TopicSelectionActivity`.

---

## 8. Explanation in Simple Words
Think of `StartActivity` as the **front door and reception desk** of a museum. Before entering the exhibition rooms, you stand at the front door where you see the museum's logo, choose what language guide book you want (English or Russian), and press the "Enter" button to step inside the hallway!
