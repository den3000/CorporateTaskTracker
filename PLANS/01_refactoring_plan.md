# План рефакторинга App.kt (Выделение экранов и ViewModel)

Текущий файл `App.kt` перегружен: в нём находится инициализация сети, тема, Scaffold, UI верхней плашки статуса сервера и UI самого контента экрана. 
Мы разобьём это на логические слои (UI и Presentation/ViewModel).

## [ВЫПОЛНЕНО] Этап 1: Вынос логики статуса сети (ServerStatusViewModel)
**Задача:** Убрать прямое использование `NetworkMonitor` из UI.
- Создадим файл `ServerStatusViewModel.kt`.
- В нём сделаем `ServerStatusViewModel`, который будет принимать `NetworkMonitor` и преобразовывать `observeStatus()` в `StateFlow`, привязанный к `viewModelScope`.

## [ВЫПОЛНЕНО] Этап 2: Вынос UI плашки статуса (ServerStatusIndicator)
**Задача:** Разгрузить `App.kt` от деталей вёрстки плашки.
- Создадим файл `ServerStatusIndicator.kt`.
- Перенесём туда Composable-функцию `ServerStatusIndicator`.
- Добавим обёртку, которая будет принимать `ServerStatusViewModel`, собирать стейт и передавать его в `ServerStatusIndicator`.

## [ВЫПОЛНЕНО] Этап 3: Создание MainViewModel для главного экрана
**Задача:** Убрать логику состояния (`showContent`) из UI.
- Создадим файл `MainViewModel.kt`.
- В нём будет храниться `MutableStateFlow<Boolean>` для состояния показа контента (по клику на кнопку).

## [ВЫПОЛНЕНО] Этап 4: Вынос главного контента (MainScreen)
**Задача:** Перенести вёрстку с кнопкой и приветствием в отдельный экран.
- Создадим файл `MainScreen.kt`.
- Перенесём туда `Column`, `Button`, `AnimatedVisibility` и `Greeting`.
- Экран будет получать стейт из `MainViewModel`.

## [ВЫПОЛНЕНО] Этап 5: Очистка App.kt
**Задача:** Собрать всё вместе.
- В `App.kt` останется только инициализация зависимостей (пока ручная, без DI), `MaterialTheme` и `Scaffold`.
- В `topBar` вызовем компонент плашки, передав ей `ServerStatusViewModel`.
- В `content` вызовем `MainScreen`, передав ему `MainViewModel`.