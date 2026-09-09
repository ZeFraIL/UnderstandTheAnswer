# توثيق الفئة: TopicSelectionActivity

---

## 1. معلومات عامة
- **اسم الفئة:** `TopicSelectionActivity`
- **النوع:** Activity (شاشة اختيار المواضيع)
- **وظيفة الفئة:** تعرض قائمة المواضيع الرياضية المتاحة في بطاقات تفاعلية MaterialCardView.
- **التفاعل مع المكونات:** تُفتح من `StartActivity` وترسل معرف الموضوع المختار إلى `MainActivity`.

---

## 2. دوال الفئة
- `onCreate(Bundle savedInstanceState)`: تقوم بتجهيز البطاقات وإسناد مستمعات النقر لفتح `MainActivity` مع معرّف الموضوع (`EXTRA_TOPIC_ID`).

---

## 3. الشرح بكلمات بسيطة
`TopicSelectionActivity` هي **فهرس الكتاب**. تنظر إلى الفصول المتاحة وتضغط على الفصل الذي تريد دراسته!
