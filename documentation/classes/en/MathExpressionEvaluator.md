# Class Documentation: MathExpressionEvaluator

---

## 1. General Information
- **Class Name:** `MathExpressionEvaluator`
- **Type:** Utility Class (Parser Engine)
- **Class Assignment:** Safely evaluates string arithmetic expressions (e.g. `(7 - 3) / (4 - 2)`) using recursive descent parsing.
- **Interaction with Components:** Used in `LinearFunctionFragment` and `ParabolaFunctionFragment` to parse user math inputs.

---

## 2. Variables (Class Fields)
*(Uses local state variables within the recursive descent object).*

---

## 3. Class Methods

### Method: `evaluate(String expression)`
- **Method Type:** `public static`
- **Return Value:** `double` (numeric evaluated result).
- **Parameters:**
  | Name | Type | Description |
  | :--- | :--- | :--- |
  | `expression` | `String` | Raw string input (e.g., `"(7-3)/(4-2)"`). |
- **What the method does:**
  1. Validates string is non-empty.
  2. Normalizes input (replaces commas with dots, strips spaces).
  3. Uses a recursive descent parser (`parseExpression`, `parseTerm`, `parseFactor`) to handle `+`, `-`, `*`, `/`, parentheses `()`, and numbers.
  4. Returns calculated `double` or throws an `IllegalArgumentException` / `ArithmeticException`.
- **When called:** When evaluating answer inputs or live dialog previews.

---

## 4. Lifecycle
*(Not an Activity or Fragment; static utility).*

---

## 5. Interface Interaction (UI)
Evaluates text entered in `EditText` fields.

---

## 6. Interaction with Other Components
Used by lesson Fragments to convert user math expressions into numerical values.

---

## 7. General Logic of the Class
Converts raw string text into tokens and calculates math operations respecting precedence (parentheses first, then multiplication/division, then addition/subtraction).

---

## 8. Explanation in Simple Words
`MathExpressionEvaluator` is a **pocket calculator engine**. When you type `(7-3)/2`, it breaks down the expression, calculates $7 - 3 = 4$, then divides $4 / 2 = 2.0$!
