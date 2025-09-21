# SmartLawyerAgenda - أجندة المحامي الذكية

تطبيق أندرويد لإدارة أجندة المحاكم اليومية للمحامين مع دعم النسخ الاحتياطية على Google Drive.

## المميزات

- **واجهة عربية RTL**: تصميم متكامل للغة العربية مع خط Amiri
- **إدارة الجلسات**: إضافة وتعديل وحذف جلسات المحكمة
- **التاريخ الهجري والميلادي**: عرض التواريخ بالتقويمين
- **النسخ الاحتياطية**: نسخ احتياطية تلقائية على Google Drive
- **قاعدة بيانات محلية**: استخدام Room Database للتخزين المحلي
- **واجهة حديثة**: تصميم Material 3 مع Jetpack Compose

## متطلبات النظام

- Android 7.0 (API 24) أو أحدث
- Google Play Services
- اتصال بالإنترنت للنسخ الاحتياطية

## إعداد المشروع

### 1. إعداد Google Services

1. اذهب إلى [Google Cloud Console](https://console.cloud.google.com/)
2. أنشئ مشروع جديد أو اختر مشروع موجود
3. فعّل Google Drive API و Google Sign-In API
4. أنشئ OAuth 2.0 credentials للتطبيق
5. حمّل ملف `google-services.json` وضعه في مجلد `app/`

### 2. إعداد قاعدة البيانات

التطبيق يستخدم Room Database محلياً، لا حاجة لإعداد خادم خارجي.

### 3. إعداد الخطوط

1. حمّل خط Amiri من [Google Fonts](https://fonts.google.com/specimen/Amiri)
2. ضع ملف `Amiri-Regular.ttf` في `app/src/main/assets/fonts/`
3. أعد تسمية الملف إلى `amiri.ttf`

### 4. بناء التطبيق

```bash
./gradlew assembleDebug
```

## هيكل المشروع

```
app/src/main/java/com/example/smartlawyeragenda/
│   MainActivity.kt
│
├───data
│   │   AppDatabase.kt
│   │   DatabaseConverters.kt
│   │   SampleDataGenerator.kt
│   │
│   ├───dao
│   │       CaseDao.kt
│   │       SessionDao.kt
│   │
│   └───entities
│           CaseEntity.kt
│           SessionEntity.kt
│
├───repository
│       MainRepository.kt
│
├───ui
│   │   AppNavHost.kt
│   │   AppNavHost_COMPLETED_TODOS.md
│   │
│   ├───animations
│   │       AppAnimations.kt
│   │
│   ├───components
│   │       CaseSearchHelper.kt
│   │       ConfirmationDialog.kt
│   │       DateFilterChips.kt
│   │       DatePickerDialog.kt
│   │       EnhancedComponents.kt
│   │       ErrorDialog.kt
│   │       GoogleSignInHelper.kt
│   │       LoadingStates.kt
│   │       SearchBar.kt
│   │       StatisticsCard.kt
│   │       ThemeToggle.kt
│   │
│   ├───navigation
│   │       NavigationConstants.kt
│   │       NavigationHelper.kt
│   │       README.md
│   │
│   ├───screens
│   │       AddCaseScreen.kt
│   │       AddEditSessionScreen.kt
│   │       AgendaScreen.kt
│   │       CasesScreen.kt
│   │       EditCaseScreen.kt
│   │       LoginScreen.kt
│   │       SettingsScreen.kt
│   │       SplashScreen.kt
│   │
│   └───theme
│           Color.kt
│           DesignSystem.kt
│           Theme.kt
│           ThemeManager.kt
│           Type.kt
│
├───utils
│       BackupManager.kt
│       ExportHelper.kt
│       HijriUtils.kt
│       NotificationHelper.kt
│
└───viewmodel
        AgendaViewModel.kt
        AgendaViewModelFactory.kt
```

## الاستخدام

### إضافة جلسة جديدة

1. اضغط على زر + في الشاشة الرئيسية
2. أدخل معلومات القضية (رقم القضية، اسم الموكل، اسم الخصم)
3. أدخل تفاصيل الجلسة (التاريخ، السبب، القرار)
4. يمكنك تحديد تاريخ الجلسة التالية لإنشاء جلسة مؤجلة تلقائياً
5. اضغط "حفظ الجلسة"

### النسخ الاحتياطية

1. اذهب إلى الإعدادات
2. سجل دخولك باستخدام Google
3. اضغط "نسخ احتياطي إلى Google Drive" لحفظ البيانات
4. اضغط "استعادة من Google Drive" لاسترجاع البيانات

### المميزات المتقدمة

- **الجلسات المؤجلة**: عند تحديد تاريخ الجلسة التالية، يتم إنشاء جلسة جديدة تلقائياً
- **البحث والفلترة**: عرض الجلسات حسب التاريخ
- **التواريخ المزدوجة**: عرض التاريخ الهجري والميلادي

## التطوير

### إضافة ميزات جديدة

1. أضف الكيانات الجديدة في `data/entities/`
2. أنشئ DAOs في `data/dao/`
3. حدث المستودع في `repository/`
4. أضف ViewModels في `viewmodel/`
5. أنشئ UI في `ui/screens/`

### الاختبار

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## المساهمة

1. Fork المشروع
2. أنشئ branch للميزة الجديدة
3. Commit التغييرات
4. Push إلى Branch
5. أنشئ Pull Request

## الترخيص

هذا المشروع مرخص تحت رخصة MIT - راجع ملف [LICENSE](LICENSE) للتفاصيل.

## الدعم

للحصول على الدعم أو الإبلاغ عن مشاكل، يرجى فتح issue في GitHub.

## التحديثات القادمة

- [ ] دعم المرفقات والملفات
- [ ] إشعارات الجلسات
- [ ] تقارير إحصائية
- [ ] دعم متعدد المستخدمين
- [ ] تصدير PDF للجلسات

---

**ملاحظة**: هذا التطبيق مخصص للمحامين العرب ويدعم اللغة العربية بالكامل مع التصميم RTL.
