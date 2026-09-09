# Class Documentation: CoordinatePointFragment

---

## 1. General Information
- **Class Name:** `CoordinatePointFragment`
- **Type:** Fragment (UI Lesson Controller)
- **Class Assignment:** Manages lesson logic for "Point in Coordinate System": generates random secret points $A$, handles input validation, gives quadrant hints, and shows feedback.
- **Interaction with Components:** Hosted inside `MainActivity`, controls `CoordinatePlaneView`.

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `targetX`, `targetY` | `int` | Coordinates of secret target point $A$. | Problem generation and answer checking. |
| `coordinatePlaneView` | `CoordinatePlaneView` | Custom Canvas view reference. | Graphic updates. |
| `etCoordX`, `etCoordY` | `EditText` | User coordinate input fields. | Answer checking. |
| `tvFeedback` | `TextView` | Status and feedback label. | Displaying hints and messages. |

---

## 3. Class Methods

### Method: `onCreateView(...)`
- **Method Type:** `public`
- **Return Value:** `View` (Inflated fragment layout).
- **What it does:** Inflates layout, binds views, attaches grid/axis switches, wires buttons ("Check", "Hint", "New Point"), and calls `generateNewPoint()`.

### Method: `generateNewPoint()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Generates random integer coordinates $A(x, y)$ in $[-14, 14]$ (excluding origin), passes them to `coordinatePlaneView.setPoint(targetX, targetY)`, and clears input fields.

### Method: `checkAnswer()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Reads input $X$ and $Y$, compares with `targetX` and `targetY`, and displays success or error feedback and Toast.

### Method: `giveHint()`
- **Method Type:** `private`
- **Return Value:** `void`
- **What it does:** Displays the quadrant (I, II, III, IV) or axis where target point $A$ lies.

---

## 4. Lifecycle (Fragment)
- `onCreateView()`: Inflates layout, initializes controls, generates initial problem.

---

## 5. Interface Interaction (UI)
Binds `CoordinatePlaneView`, `EditText` inputs, `MaterialSwitch` toggles, and Action Buttons.

---

## 6. Interaction with Other Components
Embedded inside `MainActivity`.

---

## 7. General Logic of the Class
Generates a secret point $A$ on the grid, processes user hints and drag gestures, and evaluates submitted coordinates.

---

## 8. Explanation in Simple Words
`CoordinatePointFragment` is like a **game referee**. It chooses where the secret point $A$ is hidden, checks your guesses, and gives you helpful hints!
