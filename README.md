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

> **📌 Версия на докладе — `0.0.3-aurora`; репозиторий с тех пор переехал на `0.0.4-aurora`.**
> Этот README всегда описывает **актуальное** состояние (сейчас 0.0.4: переключатель `buildVariant`,
> плагины `aurora-build` / `aurora-devices`, настоящие форк-либы вместо части полифилов).
> Код **ровно в том виде, в каком он был на докладе** (форк `0.0.3-aurora`, флаг
> `compose.aurora.enabled`, полный набор полифилов, монолитный `buildtools`-плагин) сохранён в
> коммите [`7e8e70d`](https://github.com/den3000/CorporateTaskTracker/tree/7e8e70d)
> (ветка `feature/aurora-compose-0.0.3-stable`). Что именно поменялось — в таблице ниже; как
> работать с проектом в текущем состоянии — [AGENTS.md](AGENTS.md).

<details>
<summary><b>Что изменилось при переезде <code>0.0.3-aurora</code> → <code>0.0.4-aurora</code></b></summary>

| Было (0.0.3, доклад) | Стало (0.0.4, сейчас) |
| --- | --- |
| Флаг `compose.aurora.enabled` | Свойство `buildVariant` (`-PbuildVariant=aurora`) |
| Форк `org.jetbrains.compose 0.0.3-aurora` | `0.0.4-aurora` |
| Монолитный плагин `ru.auroraos.kmp.buildtools` | Раздельные `aurora-build` + `aurora-devices` `0.0.1` |
| `:shared-ui` — один build-файл с ветками `auroraEnabled` | Два файла: `build.gradle.kts` (upstream) / `build.aurora.gradle.kts` (Аврора) |
| Полифил Koin (`shared-ui/src/koinCompat`) | Настоящий форк Koin `4.2.0-aurora` |
| Навигация через reflection-хак (`getMethod("getNavigation")`) | DSL-аксессор `compose.navigation` |
| Модуль-полифил ресурсов `:compResAuroraCompat` | Настоящий `components-resources`; от полифила остался только парсер Android-vector-XML как drop-in `painterResource` (форк рендерит лишь SVG) |
| Контракт суффикса `.generated.resources` (по нему полифил находил файл) | `customDirectory` + пустой guard-каталог; иконки — тот же XML |
| `ktor-client-curl 3.1.2-aurora` | `3.4.2-aurora` |
| Таск `appRunReleaseAfterBuild` и цепочка враппер-тасков | `buildReleasePipeline` / `runReleaseOnDevice` (из плагинов) |

Из полифилов остались только **`previewStub`** (заглушка `@Preview`) и **парсер vector-XML**.
Полный технический разбор текущего состояния — в [AGENTS.md](AGENTS.md).

</details>

## Что можно посмотреть в коде

Демка иллюстрирует те пункты доклада, которые видны прямо в исходниках:

- **Подключение Compose-форка (`0.0.4-aurora`) для Авроры** — Gradle-обвязка, `settings.gradle.kts`,
  переключатель `buildVariant`, плагины `aurora-build` / `aurora-devices`.
- **Compose Preview** — стаб превью для Авроры (`shared-ui/src/previewStub`) — единственный
  оставшийся полифил (в форке нет `ui-tooling-preview` под linux).
- **Koin, навигация, ресурсы — настоящие форк-либы 0.0.4** (раньше полифилы): Koin `4.2.0-aurora`,
  `compose.navigation`, `components-resources`. Иконки — те же Android Vector Drawable (XML), что и на
  Android/iOS: форк-загрузчик рендерит только SVG, поэтому XML-вектор мы парсим сами в `ImageVector`
  (drop-in `painterResource` в пакете `ru.den.writes.code.res` + `shared-ui/src/linuxMain/.../vectorxml`)
  — синтаксис на местах вызова канонический, tint и размеры работают.
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
> форка (`0.0.4-aurora`) и ОС. Технические «подводные камни» собраны в [AGENTS.md](AGENTS.md).

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
  точка входа, упаковка RPM и деплой по SSH (плагины `aurora-build` / `aurora-devices`). Собирается
  только в Aurora-варианте (`-PbuildVariant=aurora`).
- [`apps/iosApp`](./apps/iosApp/iosApp) — Xcode-проект iOS, потребляет фреймворк `ComposeApp`.
- [`shared`](./shared/src) — доменные модели, общие для всех таргетов (и для сервера).
- [`server`](./server/src/main/kotlin) — Ktor-сервер.

Технологии: Kotlin 2.3.10, Compose Multiplatform 1.10.2 (upstream) / 0.0.4-aurora (форк), Koin 4.1.1
(upstream) / 4.2.0-aurora (форк), Room, Ktor.

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
запуском Gradle через `-PbuildVariant=aurora`. Нужен Aurora SDK (RPM собирается в Docker), а для
деплоя — устройство, доступное по `AURORA_DEVICE_IP` (см. [NETWORK_CONFIG_README.md](./NETWORK_CONFIG_README.md)).

```shell
# только компиляция (работает без Aurora SDK)
./gradlew -PbuildVariant=aurora :auroraApp:compileKotlinLinuxX64
# сборка RPM (init sysroot > link > RPM в Docker)
./gradlew -PbuildVariant=aurora :auroraApp:buildReleasePipeline
# сборка + деплой + запуск на устройстве
./gradlew -PbuildVariant=aurora :auroraApp:runReleaseOnDevice
```

## Особенности сборки под Аврору (кратко)

- **Свойство `buildVariant`** (`-PbuildVariant=aurora`) переключает в `settings.gradle.kts` версию
  compose-плагина (форк `0.0.4-aurora` vs upstream `1.10.2`) и подключает плагины `aurora-build` /
  `aurora-devices`. Плагин выбирается один раз на запуск, поэтому **Android/iOS и Аврору нельзя
  собрать в одном запуске Gradle.** У `:shared-ui` два build-файла: `build.gradle.kts` (upstream) и
  `build.aurora.gradle.kts` (Аврора).
- **Форк Compose берётся из локальной maven-папки** — путь задаётся в `local.properties`
  (`auroraMavenPath`, напр. `../aurora-maven-0.0.4`); если не задан, используется обычный `~/.m2`.
- **Runtime-подводные камни Авроры** (сеть вне Main-диспетчера, обработка клавиатуры, корневой
  `LocalViewModelStoreOwner`, SVG-иконки «tint запечён + явный размер») и прочие инварианты подробно
  описаны в [AGENTS.md](AGENTS.md) — читать перед правками aurora-специфичного кода.
