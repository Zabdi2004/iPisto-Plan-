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
- La navegación inferior ofrece Inicio, Informes, Nuevo registro, Metas y Perfil. Nuevo registro guarda ingresos, gastos fijos o variables en sus tablas existentes; cuentas y transferencias siguen fuera del alcance actual.
- Los registros tienen fecha nullable para movimientos únicos. Room v2 añade esa columna sin inventar fechas para filas antiguas. Las fechas de los registros recurrentes describen una periodicidad, no una lista de cobros o pagos ocurridos.
- `BalanceCalculator.equivalenteMensualEstimado` es la fuente única de equivalencias: diario × 365/12, semanal × 52/12, quincenal (cada 15 días) × 365/15/12, mensual × 1 y único al importe real en el mes de su fecha. Los movimientos únicos no se anualizan.
- Inicio, Balance e Informes usan las mismas equivalencias mensuales estimadas; así Q100 semanales son Q433.33 al mes en promedio anualizado, mientras Q100 mensuales son Q100. El importe diario equivale a ×365/12 (Q100 diarios = Q3,041.67 estimados al mes). No se presenta esa proyección como dinero real recibido o pagado en un mes concreto.
- La aplicación no ofrece todavía un total real completo por mes. Aunque los movimientos únicos tienen fecha, las entradas recurrentes guardan una regla y no cada ocurrencia. Para sumar totales históricos reales se necesita una tabla de movimientos/ocurrencias con importe, fecha efectiva, tipo/categoría y vínculo opcional a la regla recurrente. Los datos legacy mantienen fecha desconocida.
- Los importes se analizan con `BigDecimal`, se limitan a dos decimales y se guardan como `Double`.
- Incluye registro e inicio de sesión local, almacenamiento de contraseñas con PBKDF2 y migración de verificación para hashes antiguos, datos financieros por usuario, metas y cálculo reactivo del balance.
- Los campos monetarios comparten `MoneyInput`; no agrupan cifras mientras se escribe y conservan la selección/cursor. Los decimales con punto o coma se convierten al guardar.
- La distribución de idiomas no está completa: hay recursos en `values` únicamente. La opción K’iche’ selecciona locale `qu`, pero no existe un conjunto de traducciones K’iche’ revisadas.
- `GraficasScreen` muestra comparativas de equivalentes mensuales estimados, gastos por categoría, saldos de deuda y progreso de metas con los datos actuales de Room. No representa totales reales por mes ni series históricas.
- Educación Financiera usa una columna desplazable verticalmente; no incluye carruseles horizontales.
- Las categorías personalizadas se pueden ingresar desde el selector de categorías. La pantalla de configuración declara las opciones que aún no están disponibles en vez de ofrecer controles sin acción.
- Las pruebas instrumentadas en `app/src/androidTest` cubren CRUD/persistencia e aislamiento por usuario en Room, separación de gastos fijos y variables, recreación posterior a eliminar, hashing de contraseñas, sesión y flujos de autenticación. La migración v1→v2 añade fechas sin asignarlas a filas anteriores.
- La aplicación todavía no incluye cuentas bancarias ni transferencias.

## Verificación reciente

En el entorno local con JDK Temurin 17 (10 de octubre de 2026):

- `testDebugUnitTest`: correcto, 13 pruebas unitarias.
- `assembleDebug`: correcto; genera `app/build/outputs/apk/debug/app-debug.apk`.
- `lintDebug`: correcto, 0 errores y 102 advertencias.
- `assembleDebugAndroidTest`: correcto; el APK instrumentado compila.
- `connectedDebugAndroidTest`: correcto en NIC-LX3 / Android 15, 9 pruebas ejecutadas y 0 fallos. Incluye navegación principal, renderizado del quetzal en varios tamaños y desplazamiento de Educación Financiera hasta el final y de regreso al inicio.
# Cálculo del equivalente mensual estimado

Los importes recurrentes se estiman en un mes promedio: diario × 365/12, semanal × 52/12, cada 15 días × 365/15/12 y mensual × 1. «Quincenal» significa exactamente cada 15 días (no dos veces por mes). «Único» solo cuenta por su importe real en el mes de su fecha de registro, sin multiplicarlo. Las fechas se guardan en milisegundos Unix; en registros anteriores a la migración permanecen desconocidas y no se les atribuye una fecha histórica.

El equivalente mensual sirve para comparar presupuestos; no es el total de transacciones liquidadas dentro de un mes calendario. Para totales reales históricos, primero se deben almacenar las ocurrencias de ingresos y gastos con su fecha efectiva (también para las periodicidades recurrentes).
