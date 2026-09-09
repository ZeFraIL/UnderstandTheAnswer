# Class Documentation: MainActivity

---

## 1. General Information
- **Class Name:** `MainActivity`
- **Type:** Activity (Host Activity)
- **Class Assignment:** Hosts topic fragments (`CoordinatePointFragment`, `LinearFunctionFragment`, `ParabolaFunctionFragment`) inside a `FragmentContainerView` and displays top toolbar with topic title and contextual help dialogs.
- **Interaction with Components:** Receives intent extras from `TopicSelectionActivity`, loads corresponding Fragment, and displays topic rules dialogs.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `currentTopicId` | `String` | Stores current active topic ID. | Used throughout `MainActivity` to set toolbar title and help dialog content. |

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
  1. Reads `EXTRA_TOPIC_ID` from Intent.
  2. Sets up layout `activity_main.xml` and edge-to-edge window insets.
  3. Configures `Toolbar`: sets title based on `currentTopicId`, sets navigation click (finish activity), inflates `R.menu.menu_main`, and attaches menu listener for `@+id/action_help`.
  4. If `savedInstanceState == null`, instantiates and commits corresponding fragment into `@+id/fragment_container`.
- **When called:** Invoked when `MainActivity` is created.

### Method: `showHelpDialog()`
- **Method Type:** `public`
- **Return Value:** `void`
- **Parameters:** None.
- **What the method does:**
  1. Selects string resource IDs for title and content based on `currentTopicId`.
  2. Formats HTML text using `Html.fromHtml(..., Html.FROM_HTML_MODE_COMPACT)`.
  3. Displays a `MaterialAlertDialogBuilder` with rules and a "Close" button.
- **When called:** Triggered when user clicks help icon `?` in toolbar.

---

## 4. Lifecycle (Activity)
- **`onCreate()`:**
  - *When called:* Screen creation.
  - *What happens:* Configures toolbar, reads topic ID, and loads initial Fragment.

---

## 5. Interface Interaction (UI)
- **UI Elements:**
  - `Toolbar` (`@+id/toolbar`): Top bar showing title, back navigation arrow, and help action icon.
  - `FragmentContainerView` (`@+id/fragment_container`): Host container for fragments.

---

## 6. Interaction with Other Components
- **Fragments:** Dynamically manages `CoordinatePointFragment`, `LinearFunctionFragment`, or `ParabolaFunctionFragment`.

---

## 7. General Logic of the Class
`MainActivity` serves as a frame. It loads the requested lesson fragment into the screen body and provides a top bar with a help button `?`.

---

## 8. Explanation in Simple Words
`MainActivity` is like a **picture frame with a title bar at the top**. The picture inside the frame changes depending on what lesson you picked!
