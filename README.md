NEXORA ADVENTURES
Aplicación móvil nativa para la gestión de experiencias turísticas
Lema: Explora. Descubre. Vive.

NEXORA ADVENTURES es un proyecto académico desarrollado como aplicación móvil nativa para Android utilizando Kotlin y Android Studio. Su objetivo es presentar destinos turísticos, administrar reservas y trabajadores, mostrar ubicaciones en un mapa interactivo y permitir que los clientes registren consultas de contacto.
Estado del proyecto: las funciones descritas en este documento corresponden a la implementación trabajada y probada en el emulador Android. Consulta la sección Limitaciones y aspectos pendientes antes de considerar el proyecto como una solución de producción.

1. Funcionalidades
   Presentación y navegación
- Pantalla Splash con presentación inicial.
- Inicio de sesión.
- Pantalla de inicio de NEXORA ADVENTURES.
- Navigation Drawer para acceder a los módulos.
- Navegación entre pantallas mediante Activities y Fragments.
- Interfaz diseñada para orientación vertical.
  Destinos turísticos
- Catálogo de destinos implementado con RecyclerView.
- Fichas con información, descripción, precio referencial, duración, dificultad, actividades y punto de encuentro.
- Apertura de la ficha desde el catálogo y desde los marcadores del mapa.
  Gestión de reservas
- Registro, consulta, edición y eliminación de reservas.
- Estados de reserva: PENDIENTE, CONFIRMADO y FINALIZADO.
- Datos gestionados: identificador, nombre del cliente, teléfono, destino, fecha del viaje, cantidad de personas, estado, punto de encuentro, latitud y longitud.
- Persistencia local mediante Room.
- Precarga del destino y del punto de encuentro al iniciar una reserva desde una ficha turística.
- Transferencia y almacenamiento de coordenadas de referencia del destino.
  Gestión de trabajadores
- Registro, consulta, edición y eliminación de trabajadores.
- Gestión de datos del trabajador y salario.
- Persistencia local mediante Room.
  Mapa y ubicación
- Mapa vectorial mediante MapLibre Native y estilo Liberty de OpenFreeMap.
- Marcadores de los cuatro destinos del catálogo.
- Trazados referenciales entre puntos de encuentro y destinos.
- Marcadores para puntos de encuentro y puntos intermedios configurados.
- Solicitud de permisos de ubicación aproximada y precisa en tiempo de ejecución.
- Obtención de ubicación mediante Google Play Services Location.
- Visualización de la ubicación obtenida y centrado de la cámara.
- En el emulador se probó la ubicación simulada de Lima.
  Contactos
- Formulario con nombre, correo electrónico, teléfono, asunto y mensaje.
- Validaciones de los campos.
- Registro local de consultas en SQLite.
  Información institucional
- Sección Nosotros con presentación, quiénes somos, misión, visión, valores y servicios.
2. Tecnologías y arquitectura
   Área	Tecnología
   Lenguaje	Kotlin
   IDE	Android Studio
   Interfaz	XML y Material Components
   Navegación	Activities, Fragments y Navigation Drawer
   Listados	RecyclerView
   Persistencia de reservas y trabajadores	Room
   Persistencia de consultas de contacto	SQLite mediante SQLiteOpenHelper
   Cartografía	MapLibre Native Android
   Estilo cartográfico	OpenFreeMap Liberty
   Ubicación	Google Play Services Location (Fused Location Provider)
   Programación asíncrona	Kotlin Coroutines y Lifecycle


Organización principal del proyecto
La estructura puede variar ligeramente según los archivos auxiliares que se hayan agregado durante el desarrollo. Los paquetes principales son:
NEXORA-ADVENTURES/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/nexoraadventures/
│           │   ├── adapter/
│           │   ├── data/
│           │   │   ├── ContactDbHelper.kt
│           │   │   ├── ReservationDatabase.kt
│           │   │   ├── ReservationDao.kt
│           │   │   ├── ReservationRepository.kt
│           │   │   └── ...
│           │   ├── model/
│           │   ├── ContactsFragment.kt
│           │   ├── NosotrosFragment.kt
│           │   ├── DestinationsFragment.kt
│           │   ├── ReservationsFragment.kt
│           │   ├── WorkersFragment.kt
│           │   ├── MapFragment.kt
│           │   ├── DestinationDetailActivity.kt
│           │   ├── ReservationFormActivity.kt
│           │   ├── WorkerFormActivity.kt
│           │   ├── LoginActivity.kt
│           │   ├── SplashActivity.kt
│           │   └── MainActivity.kt
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   ├── menu/
│           │   └── values/
│           └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
Persistencia local
La aplicación utiliza dos almacenes locales independientes:
- nexora_adventures.db: base de datos Room utilizada para reservas y trabajadores.
- nexora_contacts.db: base de datos SQLite utilizada para las consultas del formulario de contactos.
  Los datos se almacenan en el dispositivo o emulador donde se ejecuta la aplicación. No se sincronizan automáticamente con un servidor remoto.
3. Requisitos
- Android Studio compatible con la versión de Android Gradle Plugin configurada en el proyecto.
- Android SDK 36 o compatible con la configuración del módulo.
- JDK requerido por la versión de Android Gradle Plugin instalada.
- Emulador Android o dispositivo físico compatible con minSdk 23.
- Conexión a Internet para descargar dependencias y cargar los mapas de OpenFreeMap.
- Servicios de Google Play disponibles para la funcionalidad de ubicación basada en Fused Location Provider.
  Configuración Android del proyecto
  Parámetro	Configuración del proyecto
  Namespace / applicationId	com.example.nexoraadventures
  compileSdk	36
  targetSdk	36
  minSdk	23


Las versiones concretas de las dependencias se encuentran en app/build.gradle.kts. No se deben agregar versiones duplicadas sin revisar primero ese archivo.
4. Instalación y apertura del proyecto
1. Clonar el repositorio o descargar el proyecto desde GitHub.
2. Descomprimir el proyecto si se descargó como ZIP.
3. Abrir Android Studio.
4. Seleccionar Open y elegir la carpeta raíz del proyecto, donde se encuentran settings.gradle.kts y build.gradle.kts.
5. Esperar a que Android Studio realice la sincronización de Gradle.
6. Si Android Studio solicita instalar componentes del SDK, instalar los componentes indicados por el proyecto.
7. Verificar que la sincronización finalice sin errores.
   No abrir únicamente la carpeta app: debe abrirse la raíz del proyecto Gradle.
5. Ejecución
1. Iniciar un emulador Android desde Device Manager o conectar un dispositivo físico.
2. En la barra superior de Android Studio, seleccionar la configuración app.
3. Seleccionar el dispositivo de destino.
4. Pulsar Run (botón verde).
5. Esperar la instalación y el inicio de NEXORA ADVENTURES.
   Para probar la ubicación en un emulador, utilizar Extended Controls → Location y establecer una ubicación simulada. La aplicación debe tener concedido el permiso correspondiente y los servicios de ubicación del emulador deben estar activos.
6. Credenciales de demostración
   La pantalla de inicio de sesión del proyecto utiliza credenciales de demostración configuradas en la aplicación:
   Usuario	Contraseña
   admin@nexora.com	123456


Estas credenciales son exclusivamente para pruebas académicas. No deben utilizarse como mecanismo de autenticación de producción.
7. Flujo de prueba
   Se recomienda ejecutar las pruebas en este orden:
   7.1. Inicio de sesión y navegación
1. Abrir la aplicación y verificar la pantalla Splash.
2. Iniciar sesión con las credenciales de demostración.
3. Comprobar que se muestra Inicio.
4. Abrir el Navigation Drawer y recorrer las opciones disponibles.
   7.2. Destinos turísticos
1. Abrir el catálogo de destinos.
2. Verificar que el listado se muestre mediante RecyclerView.
3. Seleccionar un destino.
4. Comprobar su fotografía e información.
5. Regresar al catálogo.
   7.3. Reservas
1. Abrir Gestión de reservas.
2. Registrar una reserva con datos válidos.
3. Comprobar que aparece en el listado.
4. Editar la reserva.
5. Cambiar su estado entre pendiente, confirmado y finalizado.
6. Eliminar una reserva de prueba.
7. Cerrar y volver a abrir la aplicación para comprobar la persistencia.
8. Para una reserva iniciada desde el detalle de un destino, verificar que se precarguen el destino y el punto de encuentro.
   7.4. Trabajadores
1. Abrir Gestión de trabajadores.
2. Registrar un trabajador con los datos solicitados por el formulario.
3. Comprobar que aparece en el listado y que se muestra su salario.
4. Editar sus datos o salario.
5. Eliminar un registro de prueba.
6. Reiniciar la aplicación y comprobar la persistencia.
   7.5. Contactos
1. Abrir Contactos.
2. Pulsar Enviar consulta con los campos vacíos y comprobar las validaciones.
3. Registrar una consulta con datos válidos.
4. Abrir App Inspection → Database Inspector.
5. Comprobar el registro en nexora_contacts.db, tabla contactos.
   7.6. Nosotros
1. Abrir Nosotros desde el Navigation Drawer.
2. Revisar quiénes somos, misión, visión, valores y servicios.
3. Desplazarse hasta el final de la pantalla.
   7.7. Mapa y ubicación
1. Abrir Mapa y ubicación.
2. Esperar la carga del estilo Liberty.
3. Comprobar los marcadores de los cuatro destinos.
4. Revisar los trazados referenciales y los marcadores de puntos de encuentro.
5. Pulsar el botón de ubicación.
6. Conceder el permiso de ubicación cuando Android lo solicite.
7. En el emulador, configurar una ubicación de prueba desde Extended Controls → Location.
8. Volver al mapa y solicitar la ubicación.
9. Comprobar el marcador y las coordenadas en Logcat usando el filtro NEXORA_MAPA.
8. Evidencias para la evaluación
   Se recomienda incluir en el documento PDF capturas de:
- Splash y Login.
- Inicio y Navigation Drawer.
- Catálogo y detalle de destinos.
- Listado y formulario de reservas.
- Gestión de trabajadores mostrando salarios.
- Formulario de Contactos y consulta registrada en Database Inspector.
- Sección Nosotros.
- Mapa con destinos y trazados referenciales.
- Diálogo de permiso de ubicación de Android.
- Mapa centrado en una ubicación simulada y Logcat mostrando latitud y longitud.
9. Limitaciones y aspectos pendientes
1. MapLibre en lugar de Google Maps: la implementación cartográfica actual utiliza MapLibre Native con el estilo Liberty de OpenFreeMap. La indicación académica menciona Google Maps expresamente. Se debe confirmar con el docente si acepta esta alternativa; si exige Google Maps, será necesario adaptar el módulo.
2. Trazados referenciales: las líneas entre puntos son esquemáticas y no constituyen rutas reales por carretera o sendero. No deben utilizarse para navegación ni para estimar tiempos o distancias.
3. Puntos de encuentro: las coordenadas incorporadas son referencias de desarrollo. Deben verificarse con la empresa o con fuentes cartográficas confiables antes de utilizarlas como ubicaciones operativas.
4. Consultas de contacto: se guardan localmente en nexora_contacts.db. No se envían por correo electrónico ni se transmiten a un backend.
5. Autenticación: las credenciales de demostración no sustituyen un sistema de autenticación seguro para producción.
6. Sincronización: reservas, trabajadores y consultas no se sincronizan entre dispositivos porque no se ha documentado un backend remoto.
10. Repositorio y colaboración
    El repositorio remoto debe contener el proyecto Android Studio completo y este archivo README.md en la raíz.

Se recomienda que cada integrante realice commits identificables y descriptivos, por ejemplo:
feat: implementar formulario de reservas
feat: agregar gestión de trabajadores
feat: integrar mapa y permisos de ubicación
feat: implementar formulario de contactos
feat: agregar presentación institucional
docs: crear README del proyecto
Antes de publicar el repositorio:
- No incluir contraseñas personales, claves privadas ni archivos locales innecesarios.
- Revisar .gitignore para excluir local.properties, carpetas de compilación y archivos de configuración privados.
- Verificar que el proyecto se pueda clonar, sincronizar y ejecutar desde otra computadora.
11. Integrantes del proyecto y autoría
    Proyecto académico: NEXORA ADVENTURES
    Plataforma: Android nativo
    Lenguaje: Kotlin
    Entorno de desarrollo: Android Studio  
    Integrantes
    N.º	Apellidos y nombres
    1	Siri Vergara, Maria Alejandra
    2	Villasante Contreras, Jean Paul
    3	Santisteban Manrique, Adrian Daniel


Datos académicos
- Curso: Desarrollo de Aplicaciones Móviles 2.
- Ciclo: VII.
- Docente: Pallin Tello, Luis Miguel.
  Cada integrante debe registrar sus contribuciones mediante commits realizados desde su propia cuenta de GitHub, de acuerdo con las indicaciones del curso.