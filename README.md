# 💸 iPisto

### Tus finanzas, bajo control.

**iPisto** es una aplicación Android de finanzas personales diseñada para organizar ingresos, controlar gastos, administrar deudas y dar seguimiento a metas de ahorro. Desarrollada con Kotlin y Jetpack Compose, prioriza una experiencia visual moderna y el almacenamiento local de la información.

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Database-Room-527FFF?style=for-the-badge" alt="Room">
</p>

<p align="center">
  <b>Gestión financiera local · Visualización de datos · Presupuestos personales</b>
</p>

---

## 📑 Tabla de contenido

- [✨ Funcionalidades](#-funcionalidades)
- [🛠️ Tecnologías](#️-tecnologías)
- [🏗️ Arquitectura](#️-arquitectura)
- [🧮 Cálculo del balance financiero](#-cálculo-del-balance-financiero)
- [🔐 Seguridad y almacenamiento](#-seguridad-y-almacenamiento)
- [🚀 Instalación y ejecución](#-instalación-y-ejecución)
- [🧪 Pruebas y verificación](#-pruebas-y-verificación)
- [📌 Alcance y limitaciones](#-alcance-y-limitaciones)
- [🗺️ Mejoras futuras](#️-mejoras-futuras)

---

## ✨ Funcionalidades

iPisto reúne herramientas para comprender y administrar las finanzas personales desde una sola aplicación.

| Módulo | Descripción |
|---|---|
| 🏠 **Inicio** | Consulta el balance financiero y los principales indicadores de tus finanzas. |
| 💵 **Ingresos** | Registra y administra ingresos según su periodicidad. |
| 📉 **Gastos fijos** | Organiza compromisos recurrentes y consulta sus equivalencias mensuales estimadas. |
| 🛒 **Gastos variables** | Registra gastos que cambian según tus necesidades y hábitos de consumo. |
| 📊 **Informes** | Visualiza comparativas de ingresos y gastos, gastos por categoría y otros indicadores disponibles. |
| 🎯 **Metas de ahorro** | Define objetivos, registra avances y consulta el progreso de tus metas. |
| 💳 **Deudas** | Administra información de deudas y consulta los saldos registrados. |
| 👤 **Perfil** | Accede a la configuración personal y las opciones disponibles de la cuenta. |
| 📚 **Educación financiera** | Consulta contenido educativo mediante una interfaz desplazable. |
| 🔒 **Autenticación local** | Registra usuarios, inicia sesión, bloquea el acceso y permite recuperar contraseñas mediante preguntas de seguridad. |

La aplicación utiliza una interfaz oscura con tonos azul noche, tarjetas redondeadas y una mascota original de quetzal integrada mediante Compose.

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal. |
| **Jetpack Compose** | Construcción declarativa de la interfaz. |
| **Material 3** | Componentes y estilos visuales. |
| **MVVM** | Separación entre interfaz, estado y lógica de presentación. |
| **Room** | Persistencia local de usuarios y datos financieros. |
| **Kotlin Coroutines** | Ejecución de operaciones asíncronas. |
| **Flow** | Observación reactiva de los datos. |
| **Navigation Compose** | Navegación entre pantallas y protección de destinos privados. |
| **BigDecimal** | Análisis y normalización de importes monetarios. |
| **PBKDF2** | Derivación de credenciales con sal para la autenticación y recuperación local. |

**Enfoque offline:** la aplicación no requiere Firebase ni un backend remoto para las funcionalidades descritas. Los datos se almacenan en la base de datos local del dispositivo.

## 🏗️ Arquitectura

El proyecto organiza sus responsabilidades en módulos para facilitar el mantenimiento y la evolución del código.

```text
app/src/main/java/com/finanzaspersonales/gt/
│
├── data/
│   ├── local/          # Room, entidades y DAOs
│   └── repository/     # Acceso y persistencia de datos
│
├── domain/
│   └── calculator/     # Cálculos y normalización financiera
│
├── viewmodel/          # Estado y lógica de presentación
│
├── ui/
│   ├── components/     # Componentes reutilizables
│   ├── screens/        # Pantallas de la aplicación
│   └── theme/          # Colores, tipografía y tema visual
│
└── navigation/         # Destinos y navegación
```

### Flujo de datos

La interfaz observa el estado expuesto por los ViewModels. Estos coordinan la lógica de presentación y acceden a los repositorios, que se encargan de interactuar con Room.

```text
┌─────────────────────┐
│   Jetpack Compose   │
│      UI / Screens   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│      ViewModels     │
│   Estado y lógica   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     Repositories    │
│ Acceso a los datos  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│      Room / SQLite  │
│   Datos locales     │
└─────────────────────┘
```

## 🧮 Cálculo del balance financiero

iPisto utiliza equivalencias mensuales estimadas para comparar ingresos y gastos con distintas periodicidades.

La lógica se centraliza en `BalanceCalculator.equivalenteMensualEstimado`, evitando que las pantallas apliquen fórmulas diferentes al mismo tipo de movimiento.

### Fórmulas utilizadas

| Periodicidad | Equivalente mensual estimado |
|---|---|
| Diario | Importe × 365 / 12 |
| Semanal | Importe × 52 / 12 |
| Quincenal | Importe × 365 / 15 / 12 |
| Mensual | Importe × 1 |
| Único | Importe real correspondiente al mes de su fecha registrada |

**Importante:** en iPisto, *quincenal* significa exactamente cada 15 días, no dos veces por mes.

### Ejemplos

Suponiendo un importe de Q100:

| Periodicidad | Equivalente mensual |
|---|---:|
| Diario | Q3,041.67 |
| Semanal | Q433.33 |
| Cada 15 días | Q202.78 |
| Mensual | Q100.00 |

Estos valores representan promedios estimados, no necesariamente el dinero recibido o pagado dentro de un mes calendario específico.

Los movimientos únicos no se multiplican ni se anualizan. Se contabilizan por su importe real en el mes de su fecha registrada.

### Precisión monetaria

Los importes se procesan con `BigDecimal`, se limitan a dos decimales y se almacenan como `Double` en la persistencia actual.

Los campos monetarios utilizan el componente compartido `MoneyInput`, que permite introducir decimales con punto o coma y conserva la selección del cursor durante la escritura.

## 🔐 Seguridad y almacenamiento

La aplicación contempla autenticación local y separación de los datos financieros por usuario.

- Registro e inicio de sesión locales.
- Hash de contraseñas mediante PBKDF2 con sal.
- Recuperación local de contraseña mediante preguntas de seguridad.
- Sesión de un solo inicio: al cerrar la aplicación la sesión finaliza, por lo que se debe iniciar sesión de nuevo en el siguiente arranque.
- Persistencia mediante Room.
- Migraciones explícitas para conservar los datos durante las actualizaciones de la base de datos.
- Verificación de propiedad de los registros en las operaciones correspondientes.

Al ser una aplicación local, no debe confundirse el aislamiento entre usuarios de la aplicación con una protección absoluta frente a alguien que tenga acceso privilegiado al dispositivo o a sus archivos.

## 🚀 Instalación y ejecución

### Requisitos

- Android Studio compatible con el proyecto.
- Android SDK 34.
- JDK 17.
- Gradle Wrapper 8.5, según la configuración documentada del proyecto.

### Obtener el código

```bash
git clone https://github.com/Zabdi2004/iPisto-Plan-.git
cd iPisto-Plan-
```

Abre el proyecto en Android Studio y espera a que termine la sincronización de Gradle.

### Compilar en Windows

Desde PowerShell, en la raíz del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
```

### Generar el APK

Si la compilación termina correctamente, el APK de depuración se encuentra en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Puedes instalarlo en un dispositivo Android compatible para probar la aplicación.

## 🧪 Pruebas y verificación

El proyecto incluye pruebas unitarias e instrumentadas para validar aspectos de la lógica financiera, la persistencia y la autenticación.

Entre las áreas cubiertas se encuentran:

- Operaciones CRUD y persistencia con Room.
- Aislamiento de datos por usuario.
- Separación entre gastos fijos y variables.
- Hashing de contraseñas y gestión de sesiones.
- Flujos de autenticación y recuperación de contraseña.
- Migraciones de base de datos sin pérdida de los datos existentes.

### Comandos de prueba

Pruebas unitarias:

```powershell
.\gradlew.bat testDebugUnitTest
```

Compilación de la aplicación:

```powershell
.\gradlew.bat assembleDebug
```

Análisis estático:

```powershell
.\gradlew.bat lintDebug
```

Compilación de pruebas instrumentadas:

```powershell
.\gradlew.bat assembleDebugAndroidTest
```

Ejecución en un dispositivo o emulador conectado:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

### Última verificación documentada

Según los resultados reportados en el entorno local el 10 de octubre de 2026:

| Comprobación | Resultado reportado |
|---|---|
| `testDebugUnitTest` | 13 pruebas unitarias aprobadas |
| `assembleDebug` | Compilación correcta |
| `lintDebug` | 0 errores y 102 advertencias |
| `assembleDebugAndroidTest` | Compilación correcta |
| `connectedDebugAndroidTest` | 13 pruebas ejecutadas, 0 fallos |

Los resultados anteriores corresponden a esa ejecución y no garantizan que futuras modificaciones sigan compilando correctamente.

## 📌 Alcance y limitaciones

La documentación también debe dejar claro qué representa la información financiera y qué funcionalidades todavía no están implementadas.

### Totales estimados frente a totales reales

Los módulos Inicio, Balance e Informes utilizan equivalencias mensuales estimadas para comparar movimientos recurrentes.

Actualmente, la aplicación no dispone de un historial completo de ocurrencias de ingresos y gastos recurrentes. Por ello, los gráficos no representan necesariamente el total real de transacciones liquidadas durante cada mes ni series históricas de movimientos.

Para implementar esos informes sería necesario registrar cada ocurrencia con información como:

- Importe.
- Fecha efectiva.
- Tipo y categoría.
- Referencia opcional a la regla recurrente de origen.

### Fechas y registros anteriores

Los movimientos únicos pueden incluir una fecha registrada. Las entradas recurrentes representan reglas de periodicidad, no una lista de cobros o pagos que ya hayan ocurrido.

Las fechas se almacenan como milisegundos Unix. Los registros antiguos a los que no se les conocía una fecha conservan ese estado, sin atribuirles fechas históricas inventadas.

### Funcionalidades fuera del alcance actual

- Cuentas bancarias.
- Transferencias entre cuentas.
- Registro histórico completo de ocurrencias recurrentes.
- Traducción al idioma Kaqchikel (parcial: las cadenas sin traducción disponible conservan el texto en español como fallback).

La opción de idioma Kaqchikel selecciona el locale `cak` y toma sus traducciones de `app/src/main/res/values-cak/strings.xml`. Las cadenas que no cuentan con traducción en el diccionario de pruebas conservan el texto original en español como valor por defecto.

La configuración presenta las opciones que todavía no están disponibles, en lugar de ofrecer controles que aparenten funcionar sin implementar su comportamiento.

## 🗺️ Mejoras futuras

Las siguientes mejoras representan posibles líneas de evolución del proyecto:

- [ ] Historial de transacciones con fechas efectivas.
- [ ] Informes mensuales basados en movimientos reales.
- [ ] Cuentas bancarias y transferencias.
- [ ] Internacionalización completa y traducciones revisadas.
- [ ] Ampliación de las pruebas automatizadas de interfaz.
- [ ] Exportación de informes financieros.

---

<p align="center">
  <b>iPisto</b><br>
  Finanzas personales, con claridad y control.
</p>
