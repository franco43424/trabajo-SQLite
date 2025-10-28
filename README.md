🏥 PetCare Pro: Asistente de Gestión Veterinaria

¡Bienvenid@ al repositorio de PetCare Pro!
Una aplicación de gestión móvil desarrollada en Java y Android Studio, diseñada para simplificar el manejo de registros de clientes, mascotas y citas en clínicas veterinarias.

📌 ¿Qué es PetCare Pro?

PetCare Pro es un sistema de registros completo desarrollado para Android. Su objetivo principal es ofrecer una solución local y eficiente para la gestión de datos CRUD (Crear, Leer, Actualizar, Eliminar) de dueños de mascotas y su historial médico.

El proyecto está diseñado con un enfoque práctico y educativo, ideal para demostrar habilidades en el desarrollo nativo de Android y la persistencia de datos con SQLite.

🧰 Información Técnica Detallada

Lenguaje Principal: ☕ Java (versión 1.8+)

IDE de Desarrollo: 📱 Android Studio

Sistema de Build: 🧩 Gradle

Plugin Android Gradle (AGP): ⚙️ Versión moderna de 8.x

SDK Mínimo (minSdk): 📱 24 (Android 7.0 Nougat)

SDK Objetivo (targetSdk): 🎯 34 (Android 14)

Base de Datos: 💾 SQLite integrada y persistente

Diseño UI: 🎨 XML nativo y estilos personalizados

Arquitectura: 🧠 MVC (Model-View-Controller) simplificada

🛠️ Características Principales

✅ Gestión Completa de Clientes: Permite registrar, modificar y eliminar perfiles de dueños de mascotas (con campos como nombre, telefono).

🐶 Perfiles Detallados de Mascotas: Creación de registros de animales vinculados a un dueño (incluye nombre, edad, y raza_id). También permite modificar y eliminar perfiles de mascotas.

🗓️ Agenda de Citas: Un módulo completo para:

Agendar Citas: Programar servicios o consultas, especificando fecha, hora, motivo, y asociándolos a una Mascota específica.

Visualizar Citas: Revisar eventos programados.

Eliminar Citas: Cancelar o borrar un evento.

🔒 Persistencia Local: Todos los datos se almacenan de forma segura en una base de datos SQLite, directamente en el dispositivo.

🏗️ Estructura Modular: El código está organizado de manera limpia y escalable, facilitando futuras extensiones.

✨ Compatibilidad Amplia: Optimizado para la mayoría de los dispositivos Android modernos (API 24+).

🗄️ Estructura de la Base de Datos (SQLite)

El esquema relacional se basa en el archivo de contrato (PetContract.java) y organiza los datos en cuatro tablas principales:

duenos:

Propósito: Almacena la información de los propietarios/clientes.

Columnas Clave: _ID, nombre, telefono.

Relaciones: No tiene claves foráneas.

razas:

Propósito: Contiene un catálogo de las diferentes razas de mascotas.

Columnas Clave: _ID, nombre.

Relaciones: No tiene claves foráneas.

mascotas:

Propósito: Guarda los registros detallados de cada animal.

Columnas Clave: _ID, nombre, edad.

Relaciones: Incluye claves foráneas para dueno_id (vinculando a la tabla duenos) y raza_id (vinculando a la tabla razas).

citas:

Propósito: Gestiona la agenda de eventos y servicios programados.

Columnas Clave: _ID, fecha, hora, motivo.

Relaciones: Contiene una clave foránea pet_id que la vincula directamente con la mascota en la tabla mascotas.

🚀 Instalación y Ejecución Local

Sigue estos pasos para poner en marcha el proyecto en tu máquina:

Clonar el Repositorio: Abre tu terminal o línea de comandos y ejecuta:

git clone [URL_DEL_REPOSITORIO_AQUI]
cd PetCarePro


Abrir en Android Studio:

Inicia Android Studio.

Selecciona File > Open... en el menú.

Navega hasta la carpeta PetCarePro que acabas de clonar y haz clic en Open.

Sincronizar y Ejecutar:

Espera a que Android Studio sincronice el proyecto con Gradle (esto puede tardar unos minutos).

Una vez sincronizado, selecciona tu emulador o dispositivo físico (se recomienda un Target SDK 34).

Haz clic en el botón Run (el ícono de ▶️ verde) en la barra de herramientas para compilar e instalar la aplicación.
