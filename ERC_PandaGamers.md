# ESPECIFICACIÓN DE REQUERIMIENTOS DEL CLIENTE (ERC)

## Proyecto: PandaGamers — Plataforma de Comercio Electrónico de Productos Gaming

**Versión:** 1.0  
**Fecha:** 4 de diciembre de 2025  
**Preparado por:** Equipo de Desarrollo  
**Estado:** Aprobado

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Documento

Este documento establece de manera formal los requerimientos funcionales y no funcionales solicitados por el cliente para el desarrollo de **PandaGamers**, una plataforma de comercio electrónico especializada en la venta de productos gaming. El presente documento sirve como acuerdo entre el cliente y el equipo de desarrollo, y establece las bases para el diseño, implementación y validación del sistema.

### 1.2 Alcance del Sistema

PandaGamers es una aplicación web que permite a usuarios comprar productos gaming (consolas, accesorios, mouses, sillas, poleras, juegos de mesa, entre otros) con autenticación segura, carrito de compras, historial de órdenes, y un panel administrativo para la gestión de inventario, ofertas y usuarios.

**Incluye:**
- Autenticación segura con JWT y roles de usuario
- Gestión de usuarios (registro, login, perfiles)
- Catálogo de productos con búsqueda y filtrado
- Carrito de compras y órdenes
- Integración con pasarela de pagos (Transbank simulado)
- Panel administrativo para CRUD de productos, ofertas y usuarios
- Historial de compras por usuario

**Excluye:**
- Soporte a múltiples idiomas en esta versión inicial
- Sistema de reseñas de productos
- Notificaciones por correo electrónico (planificado para versiones futuras)
- Sistema de devoluciones
- Integración con redes sociales

### 1.3 Definiciones, Siglas y Abreviaturas

| Término | Definición |
|---------|-----------|
| **JWT** | JSON Web Token — estándar para autenticación y autorización |
| **CRUD** | Create, Read, Update, Delete — operaciones básicas de datos |
| **USER** | Rol de usuario regular con permisos de compra |
| **ADMIN** | Rol de administrador con permisos de gestión completa |
| **Stock** | Cantidad de unidades disponibles de un producto |
| **Oferta/Promoción** | Descuento aplicado a uno o varios productos |
| **Orden de Compra** | Documento que registra una transacción de compra |
| **Transbank** | Pasarela de pagos simulada (no integración real) |
| **MongoDB** | Base de datos NoSQL utilizada por el backend |
| **Spring Boot** | Framework Java para el desarrollo del backend |
| **React** | Biblioteca JavaScript para el desarrollo del frontend |
| **API REST** | Interfaz de comunicación entre frontend y backend |
| **CORS** | Cross-Origin Resource Sharing — política de seguridad |

### 1.4 Referencias

- IEEE 830-1993: Recomendaciones para especificación de requerimientos de software
- Estándar REST para diseño de APIs
- Documentación de Spring Security para autenticación JWT
- Documentación de MongoDB para modelado de datos
- Principios de diseño UX/UI para plataformas de comercio electrónico

---

## 2. DESCRIPCIÓN GENERAL DEL SISTEMA

### 2.1 Perspectiva del Producto

PandaGamers es una aplicación web moderna de comercio electrónico que funciona en una arquitectura de tres capas:

**Capa de Presentación (Frontend)**
- Interfaz de usuario interactiva desarrollada en React
- Accesible desde navegadores web en dispositivos de escritorio y móviles
- Responsiva y orientada al usuario final

**Capa de Aplicación (Backend)**
- Servidor REST desarrollado en Spring Boot con Java 21
- Implementa lógica de negocio, validaciones y procesamiento de datos
- Gestiona autenticación, autorización y transacciones

**Capa de Datos**
- Base de datos MongoDB NoSQL
- Almacena usuarios, productos, ofertas, órdenes y transacciones
- Garantiza persistencia y disponibilidad de datos

La comunicación entre capas se realiza mediante APIs REST bajo protocolo HTTP/HTTPS con tokens JWT para autenticación.

### 2.2 Funcionalidades del Sistema (Resumen)

| Módulo | Funcionalidades Principales |
|--------|---------------------------|
| **Autenticación** | Login, registro, generación de JWT, gestión de sesiones |
| **Gestión de Usuarios** | Crear perfil, actualizar datos, visualizar perfil, eliminar cuenta (admin) |
| **Catálogo de Productos** | Listar productos, ver detalles, buscar, filtrar por categoría |
| **Administración de Productos** | CRUD completo, gestión de stock, importación masiva de productos |
| **Ofertas/Promociones** | CRUD de ofertas, aplicación de descuentos a productos, listado público y admin |
| **Carrito y Órdenes** | Agregar/eliminar items, crear orden, ver historial, actualizar estado |
| **Pagos** | Crear transacción de pago (Transbank simulado), confirmar pago, actualizar estado de orden |
| **Panel Admin** | Gestión de usuarios, productos, ofertas, órdenes, reportes básicos |

### 2.3 Usuarios del Sistema

#### 2.3.1 Usuario Regular (Role: USER)

**Perfil:**
- Persona que compra productos gaming en la plataforma
- Puede registrarse, acceder a su perfil y realizar compras

**Permisos:**
- Ver catálogo de productos (lectura)
- Crear y gestionar órdenes de compra
- Ver su historial de órdenes
- Actualizar su perfil personal
- Acceder a ofertas públicas

**No puede:**
- Crear, modificar o eliminar productos
- Crear o gestionar ofertas
- Ver órdenes de otros usuarios
- Acceder al panel administrativo

#### 2.3.2 Administrador (Role: ADMIN)

**Perfil:**
- Personal del equipo de PandaGamers responsable de la gestión y operación del sistema
- Tiene control total sobre la plataforma

**Permisos:**
- CRUD completo de productos (crear, leer, actualizar, eliminar)
- Gestión de stock (actualizar cantidades disponibles)
- Importación masiva de productos
- CRUD completo de ofertas/promociones
- Ver todas las órdenes del sistema
- Eliminar órdenes
- Ver y gestionar todos los usuarios
- Crear usuarios manualmente
- Acceso al panel administrativo completo

### 2.4 Restricciones del Sistema

**Restricciones Técnicas:**
- Disponibilidad requerida: 99% durante horario de operación (lunes a viernes, 8:00 a 22:00)
- Tiempo de respuesta máximo: 2 segundos para operaciones de lectura
- Capacidad de almacenamiento inicial: 100 GB MongoDB
- Usuarios concurrentes soportados: 500 simultáneos en versión inicial

**Restricciones de Negocio:**
- Solo se permiten dos roles: USER y ADMIN
- Los descuentos de ofertas no pueden superar el 100% del precio base
- El stock no puede ser negativo
- Las órdenes no pueden ser modificadas después de ser completadas

**Restricciones de Seguridad:**
- Todas las transacciones sensibles requieren autenticación JWT
- Las contraseñas deben tener mínimo 6 caracteres
- El backend solo acepta peticiones desde el frontend (CORS configurado)
- Los datos de pago se simulan (no se procesan datos reales de tarjeta)

### 2.5 Suposiciones

- El cliente tiene acceso a internet de banda ancha (mínimo 1 Mbps)
- Los usuarios utilizan navegadores modernos (Chrome, Firefox, Safari, Edge versión reciente)
- La integración con Transbank en esta fase es simulada, sin procesar pagos reales
- Se asume que los administradores conocen operaciones básicas de gestión de catálogos
- Los datos se respaldan regularmente en la infraestructura del cliente
- La plataforma operará en un servidor con acceso a MongoDB

---

## 3. OBJETIVOS DEL SISTEMA

### 3.1 Objetivos Generales

1. **Facilitar la compra en línea** de productos gaming de manera segura, intuitiva y confiable
2. **Incrementar las ventas** de PandaGamers mediante una plataforma digital moderna
3. **Mejorar la gestión de inventario** con herramientas administrativas eficientes
4. **Garantizar la seguridad** de datos de usuarios y transacciones mediante autenticación y autorización
5. **Optimizar la experiencia del usuario** con interfaz responsiva y procesos de compra simples

### 3.2 Objetivos Específicos

| Objetivo | Descripción | Indicador de Éxito |
|----------|-------------|------------------|
| Autenticación segura | Implementar JWT con roles diferenciados | 100% de usuarios autenticados con token válido |
| Catálogo funcional | Mostrar mínimo 50 productos con búsqueda | Búsqueda responde en < 1 segundo |
| Gestión de órdenes | Registrar y procesar órdenes correctamente | 99.9% de órdenes registradas sin errores |
| Administración | Permitir CRUD de productos y ofertas | Admin gestiona 10+ productos/día sin problemas |
| Integración de pagos | Simular transacciones Transbank | 100% de órdenes pueden ser pagadas en sandbox |

---

## 4. USUARIOS DEL SISTEMA

### 4.1 Matriz de Usuarios y Permisos

| Operación | Usuario Regular | Administrador |
|-----------|-----------------|---------------|
| **Ver Productos** | ✓ | ✓ |
| **Crear Producto** | ✗ | ✓ |
| **Actualizar Producto** | ✗ | ✓ |
| **Eliminar Producto** | ✗ | ✓ |
| **Importar Productos** | ✗ | ✓ |
| **Actualizar Stock** | ✗ | ✓ |
| **Crear Oferta** | ✗ | ✓ |
| **Actualizar Oferta** | ✗ | ✓ |
| **Eliminar Oferta** | ✗ | ✓ |
| **Ver Ofertas Públicas** | ✓ | ✓ |
| **Ver Todas las Órdenes** | ✗ | ✓ |
| **Ver Sus Órdenes** | ✓ | ✗ |
| **Crear Orden** | ✓ | ✓ |
| **Eliminar Orden** | ✗ | ✓ |
| **Ver Perfil** | ✓ | ✓ |
| **Actualizar Perfil** | ✓ | ✓ |
| **Ver Todos los Usuarios** | ✗ | ✓ |
| **Crear Usuario** | ✗ | ✓ |
| **Eliminar Usuario** | ✗ | ✓ |

---

## 5. REQUERIMIENTOS FUNCIONALES (RF)

### 5.1 Módulo de Autenticación

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-01** | El sistema debe permitir a un usuario registrarse con email, nombre y contraseña | email, nombre, password | Usuario creado, mensaje de confirmación |
| **RF-02** | El sistema debe validar que el email sea único en el registro | email duplicado | Error: Email ya existe |
| **RF-03** | El sistema debe generar un token JWT al realizar login exitoso | email, password válidos | JWT token con duración de sesión |
| **RF-04** | El sistema debe rechazar login con credenciales incorrectas | email/password inválidos | Error 401: Credenciales inválidas |
| **RF-05** | El sistema debe permitir crear admin con código de administrador | email, password, adminCode | Usuario ADMIN creado |
| **RF-06** | El sistema debe validar token JWT en cada petición protegida | HTTP header Authorization | Token válido o 401 Unauthorized |

### 5.2 Módulo de Usuarios

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-07** | El usuario debe poder ver su perfil personal | token JWT | nombre, email, rol, fecha de creación |
| **RF-08** | El usuario debe poder actualizar su información de perfil | nombre, email, datos personales | Perfil actualizado |
| **RF-09** | Admin debe poder listar todos los usuarios del sistema | token ADMIN | Lista de usuarios con detalles |
| **RF-10** | Admin debe poder crear manualmente un nuevo usuario | email, password, rol | Usuario creado |
| **RF-11** | Admin debe poder eliminar un usuario del sistema | user_id | Usuario eliminado, órdenes huérfanas |

### 5.3 Módulo de Productos

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-12** | El sistema debe listar todos los productos disponibles | sin parámetros | Lista de productos con detalles (nombre, precio, stock, descripción, categoría) |
| **RF-13** | El usuario debe poder filtrar productos por categoría | category | Productos filtrados por categoría |
| **RF-14** | El usuario debe poder buscar productos por nombre | search_term | Productos cuyo nombre contenga el término |
| **RF-15** | El usuario debe poder ver detalles completos de un producto | product_id | Nombre, descripción, precio, stock, categoría, imágenes |
| **RF-16** | Admin debe poder crear un nuevo producto | nombre, descripción, precio, stock, categoría | Producto creado, asignado ID único |
| **RF-17** | Admin debe poder actualizar datos de un producto existente | product_id, campos a actualizar | Producto actualizado |
| **RF-18** | Admin debe poder eliminar un producto del catálogo | product_id | Producto eliminado, stock a 0 |
| **RF-19** | Admin debe poder actualizar el stock de un producto | product_id, nueva_cantidad | Stock actualizado |
| **RF-20** | Admin debe poder importar múltiples productos en lote | archivo/lista de productos | Productos importados, duplicados actualizados por nombre |
| **RF-21** | El sistema debe validar que el stock no sea negativo | operación que intente stock negativo | Error: Stock no puede ser negativo |

### 5.4 Módulo de Ofertas

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-22** | Admin debe poder crear una nueva oferta/promoción | product_id, descuento_porcentaje, descripción, vigencia | Oferta creada |
| **RF-23** | Admin debe poder actualizar los detalles de una oferta | offer_id, campos actualizables | Oferta actualizada |
| **RF-24** | Admin debe poder eliminar una oferta | offer_id | Oferta eliminada, producto vuelve a precio normal |
| **RF-25** | Admin debe poder listar todas las ofertas (vista privada) | token ADMIN | Lista completa de ofertas con detalles |
| **RF-26** | El usuario debe poder ver ofertas públicas/activas | sin parámetros | Lista de ofertas vigentes con descuento aplicado |
| **RF-27** | El sistema debe aplicar automáticamente el descuento de oferta al mostrar productos | product_id con oferta | Precio mostrado = Precio base - (Precio base × descuento%) |
| **RF-28** | El sistema debe validar que el descuento no supere 100% | descuento_porcentaje | Error si descuento >= 100 |

### 5.5 Módulo de Órdenes de Compra

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-29** | El usuario debe poder crear una orden de compra con items del carrito | lista_items (producto_id, cantidad), dirección_envío | Orden creada, asignado order_id único |
| **RF-30** | El sistema debe validar disponibilidad de stock antes de crear orden | item.cantidad vs product.stock | Error si stock insuficiente |
| **RF-31** | El sistema debe calcular subtotal, IVA y total de la orden | items de orden | subtotal, IVA (19%), total = subtotal + IVA |
| **RF-32** | El sistema debe aplicar costos de envío según opción seleccionada | delivery_option (standard, express, retiro) | costo_envío sumado al total |
| **RF-33** | El usuario debe poder ver el historial de sus órdenes | token JWT user | Lista de órdenes del usuario con estado y fecha |
| **RF-34** | El usuario debe poder ver detalles de una orden específica | order_id | order_id, items, subtotal, IVA, total, estado, fecha |
| **RF-35** | Admin debe poder ver todas las órdenes del sistema | token ADMIN | Lista de todas las órdenes con usuario asociado |
| **RF-36** | Admin debe poder eliminar una orden del sistema | order_id | Orden eliminada, stock restaurado |
| **RF-37** | El sistema debe actualizar el estado de la orden | order_id, nuevo_estado | Estado cambio a Pendiente/Pagada/Completada/Fallida |
| **RF-38** | El usuario debe poder marcar orden como completada después del pago | order_id | Estado cambia a "Completada" |
| **RF-39** | El sistema debe decrementar stock al completarse una orden | order_id confirmada | stock_producto -= cantidad_ordenada |

### 5.6 Módulo de Pagos (Transbank Simulado)

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-40** | El usuario debe poder iniciar un pago desde una orden | order_id, monto, URL retorno | Redirección a formulario pago (sandbox), token de transacción |
| **RF-41** | El sistema debe generar un token único por transacción | transacción | Token válido durante 30 minutos |
| **RF-42** | El usuario debe poder confirmar el resultado del pago | token de transacción | Estado pago (aprobado, rechazado, cancelado) |
| **RF-43** | El sistema debe actualizar el estado de la orden tras confirmación de pago | pago confirmado | Si aprobado: orden.status = "Pagada", si rechazado: "Fallida" |
| **RF-44** | El sistema debe registrar el resultado de cada transacción | detalles transacción | Log de transacción guardado |

### 5.7 Módulo de Carro de Compras

| ID | Descripción | Entrada | Salida |
|----|-----------:|---------|--------|
| **RF-45** | El usuario debe poder agregar un producto al carro | product_id, cantidad | Item agregado al carro |
| **RF-46** | El usuario debe poder eliminar un item del carro | item_id | Item eliminado del carro |
| **RF-47** | El usuario debe poder ver el carro actual | sin parámetros | Lista items carro, subtotal |
| **RF-48** | El usuario debe poder vaciar el carro | sin parámetros | Carro vacío |
| **RF-49** | El sistema debe calcular total del carro incluyendo ofertas | items en carro | Total con descuentos aplicados |

---

## 6. REQUERIMIENTOS NO FUNCIONALES (RNF)

### 6.1 Seguridad

| ID | Descripción | Criterio de Aceptación |
|----|-------------|----------------------|
| **RNF-01** | Autenticación mediante JWT | Todos los endpoints sensibles requieren token válido |
| **RNF-02** | Autorización basada en roles | URLs /admin/** solo accesibles con rol ADMIN |
| **RNF-03** | Contraseñas hasheadas | Nunca almacenar contraseñas en texto plano |
| **RNF-04** | CORS configurado | Solo localhost:3000 puede consumir API |
| **RNF-05** | HTTPS recomendado | Configurar SSL/TLS en producción |
| **RNF-06** | Validación de entrada | Todos los campos validados (tipo, longitud, formato) |
| **RNF-07** | Protección CSRF | Tokens CSRF en formularios sensibles |

### 6.2 Rendimiento

| ID | Descripción | Criterio de Aceptación |
|----|-------------|----------------------|
| **RNF-08** | Tiempo de respuesta | Máximo 2 segundos para GET /products |
| **RNF-09** | Tiempo de respuesta crear orden | Máximo 3 segundos para POST /orders |
| **RNF-10** | Caché de productos | Productos cacheados 5 minutos en cliente |
| **RNF-11** | Índices de BD | Índices en email, product_id, order_id en MongoDB |
| **RNF-12** | Compresión de respuestas | Habilitar gzip en respuestas > 1KB |
| **RNF-13** | Lazy loading imágenes | Imágenes cargadas bajo demanda |

### 6.3 Disponibilidad

| ID | Descripción | Criterio de Aceptación |
|----|-------------|----------------------|
| **RNF-14** | Uptime | 99% de disponibilidad horario de operación |
| **RNF-15** | Recuperación de fallos | Sistema recuperable en < 15 minutos |
| **RNF-16** | Respaldo de datos | Backup diario de MongoDB |
| **RNF-17** | Redundancia BD | BD configurada con réplica |

### 6.4 Usabilidad

| ID | Descripción | Criterio de Aceptación |
|----|-------------|----------------------|
| **RNF-18** | Interfaz responsiva | Funciona en dispositivos ≥ 320px ancho |
| **RNF-19** | Navegación intuitiva | Menú principal accesible en < 2 clics |
| **RNF-20** | Mensajes de error claros | Errores en español, descriptivos |
| **RNF-21** | Accesibilidad | Cumplir WCAG 2.1 nivel AA |
| **RNF-22** | Documentación | Manual de usuario disponible |

### 6.5 Compatibilidad

| ID | Descripción | Criterio de Aceptación |
|----|-------------|----------------------|
| **RNF-23** | Navegadores soportados | Chrome 90+, Firefox 88+, Safari 14+, Edge 90+ |
| **RNF-24** | Sistemas operativos | Windows 10+, macOS 10.14+, Linux Ubuntu 20.04+ |
| **RNF-25** | Dispositivos móviles | iOS 12+, Android 8+ |
| **RNF-26** | Versión Java | Java 21 LTS |
| **RNF-27** | Versión Node.js | Node.js 16+ |

### 6.6 Confiabilidad

| ID | Descripción | Criterio de Aceptación |
|----|-------------|----------------------|
| **RNF-28** | Integridad de datos | Transacciones ACID en órdenes |
| **RNF-29** | Consistencia de stock | Stock nunca negativo bajo concurrencia |
| **RNF-30** | Auditoría | Log de cambios en productos y órdenes |
| **RNF-31** | Recuperación ante caída | Estado de sesión restaurado tras desconexión |

---

## 7. REGLAS DE NEGOCIO

### 7.1 Gestión de Usuarios y Autenticación

| RN | Descripción |
|----|-------------|
| **RN-01** | Un usuario debe tener un email único en el sistema |
| **RN-02** | Solo usuarios con rol ADMIN pueden gestionar productos y ofertas |
| **RN-03** | El código de administrador es necesario para crear cuentas ADMIN |
| **RN-04** | Las sesiones expiran tras 24 horas de inactividad |
| **RN-05** | Un usuario no puede cambiar su rol después de creado (sin intervención admin) |

### 7.2 Gestión de Productos

| RN | Descripción |
|----|-------------|
| **RN-06** | Todo producto debe tener un nombre, precio y categoría |
| **RN-07** | El stock inicial de un producto no puede ser negativo |
| **RN-08** | El precio de un producto no puede ser negativo ni cero |
| **RN-09** | Cuando se importan productos, se actualizan si el nombre coincide |
| **RN-10** | Un producto con stock 0 se muestra como "agotado" pero no se oculta del catálogo |

### 7.3 Gestión de Ofertas

| RN | Descripción |
|----|-------------|
| **RN-11** | Un producto solo puede tener una oferta activa simultáneamente |
| **RN-12** | El descuento debe estar entre 1% y 99% |
| **RN-13** | Una oferta se muestra automáticamente en el catálogo si está vigente |
| **RN-14** | Al eliminar una oferta, el producto vuelve a su precio original |

### 7.4 Gestión de Órdenes

| RN | Descripción |
|----|-------------|
| **RN-15** | Una orden no puede contener items con stock 0 en el momento de crear |
| **RN-16** | El stock se reserva cuando se crea la orden (no se decrementa hasta confirmación de pago) |
| **RN-17** | Una orden completada no puede ser modificada (solo eliminada por admin) |
| **RN-18** | El IVA se calcula como 19% del subtotal |
| **RN-19** | El costo de envío varía según la opción: Standard 5k, Express 12k, Retiro 0 |
| **RN-20** | Si un pago falla, la orden vuelve a estado "Fallida" y el stock se libera |

### 7.5 Gestión de Pagos

| RN | Descripción |
|----|-------------|
| **RN-21** | Los pagos se procesan en ambiente sandbox (simulación Transbank) |
| **RN-22** | Un token de pago expira tras 30 minutos de generado |
| **RN-23** | No se almacenan datos reales de tarjetas de crédito |
| **RN-24** | Cada transacción genera un registro auditable |

---

## 8. RESTRICCIONES TÉCNICAS

### 8.1 Restricciones de Plataforma

| Restricción | Descripción |
|-------------|-------------|
| **Lenguaje Backend** | Java 21 LTS con Spring Boot 3.5.8 |
| **Framework Web** | Spring Web, Spring Security, Spring Data MongoDB |
| **Base de Datos** | MongoDB (NoSQL) |
| **Lenguaje Frontend** | JavaScript con biblioteca React |
| **Gestor de Dependencias Frontend** | npm o yarn |
| **Gestor de Dependencias Backend** | Gradle (wrapper incluido) |
| **Servidor de Aplicaciones** | Tomcat (integrado en Spring Boot) |

### 8.2 Restricciones de Integración

| Restricción | Descripción |
|-------------|-------------|
| **Comunicación** | API REST sobre HTTP/HTTPS |
| **Formato de Datos** | JSON |
| **Autenticación** | JWT (JSON Web Token) |
| **CORS** | Configurado para localhost:3000 |
| **Puerto Backend** | 8080 (por defecto) |
| **Puerto Frontend** | 3000 (por defecto) |

### 8.3 Restricciones de Despliegue

| Restricción | Descripción |
|-------------|-------------|
| **Infraestructura** | Servidor con acceso a MongoDB |
| **Versión Base Datos** | MongoDB 4.0+ |
| **Memoria Mínima** | 2 GB para backend, 1 GB para frontend |
| **Almacenamiento** | 100 GB inicial para MongoDB |
| **Conexión de Red** | Mínimo 1 Mbps |

---

## 9. CASOS DE USO

### 9.1 Listado de Casos de Uso Principales

| ID | Caso de Uso | Actor Primario | Descripción Breve |
|----|-------------|-----------------|------------------|
| **CU-01** | Registrar nuevo usuario | Usuario | El usuario se registra con email, nombre y contraseña |
| **CU-02** | Iniciar sesión (Login) | Usuario | El usuario se autentica y recibe token JWT |
| **CU-03** | Ver catálogo de productos | Usuario | El usuario visualiza todos los productos disponibles |
| **CU-04** | Buscar/Filtrar productos | Usuario | El usuario busca productos por nombre o filtra por categoría |
| **CU-05** | Ver detalles de producto | Usuario | El usuario visualiza información completa de un producto |
| **CU-06** | Agregar item al carro | Usuario | El usuario agrega un producto al carro de compras |
| **CU-07** | Ver carro de compras | Usuario | El usuario visualiza items y total del carro |
| **CU-08** | Crear orden de compra | Usuario | El usuario crea una orden a partir de items del carro |
| **CU-09** | Procesar pago (Transbank) | Usuario | El usuario realiza el pago de una orden |
| **CU-10** | Ver historial de órdenes | Usuario | El usuario consulta sus compras anteriores |
| **CU-11** | Crear producto | Administrador | Admin ingresa nuevo producto al catálogo |
| **CU-12** | Actualizar producto | Administrador | Admin modifica datos de un producto existente |
| **CU-13** | Eliminar producto | Administrador | Admin retira un producto del catálogo |
| **CU-14** | Importar productos en lote | Administrador | Admin carga múltiples productos simultáneamente |
| **CU-15** | Crear oferta | Administrador | Admin establece promoción en un producto |
| **CU-16** | Ver todas las órdenes | Administrador | Admin consulta órdenes de todos los usuarios |
| **CU-17** | Gestionar usuarios | Administrador | Admin crear, ver o eliminar usuarios |

### 9.2 Especificación Detallada de Casos de Uso Críticos

#### Caso de Uso 02: Iniciar Sesión (Login)

**Actor:** Usuario  
**Precondiciones:** Usuario registrado en el sistema  
**Flujo Principal:**

1. El usuario accede a la página de login
2. Sistema presenta formulario con campos: email y contraseña
3. Usuario ingresa sus credenciales
4. Usuario hace clic en botón "Ingresar"
5. Sistema valida email formato correcto
6. Sistema verifica credenciales en base de datos
7. Si son correctas: Sistema genera token JWT
8. Sistema retorna token y datos del usuario
9. Frontend almacena token en localStorage
10. Sistema redirige a página principal

**Flujos Alternativos:**

| Paso | Condición | Acción |
|-----|-----------|--------|
| 6 | Email no existe | Sistema muestra: "Usuario no encontrado" |
| 6 | Contraseña incorrecta | Sistema muestra: "Credenciales inválidas" |
| 6 | Cuenta desactivada | Sistema muestra: "Cuenta desactivada" |

**Postcondiciones:** Usuario autenticado, sesión activa con JWT válido

---

#### Caso de Uso 08: Crear Orden de Compra

**Actor:** Usuario  
**Precondiciones:** Usuario autenticado, carro con items, stock disponible  
**Flujo Principal:**

1. Usuario visualiza carro con items
2. Usuario ingresa dirección de envío
3. Usuario selecciona opción de envío (Standard/Express/Retiro)
4. Usuario revisa resumen: items, subtotal, IVA, total
5. Usuario hace clic en "Crear Orden"
6. Sistema valida stock disponible para cada item
7. Sistema calcula subtotal, IVA (19%), costo envío, total
8. Sistema crea registro de orden en BD
9. Sistema genera order_id único
10. Sistema decrementa stock (si pago confirmado)
11. Sistema retorna order_id y enlace a pago
12. Sistema vacía carro del usuario

**Validaciones:**
- Stock debe ser >= cantidad solicitada
- Dirección no puede estar vacía
- Al menos 1 item en carro

**Postcondiciones:** Orden creada, carro vacío, listo para procesar pago

---

#### Caso de Uso 15: Crear Oferta

**Actor:** Administrador  
**Precondiciones:** Admin autenticado, producto existe  
**Flujo Principal:**

1. Admin accede a panel de ofertas
2. Admin hace clic en "Crear Nueva Oferta"
3. Sistema presenta formulario
4. Admin selecciona producto (dropdown)
5. Admin ingresa descuento % (1-99)
6. Admin ingresa descripción de oferta
7. Admin selecciona fecha de vigencia
8. Admin hace clic en "Crear"
9. Sistema valida datos
10. Sistema crea oferta en BD
11. Sistema muestra confirmación: "Oferta creada"
12. Oferta aparece automáticamente en catálogo

**Validaciones:**
- Descuento entre 1 y 99%
- Producto no puede tener oferta activa

**Postcondiciones:** Oferta activa, descuento aplicado en catálogo

---

### 9.3 Diagrama de Casos de Uso (ASCII Art)

```
┌─────────────────────────────────────────────────────────┐
│                  SISTEMA PANDAGAMERS                    │
└─────────────────────────────────────────────────────────┘

                        ┌──────────────┐
                        │   Usuario    │
                        └──────────────┘
                              │
           ┌──────────────────┼──────────────────┐
           │                  │                  │
    ┌─────────────┐   ┌──────────────┐  ┌───────────────┐
    │ Registrarse │   │ Iniciar      │  │ Ver Catálogo  │
    │ (CU-01)     │   │ Sesión       │  │ (CU-03)       │
    └─────────────┘   │ (CU-02)      │  └───────────────┘
                      └──────────────┘
                              │
           ┌──────────────────┼──────────────────┐
           │                  │                  │
    ┌──────────────┐   ┌──────────────┐  ┌──────────────┐
    │ Buscar/      │   │ Ver Detalles │  │ Agregar al   │
    │ Filtrar      │   │ Producto     │  │ Carro        │
    │ (CU-04)      │   │ (CU-05)      │  │ (CU-06)      │
    └──────────────┘   └──────────────┘  └──────────────┘
                              │
                        ┌─────────────┐
                        │ Ver Carro   │
                        │ (CU-07)     │
                        └─────────────┘
                              │
                        ┌─────────────┐
                        │ Crear Orden │
                        │ (CU-08)     │
                        └─────────────┘
                              │
                        ┌─────────────┐
                        │ Procesar    │
                        │ Pago        │
                        │ (CU-09)     │
                        └─────────────┘
                              │
                        ┌─────────────┐
                        │ Ver         │
                        │ Historial   │
                        │ (CU-10)     │
                        └─────────────┘


                     ┌──────────────────┐
                     │ Administrador    │
                     └──────────────────┘
                              │
           ┌──────────────────┼──────────────────┐
           │                  │                  │
    ┌─────────────┐   ┌──────────────┐  ┌───────────────┐
    │ Gestionar   │   │ Gestionar    │  │ Gestionar     │
    │ Productos   │   │ Ofertas      │  │ Usuarios      │
    │ (CU-11..14) │   │ (CU-15)      │  │ (CU-17)       │
    └─────────────┘   └──────────────┘  └───────────────┘
           │                                      │
           │                               ┌──────────────┐
           │                               │ Ver Todas    │
           │                               │ las Órdenes  │
           └───────────────┬────────────────│ (CU-16)      │
                           │               └──────────────┘
                    ┌─────────────┐
                    │ Panel Admin │
                    │ (Dashboard) │
                    └─────────────┘
```

---

## 10. DIAGRAMAS DE ARQUITECTURA

### 10.1 Arquitectura General del Sistema

```
┌───────────────────────────────────────────────────────────────┐
│                      CLIENTE (NAVEGADOR)                      │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │         FRONTEND - React (puerto 3000)                  │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │  │
│  │  │ Componentes  │  │ Context API  │  │ Formularios  │  │  │
│  │  │ Páginas      │  │ Estado       │  │ Validación   │  │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  │  │
│  └──────────────────────────┬───────────────────────────────┘  │
└───────────────────────────────┼────────────────────────────────┘
                                │
                       HTTP/HTTPS (REST API)
                    Authorization: Bearer JWT
                                │
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                    SERVIDOR (BACKEND)                         │
│         Spring Boot 3.5.8 + Java 21 (puerto 8080)            │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │               CAPA DE PRESENTACIÓN (REST)               │  │
│  │  /api/auth/*  /api/products/*  /api/orders/*  etc.      │  │
│  │  Controladores REST, Validación entrada                 │  │
│  └──────────────────────────┬────────────────────────────┘  │
│  ┌──────────────────────────┴────────────────────────────┐  │
│  │            CAPA DE LÓGICA DE NEGOCIO                  │  │
│  │  Services, Validaciones, Cálculos                     │  │
│  │  AuthService, ProductService, OrderService, etc.      │  │
│  └──────────────────────────┬────────────────────────────┘  │
│  ┌──────────────────────────┴────────────────────────────┐  │
│  │          CAPA DE ACCESO A DATOS (DAO)                 │  │
│  │  Repositories, Queries, Transacciones                 │  │
│  │  UserRepository, ProductRepository, etc.              │  │
│  └──────────────────────────┬────────────────────────────┘  │
│  ┌──────────────────────────┴────────────────────────────┐  │
│  │         CAPA DE SEGURIDAD (Spring Security)           │  │
│  │  JWT Filter, Autenticación, Autorización              │  │
│  │  @PreAuthorize, hasRole() validations                 │  │
│  └──────────────────────────┬────────────────────────────┘  │
└───────────────────────────────┼────────────────────────────────┘
                                │
                         MongoDB Queries
                                │
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                      BASE DE DATOS                            │
│         MongoDB (NoSQL) - Puerto 27017 (default)              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Collections:                                            │  │
│  │  - users          (email, password_hash, role)         │  │
│  │  - products       (nombre, precio, stock, categoría)   │  │
│  │  - offers         (product_id, descuento, vigencia)    │  │
│  │  - orders         (user_id, items, total, estado)      │  │
│  │  - transactions   (order_id, monto, estado, token)     │  │
│  │  - cart           (user_id, items, timestamp)          │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────┘

                    CONFIGURACIÓN ADICIONAL:

  ┌─────────────────────────────────────────────┐
  │ CORS Configuration: localhost:3000 permitido │
  │ JWT Secret: configurado en application.props │
  │ Logging: archivos de log en /logs            │
  │ Transbank Sandbox: API simulada              │
  └─────────────────────────────────────────────┘
```

### 10.2 Flujo de Autenticación y Autorización

```
┌──────────────┐
│ Usuario      │ 1. POST /api/auth/login
│ (Frontend)   │    {email, password}
└──────────────┘
       │
       ▼
┌──────────────────────────────────────┐
│ Backend                              │
│ AuthController.login()               │
│ 2. Validar credenciales              │
│ 3. Si OK: generar JWT                │
│ 4. Retornar token + user data        │
└──────────────────────────────────────┘
       │
       │ JWT Token (válido 24h)
       ▼
┌──────────────┐
│ localStorage │
│ (Frontend)   │
└──────────────┘
       │
       │ Almacenar token
       │
       │ 5. En cada petición protegida:
       │    Authorization: Bearer <JWT>
       ▼
┌──────────────────────────────────────┐
│ Backend                              │
│ JwtFilter (Spring Security)          │
│ 6. Validar token                     │
│ 7. Extraer claims (user_id, role)    │
│ 8. Verificar signature y expiración  │
└──────────────────────────────────────┘
       │
       ├─ Token válido ─────┬─ Token inválido ──┐
       │                    │                   │
       ▼                    ▼                   ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Autorización     │  │ 401 Unauthorized │  │ Token expirado   │
│ @PreAuthorize    │  │ Request rechazado│  │ Ir a login       │
│ hasRole()        │  │                  │  │                  │
└──────────────────┘  └──────────────────┘  └──────────────────┘
       │
       ├─ ADMIN ─────────┬─ USER ───────────┐
       │                 │                  │
       ▼                 ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Acceso a     │  │ Acceso       │  │ Acceso       │
│ endpoints    │  │ denegado     │  │ permitido    │
│ /admin/*     │  │ /admin/*     │  │ /orders/*    │
│ 200 OK       │  │ 403 Forbidden│  │ 200 OK       │
└──────────────┘  └──────────────┘  └──────────────┘
```

### 10.3 Flujo de Creación de Orden y Pago

```
┌──────────────────────────────────────────────────────────────┐
│ 1. USUARIO: Ver carro                                        │
│    Items: [Producto A (qty 2), Producto B (qty 1)]          │
│    Subtotal: $100.000                                        │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. USUARIO: Click "Crear Orden"                              │
│    Ingresa dirección, elige envío (Standard $5k)             │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. BACKEND: POST /api/orders/create/{userId}                 │
│    - Validar stock disponible ✓                              │
│    - Calcular IVA: 100k * 0.19 = 19k                         │
│    - Total: 100k + 19k + 5k = 124k                           │
│    - Crear orden en BD, status: "Pendiente"                  │
│    - Generar order_id: ORD-2025-001                          │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. RESPUESTA: 200 OK                                         │
│    {                                                         │
│      order_id: "ORD-2025-001",                               │
│      total: 124000,                                          │
│      status: "Pendiente",                                    │
│      payment_url: "/checkout"                                │
│    }                                                         │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. USUARIO: Click "Ir a Pago"                                │
│    Redirige a: /checkout/ORD-2025-001                        │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 6. BACKEND: POST /api/pay/create                             │
│    ?orderId=ORD-2025-001&amount=124000&returnUrl=...        │
│    - Crear transacción                                       │
│    - Generar token: TRANSBANK_TOKEN_XYZ123                   │
│    - Guardar en BD                                           │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 7. RESPUESTA                                                 │
│    {                                                         │
│      token: "TRANSBANK_TOKEN_XYZ123",                        │
│      url: "https://sandbox.transbank.cl/pay?token=..."     │
│    }                                                         │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 8. USUARIO: Redirigido a Transbank (sandbox)                 │
│    Ingresa datos pago simulados                              │
│    Click "Confirmar pago"                                    │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 9. TRANSBANK: Procesa pago, retorna a returnUrl              │
│    returnUrl incluye token en query param                    │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│ 10. BACKEND: GET /api/pay/confirm/{token}                    │
│     - Validar token                                          │
│     - Consultar estado en Transbank                          │
│     - Procesar respuesta                                     │
└──────────────────────────────────────────────────────────────┘
       │
       ├─ APROBADO ─────────┬─ RECHAZADO ──────┐
       │                    │                  │
       ▼                    ▼                  ▼
┌──────────────┐     ┌────────────────┐  ┌────────────────┐
│ • Actualizar │     │ • Actualizar   │  │ • Actualizar   │
│   orden a    │     │   orden a      │  │   orden a      │
│   "Pagada"   │     │   "Fallida"    │  │   "Cancelada"  │
│ • Decremen-  │     │ • Liberar      │  │ • Liberar      │
│   tar stock  │     │   stock        │  │   stock        │
│ • Log trans. │     │ • Log trans.   │  │ • Log trans.   │
│ • 200 OK     │     │ • 402 Payment  │  │ • 402 Payment  │
└──────────────┘     └────────────────┘  └────────────────┘
       │                    │                    │
       └────────────────────┴────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────┐
│ 11. FRONTEND: Mostrar resultado al usuario                   │
│     ✓ "Pago procesado exitosamente"                          │
│     ✗ "El pago fue rechazado"                                │
└──────────────────────────────────────────────────────────────┘
```

---

## ANEXOS

### Anexo A: Glosario Técnico

| Término | Definición |
|---------|-----------|
| **API REST** | Interfaz de programación que sigue principios REST para CRUD |
| **JWT** | Estándar de seguridad para tokens autenticación sin sesión servidor |
| **Token** | Cadena de caracteres que representa identidad y permisos del usuario |
| **Hash** | Función criptográfica que convierte contraseña en código irreversible |
| **CORS** | Mecanismo seguridad navegador que controla peticiones cross-domain |
| **Sandbox** | Ambiente seguro para pruebas sin afectar producción |
| **Índice** | Estructura BD que acelera búsquedas en campos específicos |
| **Transacción ACID** | Operación DB que garantiza atomicidad, consistencia, aislamiento y durabilidad |
| **Caching** | Almacenamiento temporal de datos para acelerar acceso |
| **Load Balancer** | Distribuidor de tráfico entre múltiples servidores |

### Anexo B: Criterios de Aceptación Generales

1. **Funcionalidad:** Todos los RF deben implementarse exactamente como se especifican
2. **Seguridad:** JWT obligatorio en endpoints protegidos, roles validados
3. **Rendimiento:** Respuestas en tiempo máximo especificado
4. **Disponibilidad:** Sistema operativo 99% del tiempo
5. **Usabilidad:** Interfaz clara, mensajes en español, navegación intuitiva
6. **Testing:** Mínimo 80% de cobertura de tests unitarios
7. **Documentación:** Código documentado, API especificada en Swagger
8. **Escalabilidad:** Arquitectura preparada para crecer a 1000+ usuarios

---

**Documento Aprobado por:**

- Cliente: PandaGamers
- Equipo de Desarrollo
- Fecha Aprobación: 4 de diciembre de 2025

---

**Historial de Cambios**

| Versión | Fecha | Descripción |
|---------|-------|------------|
| 1.0 | 04/12/2025 | Documento inicial ERC creado |

---

*Este documento es confidencial y de propiedad de PandaGamers. Su reproducción o distribución sin autorización está prohibida.*
