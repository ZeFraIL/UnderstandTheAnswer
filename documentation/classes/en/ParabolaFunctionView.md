# Class Documentation: ParabolaFunctionView

---

## 1. General Information
- **Class Name:** `ParabolaFunctionView`
- **Type:** Custom View (Graphics / Canvas Component)
- **Class Assignment:** Renders coordinate grid, target parabola $y = a_0 x^2 + b_0 x + c_0$, and 3 interactive hint tools (Template Transformer, Axis of Symmetry, and 3 Points on Parabola).
- **Interaction with Components:** Hosted in `fragment_parabola_function.xml` and controlled by `ParabolaFunctionFragment`.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `targetA`, `targetB`, `targetC` | `double` | Target parabola parameters. | `onDraw` and touch calculations. |
| `targetVertexX`, `targetVertexY` | `double` | Vertex coordinates $(x_0, y_0)$. | Anti-cheat vertex checks. |
| `templateA`, `tmplX0`, `tmplY0` | `double / int` | Template parabola parameters. | Mode 1 drawing and dragging. |
| `axisX` | `int` | X-position of vertical symmetry axis. | Mode 2 drawing and dragging. |
| `revealedPoints` | `List<Point>` | Revealed points on target parabola. | Mode 3 drawing and tapping. |

---

## 3. Class Methods

### Method: `setTargetFunction(double a, double b, double c)`
- **Method Type:** `public`
- **Return Value:** `void`
- **What it does:** Calculates vertex $(x_0, y_0)$, sets target parabola, resets template vertex away from target, and invalidates view.

### Method: `onTouchEvent(MotionEvent event)`
- **Method Type:** `public`
- **Return Value:** `boolean`
- **What it does:**
  - *Mode 1:* Handles dragging template vertex $(x_0^{tmpl}, y_0^{tmpl})$ with anti-cheat radius $R \ge 2.0$.
  - *Mode 2:* Handles dragging vertical axis line $x = x_{axis}$. Fires match event when $x_{axis} == x_0$.
  - *Mode 3:* Detects taps on target parabola to reveal up to 3 integer points $M_1, M_2, M_3$.

### Method: `onDraw(Canvas canvas)`
- **Method Type:** `protected`
- **Return Value:** `void`
- **What it does:** Draws grid, axes, target blue parabola, anti-cheat warning circle (if triggered), and mode-specific overlays (template parabola, axis of symmetry, or 3 revealed points).

---

## 4. Lifecycle
Custom View lifecycle: `onMeasure()` -> `onDraw()`.

---

## 5. Interface Interaction (UI)
Handles drag and tap gestures on Canvas.

---

## 6. Interaction with Other Components
Notifies `ParabolaFunctionFragment` via `OnParabolaChangeListener`.

---

## 7. General Logic of the Class
Draws target parabola $y = ax^2 + bx + c$ and provides 3 interactive tools to investigate its vertex, axis of symmetry, and points.

---

## 8. Explanation in Simple Words
`ParabolaFunctionView` is a **parabola workshop**. You can drag a template parabola, slide a vertical symmetry axis, or tap points to figure out $a, b, c$!
