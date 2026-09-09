# Class Documentation: CoordinatePlaneView

---

## 1. General Information
- **Class Name:** `CoordinatePlaneView`
- **Type:** Custom View (Graphics / Canvas Component)
- **Class Assignment:** Renders a $[-15..+15]$ coordinate plane, axes, arrows, numbers, target point $A$, interactive hint points ($H_1, H_2$), and anti-cheat warning zone.
- **Interaction with Components:** Used inside `fragment_coordinate_point.xml` and controlled by `CoordinatePointFragment`.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `MIN_VAL` / `MAX_VAL` | `int` (static final) | Grid bounds ($-15$ and $+15$). | Canvas drawing and coordinate clamping. |
| `FORBIDDEN_RADIUS` | `double` (static final) | Anti-cheat radius ($2.0$ units). | Distance checking in `addHintPoint` and `isTooCloseToTarget`. |
| `pointX`, `pointY` | `int` | Coordinates of secret target point $A$. | `onDraw` and anti-cheat checking. |
| `hintPoints` | `List<Point>` | List of placed hint points $H_1, H_2$. | `onDraw` and touch gestures. |
| `draggedHintIndex` | `int` | Index of currently dragged hint point. | `onTouchEvent`. |
| `isGridVisible` / `isAxisValuesVisible` | `boolean` | Visibility toggles for grid lines and numbers. | `onDraw`. |
| `showForbiddenWarning` | `boolean` | Flag for translucent red anti-cheat warning zone. | `onDraw` and `triggerForbiddenWarning`. |

---

## 3. Class Methods

### Method: `setPoint(int x, int y)`
- **Method Type:** `public`
- **Return Value:** `void`
- **Parameters:** $x, y$ (target point coordinates).
- **What it does:** Sets secret target point $A(x, y)$, clears existing hint points, and calls `invalidate()`.

### Method: `addHintPoint(int x, int y)`
- **Method Type:** `public`
- **Return Value:** `boolean` (`true` if point added, `false` if blocked).
- **Parameters:** $x, y$ (grid coordinates).
- **What it does:** Checks anti-cheat radius $R \le 2.0$. If too close, calls `triggerForbiddenWarning()` and returns `false`. If allowed and count $< 2$, adds point and re-renders.

### Method: `onTouchEvent(MotionEvent event)`
- **Method Type:** `public`
- **Return Value:** `boolean`
- **What it does:** Converts touch pixel coordinates to integer grid coordinates. Handles dragging existing hint points or tapping to place a new hint point. Calls `requestDisallowInterceptTouchEvent(true)` during drag.

### Method: `onDraw(Canvas canvas)`
- **Method Type:** `protected`
- **Return Value:** `void`
- **What it does:** Draws white background, grid lines, black axes, arrows, X/Y labels, number marks, translucent red anti-cheat zone (if triggered), hint points $H_1, H_2$, and target point $A$.

---

## 4. Lifecycle
Custom View lifecycle: `onMeasure()` -> `onDraw()`.

---

## 5. Interface Interaction (UI)
Responds to touch gestures on Canvas.

---

## 6. Interaction with Other Components
Communicates with `CoordinatePointFragment` via `OnPointTapListener` and `OnPointDragListener`.

---

## 7. General Logic of the Class
Renders an interactive math grid. Converts touch pixels to grid units and updates hint points with anti-cheat protection.

---

## 8. Explanation in Simple Words
`CoordinatePlaneView` is an **interactive digital whiteboard**. It draws graph paper, axes, and points, and lets you drag blue hint points with your finger!
