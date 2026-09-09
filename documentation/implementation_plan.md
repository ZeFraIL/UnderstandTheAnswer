# План реализации локализации (English / Русский) и добавления иконки приложения

Реализация поддержки двух языков интерфеса (по умолчанию English при первом старте) с запоминанием выбора в `SharedPreferences` и добавлением `app_icon` на стартовый экран.

## Компоненты и изменения

### 1. Менеджер языка (`LanguageManager.java`)
- **[NEW] [LanguageManager.java](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/java/zeev/fraiman/understandtheanswer/LanguageManager.java)**
  - Константы: `LANG_EN = "en"`, `LANG_RU = "ru"`.
  - Чтение выбранного языка из `SharedPreferences` (по умолчанию `"en"` при первом старте).
  - Применение выбранной локали через `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))`.

### 2. Ресурсы локализации (Strings)
- **[NEW] [res/values-ru/strings.xml](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/res/values-ru/strings.xml)**
  - Все текущие русские строки переносятся в файл локали `values-ru`.
- **[MODIFY] [res/values/strings.xml](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/res/values/strings.xml)**
  - Перевод всех дефолтных строк на английский язык (`English`).

### 3. Стартовый экран (`StartActivity` & `activity_start.xml`)
- **[MODIFY] [activity_start.xml](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/res/layout/activity_start.xml)**
  - Добавление `ImageView` с иконкой `@drawable/app_icon`.
  - Добавление кнопок выбора языка `MaterialButtonToggleGroup` («English» | «Русский»).
- **[MODIFY] [StartActivity.java](file:///C:/Users/zeevf/AndroidStudioProjects/UnderstandTheAnswer/app/src/main/java/zeev/fraiman/understandtheanswer/StartActivity.java)**
  - Инициализация языка при запуске через `LanguageManager.initLanguage(this)`.
  - Обработка кликов по кнопкам «English» и «Русский» с переключением локали на лету.

---

## Verification Plan

### Automated Tests / Build Verification
- Сборка проекта через Gradle (`app:assembleDebug`).

### Manual Verification
- Запуск приложения на устройстве/эмуляторе при первом старте:
  1. Отображение иконки `app_icon` в центре стартового экрана.
  2. Проверка, что при первом старте весь интерфейс отображается на английском языке (`English`).
  3. Нажатие на кнопку «Русский»: интерфейс мгновенно переключается на русский язык.
  4. Перезапуск приложения: выбранный язык сохраняется для будущих запусков.
