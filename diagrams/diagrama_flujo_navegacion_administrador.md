# Diagrama de flujo de navegación del administrador (AdminPanel)

Incluye las vistas, formularios con validación y acciones CRUD.

```mermaid
flowchart TD
  AdminPanel[AdminPanel (/admin)] --> Dashboard[Dashboard]
  AdminPanel --> ProductosTab[Productos Tab]
  AdminPanel --> UsuariosTab[Usuarios Tab]
  AdminPanel --> OrdenesTab[Órdenes Tab]
  AdminPanel --> OfertasTab[Ofertas Tab]

  ProductosTab --> ProductosList[Lista de Productos]
  ProductosList --> CreateProduct[Crear Producto\n(Formulario VALIDACIÓN JS)]
  ProductosList --> EditProduct[Editar Producto\n(Formulario VALIDACIÓN JS)]
  EditProduct --> SaveProduct[Guardar -> productsAPI.update/create]
  CreateProduct --> SaveProduct
  ProductosList --> DeleteProduct[Eliminar Producto -> productsAPI.delete]

  UsuariosTab --> UsuariosList[Lista de Usuarios]
  UsuariosList --> CreateUser[Crear Usuario\n(Formulario VALIDACIÓN JS)]
  UsuariosList --> EditUser[Editar Usuario\n(Formulario VALIDACIÓN JS)]
  CreateUser --> SaveUser
  EditUser --> SaveUser
  UsuariosList --> DeleteUser[Eliminar Usuario]

  OrdenesTab --> OrdenesList[Lista de Órdenes]
  OrdenesList --> OrdenDetail[Detalle Orden]
  OrdenDetail --> UpdateStatus[Actualizar Estado\n(PUT orders/status)]

  OfertasTab --> OfertasList[Lista de Ofertas]
  OfertasList --> CreateOferta[Crear/Editar Oferta\n(Formulario VALIDACIÓN JS)]

  classDef adminForms fill:#fecaca,stroke:#dc2626,color:#000;
  class CreateProduct,EditProduct,CreateUser,EditUser,CreateOferta adminForms;

  style AdminPanel fill:#f8fafc,stroke:#0f172a
```

Versión ASCII:

AdminPanel (/admin)
├─ Dashboard
├─ Productos
│  ├─ Lista de Productos
│  │  ├─ Crear Producto (Formulario con validación JS)
│  │  ├─ Editar Producto (Formulario con validación JS)
│  │  └─ Eliminar Producto
│  └─ Importar JSON (validación de formato)
├─ Usuarios
│  ├─ Lista de Usuarios
│  │  ├─ Crear Usuario (Formulario validación: email, password, role)
│  │  ├─ Editar Usuario
│  │  └─ Eliminar Usuario
├─ Órdenes
│  ├─ Lista de Órdenes
│  └─ Detalle de Orden (Actualizar estado)
└─ Ofertas
   ├─ Lista de Ofertas
   └─ Crear/Editar Oferta (Form validación: productId, discount range)

Validaciones recomendadas en formularios de Admin:
- Producto: nombre no vacío, precio numérico >0, stock entero >=0, imagen URL válida
- Usuario: email válido, password mínimo 6 caracteres, role en [user, admin]
- Oferta: productId existente, discount entre 1% y 100%
- Import JSON: validar esquema y detectar duplicados antes de enviar
