# InWave
Репозиторий команды "InWave" - проекта для дисциплины "Проектная деятельность" ЮФУ иММиКн им. И.И.Воровича 25' - 26'

![Android](https://img.shields.io/badge/platform-Android-green)
![Kotlin](https://img.shields.io/badge/language-Kotlin-blueviolet)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-03DAC5)

Android-приложение стримингового сервиса для независимых исполнителей и аудиоконтента, свободного от авторских прав.

**InWave** делает акцент на социальное взаимодействие между слушателями и авторами через
комментарии с таймкодами, общие плейлисты и пользовательский контент.



## Фичи

- Воспроизведение аудио (локальное и облачное)
- Комментарии к трекам с таймкодами
- Профили пользователей с историей активности
- Общие плейлисты
- Социальная лента (лайки, комментарии, плейлисты)
- Загрузка пользовательских аудиофайлов в облако
- Упрощённые рекомендации (по жанрам и лайкам)

## Скриншоты
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/75b66faa-16bc-4e77-ad15-020a97345122" width="250"/><br/>
      InWave — главная
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/bec75440-3c5b-4d23-8551-12b37fbd0b26" width="250"/><br/>
      InWave — страница артиста
    </td>
  </tr>
  
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/098be022-fc2a-428f-86c6-d89ef81c5e53" width="250"/><br/>
      InWave — плеер
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/11e961d9-7a34-47dc-ae57-59bff67b55d0" width="250"/><br/>
      InWave — страница релиза
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/434bf60d-623b-4538-8d99-2532ec015944" width="250"/><br/>
      Системный плеер
    </td>
    <td align="center">
      <img src="screenshots/Screenshot_20251218_150903_One UI Home.jpg" width="250"/><br/>
      Экран 4
    </td>
  </tr>
</table>

## Стадии развития

**MVP / нулевая версия:**
- Воспроизведение локальных аудиофайлов
- Список треков из памяти устройства
- Базовый интерфейс плеера

**Предзащита**
- Главная страница с анимированной волной
- Топ артистов
- Топ релизов
- Страница реализа
- Страница артиста
- Исправление ошибок совместимости со старыми версиями Android

## Стек технологий
### Android

- Jetpack Compose
- Ktor Client
- Media3 (ExoPlayer)
- Dagger-Hilt
- Coil

### Backend

- Ktor Server
- Exposed ORM
- Garage (S3-совместимое хранилище)
- Nginx
- Docker

## Состав команды
### 2 Курс
- 2.1 Бельчиков Сергей
- 2.1 Белый Роман
- 2.5 Переверзева Анастасия

## 1 Курс
- 1.2 Крылов Максим
- 1.2 Каримджанов Асад
- 1.3 Большаков Иван

## Требования

- Android Studio (последняя стабильная версия)
- Android SDK 28+ (>= Android 9)
- Gradle
