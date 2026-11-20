# Widget Creator

Android-приложение для создания собственных виджетов на домашний экран.

## Возможности

- Создание неограниченного количества виджетов четырёх типов:
  - **Text** — произвольный текст;
  - **Clock** — часы с датой;
  - **Date** — дата с временем;
  - **Counter** — счётчик с кнопкой +1.
- Настройка оформления: имя, фон и цвет текста (палитра), жирный шрифт,
  размер (2x1 / 4x2 / 4x3), кнопка обновления.
- Живой предпросмотр виджета прямо в редакторе.
- Хранение шаблонов в Room.
- Все пользовательские виджеты обслуживаются одним универсальным
  `AppWidgetProvider` (`DynamicWidgetProvider`) и одним layout (`RemoteViews`).
- При добавлении виджета на рабочий стол открывается экран выбора шаблона
  (`WidgetConfigureActivity`).
- Любое изменение шаблона автоматически обновляет все размещённые экземпляры.

## Структура проекта

```
app/src/main/java/com/widgetcreator/app/
  data/      # Room: модели, DAO, база
  ui/        # Compose: список, редактор, предпросмотр, ViewModel
  widget/    # AppWidgetProvider, RemoteViews-рендерер, configure-экран
  util/      # Application, палитра цветов
```

## Сборка

Требования: JDK 17, Android SDK 34.

### Настройка Android SDK (обязательно)

Gradle должен знать, где установлен Android SDK. Выберите один из способов:

**Вариант 1 — файл local.properties (рекомендуется)**

Создайте в корне проекта файл `local.properties` (рядом с `build.gradle.kts`)
и укажите путь к SDK. Готовый шаблон лежит в `local.properties.example`:

```
sdk.dir=C\:\\Users\\Yarosib\\AppData\\Local\\Android\\Sdk
```

Как найти путь к SDK:
- Windows: обычно `C:\Users\<имя>\AppData\Local\Android\Sdk`
- Linux: `/home/<имя>/Android/Sdk`
- macOS: `/Users/<имя>/Library/Android/sdk`
- Или посмотрите в Android Studio: File -> Settings -> Languages & Frameworks -> Android SDK (поле Android SDK Location)

Экранирование для Windows обязательно: двоетания записываются как `C\\:`, слеги как `\\`.

**Вариант 2 — переменная окружения ANDROID_HOME / ANDROID_SDK_ROOT**

Windows (cmd):
```
setx ANDROID_HOME "C:\Users\Yarosib\AppData\Local\Android\Sdk"
```
после чего перезапустить терминал.

Linux/macOS:
```
export ANDROID_HOME=$HOME/Android/Sdk
```

Также убедитесь, что через SDK Manager установлены пакеты **Platform SDK 34** и **Build-Tools**.

### Компиляция

```
./gradlew assembleDebug        # Linux/macOS
gradlew.bat assembleDebug      # Windows
./gradlew installDebug         # установка на подключённое устройство
```

APK появится в `app/build/outputs/apk/debug/app-debug.apk`.

Примечание: бинарные файлы gradle wrapper (`gradle-wrapper.jar`, `gradlew`) включены в репозиторий.
Если их нет, сгенерируйте командой `gradle wrapper --gradle-version 8.7` либо используйте Gradle из Android Studio.

## Использование

1. Запустите приложение и нажмите **+**.
2. Настройте тип, текст, цвета и размер, сохраните.
3. Долгое нажатие на домашнем экране -> Виджеты -> "Widget Creator".
4. Выберите шаблон на экране настройки — виджет появится на рабочем столе.
