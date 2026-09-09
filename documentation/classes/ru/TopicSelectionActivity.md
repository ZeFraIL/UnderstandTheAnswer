# Документация класса: TopicSelectionActivity

---

## 1. Общая информация
- **Имя класса:** `TopicSelectionActivity`
- **Тип:** Activity (Экран выбора разделов)
- **Назначение класса:** Отображает список доступных математических тем («Точка в системе координат», «Линейная функция», «Квадратичная функция») в виде интерактивных карточек MaterialCardView.
- **Взаимодействие с другими компонентами:** Запускается из `StartActivity`. Передаёт выбранный ID темы в `MainActivity` через параметры Intent.

---

## 2. Переменные (поля класса)
| Имя | Тип | Назначение | Где используется |
| :--- | :--- | :--- | :--- |
| `EXTRA_TOPIC_ID` | `String` (static final) | Ключ Intent для ID темы. | В `TopicSelectionActivity` и `MainActivity`. |
| `TOPIC_POINT_COORDINATE` | `String` (static final) | ID темы «Точка в системе координат». | В `TopicSelectionActivity` и `MainActivity`. |
| `TOPIC_LINEAR_FUNCTION` | `String` (static final) | ID темы «Линейная функция». | В `TopicSelectionActivity` и `MainActivity`. |
| `TOPIC_QUADRATIC_FUNCTION` | `String` (static final) | ID темы «Квадратичная функция». | В `TopicSelectionActivity` и `MainActivity`. |

---

## 3. Методы класса

### Имя метода: `onCreate(Bundle savedInstanceState)`
- **Тип метода:** `protected`
- **Возвращаемое значение:** `void`
- **Параметры:**
  | Имя | Тип | Описание |
  | :--- | :--- | :--- |
  | `savedInstanceState` | `Bundle` | Сохранённое состояние активности. |
- **Что делает метод (подробно):**
  1. Вызывает `super.onCreate(savedInstanceState)` и активирует `EdgeToEdge`.
  2. Разворачивает макет `activity_topic_selection.xml`.
  3. Находит карточки `cardTopicPoint`, `cardTopicLinear` и `cardTopicQuadratic`.
  4. Настраивает клики по карточкам для запуска `MainActivity` с нужным `EXTRA_TOPIC_ID`.
- **Когда вызывается:** Вызывается системой при открытии экрана выбора тем.

---

## 4. Жизненный цикл (Activity)
- **`onCreate()`:** Загружает макет и привязывает обработчики кликов по карточкам тем.

---

## 5. Взаимодействие с интерфейсом (UI)
- **Элементы UI:** Карточки `MaterialCardView` для каждой темы.

---

## 6. Взаимодействие с другими компонентами
- **Intents:** Отправляет `Intent` в `MainActivity` с выбранной темой.

---

## 7. Общая логика класса
Пользователь выбирает карточку темы. Экран прикрепляет ID темы к `Intent` и запускает главный экран.

---

## 8. Объяснение простыми словами
`TopicSelectionActivity` — это **оглавление учебника**. Вы смотрите на список глав и нажимаете на ту, которую хотите изучить!
