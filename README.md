# SmartLawyerAgenda

SmartLawyerAgenda is an Android application for managing legal cases and court sessions with an Arabic-first, RTL-friendly user experience.  
تطبيق SmartLawyerAgenda هو تطبيق أندرويد لإدارة القضايا والجلسات القضائية مع تجربة استخدام عربية بالكامل ودعم اتجاه RTL.

The app combines local-first data storage (Room) with optional Google Drive backup/restore and file export (JSON/CSV).  
يجمع التطبيق بين التخزين المحلي أولاً (Room) مع دعم اختياري للنسخ الاحتياطي والاستعادة عبر Google Drive وتصدير الملفات بصيغتي JSON وCSV.

## Highlights | المميزات

- Arabic and RTL-focused UI with Amiri typography.  
  واجهة عربية موجهة لـ RTL مع خط Amiri.
- Case management: create, edit, search, activate/deactivate, and delete cases.  
  إدارة القضايا: إضافة وتعديل وبحث وتفعيل/تعطيل وحذف القضايا.
- Session management: create, edit, delete, filter, and update session status.  
  إدارة الجلسات: إضافة وتعديل وحذف وتصفية وتحديث حالة الجلسة.
- Date intelligence: day/week/month/upcoming filters, plus Hijri and Gregorian display.  
  مرونة في التواريخ: فلاتر يوم/أسبوع/شهر/القادم مع عرض هجري وميلادي.
- Backup and restore through Google Drive integration.  
  نسخ احتياطي واستعادة عبر تكامل Google Drive.
- Data export and sharing via JSON and CSV.  
  تصدير ومشاركة البيانات بصيغتي JSON وCSV.
- Built with Jetpack Compose and Material 3.  
  مبني باستخدام Jetpack Compose وMaterial 3.

## Tech Stack | التقنيات المستخدمة

- Kotlin + Coroutines + Flow  
  كوتلن + Coroutines + Flow
- Jetpack Compose + Material 3  
  جيتباك كومبوز + Material 3
- Room Database (KSP)  
  قاعدة بيانات Room (مع KSP)
- AndroidX Navigation Compose  
  AndroidX Navigation Compose
- Google Credential Manager + Google Sign-In  
  Google Credential Manager + تسجيل الدخول عبر Google
- Google Drive API  
  Google Drive API

## Architecture Overview | نظرة على المعمارية

- `data/`: Room entities, DAOs, database setup.  
  `data/`: الكيانات وواجهات DAO وإعداد قاعدة البيانات.
- `repository/`: `MainRepository` for business/data operations.  
  `repository/`: يحتوي `MainRepository` لعمليات البيانات ومنطق التطبيق.
- `viewmodel/`: `AgendaViewModel` state orchestration and user actions.  
  `viewmodel/`: يحتوي `AgendaViewModel` لإدارة الحالة وأفعال المستخدم.
- `ui/`: navigation, screens, reusable components, and theme.  
  `ui/`: التنقل والشاشات والمكونات القابلة لإعادة الاستخدام والثيم.
- `utils/`: backup, export, calendar/date helpers.  
  `utils/`: أدوات النسخ الاحتياطي والتصدير والتاريخ.

## Requirements | المتطلبات

- Android Studio (latest stable recommended)  
  Android Studio (يفضل أحدث إصدار مستقر)
- JDK 21  
  JDK 21
- Android SDK (`compileSdk = 36`, `targetSdk = 36`, `minSdk = 26`)  
  Android SDK (`compileSdk = 36`, `targetSdk = 36`, `minSdk = 26`)

## Quick Start | البدء السريع

1. Clone the repository.  
   قم باستنساخ المستودع.
2. Place `google-services.json` inside `app/` (see Google setup below).  
   ضع ملف `google-services.json` داخل `app/` (راجع إعداد Google أدناه).
3. Open the project in Android Studio.  
   افتح المشروع في Android Studio.
4. Sync Gradle.  
   نفّذ مزامنة Gradle.
5. Run:  
   شغّل:

```bash
./gradlew assembleDebug
```

On Windows PowerShell:  
على Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

## Google Setup (Sign-In + Drive Backup) | إعداد Google (تسجيل الدخول + النسخ الاحتياطي عبر Drive)

1. Create a Google Cloud/Firebase project.  
   أنشئ مشروع Google Cloud/Firebase.
2. Register Android app package: `com.example.smartlawyeragenda`.  
   سجّل حزمة تطبيق أندرويد: `com.example.smartlawyeragenda`.
3. Enable Google Drive API.  
   فعّل Google Drive API.
4. Configure Google Sign-In / OAuth credentials.  
   اضبط بيانات اعتماد Google Sign-In / OAuth.
5. Download `google-services.json` and place it in `app/`.  
   حمّل ملف `google-services.json` وضعه داخل `app/`.
6. Sync project and verify `default_web_client_id` is generated.  
   نفّذ مزامنة المشروع وتأكد من توليد `default_web_client_id`.

If `default_web_client_id` is missing, sign-in will fail at runtime.  
إذا كان `default_web_client_id` غير موجود، سيفشل تسجيل الدخول أثناء التشغيل.

## Build and Test | البناء والاختبار

```bash
./gradlew assembleDebug
./gradlew test
```

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat test
```

## Project Layout | هيكل المشروع

```text
app/src/main/java/com/example/smartlawyeragenda/
  data/
  repository/
  ui/
  utils/
  viewmodel/
```

## Documentation | الوثائق

- `FUNCTIONALITY_DOCUMENTATION.md`: feature and architecture notes.  
  `FUNCTIONALITY_DOCUMENTATION.md`: توثيق الوظائف والمعمارية.
- `CODE_REVIEW.md`: resolved issues and follow-up items.  
  `CODE_REVIEW.md`: المشكلات التي تم حلها وعناصر المتابعة.
- `TYPOGRAPHY_GUIDE.md`: font and typography standards.  
  `TYPOGRAPHY_GUIDE.md`: معايير الخطوط والطباعة.

## Roadmap Ideas | أفكار خارطة الطريق

- Attachments and file linking per case/session.  
  المرفقات وربط الملفات لكل قضية/جلسة.
- Session reminders/notifications.  
  تذكيرات وإشعارات الجلسات.
- Enhanced reporting and analytics.  
  تقارير وتحليلات متقدمة.
- Multi-user collaboration.  
  دعم التعاون بين عدة مستخدمين.
- Rich PDF exports.  
  تصدير PDF متقدم.
