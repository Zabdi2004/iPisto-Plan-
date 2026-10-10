# iPisto

Aplicación Android local de finanzas personales para Guatemala, escrita en Kotlin con Jetpack Compose, Material 3, MVVM, Room, Flow y Navigation Compose. Los datos financieros se guardan localmente; el proyecto no requiere Firebase.

## Requisitos y compilación

- Android Studio con Android SDK 34.
- JDK 17 (el wrapper del proyecto usa Gradle 8.5).
- En Windows, abre el proyecto en Android Studio o ejecuta desde PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Estructura

- `data/local`: entidades, DAOs y base de datos Room.
- `data/repository`: acceso a usuarios, finanzas y metas.
- `viewmodel`: estado de autenticación y finanzas.
- `domain/calculator`: normalización mensual y balance financiero.
- `ui`: pantallas y componentes Compose.
- `navigation`: destinos y protección de pantallas privadas.

## Estado conocido

- Incluye registro e inicio de sesión local, almacenamiento de contraseñas con PBKDF2 y migración de verificación para hashes antiguos, datos financieros por usuario, metas y cálculo reactivo del balance.
- Los campos monetarios comparten `MoneyInput`; no agrupan cifras mientras se escribe y conservan la selección/cursor. Los decimales con punto o coma se convierten al guardar.
- La distribución de idiomas no está completa: hay recursos en `values` únicamente. La opción K’iche’ selecciona locale `qu`, pero no existe un conjunto de traducciones K’iche’ revisadas.
- `GraficasScreen` muestra comparativas mensuales normalizadas, gastos por categoría, saldos de deuda y progreso de metas con los datos actuales de Room. El modelo no incluye fechas de transacción ni rangos históricos, así que no puede representar una serie temporal real.
- Las categorías personalizadas se pueden ingresar desde el selector de categorías. La pantalla de configuración declara las opciones que aún no están disponibles en vez de ofrecer controles sin acción.
- Las pruebas instrumentadas en `app/src/androidTest` cubren el aislamiento CRUD/persistencia de ingresos en Room, hashing de contraseñas, restauración/cierre de sesión y el flujo de registro e inicio de sesión del `AuthViewModel`. Se compilan, pero no se ejecutaron porque el AVD requiere un controlador de aceleración de hardware ausente. La navegación todavía requiere pruebas instrumentadas.
- El esquema Room continúa en versión 1; no se han comprobado migraciones desde instalaciones con datos reales.
- La aplicación no registra transacciones con fechas, cuentas bancarias ni transferencias.

## Verificación reciente

En el entorno local con JDK Temurin 17:

- `testDebugUnitTest`: correcto (10 pruebas unitarias, 0 fallos; balance, entrada monetaria y validación de categorías).
- `assembleDebug`: correcto; genera `app/build/outputs/apk/debug/app-debug.apk`.
- `lintDebug`: correcto después de corregir la coordenada de Espresso y compatibilidad con `minSdk 21`; el informe mantiene advertencias que deben revisarse.
- Las pruebas instrumentadas se compilaron con `assembleDebugAndroidTest`, pero no se ejecutaron: el AVD configurado requiere aceleración de hardware y el controlador de hipervisor no está instalado. No se instalaron controladores ni se modificó configuración del sistema.
