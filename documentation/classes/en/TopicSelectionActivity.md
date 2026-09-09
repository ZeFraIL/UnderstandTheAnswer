# Class Documentation: TopicSelectionActivity

---

## 1. General Information
- **Class Name:** `TopicSelectionActivity`
- **Type:** Activity (Screen Component)
- **Class Assignment:** Displays the list of available math topics ("Point in Coordinate System", "Linear Function", "Quadratic Function") as Material cards.
- **Interaction with Components:** Launched from `StartActivity`. Passes selected topic ID to `MainActivity` via Intent extras.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `EXTRA_TOPIC_ID` | `String` (static final) | Intent extra key for topic ID. | Used in `TopicSelectionActivity` and `MainActivity`. |
| `TOPIC_POINT_COORDINATE` | `String` (static final) | Identifier for "Point in Coordinate System" topic. | Used in `TopicSelectionActivity` and `MainActivity`. |
| `TOPIC_LINEAR_FUNCTION` | `String` (static final) | Identifier for "Linear Function" topic. | Used in `TopicSelectionActivity` and `MainActivity`. |
| `TOPIC_QUADRATIC_FUNCTION` | `String` (static final) | Identifier for "Quadratic Function" topic. | Used in `TopicSelectionActivity` and `MainActivity`. |

---

## 3. Class Methods

### Method: `onCreate(Bundle savedInstanceState)`
- **Method Type:** `protected`
- **Return Value:** `void`
- **Parameters:**
  | Name | Type | Description |
  | :--- | :--- | :--- |
  | `savedInstanceState` | `Bundle` | Saved instance state bundle. |
- **What the method does (in detail):**
  1. Calls `super.onCreate(savedInstanceState)`.
  2. Enables `EdgeToEdge` display and inflates `R.layout.activity_topic_selection`.
  3. Configures system insets listener on `@+id/topic_selection_main`.
  4. Finds `cardTopicPoint`, `cardTopicLinear`, and `cardTopicQuadratic`.
  5. Attaches click listeners to each card to launch `MainActivity` with its corresponding `EXTRA_TOPIC_ID`.
- **When called:** Invoked by Android OS when activity is created.
- **What is important to understand:** Ensures intent extras match topic constants so `MainActivity` inflates the correct fragment.

---

## 4. Lifecycle (Activity)
- **`onCreate()`:**
  - *When called:* When topic selection screen is opened.
  - *What happens:* Sets layout, applies window padding, and wires topic card click listeners.

---

## 5. Interface Interaction (UI)
- **UI Elements Used:**
  - `MaterialCardView` (`@+id/cardTopicPoint`, `@+id/cardTopicLinear`, `@+id/cardTopicQuadratic`): Interactive cards for selecting topics.
- **Handled Events:** Card click events launching `MainActivity`.

---

## 6. Interaction with Other Components
- **Intents:** Sends `Intent` to `MainActivity` carrying `EXTRA_TOPIC_ID`.

---

## 7. General Logic of the Class
The user selects a math topic card. The activity attaches the topic ID to an `Intent` and launches `MainActivity`.

---

## 8. Explanation in Simple Words
`TopicSelectionActivity` is like a **table of contents** in a textbook. You look at the chapters ("Point in Coordinate System", "Linear Function", "Quadratic Function") and tap on the chapter you want to study!
