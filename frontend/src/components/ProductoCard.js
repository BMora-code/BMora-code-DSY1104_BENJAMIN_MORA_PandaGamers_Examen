import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../context/CartContext";
import { AuthContext } from "../context/AuthContext";

const ProductoCard = ({ producto }) => {
  const { agregarAlCarrito } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleAddToCart = (e) => {
    e.stopPropagation(); // Evitar navegación al hacer clic en el botón

    // Verificar si hay usuario activo
    if (!user) {
      alert("Debes iniciar sesión para agregar productos al carrito.");
      return;
    }

    // Normalizar fields del backend
    const priceNum = Number(producto.price ?? 0) || 0;
    // Adaptar el formato del producto para el carrito (usar formato en inglés para consistencia)
    const cartItem = {
      id: producto.id,
      name: producto.name,
      price: priceNum,
      image: producto.image,
      quantity: 1
    };
    agregarAlCarrito(cartItem);

    // Mostrar anuncio personalizado
    const anuncio = document.createElement('div');
    anuncio.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      background: linear-gradient(135deg, #39FF14, #FFD700);
      color: #000;
      padding: 15px 20px;
      border-radius: 10px;
      box-shadow: 0 4px 20px rgba(57, 255, 20, 0.3);
      z-index: 10000;
      font-weight: bold;
      animation: slideIn 0.5s ease-out;
    `;
    anuncio.innerHTML = `
      <div style="display: flex; align-items: center; gap: 10px;">
        <span>🛒</span>
        <span>"${producto.name}" agregado al carrito</span>
        <button onclick="this.parentElement.parentElement.remove()" style="background: none; border: none; color: #000; font-size: 18px; cursor: pointer; margin-left: 10px;">×</button>
      </div>
    `;

    // Agregar estilos de animación si no existen
    if (!document.getElementById('cart-notification-styles')) {
      const style = document.createElement('style');
      style.id = 'cart-notification-styles';
      style.textContent = `
        @keyframes slideIn {
          from { transform: translateX(100%); opacity: 0; }
          to { transform: translateX(0); opacity: 1; }
        }
      `;
      document.head.appendChild(style);
    }

    document.body.appendChild(anuncio);

    // Auto-remover después de 4 segundos
    setTimeout(() => {
      if (anuncio.parentElement) {
        anuncio.remove();
      }
    }, 4000);
  };

  const handleCardClick = () => {
    navigate(`/productos/${producto.id}`);
  };

  return (
    <div className="card h-100 shadow-sm" onClick={handleCardClick} style={{ cursor: 'pointer', background: 'var(--surface)', border: '1px solid var(--border)', color: 'var(--text)' }}>
      <img
        src={producto.image ? encodeURI(producto.image) : "https://via.placeholder.com/300x200/6c757d/ffffff?text=Producto"}
        className="card-img-top"
        alt={producto.name}
        style={{ height: '200px', objectFit: 'cover' }}
        onError={(e) => {
          e.target.src = "https://via.placeholder.com/300x200/6c757d/ffffff?text=Producto";
        }}
      />
      <div className="card-body d-flex flex-column" style={{ paddingBottom: '1rem' }}>
        <h5 className="card-title" style={{ color: 'var(--text)', marginBottom: '0.25rem' }}>{producto.name}</h5>
        <p className="card-text small" style={{ color: 'var(--muted)', minHeight: '3.2rem', marginBottom: '0.5rem' }}>{producto.description}</p>

        {/* Mostrar precio: prioridad a oferta, luego DUOC, luego precio normal */}
        {(() => {
          const priceNum = producto.price ?? 0;
          // Si existe oferta para este producto, mostrar precio de oferta y precio original
          if (producto.offer) {
            const orig = producto.originalPrice ?? priceNum;
            const saved = Math.max(0, orig - priceNum);
            const percent = (typeof producto.offer?.discount !== 'undefined' && producto.offer?.discount !== null)
              ? producto.offer.discount
              : (orig > 0 ? Math.round((1 - (priceNum / orig)) * 100) : 0);
            return (
              <div className="mb-2">
                <p className="card-text small mb-0" style={{ color: 'var(--muted)', textDecoration: 'line-through' }}>
                  Precio original: ${orig.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}
                </p>
                <p className="card-text fw-bold h5 mb-0" style={{ color: 'var(--accent)' }}>
                  ${priceNum.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')} ({percent}% OFF)
                </p>
                <p className="small" style={{ color: 'var(--muted)' }}>
                  ¡Ahorras ${saved.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}!
                </p>
              </div>
            );
          }

          if (user && user.hasDuocDiscount) {
            return (
              <div className="mb-2">
                <p className="card-text small mb-0" style={{ color: 'var(--muted)', textDecoration: 'line-through' }}>
                  Precio original: ${priceNum.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}
                </p>
                <p className="card-text fw-bold h5 mb-0" style={{ color: 'var(--accent)' }}>
                  Precio con descuento DUOC: ${Math.round(priceNum * 0.8).toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')} (20% OFF)
                </p>
              </div>
            );
          }

          return (
            <p className="card-text fw-bold h5 mb-1" style={{ color: 'var(--accent)' }}>
              ${priceNum.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}
            </p>
          );
        })()}

        {/* Stock: inmediatamente debajo del precio */}
        <div
          className="product-stock mb-1"
          style={{
            fontSize: '0.9rem',
            fontWeight: 600,
            color: 'var(--text)',
            textAlign: 'left'
          }}
          aria-live="polite"
        >
          Disponible: {typeof producto?.stock !== 'undefined' ? producto.stock : 0}
        </div>

        {/* Categoría */}
        <div className="mb-2">
          <span className="badge" style={{ background: 'var(--surface)', color: 'var(--text)', border: '1px solid var(--border)' }}>{producto.category}</span>
        </div>

        <div className="mt-auto">
          <div className="d-grid gap-2">
            <button
              className="btn-neon"
              onClick={handleAddToCart}
              disabled={producto.stock <= 0}
            >
              {producto.stock > 0 ? 'Agregar al carrito' : 'Agotado'}
            </button>
            <button className="btn-outline btn-sm">
              Ver detalles
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductoCard;
