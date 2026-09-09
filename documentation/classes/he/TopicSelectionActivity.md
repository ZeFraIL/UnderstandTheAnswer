# תיעוד מחלקה: TopicSelectionActivity

---

## 1. מידע כללי
- **שם המחלקה:** `TopicSelectionActivity`
- **סוג:** Activity (מסך בחירת נושאים)
- **תפקיד המחלקה:** מציג את רשימת הנושאים המתמטיים הזמינים בכרטיסיות MaterialCardView אינטראקטיביות.
- **אינטראקציה עם רכיבים אחרים:** מופעל מ-`StartActivity` ומעביר את מזהה הנושא שנבחר ל-`MainActivity`.

---

## 2. משתנים (שדות המחלקה)
| שם | סוג | תפקיד | היכן בשימוש |
| :--- | :--- | :--- | :--- |
| `EXTRA_TOPIC_ID` | `String` (static final) | מפתח Intent למזהה הנושא. | ב-`TopicSelectionActivity` וב-`MainActivity`. |
| `TOPIC_POINT_COORDINATE` | `String` (static final) | מזהה נושא "נקודה במערכת צירים". | ב-`TopicSelectionActivity` וב-`MainActivity`. |
| `TOPIC_LINEAR_FUNCTION` | `String` (static final) | מזהה נושא "פונקציה קווית". | ב-`TopicSelectionActivity` וב-`MainActivity`. |
| `TOPIC_QUADRATIC_FUNCTION` | `String` (static final) | מזהה נושא "פונקציה ריבועית". | ב-`TopicSelectionActivity` וב-`MainActivity`. |

---

## 3. מתודות המחלקה

### מתודה: `onCreate(Bundle savedInstanceState)`
- **סוג מתודה:** `protected`
- **ערך החזרה:** `void`
- **מה המתודה עושה:** מנפחת את פריסת המסך, מקשרת את כרטיסיות הנושאים ומגדירה מאזיני לחיצה להפעלת `MainActivity` עם מזהה הנושא.

---

## 4. מחזור חיים (Activity)
- **`onCreate()`:** טוען פריסה ומקשר כרטיסיות נושאים.

---

## 5. אינטראקציה עם הממשק (UI)
כרטיסיות `MaterialCardView` עבור כל נושא מתמטי.

---

## 6. אינטראקציה עם רכיבים אחרים
שולח `Intent` ל-`MainActivity` עם הנושא שנבחר.

---

## 7. לוגיקה כללית של המחלקה
המשתמש בוחר כרטיסיית נושא. האפליקציה מעבירה את מזהה הנושא ב-Intent ופותחת את המסך הראשי.

---

## 8. הסבר במילים פשוטות
`TopicSelectionActivity` הוא **תוכן העניינים של הספר**. אתם מסתכלים על פרקי הלימוד ולוחצים על הפרק שברצונכם לתרגל!
