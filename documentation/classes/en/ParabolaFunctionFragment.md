# Class Documentation: ParabolaFunctionFragment

---

## 1. General Information
- **Class Name:** `ParabolaFunctionFragment`
- **Type:** Fragment (UI Lesson Controller)
- **Class Assignment:** Manages lesson logic for "Quadratic Function": problem generation, mode switching (`ChipGroup`), math expression parsing, and system of 3 equations dialogs.
- **Interaction with Components:** Hosted in `MainActivity`, controls `ParabolaFunctionView`, uses `MathExpressionEvaluator`.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `targetA`, `targetB`, `targetC` | `double` | Target parabola parameters. | Problem generation and answer verification. |
| `parabolaFunctionView` | `ParabolaFunctionView` | Custom view reference. | Graphic updates and mode control. |
| `etA`, `etB`, `etC` | `EditText` | User input fields for $a, b, c$. | Answer evaluation. |
| `cardTopCalcInfo` | `MaterialCardView` | Top interactive card for formula preview. | Mode 2 & Mode 3 info and dialog triggers. |

---

## 3. Class Methods

### Method: `onCreateView(...)`
- **Method Type:** `public`
- **Return Value:** `View`
- **What it does:** Inflates layout, binds views, attaches `ChipGroup` mode selector, sets up `ParabolaFunctionView` listener, and calls `generateNewTask()`.

### Method: `generateNewTask()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Selects random $a_0 \in \{\pm 1, \pm 2, \pm 0.5\}$ and vertex $(x_0, y_0) \in [-5, 5]$, computes $b_0, c_0$, updates `parabolaFunctionView`, and clears input fields.

### Method: `checkAnswer()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Evaluates $a, b, c$ expressions using `MathExpressionEvaluator.evaluate(...)` and checks against $a_0, b_0, c_0$.

### Method: `showInteractiveSystemDialog(List<Point> points)`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Opens a workpad dialog with system of 3 equations and live expression evaluation where students input expressions for $a, b, c$ or view step-by-step solutions.

---

## 4. Lifecycle (Fragment)
`onCreateView()` initializes views, mode listeners, and generates initial task.

---

## 5. Interface Interaction (UI)
Binds `ParabolaFunctionView`, `ChipGroup`, `MaterialSwitch`es, `EditText` inputs with expression evaluators, and dialogs.

---

## 6. Interaction with Other Components
Embedded in `MainActivity`.

---

## 7. General Logic of the Class
Generates random quadratic function tasks, provides 3 hint tools, parses mathematical expressions, and checks student answers.

---

## 8. Explanation in Simple Words
`ParabolaFunctionFragment` is a **parabola trainer**. It hides a parabola, lets you use 3 hint tools to inspect its shape, and evaluates your $a, b, c$ answers!
