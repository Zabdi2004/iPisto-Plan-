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

- La identidad visual usa el tema oscuro azul noche de iPisto, tokens centralizados en `ui/theme/Theme.kt`, tarjetas redondeadas y una mascota vectorial original dibujada con Compose.
- La navegación inferior ofrece Inicio, Informes, Nuevo registro y Perfil. Cuentas aparece deshabilitado porque aún no existe un modelo persistente de cuentas; Transferencia también permanece deshabilitada. Nuevo registro guarda ingresos, gastos fijos o variables en sus tablas existentes.
- Los registros no guardan fecha/hora ni notas separadas (la descripción es el nombre existente); por eso Informes muestra equivalentes mensuales actuales y no historial diario. Cuentas, transferencias, calendarios, libros, búsqueda histórica y fotos de recibos no están implementados.
- La equivalencia mensual usada por `BalanceCalculator` es semanal × 52 / 12, quincenal × 26 / 12 y mensual × 1. Los importes introducidos se analizan con `BigDecimal`, se limitan a dos decimales y después se guardan como `Double`, conforme al esquema existente; no se ha hecho una migración a unidades menores.
- Incluye registro e inicio de sesión local, almacenamiento de contraseñas con PBKDF2 y migración de verificación para hashes antiguos, datos financieros por usuario, metas y cálculo reactivo del balance.
- Los campos monetarios comparten `MoneyInput`; no agrupan cifras mientras se escribe y conservan la selección/cursor. Los decimales con punto o coma se convierten al guardar.
- La distribución de idiomas no está completa: hay recursos en `values` únicamente. La opción K’iche’ selecciona locale `qu`, pero no existe un conjunto de traducciones K’iche’ revisadas.
- `GraficasScreen` muestra comparativas mensuales normalizadas, gastos por categoría, saldos de deuda y progreso de metas con los datos actuales de Room. El modelo no incluye fechas de transacción ni rangos históricos, así que no puede representar una serie temporal real.
- Las categorías personalizadas se pueden ingresar desde el selector de categorías. La pantalla de configuración declara las opciones que aún no están disponibles en vez de ofrecer controles sin acción.
- Las pruebas instrumentadas en `app/src/androidTest` cubren CRUD/persistencia e aislamiento por usuario en Room, separación de gastos fijos y variables, hashing de contraseñas, restauración/cierre de sesión y flujos de registro, inicio de sesión y renombrado del `AuthViewModel`. Se compilan, pero no se ejecutaron: no hay dispositivo conectado y ADB no pudo iniciar su servidor en este entorno. La navegación todavía requiere pruebas instrumentadas.
- El esquema Room continúa en versión 1; no se han comprobado migraciones desde instalaciones con datos reales.
- La aplicación no registra transacciones con fechas, cuentas bancarias ni transferencias.

## Verificación reciente

En el entorno local con JDK Temurin 17:

- `testDebugUnitTest`: correcto (11 pruebas unitarias, 0 fallos; balance, entrada monetaria —incluye decimales, negativos, límite y edición del cursor— y validación de categorías).
- `assembleDebug`: correcto; genera `app/build/outputs/apk/debug/app-debug.apk` (19,351,764 bytes, 2026-10-09 23:11 local).
- `lintDebug`: correcto, 0 errores y 101 advertencias. Incluye revisiones Compose omitidas por una incompatibilidad de APIs en la biblioteca de lint, además de advertencias de recursos, icono y preferencias ya existentes.
- `assembleDebugAndroidTest`: correcto; las pruebas instrumentadas se compilaron, pero no se ejecutaron porque ADB no pudo iniciar su servidor y no había dispositivo conectado. No se instalaron controladores ni se modificó configuración del sistema.
