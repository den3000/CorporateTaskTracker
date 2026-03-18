# План внедрения Навигации (Compose Navigation KMP)

Этот план описывает шаги по добавлению многоэкранности в наше приложение. Мы будем использовать официальный порт `navigation-compose` от JetBrains и современный Type-Safe подход (безопасная передача аргументов через объекты и дата-классы с помощью `kotlinx.serialization`).

## [ВЫПОЛНЕНО] Подготовка: Ветка Git
**Задача:** Создать новую ветку для работы над фичей.
- Создание ветки `feature/navigation` (выполняется пользователем).

## [ВЫПОЛНЕНО] Этап 1: Подключение зависимостей
**Задача:** Добавить библиотеки навигации и сериализации.
- Добавление плагина `kotlin.plugin.serialization` в `libs.versions.toml` и `build.gradle.kts`.
- Добавление библиотек `navigation-compose` (от JetBrains) и `kotlinx-serialization-json`.
- Синхронизация проекта.

## [ВЫПОЛНЕНО] Этап 2: Описание маршрутов (Type-Safe Routes)
**Задача:** Создать строгую типизацию для наших экранов.
- Создание пакета `navigation` и файла `Routes.kt`.
- Описание маршрута списка задач: `@Serializable object TaskListRoute`.
- Описание маршрута деталей задачи с аргументом: `@Serializable data class TaskDetailRoute(val taskId: Int)`.
- Описание маршрута настроек: `@Serializable object SettingsRoute`.

## [ВЫПОЛНЕНО] Этап 3: Создание экранов (TaskListScreen, TaskDetailScreen, SettingsScreen)
**Задача:** Подготовить UI для новых экранов.
- Текущий `MainScreen` -> `SettingsScreen` (и переименование `MainViewModel` -> `SettingsViewModel`).
- Создаём новый `TaskListScreen` (с `TaskListViewModel`).
- Добавление в `TaskListScreen` кнопки "Открыть задачу №1" для теста навигации.
- Добавление в `TaskListScreen` кнопки "Открыть настройки" для теста навигации.
- Создание нового файла `TaskDetailScreen.kt` с простым UI, который принимает `taskId` и имеет кнопку "Назад".
- По нажатию на кнопку "Открыть настройки" открываем `SettingsScreen`.

## [ВЫПОЛНЕНО] Этап 4: Настройка NavHost в App.kt
**Задача:** Связать экраны вместе.
- В файле `App.kt` создание контроллера: `val navController = rememberNavController()`.
- Замена прямого вызова экрана внутри `Scaffold` на `NavHost`.
- Описание графа навигации: `composable<TaskListRoute> { ... }`, `composable<TaskDetailRoute> { ... }` и `composable<SettingsRoute> { ... }`.
- Проброс лямбд обработчиков навигации (например, `onNavigateToTask`) в экраны.
- Не забываем про то, что смена темы должна работать на уровне всего приложения, и нужен hoisting для `SettingsViewModel`.

## [ВЫПОЛНЕНО] Этап 5: Добавление Navigation Bar с кнопкой "Назад"
**Задача:** Добавить полноценный `TopAppBar` из Material 3 (с тайтлом и кнопкой возврата).
- В `AppNavigation` (или `App.kt`) вынести управление `TopAppBar` с отслеживанием текущего маршрута (`navController.currentBackStackEntryAsState()`).
- В `TopAppBar` добавить кнопку "Назад", которая появляется, когда `navController.previousBackStackEntry != null` (или когда маршрут не `TaskListRoute`).
- Убрать временные кнопки `← Назад` из центров экранов `SettingsScreen` и `TaskDetailScreen`.
- Убедиться в корректности обработки системных отступов (`WindowInsets`).

## [ВЫПОЛНЕНО] Этап 5.1: Интеграция ServerStatusIndicator в TopAppBar (Иконкой)
**Задача:** Превратить громоздкую плашку статуса в элегантную иконку в `TopAppBar`.
- Убрать `ServerStatusIndicator` из `SettingsScreen` (он должен быть глобальным).
- Изменить логику `ServerStatusIndicator`: вместо прямоугольной плашки (`Box` с текстом) он должен возвращать `Icon`.
- Иконка `CloudOff` для `OFFLINE` (красная).
- Иконка `QuestionMark` для `CONNECTING` (серая/оранжевая).
- Ничего (или прозрачная заглушка) для `ONLINE`.
- Внедрить `ServerStatusIndicator` в параметр `actions` внутри `TopAppBar`.

## [ВЫПОЛНЕНО] Этап 5.2: Перенос кнопки Настроек в TopAppBar
**Задача:** Убрать временную кнопку перехода в настройки из `TaskListScreen`.
- В `TaskListScreen` удалить кнопку `Настройки (Смена темы) ⚙️` и лямбду `onNavigateToSettings`.
- В `AppNavigation` убрать передачу этой лямбды.
- В `AppTopBar` добавить иконку шестерёнки в секцию `actions`.
- По нажатию на шестерёнку делать переход на экран настроек `navController.navigate(SettingsRoute)`. (Саму шестерёнку можно не показывать, если пользователь уже на экране настроек).

## [ВЫПОЛНЕНО] Этап 6: Слияние в основную ветку (Merge)
**Задача:** Влить фичу в основную ветку без Fast-Forward.
- Переключение на основную ветку (`main` или `master`).
- Выполнение команды `git merge --no-ff feature/navigation`.

**Готово!**
