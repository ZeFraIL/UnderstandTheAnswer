# תיעוד מחלקה: LanguageManager

---

## 1. מידע כללי
- **שם המחלקה:** `LanguageManager`
- **סוג:** Utility Class (מנהל שפה סטטי)
- **תפקיד המחלקה:** מנהל את שמירת השפה ב-`SharedPreferences` ומחיל את השפה על האפליקציה דרך `AppCompatDelegate`.

---

## 2. משתנים (שדות המחלקה)
| שם | סוג | תפקיד | היכן בשימוש |
| :--- | :--- | :--- | :--- |
| `PREF_NAME` | `String` (static final) | שם קובץ ההגדרות. | ב-`getLanguage` וב-`setLanguage`. |
| `KEY_LANGUAGE` | `String` (static final) | מפתח השפה. | ב-`getLanguage` וב-`setLanguage`. |
| `LANG_EN` / `LANG_RU` | `String` (static final) | קודי השפה ("en" / "ru"). | קבועי שפה. |

---

## 3. מתודות המחלקה
- `getLanguage(Context context)`: קורא את השפה השמורה (ברירת מחדל "en").
- `setLanguage(Context context, String langCode)`: שומר שפה ומחיל אותה דרך `AppCompatDelegate`.
- `initLanguage(Context context)`: מחיל את השפה השמורה בהפעלת האפליקציה.

---

## 4. מחזור חיים
*(מחלקה סטטית).*

---

## 5. הסבר במילים פשוטות
`LanguageManager` הוא **פנקס הזיכרון של המתורגמן**. בכל פעם שאתם פותחים את האפליקציה, הוא בודק בפנקס באיזו שפה אתם מעדיפים לקרוא!
