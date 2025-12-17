# Diagrama de flujo de navegación del sistema administrativo (Propuesta Final)

Diagrama que muestra pantallas y flujos clave del sistema administrativo, incluyendo validación y acciones persistentes.

```mermaid
flowchart TD
  SYS[Sistema Administrativo] --> LoginAdmin[Login Admin]
  LoginAdmin --> AdminPanel[AdminPanel (/admin)]
  AdminPanel --> Dashboard
  AdminPanel --> Productos
  AdminPanel --> Usuarios
  AdminPanel --> Ordenes
  AdminPanel --> Ofertas

  Productos --> ProductosList
  ProductosList --> ProductCreate[Crear Producto\n(Formulario VALIDACIÓN JS)]
  ProductCreate --> ProductSave[POST /products]
  ProductSave --> ProductosList
  ProductosList --> ProductEdit[Editar Producto\n(Formulario VALIDACIÓN JS)]
  ProductEdit --> ProductSave
  ProductosList --> ProductDelete[Eliminar Producto\n(DELETE /products/:id)]

  Usuarios --> UsuariosList
  UsuariosList --> UserCreate[Crear Usuario\n(Formulario VALIDACIÓN JS)]
  UserCreate --> UserSave[POST /users]
  UsuariosList --> UserEdit[Editar Usuario\n(Formulario VALIDACIÓN JS)]
  UserEdit --> UserSave

  Ordenes --> OrdenesList
  OrdenesList --> OrdenDetail
  OrdenDetail --> OrdenUpdate[Actualizar Estado\n(Validar transición de estado)]

  Ofertas --> OfertasList
  OfertasList --> OfertaCreate[Crear/Editar Oferta\n(Formulario VALIDACIÓN JS)]

  note right of ProductCreate: Validaciones: nombre, precio, stock, imagenURL
  note right of UserCreate: Validaciones: email (regex), password (min6), role
  note right of OrdenUpdate: Validar estados permitidos: [PENDIENTE->ENVIADO->ENTREGADO]

  classDef sysForms fill:#ecfccb,stroke:#16a34a,color:#000;
  class ProductCreate,ProductEdit,UserCreate,UserEdit,OfertaCreate sysForms;

  style SYS fill:#f1f5f9,stroke:#0f172a
```

Versión ASCII:

Sistema Administrativo
├─ Login Admin
└─ AdminPanel
   ├─ Dashboard
   ├─ Productos
   │  ├─ Lista
   │  ├─ Crear Producto (Validación JS: nombre, precio, stock, imagenURL)
   │  ├─ Editar Producto
   │  └─ Eliminar Producto
   ├─ Usuarios
   │  ├─ Crear Usuario (Validación: email regex, password min6, role)
   │  └─ Editar Usuario
   ├─ Órdenes
   │  ├─ Lista
   │  └─ Detalle (Actualizar estado con validación de transición)
   └─ Ofertas
      ├─ Lista
      └─ Crear/Editar Oferta (validar rango de descuento)

Recomendaciones adicionales:
- Aplicar validación en frontend y validar en backend (API) para evitar inconsistencias.
- Usar schema validation (Joi, Yup o Zod) en frontend para forms complejos y en backend (Spring validations + DTOs).
- Registrar auditoría de cambios realizados por administradores (quién, qué, cuándo).
