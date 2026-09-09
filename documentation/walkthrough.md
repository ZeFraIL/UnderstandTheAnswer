# Walkthrough — Добавление иконки приложения и Двуязычная Локализация (English / Русский)

Добавлена иконка `app_icon` на стартовый экран и реализована полноценная двуязычная система локализации с выбором языка и сохранением в `SharedPreferences`.

## Реализованные изменения

### 1. Менеджер языка (`LanguageManager.java`)
- [LanguageManager.java](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/java/zeev/fraiman/understandtheanswer/LanguageManager.java)
- Читает и сохраняет выбранный код языка (`"en"` или `"ru"`) в `SharedPreferences`.
- При первом запуске по умолчанию устанавливается английский язык (`"en"`).
- Применяет выбранный язык ко всему приложению через `AppCompatDelegate.setApplicationLocales(...)`.

### 2. Локализация ресурсов (`strings.xml`)
- [res/values/strings.xml](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/res/values/strings.xml) — основные английские тексты (`English`).
- [res/values-ru/strings.xml](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/res/values-ru/strings.xml) — русские переводы всех фрагментов, карточек, кнопок и справок.

### 3. Иконка и Переключатель на стартовом экране
- [activity_start.xml](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/res/layout/activity_start.xml)
  - Добавлен логотип `@drawable/app_icon` ($110\text{dp} \times 110\text{dp}$) над заголовком.
  - Добавлена группа кнопок `MaterialButtonToggleGroup` («English 🇬🇧» | «Русский 🇷🇺»).
- [StartActivity.java](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/java/zeev/fraiman/understandtheanswer/StartActivity.java)
  - Инициализирует язык приложения перед отрисовкой.
  - Переключает локаль на лету с сохранением для будущих запусков.

---

## Результаты проверки
- **Сборка проекта:** Успешно выполнена команда `app:assembleDebug`.
