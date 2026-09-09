# Class Documentation: LanguageManager

---

## 1. General Information
- **Class Name:** `LanguageManager`
- **Type:** Utility Class (Static Helper)
- **Class Assignment:** Manages language preference persistence in `SharedPreferences` and applies locale settings across the app via `AppCompatDelegate`.
- **Interaction with Components:** Used by `StartActivity` and app components to initialize and switch languages ("en" or "ru").

---

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where Used |
| :--- | :--- | :--- | :--- |
| `PREF_NAME` | `String` (static final) | Preference file name (`app_language_prefs`). | Used in `getLanguage` and `setLanguage`. |
| `KEY_LANGUAGE` | `String` (static final) | Preference key for language code (`selected_language`). | Used in `getLanguage` and `setLanguage`. |
| `LANG_EN` | `String` (static final) | English language code (`"en"`). | Public language constant. |
| `LANG_RU` | `String` (static final) | Russian language code (`"ru"`). | Public language constant. |

---

## 3. Class Methods

### Method: `getLanguage(Context context)`
- **Method Type:** `public static`
- **Return Value:** `String` (Language code, e.g. `"en"` or `"ru"`).
- **Parameters:**
  | Name | Type | Description |
  | :--- | :--- | :--- |
  | `context` | `Context` | Android Context. |
- **What it does:** Reads saved language from `SharedPreferences`. Defaults to `"en"` on first start.

### Method: `setLanguage(Context context, String langCode)`
- **Method Type:** `public static`
- **Return Value:** `void`
- **Parameters:**
  | Name | Type | Description |
  | :--- | :--- | :--- |
  | `context` | `Context` | Android Context. |
  | `langCode` | `String` | Language code (`"en"` or `"ru"`). |
- **What it does:** Saves `langCode` to `SharedPreferences` and calls `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode))`.

### Method: `initLanguage(Context context)`
- **Method Type:** `public static`
- **Return Value:** `void`
- **Parameters:**
  | Name | Type | Description |
  | :--- | :--- | :--- |
  | `context` | `Context` | Android Context. |
- **What it does:** Reads saved language and applies it via `AppCompatDelegate`.

---

## 4. Lifecycle
*(Not an Activity or Fragment; static utility).*

---

## 5. Interface Interaction (UI)
Does not directly contain UI elements. Called when language toggle buttons are clicked.

---

## 6. Interaction with Other Components
- **SharedPreferences:** Writes and reads language setting.
- **AppCompatDelegate:** Applies locale settings globally.

---

## 7. General Logic of the Class
Stores language preference on disk and tells Android OS which language strings file (`values` vs `values-ru`) to load.

---

## 8. Explanation in Simple Words
`LanguageManager` is like a **memory notebook for an interpreter**. Whenever you open the app, it checks the notebook to remember whether you prefer to read in English or Russian!
