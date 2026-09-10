# Class Documentation: LinearFunctionFragment

---

## 1. General Information
- **Class Name:** `LinearFunctionFragment`
- **Type:** Fragment (UI Lesson Controller)
- **Class Assignment:** Manages lesson logic for "Linear Function": problem generation, mode switching (`ChipGroup`), math expression parsing, interactive calculation dialogs.
- **Interaction with Components:** Hosted in `MainActivity`, controls `LinearFunctionView`, uses `MathExpressionEvaluator`.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `targetK`, `targetB` | `double` | Target linear function parameters $y = k_0 x + b_0$. | Problem generation and answer verification. |
| `linearFunctionView` | `LinearFunctionView` | Custom view reference. | Graphic updates and mode control. |
| `etK`, `etB` | `EditText` | User input fields for $k$ and $b$. | Answer evaluation. |
| `cardTopCalcInfo` | `MaterialCardView` | Top interactive card for formula preview. | Mode 2 & Mode 3 info and dialog triggers. |

---

## 3. Class Methods

### Method: `onCreateView(...)`
- **Method Type:** `public`
- **Return Value:** `View`
- **What it does:** Inflates layout `fragment_linear_function.xml` (using `CoordinatorLayout` and persistent `BottomSheetBehavior`), binds views, attaches `ChipGroup` mode selector, sets up `LinearFunctionView` listener, and calls `generateNewTask()`.

### Method: `generateNewTask()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Selects random $k_0 \in \{-3..3, \pm 0.5\}$ and $b_0 \in [-8, 8]$, updates `linearFunctionView`, and clears input fields.

### Method: `checkAnswer()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Evaluates $k$ and $b$ expressions using `MathExpressionEvaluator.evaluate(...)` and checks against `targetK` and `targetB`.

### Method: `showInteractiveCalcDialog(Point p1, Point p2)`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Opens a workpad dialog with live evaluation preview where students input expressions for $k$ and $b$ or view step-by-step solutions.

---

## 4. Lifecycle (Fragment)
`onCreateView()` initializes views, mode listeners, and generates initial task.

---

## 5. Interface Interaction (UI)
- **Layout Architecture:** `CoordinatorLayout` root.
  - Pinned Top Area: `LinearFunctionView` and `cardTopCalcInfo` stay fixed at top of screen without scrolling.
  - Persistent Bottom Sheet (`bottomSheetCard`): Anchored at bottom with `peekHeight="190dp"` containing drag handle, input fields $k, b$, action buttons, `ChipGroup` mode selector, grid switches, and feedback text.
- **Handled Events:** Button clicks, chip selection, toggle switches, and touch gestures on Canvas.

---

## 6. Interaction with Other Components
Embedded in `MainActivity`.

---

## 7. General Logic of the Class
Generates random linear function tasks, provides 3 hint tools, parses mathematical expressions, and checks student answers inside a persistent bottom sheet controls panel.

---

## 8. Explanation in Simple Words
`LinearFunctionFragment` is a **math tutor**. It gives you a secret line pinned on the top board, lets you use 3 hint tools in a pull-up bottom drawer, and checks your math formulas!
