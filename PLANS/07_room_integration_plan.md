# План интеграции Room (Local Database для Мобильных Клиентов)

Этот план описывает внедрение локальной БД Room именно в мобильную часть приложения (`composeApp`), не затрагивая серверный модуль. Мы будем использовать Room KMP для кеширования задач на Android и iOS.

Документация по Room на KMP
https://developer.android.com/kotlin/multiplatform/room

## Подготовка: Ветка Git
- Создание ветки `feature/room-database` (выполняется пользователем).

## [ВЫПОЛНЕНО] Этап 1: Реорганизация Моделей (Shared)
**Задача:** Вынести модели задач в модуль `:shared`, чтобы сервер и клиент говорили на одном языке.
- Перенос `Task.kt` и `TaskPriority.kt` в `shared/src/commonMain/kotlin/.../domain/model`.
- Исправление импортов в `composeApp`.
- Теперь `:server` тоже сможет использовать эти модели для будущих API.
- **[Ожидаем аппрув -> Выполняем `git commit`]**

## [ВЫПОЛНЕНО] Этап 2: Подключение Room в composeApp
**Задача:** Добавить Room KMP только в мобильный модуль.
- Добавление плагина KSP в `libs.versions.toml` и `build.gradle.kts` (root и `:composeApp`).
- Добавление зависимостей `androidx.room:room-runtime` и `compiler` в `composeApp/build.gradle.kts`.
- Настройка путей для генерации кода (Room требует KSP).
- **[Ожидаем аппрув -> Выполняем `git commit`]**

## [ОЖИДАЕТ] Этап 3: Создание Сущностей и DAO (composeApp)
**Задача:** Реализовать логику хранения в мобильном приложении.
- Создание `TaskEntity.kt` в `composeApp` (это будет "плоская" модель для БД).
- Создание `TaskDao.kt` с методами `upsert`, `delete` и `getAllTasks` (Flow).
- Написание мапперов: `TaskEntity` <-> `Task` (из shared).
- **[Ожидаем аппрув -> Выполняем `git commit`]**

## [ОЖИДАЕТ] Этап 4: База данных и Платформенная реализация
**Задача:** Настроить создание БД для Android и iOS.
- Определение `AppDatabase` (расширяет `RoomDatabase`).
- Создание `expect fun getDatabaseBuilder()` в `commonMain` модуля `composeApp`.
- Реализация `actual` для Android (использование `Context`) и iOS (путь к `NSDocumentDirectory`).
- **[Ожидаем аппрув -> Выполняем `git commit`]**

## [ОЖИДАЕТ] Этап 5: Репозиторий и Интеграция с UI
**Задача:** Заменить фейковые данные на реальную БД через Koin.
- Создание `LocalTaskRepository` в `composeApp`.
- Регистрация БД и Репозитория в Koin (внутри `App.kt` или модуля DI).
- Обновление `TaskListViewModel` и `TaskDetailViewModel` для работы с репозиторием.
- **[Ожидаем аппрув -> Выполняем `git commit`]**

## [ОЖИДАЕТ] Этап 6: Слияние (Merge)
**Задача:** Влить фичу в основную ветку.
- Переключение на основную ветку.
- Выполнение команды `git merge --no-ff feature/room-database`.
- **Готово!**
