# PLAN DE IMPLEMENTACIÓN - APLICACIÓN ANDROID DE FINANZAS PERSONALES

## Proyecto

**Nombre:** iPisto  
**Aplicación:** Finanzas Personales GT  
**Plataforma:** Android  
**Moneda:** Quetzales Guatemaltecos (Q)

---

## Stack Tecnológico

- **Lenguaje:** Kotlin
- **Interfaz:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose
- **Arquitectura:** MVVM
- **Estado:** ViewModel + StateFlow
- **Base de datos:** Room (persistencia local)
- **Concurrencia:** Kotlin Coroutines + Flow
- **Patrón:** Repository Pattern

---

## Arquitectura

### Flujo de Datos

```
UI (Compose Screens)
    ↓
ViewModel (StateFlow)
    ↓
Repository
    ↓
DAO (Room)
    ↓
Room Database (Local)
```

### Responsabilidades

- **UI:** Renderizar interfaz, manejar eventos de usuario
- **ViewModel:** Estado de la UI, lógica de presentación, coordinación
- **Repository:** Abstracta el acceso a datos, coordina múltiples DAOs
- **DAO:** Consultas a la base de datos Room
- **Room:** Persistencia local en el dispositivo

---

## Estructura de Paquetes

```
com.finanzaspersonales.gt
├── data/
│   ├── local/
│   │   ├── entity/          # Entidades Room (User, Ingreso, etc.)
│   │   ├── dao/              # Acceso a datos (UserDao, IngresoDao, etc.)
│   │   └── AppDatabase.kt   # Base de datos Room
│   └── repository/          # Repositorios (UserRepository, FinanzasRepository)
├── domain/
│   ├── model/               # Modelos de dominio (BalanceFinanciero)
│   └── calculator/          # Lógica de cálculos financieros
├── viewmodel/               # ViewModels (AuthViewModel, FinanzasViewModel)
├── navigation/              # Navegación con Navigation Compose
├── ui/
│   ├── screens/             # Pantallas (Login, Inicio, Gráficas, Metas)
│   ├── components/          # Componentes reutilizables
│   └── theme/               # Tema y estilos (Material 3)
├── utils/                   # Utilidades (formateo de moneda, validaciones)
└── MainActivity.kt          # Activity principal
```

**Nota:** La existencia de estos directorios NO significa que las funcionalidades estén implementadas. Cada fase agrega las clases correspondientes.
---

## Entidades Previstas

### User
- id: Long (PK, autoGenerate)
- nombreUsuario: String (UNIQUE)
- passwordHash: String
- fechaCreacion: Long

### Ingreso
- id: Long (PK, autoGenerate)
- userId: Long (FK)
- nombre: String
- cantidad: Double
- periodicidad: String (Semanal/Quincenal/Mensual)

### GastoFijo
- id: Long (PK, autoGenerate)
- userId: Long (FK)
- nombre: String
- categoria: String
- cantidad: Double
- periodicidad: String

### GastoVariable
- id: Long (PK, autoGenerate)
- userId: Long (FK)
- nombre: String
- categoria: String
- cantidad: Double
- periodicidad: String

### Deuda
- id: Long (PK, autoGenerate)
- userId: Long (FK)
- nombre: String
- montoTotal: Double
- pagoPeriodico: Double
- periodicidad: String

### MetaAhorro
- id: Long (PK, autoGenerate)
- userId: Long (FK)
- nombre: String
- cantidadObjetivo: Double
- cantidadAhorrada: Double
- aporteMensual: Double

### BalanceFinanciero (modelo de dominio, no persistido)
- ingresosMensuales: Double
- gastosFijosMensuales: Double
- gastosVariablesMensuales: Double
- pagosDeudaMensuales: Double
- deudaTotal: Double
- dineroDisponible: Double
- porcentajeUtilizado: Double
- estadoFinanciero: String
---

## Seguridad

- Las contraseñas se almacenan mediante hash (SHA-256 con sal).
- Nunca se almacena la contraseña en texto plano.
- La identificación del usuario actual se almacena de forma segura (SharedPreferences encriptado).
- Todas las consultas financieras se filtran por userId.
- No se envía información a servidores externos.

---

## DAOs Previstos

- **UserDao:** insert, getAll, findByUsername, delete, updatePassword
- **IngresoDao:** insert, getAllByUser, delete, update
- **GastoFijoDao:** insert, getAllByUser, delete, update
- **GastoVariableDao:** insert, getAllByUser, delete, update
- **DeudaDao:** insert, getAllByUser, delete, update
- **MetaAhorroDao:** insert, getAllByUser, delete, update

Todas las consultas financieras filtran por userId.

---

## Repositorios Previstos

- **UserRepository:** registerUser, updateUser, authenticateUser, deleteUser, getCurrentUser
- **FinanzasRepository:** crud de ingresos, gastos fijos, gastos variables, deudas
- **MetasRepository:** crud de metas de ahorro

---

## Fases de Desarrollo

### FASE 0 - Análisis y Planificación
- Definir arquitectura
- Crear estructura de directorios
- Configurar Gradle
- Crear base mínima (MainActivity, AndroidManifest)

### FASE 1 - Room, Entidades, DAO y Repositories
- Crear entidades Room
- Crear DAOs
- Crear AppDatabase
- Crear repositories
- Probar aislamiento de usuarios

### FASE 2 - Registro, Login y Sesiones
- Pantalla de bienvenida
- Pantalla de registro
- Pantalla de login
- Hash de contraseñas
- Sesión persistente
- Cierre de sesión
- Eliminación de cuenta

### FASE 3 - CRUD Financiero
- Ingresos (CRUD)
- Gastos Fijos (CRUD)
- Gastos Variables (CRUD)
- Deudas (CRUD)
- Validaciones de entrada

### FASE 4 - Motor de Cálculos Financieros
- Normalización mensual
- Cálculo de balance
- Porcentaje utilizado
- Estado financiero
- Lógica centralizada

### FASE 5 - Inicio, Balance, Recomendaciones y Metas
- Pantalla de inicio
- Botón "Calcular Balance"
- Recomendaciones financieras
- Metas de ahorro
- Simulador de ahorro
- Botón "Nueva Evaluación"

### FASE 6 - Gráficas Dinámicas
- Gráfica de ingresos vs gastos
- Gráfica de distribución de gastos
- Gráfica de deudas
- Gráfica de ahorro
- Estados vacíos

### FASE 7 - Perfil, Configuración, Idiomas y Educación Financiera
- Pantalla de perfil
- Cambio de contraseña
- Configuración
- Internacionalización (Español, Inglés, K'iche')
- Educación financiera

### FASE 8 - Diseño Responsive, Pruebas y Auditoría
- Diseño adaptable (teléfonos y tablets)
- Pruebas integrales
- Auditoría final
