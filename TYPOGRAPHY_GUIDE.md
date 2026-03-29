# Typography Usage Guide - SmartLawyerAgenda

## Overview

This guide provides standardized typography usage for the SmartLawyerAgenda app, ensuring consistent Amiri font application across all Text composables.

## Current Setup

The app uses **MaterialTheme.typography** as the primary typography system with Amiri font family integrated through `Typography` in `Type.kt`.

### Key Files:
- `Type.kt` - Main typography definitions with Amiri font
- `DesignSystem.kt` - Typography utilities and deprecated AppTypography
- `Theme.kt` - MaterialTheme configuration

## ✅ Recommended Approach: MaterialTheme.typography

**Always use `MaterialTheme.typography.*` for consistent typography.**

### Basic Usage

```kotlin
Text(
    text = "عنوان القضية",
    style = MaterialTheme.typography.headlineSmall
)
```

### With Color

```kotlin
Text(
    text = "نص ملون",
    style = MaterialTheme.typography.bodyMedium,
    color = AppColors.Primary
)
```

### With Custom Modifications (Preserving Amiri Font)

When you need to modify typography styles, use `TypographyUtils` to preserve the Amiri font family:

```kotlin
// ✅ CORRECT - Preserves Amiri font
Text(
    text = "نص عريض",
    style = TypographyUtils.bold(MaterialTheme.typography.titleMedium)
)

// ✅ CORRECT - Multiple modifications
Text(
    text = "نص مخصص",
    style = TypographyUtils.withAmiriFont(
        MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1565C0)
    )
)
```

## ❌ Avoid These Patterns

### Don't use .copy() directly (loses Amiri font)

```kotlin
// ❌ WRONG - May lose Amiri font family
Text(
    text = "عنوان",
    style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1565C0)
    )
)
```

### Don't mix AppTypography and MaterialTheme.typography

```kotlin
// ❌ WRONG - Inconsistent approach
Text(
    text = "نص",
    style = AppTypography.HeadlineSmall  // Deprecated
)

Text(
    text = "نص آخر",
    style = MaterialTheme.typography.headlineSmall  // Correct
)
```

## Typography Hierarchy

| Style | Usage | Font Size |
|-------|-------|-----------|
| `displayLarge` | Hero titles, splash screens | 57sp |
| `displayMedium` | Main page titles | 45sp |
| `displaySmall` | Section headers | 36sp |
| `headlineLarge` | Major headings | 32sp |
| `headlineMedium` | Page titles | 28sp |
| `headlineSmall` | Screen titles | 24sp |
| `titleLarge` | Card titles | 22sp |
| `titleMedium` | Component titles | 16sp |
| `titleSmall` | Small titles | 14sp |
| `bodyLarge` | Main content | 16sp |
| `bodyMedium` | Regular text | 14sp |
| `bodySmall` | Secondary text | 12sp |
| `labelLarge` | Button labels | 14sp |
| `labelMedium` | Small labels | 12sp |
| `labelSmall` | Tiny labels | 11sp |

## TypographyUtils Functions

### `TypographyUtils.bold(style: TextStyle)`
Creates a bold version while preserving Amiri font:

```kotlin
Text(
    text = "عنوان عريض",
    style = TypographyUtils.bold(MaterialTheme.typography.titleLarge)
)
```

### `TypographyUtils.withColor(style: TextStyle, color: Color)`
Applies color while preserving Amiri font:

```kotlin
Text(
    text = "نص ملون",
    style = TypographyUtils.withColor(
        MaterialTheme.typography.bodyMedium,
        AppColors.Primary
    )
)
```

### `TypographyUtils.withAmiriFont(...)`
Full customization while preserving Amiri font:

```kotlin
Text(
    text = "نص مخصص بالكامل",
    style = TypographyUtils.withAmiriFont(
        MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color(0xFF1565C0),
        letterSpacing = 0.5.sp
    )
)
```

## Common Patterns

### Screen Titles
```kotlin
Text(
    text = "إضافة دعوى جديدة",
    style = TypographyUtils.bold(MaterialTheme.typography.headlineSmall),
    color = Color.White
)
```

### Card Headers
```kotlin
Text(
    text = "معلومات القضية",
    style = TypographyUtils.bold(MaterialTheme.typography.titleMedium)
)
```

### Error Messages
```kotlin
Text(
    text = "خطأ في البيانات",
    style = TypographyUtils.withColor(
        MaterialTheme.typography.bodyMedium,
        MaterialTheme.colorScheme.error
    )
)
```

### Statistics/Counts
```kotlin
Text(
    text = "عدد الجلسات: ${count}",
    style = TypographyUtils.withAmiriFont(
        MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = Color.Yellow
    )
)
```

### Form Labels
```kotlin
Text(
    text = "رقم القضية",
    style = MaterialTheme.typography.labelLarge,
    fontWeight = FontWeight.Medium
)
```

### Secondary Text
```kotlin
Text(
    text = "نص ثانوي",
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant
)
```

## Migration from AppTypography

If you find any remaining `AppTypography.*` usage, replace with `MaterialTheme.typography.*`:

```kotlin
// Before (deprecated)
Text(text = "نص", style = AppTypography.HeadlineSmall)

// After (correct)
Text(text = "نص", style = MaterialTheme.typography.headlineSmall)
```

## Font Family Preservation

The Amiri font family is automatically applied to all typography styles. When using `TypographyUtils`, the font family is explicitly preserved:

```kotlin
// All these preserve Amiri font:
TypographyUtils.bold(MaterialTheme.typography.titleMedium)
TypographyUtils.withColor(MaterialTheme.typography.bodyLarge, Color.Red)
TypographyUtils.withAmiriFont(MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
```

## Best Practices

1. **Always use MaterialTheme.typography.*** as the base
2. **Use TypographyUtils for modifications** to preserve Amiri font
3. **Be consistent** - don't mix different typography approaches
4. **Choose appropriate hierarchy** - use larger styles for more important content
5. **Test on different screen sizes** to ensure readability
6. **Consider RTL layout** - all typography is optimized for Arabic text

## Troubleshooting

### Font not appearing as Amiri?
- Ensure you're using `TypographyUtils` for modifications
- Check that `MaterialTheme` is properly configured
- Verify font files are in `res/font/` directory

### Inconsistent typography?
- Replace all `AppTypography.*` with `MaterialTheme.typography.*`
- Use `TypographyUtils` instead of `.copy()` for modifications
- Follow the patterns in this guide

### Text not displaying correctly?
- Check RTL layout configuration
- Verify text alignment for Arabic content
- Ensure proper color contrast

## Summary

- ✅ Use `MaterialTheme.typography.*` for all text styles
- ✅ Use `TypographyUtils` for modifications to preserve Amiri font
- ❌ Don't use `.copy()` directly on typography styles
- ❌ Don't use deprecated `AppTypography.*`
- 🎯 Maintain consistency across all screens and components

This approach ensures that all text in your app consistently uses the beautiful Amiri font family while maintaining proper Material Design typography hierarchy.

