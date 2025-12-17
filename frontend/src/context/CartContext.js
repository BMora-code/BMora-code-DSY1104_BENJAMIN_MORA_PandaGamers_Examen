import React, { createContext, useState, useEffect, useCallback, useContext } from 'react';
import { AuthContext } from './AuthContext';

export const CartContext = createContext({
  cartItems: [],
  addToCart: () => {},
  removeFromCart: () => {},
  updateQuantity: () => {},
  totalItems: 0,
  agregarAlCarrito: () => {},
  eliminarDelCarrito: () => {},
  actualizarCantidad: () => {},
  clearCart: () => {},
  getTotal: () => 0
});

const getCartStorageKey = (userId) => {
  // Usa el ID del usuario si está disponible, de lo contrario usa 'guest'
  return userId ? `cart_${userId}` : 'cart_guest';
};

const loadCart = (userId) => {
  try {
    const key = getCartStorageKey(userId);
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : [];
  } catch (e) {
    return [];
  }
};

export const CartProvider = ({ children }) => {
  const { user } = useContext(AuthContext);
  const [cart, setCart] = useState(() => loadCart(user?.id));

  // Persistir cart cuando cambie (con key específica del usuario)
  useEffect(() => {
    try {
      const key = getCartStorageKey(user?.id);
      localStorage.setItem(key, JSON.stringify(cart));
    } catch (e) { /* ignore */ }
  }, [cart, user?.id]);

  // Cambiar de usuario: recargar carrito del nuevo usuario
  useEffect(() => {
    setCart(loadCart(user?.id));
  }, [user?.id]);

  // Sincronizar entre pestañas del mismo usuario
  useEffect(() => {
    const onStorage = (e) => {
      const expectedKey = getCartStorageKey(user?.id);
      if (e.key === expectedKey) {
        try {
          setCart(e.newValue ? JSON.parse(e.newValue) : []);
        } catch (err) {
          setCart([]);
        }
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, [user?.id]);

  // Agregar item al carrito o incrementar cantidad si ya existe
  const addToCart = useCallback((product, quantity = 1) => {
    if (!product || !product.id) return;

    setCart(prev => {
      const exists = prev.find(item => item.id === product.id);
      if (exists) {
        // Si existe, incrementar cantidad y mantener alias 'cantidad' para compatibilidad
        return prev.map(item =>
          item.id === product.id
            ? { ...item, quantity: Number(item.quantity || 0) + Number(quantity || 1), cantidad: Number(item.cantidad || item.quantity || 0) + Number(quantity || 1) }
            : item
        );
      }
      // Si no existe, agregar nuevo item con estructura {id, name, price, image, quantity}
      const qty = Number(quantity || 1);
      return [...prev, {
        id: product.id,
        name: product.name,
        price: Number(product.price || 0),
        image: product.image,
        quantity: qty,
        cantidad: qty
      }];
    });
  }, []);

  // Eliminar completamente un item del carrito
  const removeFromCart = useCallback((id) => {
    // Si no se entrega un id válido, no hacer nada
    if (id === undefined || id === null) return;

    setCart(prev => {
      // Asegurarnos de que prev es un array
      if (!Array.isArray(prev)) return prev || [];
      // Filtrar solo los items cuyo id NO coincide (eliminar solo el que coincide)
      return prev.filter(item => item && item.id !== id);
    });
  }, []);

  // Eliminar N unidades de un item. Si la cantidad resultante es 0, se elimina el item.
  const removeUnitsFromCart = useCallback((id, cantidad = 1) => {
    if (id === undefined || id === null) return;
    const removeQty = Number(cantidad || 1);

    setCart(prev => {
      if (!Array.isArray(prev)) return prev || [];
      return prev.reduce((acc, item) => {
        if (!item || item.id !== id) {
          acc.push(item);
          return acc;
        }
        const current = Number(item.quantity ?? item.cantidad ?? 0);
        const newQty = Math.max(0, current - removeQty);
        if (newQty > 0) {
          acc.push({ ...item, quantity: newQty, cantidad: newQty });
        }
        return acc;
      }, []);
    });
  }, []);

  // Actualizar cantidad de un item
  const updateQuantity = useCallback((id, quantity) => {
    const newQty = Number(quantity || 0);
    if (newQty <= 0) {
      removeFromCart(id);
      return;
    }
    setCart(prev =>
      prev.map(item =>
        item.id === id ? { ...item, quantity: newQty, cantidad: newQty } : item
      )
    );
  }, [removeFromCart]);

  // Limpiar carrito completamente
  const clearCart = useCallback(() => {
    setCart([]);
    // También limpiar del localStorage explícitamente
    try {
      const key = getCartStorageKey(user?.id);
      localStorage.removeItem(key);
    } catch (e) { /* ignore */ }
  }, [user?.id]);

  // Calcular total de dinero
  const getTotal = useCallback(() =>
    cart.reduce((sum, item) => sum + (Number(item.price || 0) * Number(item.quantity ?? item.cantidad ?? 0)), 0)
  , [cart]);

  // Calcular total de items (suma de cantidades) - incluye compatibilidad con 'cantidad'
  const totalItems = cart.reduce((sum, item) => sum + Number(item.quantity ?? item.cantidad ?? 0), 0);

  // Aliases en español para compatibilidad
  const agregarAlCarrito = addToCart;
  // `eliminarDelCarrito` ahora reduce unidades (por defecto 1). Para eliminar todo usar `removeFromCart`.
  const eliminarDelCarrito = removeUnitsFromCart;
  const actualizarCantidad = updateQuantity;

  return (
    <CartContext.Provider value={{
      cartItems: cart,
      addToCart,
      removeFromCart,
      updateQuantity,
      totalItems,
      agregarAlCarrito,
      eliminarDelCarrito,
      actualizarCantidad,
      clearCart,
      getTotal,
      cart  // Mantener 'cart' para compatibilidad con código existente
    }}>
      {children}
    </CartContext.Provider>
  );
};

export default CartContext;
