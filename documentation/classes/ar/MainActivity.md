# توثيق الفئة: MainActivity

---

## 1. معلومات عامة
- **اسم الفئة:** `MainActivity`
- **النوع:** Activity (الشاشة الحاوية الرئيسية)
- **وظيفة الفئة:** تستضيف أجزاء الدروس (`CoordinatePointFragment`, `LinearFunctionFragment`, `ParabolaFunctionFragment`) وتعرض شريط الأدوات مع زر المساعدة.

---

## 2. دوال الفئة
- `onCreate(Bundle savedInstanceState)`: يقرأ معرّف الموضوع، يضبط العنوان ويحمل القطعة المناسبة.
- `showHelpDialog()`: يعرض نافذة حوارية بقواعد الموضوع بصيغة HTML.

---

## 3. الشرح بكلمات بسيطة
`MainActivity` هي **إطار الصورة مع عنوان في الأعلى**. الصورة داخل الإطار تتغير حسب الدرس الذي اخترته!
