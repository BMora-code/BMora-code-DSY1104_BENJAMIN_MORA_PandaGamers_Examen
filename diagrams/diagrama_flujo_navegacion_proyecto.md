# Diagrama de flujo de navegación del proyecto (Visión General)

Formato: Mermaid (recomendado para render). Debajo está la versión ASCII.

```mermaid
flowchart TD
  App["App.js\n(BrowserRouter + Providers)"] --> Header["Header\n(nav + user + cart)"]
  App --> Home["Home"]
  App --> Productos["Productos (Catálogo)"]
  App --> ProductDetail["ProductDetail\n(/producto/:id)"]
  App --> Carrito["Carrito\n(/carrito)"]
  App --> Checkout["Checkout\n(/checkout)\n<- Formulario (validación JS)"]
  App --> Login["Login/Register\n(Formularios con validación)"]
  App --> AdminPanel["AdminPanel\n(/admin)\n(Login Admin)\n<- Formularios ADMIN (validación)"]

  Header --> Home
  Header --> Productos
  Header --> Carrito
  Header --> Login
  Header --> AdminPanel

  Productos --> ProductDetail
  ProductDetail --> Carrito["Agregar al carrito"]
  Carrito --> Checkout
  Checkout --> Payment["Pago (Transbank sim)"]
  Payment --> Success["Checkout Success"]
  Payment --> Error["Checkout Error"]

  style Checkout fill:#fff3bf,stroke:#f59e0b
  style Login fill:#fff3bf,stroke:#f59e0b
  style AdminPanel fill:#fecaca,stroke:#dc2626

  classDef forms fill:#fff3bf,stroke:#f59e0b,color:#000;
  class Checkout,Login forms;
  class AdminPanel forms;
```


Versión ASCII rápida:

App.js (Router + Providers)
├─ Header (nav, user, cart)
├─ Home
├─ Productos --> ProductDetail
│    └─ (Agregar al Carrito)
├─ Carrito --> Checkout (Formulario con validación JS)
│    └─ Pago (Transbank sim) -> Success / Error
├─ Login / Register (Formularios con validación JS)
└─ AdminPanel (requiere role admin) -> Tabs (Dashboard, Productos, Usuarios, Órdenes, Ofertas)
    ├─ Formularios de Admin: Crear/Editar Producto (validación), Crear/Editar Usuario (validación), Importar JSON


Leyenda:
- Nodos en amarillo: vistas con formularios y validación JS (Login, Register, Checkout)
- Nodos en rojo: panel administrativo y sus formularios (más sensibles)


Recomendación de uso:
- Renderizar el diagrama Mermaid en VS Code con la extensión "Markdown Preview Mermaid Support" o pegando en https://mermaid.live para exportar PNG/SVG.
- Usa la versión ASCII para documentación rápida o impresiones simples.
