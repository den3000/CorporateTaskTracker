# Corporate Task Tracker

Демо-проект к докладу **«Пробуем Compose-alpha для ОС Аврора»**.

Простой трекер задач на **Kotlin Multiplatform / Compose Multiplatform**: список задач,
создание/редактирование/удаление, приоритеты, отметка «выполнено», тёмная тема, индикатор статуса
бэкенда и синхронизация с Ktor-сервером. Один и тот же Compose UI собирается под **Android, iOS и
Аврора ОС** (на скриншотах в докладе — три устройства в ряд с одним и тем же экраном), бэкенд — на
Ktor. Задача проекта — на живом приложении показать, насколько сегодня реально писать под Аврору на
Compose, переиспользуя код Android/iOS.

## О докладе

- **Спикер:** Денис Супрун ([@DEN_WRITES_KOD](https://t.me/DEN_WRITES_KOD)) — 10+ лет в мобильной
  разработке, участник программы бета-тестирования Аврора ОС, Аврора-энтузиаст и спикер.
- **Конференция:** Mobius 2026 Spring (весна 2026, май).
- **Идея:** пройти путь запуска типичного приложения под Аврору на Compose-alpha — что подключается
  из коробки, чего не хватает и как это обойти.

## Что можно посмотреть в коде

Демка иллюстрирует те пункты доклада, которые видны прямо в исходниках:

- **Подключение Compose-alpha (форк) для Авроры** — Gradle-обвязка, `settings.gradle.kts`, версии.
- **Compose Preview** — стаб превью для Авроры (`shared-ui/src/previewStub`).
- **Дополнение Koin** — полифил DI для Авроры (`shared-ui/src/koinCompat`).
- **Compose Resources** — свой ридер ресурсов (модуль `compResAuroraCompat`).
- **Фокус, клавиатура, отступы, тема** — `PlatformModifier.linux.kt` и обработка высоты клавиатуры.

**Modal Bottom Sheet** в коде виден только как нерабочий/обойдённый случай — он упирается в
отсутствие `MainUIDispatcher` в Skiko-порте форка (в сообществе Aurora Developers уже есть порт с
фиксом).

## Итоги доклада

Главный вывод: **время реально пришло — есть всё, чтобы взять и попробовать.** SDK и симулятор
работают (в том числе на маках с M-чипами), есть собственно порт Compose и минимальный набор
библиотек, а недостающее докручивается полифилами или обходится. Подробный разбор каждого пункта —
в самом докладе.

> ⚠️ Это учебная демка под **alpha/форк** Compose для Авроры. Она намеренно содержит полифилы и
> обходы, которые в докладе и разбираются, — часть из них хрупкая и завязана на конкретную версию
> форка (`0.0.3-aurora`) и ОС. Технические «подводные камни» собраны в [AGENTS.md](AGENTS.md).

---

## Структура проекта

Общий Compose UI вынесен в модуль-библиотеку, а под каждую платформу — тонкий модуль-приложение,
который от неё зависит:

- [`shared-ui`](./shared-ui/src) — KMP-**библиотека**: общий Compose UI, ViewModel'и,
  data/repository/network-слои и DI. Объявляет таргеты Android/iOS/Linux, держит базу Room (+ KSP)
  и собирает iOS-фреймворк `ComposeApp`.
  - [`commonMain`](./shared-ui/src/commonMain/kotlin) — общий код для всех таргетов;
  - платформенные папки ([androidMain](./shared-ui/src/androidMain/kotlin),
    [iosMain](./shared-ui/src/iosMain/kotlin), [linuxMain](./shared-ui/src/linuxMain/kotlin)) —
    `actual`-реализации.
- [`apps/androidApp`](./apps/androidApp/src) — тонкое Android-приложение (`MainActivity`, манифест,
  иконки), зависит от `:shared-ui`. Собирается в upstream-варианте (по умолчанию).
- [`apps/auroraApp`](./apps/auroraApp/src) — приложение под Аврору: linux-исполняемые бинарники,
  точка входа, упаковка RPM и деплой по SSH. Собирается только в Aurora-варианте.
- [`apps/iosApp`](./apps/iosApp/iosApp) — Xcode-проект iOS, потребляет фреймворк `ComposeApp`.
- [`shared`](./shared/src) — доменные модели, общие для всех таргетов (и для сервера).
- [`server`](./server/src/main/kotlin) — Ktor-сервер.

Технологии: Kotlin 2.3.10, Compose Multiplatform 1.10.2 (upstream) / 0.0.3-aurora (форк), Koin, Room,
Ktor.

## Как собрать и запустить

### Android

```shell
./gradlew :androidApp:assembleDebug        # macOS/Linux
.\gradlew.bat :androidApp:assembleDebug     # Windows
```

### iOS

Открыть [`apps/iosApp`](./apps/iosApp) в Xcode и запустить, либо собрать фреймворк:

```shell
./gradlew :shared-ui:linkDebugFrameworkIosSimulatorArm64
```

### Сервер (Ktor)

```shell
./gradlew :server:run        # поднимается на :8080
```

### Аврора ОС

Aurora-вариант глобально переключает compose-плагин на форк, поэтому собирается **отдельным**
запуском Gradle через `-Pcompose.aurora.enabled=true`. Нужен Aurora SDK, а для деплоя — устройство,
доступное по `AURORA_DEVICE_IP` (см. [NETWORK_CONFIG_README.md](./NETWORK_CONFIG_README.md)).

```shell
# только компиляция (работает без Aurora SDK)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:compileKotlinLinuxX64
# полная сборка + RPM + деплой на устройство (нужен Aurora SDK + устройство)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:appRunReleaseAfterBuild
```

## Особенности сборки под Аврору (кратко)

- **Флаг `compose.aurora.enabled`** переключает в `settings.gradle.kts` саму версию compose-плагина
  (форк `0.0.3-aurora` vs upstream `1.10.2`). Плагин выбирается один раз на запуск, поэтому
  **Android/iOS и Аврору нельзя собрать в одном запуске Gradle.**
- **Форк Compose берётся из локальной maven-папки** — путь задаётся в `local.properties`
  (`auroraMavenPath`, напр. `../aurora-maven-0.0.3`); если не задан, используется обычный `~/.m2`.
- **Runtime-подводные камни Авроры** (сеть вне Main-диспетчера, обработка клавиатуры, кэширование
  ViewModel в koin-полифиле) и прочие инварианты подробно описаны в [AGENTS.md](AGENTS.md) — читать
  перед правками aurora-специфичного кода.
