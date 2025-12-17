# Diagrama de flujo de navegación de la tienda

Mermaid (renderizar en VS Code o mermaid.live). Incluye validación JS destacada.

```mermaid
flowchart LR
  Home[Home] --> Catalog[Productos]
  Catalog --> Filter[Filtrar / Buscar]
  Catalog --> ProductDetail[ProductDetail\n(/producto/:id)]
  ProductDetail --> AddCart[Agregar al Carrito]
  AddCart --> Cart[Carrito (/carrito)]
  Cart --> EditQty[Editar Cantidad]
  Cart --> Remove[Eliminar Item]
  Cart --> Checkout[Checkout\n(Formulario de envío - VALIDACIÓN JS)]
  Checkout --> Payment[Crear Orden \nordersAPI.create]
  Payment --> Transbank[Transbank Simulación]
  Transbank --> Success[Éxito\nVaciar carrito]
  Transbank --> Fail[Error\nMantener carrito]

  class Checkout,ProductDetail,Cart forms;
  classDef forms fill:#fff3bf,stroke:#f59e0b;

  click ProductDetail "#ProductDetail" "Ir a detalles"
```

Versión ASCII:

Home -> Productos
Productos -> (Filtrar / Buscar)
Productos -> ProductDetail (/producto/:id)
ProductDetail -> Agregar al Carrito -> Carrito
Carrito -> Editar Cantidad / Eliminar Item
Carrito -> Checkout (formulario con validación JavaScript)
Checkout -> ordersAPI.create -> transbankAPI.createTransaction -> Confirmación
Confirmación -> Éxito (vaciar carrito) o Error (mantener carrito)

Formularios con validación JS en la tienda:
- Login / Register (antes de checkout si no logueado)
- Checkout (dirección, teléfono, ciudad) - validaciones: campos obligatorios, formatos
- ProductDetail: selector de cantidad (validar stock)

Recomendaciones de validación (JS):
- Validar campos requeridos y formatos (teléfono regex, dirección longitud mínima)
- Validar stock antes de enviar la orden
en frontend y confirmar en backend (doble validación)
