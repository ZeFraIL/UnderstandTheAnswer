# Class Documentation: LinearFunctionView

---

## 1. General Information
- **Class Name:** `LinearFunctionView`
- **Type:** Custom View (Graphics / Canvas Component)
- **Class Assignment:** Renders coordinate grid, target linear function $y = k_0 x + b_0$, and 3 interactive hint modes (Parallel line shift, Line through 2 points, and Revealed points on graph).
- **Interaction with Components:** Hosted in `fragment_linear_function.xml` and controlled by `LinearFunctionFragment`.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `targetK`, `targetB` | `double` | Target linear function parameters $y = k_0 x + b_0$. | `onDraw` and touch calculations. |
| `currentMode` | `Mode` (Enum) | Active hint tool (`PARALLEL_LINE`, `TWO_POINTS`, `POINTS_ON_LINE`). | `onDraw` and `onTouchEvent`. |
| `parallelB` | `double` | Intercept of parallel hint line. | Mode 1 drawing and dragging. |
| `userP1`, `userP2` | `Point` | Draggable points $P_1, P_2$ for custom line. | Mode 2 drawing and dragging. |
| `revealedPoints` | `List<Point>` | Points revealed on target graph. | Mode 3 drawing and tapping. |

---

## 3. Class Methods

### Method: `setTargetFunction(double k, double b)`
- **Method Type:** `public`
- **Return Value:** `void`
- **What it does:** Sets target line $y = kx + b$, resets hint state, and invalidates view.

### Method: `onTouchEvent(MotionEvent event)`
- **Method Type:** `public`
- **Return Value:** `boolean`
- **What it does:**
  - *Mode 1:* Handles vertical dragging of parallel line $y = kx + parallelB$ with anti-cheat $|parallelB - targetB| \ge 2.0$.
  - *Mode 2:* Handles dragging user points $P_1$ and $P_2$, re-calculating user line $y = k_{user}x + b_{user}$.
  - *Mode 3:* Detects taps on target line to reveal up to 4 integer points $M_1..M_4$.

### Method: `onDraw(Canvas canvas)`
- **Method Type:** `protected`
- **Return Value:** `void`
- **What it does:** Draws grid, axes, target blue line $y = k_0 x + b_0$, anti-cheat warning corridor (if triggered), and mode-specific overlays (parallel line, 2-point line, or revealed points).

---

## 4. Lifecycle
Custom View lifecycle: `onMeasure()` -> `onDraw()`.

---

## 5. Interface Interaction (UI)
Handles drag and tap gestures on Canvas.

---

## 6. Interaction with Other Components
Notifies `LinearFunctionFragment` via `OnLineChangeListener`.

---

## 7. General Logic of the Class
Draws a target line and provides 3 interactive tools to help students discover slope $k$ and intercept $b$.

---

## 8. Explanation in Simple Words
`LinearFunctionView` is an **interactive line plotter**. You can slide parallel lines or stretch lines through two points to see how slope $k$ and shift $b$ change!
