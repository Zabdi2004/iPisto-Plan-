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

- La identidad visual usa el tema oscuro azul noche de iPisto, tokens centralizados en `ui/theme/Theme.kt`, tarjetas redondeadas y una mascota original de quetzal dibujada en Compose.
- La navegación inferior ofrece Inicio, Informes, Nuevo registro y Perfil. Nuevo registro guarda ingresos, gastos fijos o variables en sus tablas existentes; cuentas y transferencias siguen fuera del alcance actual.
- Los registros tienen fecha opcional para los movimientos únicos. Room v2 añade esa columna de forma nullable; las filas antiguas conservan fecha desconocida. Informes presenta el mes actual, sin inventar historial.
- `BalanceCalculator` es la fuente única de normalización mensual: diario × 365/12, semanal × 52/12, quincenal (cada 15 días) × 365/15/12, mensual × 1 y único en el mes de su fecha. Los importes se analizan con `BigDecimal`, se limitan a dos decimales y se guardan como `Double`.
- Incluye registro e inicio de sesión local, almacenamiento de contraseñas con PBKDF2 y migración de verificación para hashes antiguos, datos financieros por usuario, metas y cálculo reactivo del balance.
- Los campos monetarios comparten `MoneyInput`; no agrupan cifras mientras se escribe y conservan la selección/cursor. Los decimales con punto o coma se convierten al guardar.
- La distribución de idiomas no está completa: hay recursos en `values` únicamente. La opción K’iche’ selecciona locale `qu`, pero no existe un conjunto de traducciones K’iche’ revisadas.
- `GraficasScreen` muestra comparativas mensuales normalizadas, gastos por categoría, saldos de deuda y progreso de metas con los datos actuales de Room. No representa series históricas.
- Las categorías personalizadas se pueden ingresar desde el selector de categorías. La pantalla de configuración declara las opciones que aún no están disponibles en vez de ofrecer controles sin acción.
- Las pruebas instrumentadas en `app/src/androidTest` cubren CRUD/persistencia e aislamiento por usuario en Room, separación de gastos fijos y variables, recreación posterior a eliminar, hashing de contraseñas, sesión y flujos de autenticación. La migración v1→v2 añade fechas sin asignarlas a filas anteriores.
- La aplicación todavía no incluye cuentas bancarias ni transferencias.

## Verificación reciente

En el entorno local con JDK Temurin 17:

- `testDebugUnitTest`: correcto, 12 pruebas unitarias.
- `assembleDebug`: correcto; genera `app/build/outputs/apk/debug/app-debug.apk`.
- `lintDebug`: correcto, 0 errores y 101 advertencias.
- `assembleDebugAndroidTest`: correcto; el APK instrumentado compila.
- `connectedDebugAndroidTest`: correcto en NIC-LX3 / Android 15, 6 pruebas ejecutadas y 0 fallos. La navegación todavía no tiene prueba instrumentada de interacción.
# Cálculo del balance mensual

Los importes recurrentes se estiman en un mes promedio: diario × 365/12, semanal × 52/12, cada 15 días × 365/15/12 y mensual × 1. «Quincenal» significa exactamente cada 15 días (no dos veces por mes). «Único» solo cuenta en el mes de su fecha de registro. Las fechas se guardan en milisegundos Unix; en registros anteriores a la migración permanecen desconocidas y no se les atribuye una fecha histórica.
