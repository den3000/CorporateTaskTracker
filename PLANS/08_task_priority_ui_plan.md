# План рефакторинга UI выбора приоритета (ModalBottomSheet)

Этот план описывает шаги по улучшению пользовательского интерфейса выбора приоритета на экране редактирования задачи (`TaskDetailScreen`). Мы заменим текущий набор `FilterChip` на компактную кликабельную цветную плашку, которая будет открывать `ModalBottomSheet` с вариантами выбора.

## [ВЫПОЛНЕНО] Подготовка: Ветка Git
**Задача:** Создать новую ветку для работы над фичей.
- Создание ветки `feature/priority-bottom-sheet` (выполняется пользователем).

## [ВЫПОЛНЕНО] Этап 1: Создание компонента PriorityBadge
**Задача:** Вынести логику отображения цветной плашки приоритета (как в `TaskItem`, но с текстом) в переиспользуемый компонент.
- Создать Composable функцию `PriorityBadge(priority: TaskPriority, modifier: Modifier = Modifier)`.
- Логика цветов должна быть такой же, как в `PriorityIndicator`:
  - `HIGH` -> `MaterialTheme.colorScheme.error`
  - `MEDIUM` -> `MaterialTheme.colorScheme.primaryContainer`
  - `LOW` -> `MaterialTheme.colorScheme.secondary`
- Внутри плашки выводить локализованное (или пока текстовое) название приоритета. Цвет текста должен контрастировать с фоном (например, использовать `contentColorFor` или явно заданные цвета на основе темы).

## [ВЫПОЛНЕНО] Этап 2: Обновление верстки в TaskDetailScreen
**Задача:** Заменить текущий выбор приоритета на компактный `Row`.
- В файле `TaskDetailScreen.kt` найти блок с заголовком "Приоритет" и `Row` с `FilterChip`.
- Заменить этот блок на единый `Row` с `horizontalArrangement = Arrangement.SpaceBetween` и `verticalAlignment = Alignment.CenterVertically`.
- В левой части `Row` оставить `Text("Приоритет")`.
- В правой части расположить новый `PriorityBadge`.
- Добавить на `PriorityBadge` модификатор `clickable { showBottomSheet = true }`.

## [ВЫПОЛНЕНО] Этап 3: Подготовка состояния для ModalBottomSheet
**Задача:** Добавить переменные состояния для управления видимостью шторки.
- В функции `TaskDetailContent` (или на уровне её вызова) добавить состояние: `var showBottomSheet by remember { mutableStateOf(false) }`.
- Подключить `val sheetState = rememberModalBottomSheetState()`.
- Добавить `rememberCoroutineScope()`, чтобы иметь возможность программно закрывать шторку с анимацией (вызовом `sheetState.hide()`).

## [ВЫПОЛНЕНО] Этап 4: Реализация ModalBottomSheet
**Задача:** Сверстать всплывающее снизу меню с выбором приоритета.
- Добавить блок `if (showBottomSheet) { ModalBottomSheet(...) }`.
- Внутри `ModalBottomSheet` расположить `Column` со списком из всех значений `TaskPriority.entries`.
- Каждый пункт списка должен быть кликабельным `Row`, содержащим:
  - Слева: `PriorityIndicator` (или `PriorityBadge`, чтобы наглядно показать цвет).
  - Справа: `Text` с названием приоритета.
- В обработчике клика по пункту нужно:
  1. Вызвать `onPriorityChange(priority)`.
  2. Скрыть шторку: запустить корутину `sheetState.hide()` и затем `showBottomSheet = false`.

## [ВЫПОЛНЕНО] Этап 5: Локализация и полировка (Опционально)
**Задача:** Проверить, что все тексты локализованы и превью экранов работают корректно.
- Обновить `@Preview` функции в `TaskDetailScreen.kt`, так как может понадобиться скрыть BottomSheet в превью или передать заглушечные параметры.
- При необходимости добавить названия приоритетов (Low, Medium, High) в `strings.xml` (composeResources).

## [ВЫПОЛНЕНО] Этап 6: Слияние (Merge)
**Задача:** Влить фичу в основную ветку.
- Переключение на основную ветку.
- Выполнение команды `git merge --no-ff feature/priority-bottom-sheet`.
- **Готово!**
