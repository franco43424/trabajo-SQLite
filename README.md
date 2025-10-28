🐶 PetCare Pro: Aplicación de Bienestar Nativa para Android

📝 Descripción del Proyecto y Funcionalidad

PetCare Pro es una aplicación móvil nativa desarrollada para la plataforma Android. Su principal propósito es servir como un sistema de gestión de registros para una clínica veterinaria o un asistente personal de dueños de mascotas. La aplicación permite registrar la información de los dueños, sus mascotas y gestionar su historial de citas y salud.

La aplicación está optimizada para la versión de Android (API 36.0 - Medium Phone) y utiliza una base de datos local SQLite para garantizar la persistencia de los datos en el dispositivo.

✨ Flujos de Trabajo y Características Clave

La aplicación permite la gestión completa de los perfiles y el agendamiento:

1. Gestión de Perfiles (CRUD)

Módulo

Funcionalidades

Dueños

✅ Crear: Registro de un nuevo propietario (nombre, teléfono, foto).



✅ Modificar/Medicar: (Se entiende como Modificar) Actualizar la información del dueño.



✅ Eliminar: Borrar el perfil del dueño, lo que debería manejar las referencias a sus mascotas.

Mascotas

✅ Crear: Asociar una nueva mascota a un dueño existente (nombre, edad, raza, foto).



✅ Modificar/Medicar: (Se entiende como Modificar) Actualizar los datos de la mascota.



✅ Eliminar: Borrar el perfil de la mascota.

2. Gestión de Citas (Agenda)

Agendar Cita: Programar una nueva cita, asociándola directamente a una Mascota específica, con detalles de fecha, hora y motivo.

Visualizar Citas: Revisar las citas pendientes o históricas asociadas a cada mascota.

Eliminar Cita: Cancelar o borrar un registro de cita.

🛠️ Tecnologías Utilizadas

Este proyecto es una aplicación nativa de Android y se basa en el siguiente stack tecnológico:

Componente

Tecnología

Versión / Tipo

Propósito

Plataforma

Android Studio

IDE de desarrollo.

Entorno principal de desarrollo.

Lenguaje

Java

1.8+

Lógica de negocio y desarrollo de la UI.

Base de Datos

SQLite

Base de datos relacional.

Almacenamiento persistente, local y seguro de la información.

Target SDK

Android 14 (API 36.0)

Optimización de la aplicación.

Garantizar la compatibilidad con dispositivos modernos.

🗄️ Arquitectura y Esquema de la Base de Datos Local (SQLite)

La persistencia de los datos se gestiona mediante SQLite, siguiendo un patrón modular y organizado.

1. Clases de Arquitectura de Datos

Las clases en Java utilizadas para gestionar la base de datos son:

Clase

Propósito

PetContract

Contrato de Esquema: Define constantes para los nombres de las tablas y las columnas.

DBHelper

Asistente de Base de Datos: Gestiona la creación de la base de datos (onCreate) y su actualización (onUpgrade).

PetDatabase

Capa de Abstracción/DAO: (Data Access Object) Contiene la lógica para las operaciones CRUD específicas.

PetCursorAdapter

Adaptador de UI: Enlaza los resultados de las consultas (Cursor) con los componentes de la interfaz de usuario.

2. Esquema de Tablas (PetContract Definido)

La base de datos tiene una estructura relacional con las siguientes cuatro tablas:

Tabla

Propósito

Columnas Clave

Relaciones (FK)

duenos (OwnerEntry)

Información de los propietarios de las mascotas.

_ID, nombre, telefono, photo_uri

N/A

razas (RazaEntry)

Catálogo de razas disponibles.

_ID, nombre

N/A

mascotas (MascotasEntry)

Perfiles completos de los animales.

_ID, nombre, edad, photo_uri

dueno_id (Dueños), raza_id (Razas)

citas (CitasEntry)

Agenda de eventos veterinarios o de servicio.

_ID, fecha, hora, motivo

pet_id (Mascotas)

🚀 Instalación y Ejecución

Para clonar y ejecutar la aplicación en tu entorno local:

Clonar el Repositorio:

git clone [URL_DEL_REPOSITORIO]
cd PetCarePro


Abrir en Android Studio:

Inicia Android Studio.

Selecciona File > Open... y navega hasta el directorio PetCarePro.

Configurar Dispositivo/Emulador:

Asegúrate de tener un Emulador configurado con la API 36.0 o superior, o conecta un dispositivo físico compatible.

Ejecutar la Aplicación:

Haz clic en el botón Run (triángulo verde) en la barra de herramientas de Android Studio. El IDE compilará el código en Java y desplegará la APK en el dispositivo seleccionado.
