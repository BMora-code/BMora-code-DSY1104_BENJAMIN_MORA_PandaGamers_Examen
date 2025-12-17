import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../context/CartContext";
import { AuthContext } from "../context/AuthContext";
import dataStore from "../data/dataStore";
import { ordersAPI, transbankAPI } from "../api";

const Checkout = () => {
  const { cart, eliminarDelCarrito } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  // Verificar carrito vacío al cargar el componente.
  // NOTA: NO redirigimos a login aquí para evitar que al refrescar la página
  // se fuerce el login. Permitimos checkout como invitado; si quieres exigir
  // login, controla eso de forma explícita en otro lugar.
  useEffect(() => {
    if (!Array.isArray(cart) || cart.length === 0) {
      alert("El carrito está vacío");
      navigate("/carrito");
      return;
    }
  }, [cart, navigate]);

  const [shippingInfo, setShippingInfo] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    city: "",
    region: "",
    postalCode: "",
    deliveryOption: "standard"
  });

  const [paymentInfo, setPaymentInfo] = useState({
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    cardName: ""
  });

  const [errors, setErrors] = useState({});
  const [isProcessing, setIsProcessing] = useState(false);

  // Cargar información del usuario si está logueado
  useEffect(() => {
    if (user) {
      setShippingInfo(prev => ({
        ...prev,
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || ""
      }));
    }
  }, [user]);

  // Calcular totales (igual que en Carrito.js)
  const subtotal = cart.reduce((acc, item) => acc + ((item.price || 0) * item.quantity), 0);
  const descuentoDuoc = user && user.hasDuocDiscount ? Math.round(subtotal * 0.2) : 0;
  const subtotalConDescuento = user && user.hasDuocDiscount ? subtotal - descuentoDuoc : subtotal;
  const shippingCost = shippingInfo.deliveryOption === "express" ? 5000 : (shippingInfo.deliveryOption === "pickup" ? 0 : 2500);
  const iva = Math.round(subtotalConDescuento * 0.19); // IVA solo sobre subtotal con descuento
  const total = subtotalConDescuento + iva + shippingCost; // Mismo orden que carrito: subtotal + IVA + envío

  const deliveryOptions = [
    { value: "standard", label: "Envío estándar (2-3 días)", cost: 2500 },
    { value: "express", label: "Envío express (24 horas)", cost: 5000 },
    { value: "pickup", label: "Retiro en tienda", cost: 0 }
  ];

  const handleShippingChange = (e) => {
    const { name, value } = e.target;
    setShippingInfo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePaymentChange = (e) => {
    const { name, value } = e.target;
    setPaymentInfo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateForm = () => {
    const newErrors = {};

    // Validar información de envío
    if (!shippingInfo.firstName.trim()) newErrors.firstName = "Nombre es requerido";
    if (!shippingInfo.lastName.trim()) newErrors.lastName = "Apellido es requerido";
    if (!shippingInfo.email.trim()) newErrors.email = "Email es requerido";
    if (!shippingInfo.phone.trim()) newErrors.phone = "Teléfono es requerido";
    if (!shippingInfo.address.trim()) newErrors.address = "Dirección es requerida";
    if (!shippingInfo.city.trim()) newErrors.city = "Ciudad es requerida";
    if (!shippingInfo.region.trim()) newErrors.region = "Región es requerida";
    if (!shippingInfo.postalCode.trim()) newErrors.postalCode = "Código postal es requerido";

    // Validar información de pago
    if (!paymentInfo.cardNumber.trim()) newErrors.cardNumber = "Número de tarjeta es requerido";
    if (!paymentInfo.expiryDate.trim()) newErrors.expiryDate = "Fecha de expiración es requerida";
    if (!paymentInfo.cvv.trim()) newErrors.cvv = "CVV es requerido";
    if (!paymentInfo.cardName.trim()) newErrors.cardName = "Nombre en la tarjeta es requerido";

    // Validar formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (shippingInfo.email && !emailRegex.test(shippingInfo.email)) {
      newErrors.email = "Email no válido";
    }

    // Validar formato de tarjeta
    const cardRegex = /^\d{16}$/;
    if (paymentInfo.cardNumber && !cardRegex.test(paymentInfo.cardNumber.replace(/\s/g, ''))) {
      newErrors.cardNumber = "Número de tarjeta debe tener 16 dígitos";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    if (cart.length === 0) {
      alert("El carrito está vacío");
      return;
    }

    setIsProcessing(true);

    try {
      // Si hay usuario, crear orden en el backend
      if (user && user.id) {
        console.log('Usuario encontrado:', user);
        console.log('ID del usuario:', user.id);

        // Preparar items de la orden para el backend - ESTRUCTURA EXACTA ESPERADA POR BACKEND
        const orderItems = cart.map(item => ({
          productId: item.id,
          name: item.name,
          price: item.price,
          quantity: item.quantity
        }));

        console.log('Items para la orden:', orderItems);

        // Preparar datos completos de la orden
        const orderData = {
          items: orderItems,
          subtotal: subtotalConDescuento,
          shippingCost: shippingCost,
          iva: iva,
          total: total,
          shippingInfo: {
            name: `${shippingInfo.firstName} ${shippingInfo.lastName}`,
            address: shippingInfo.address,
            city: shippingInfo.city,
            region: shippingInfo.region,
            postalCode: shippingInfo.postalCode,
            phone: shippingInfo.phone
          },
          deliveryOption: shippingInfo.deliveryOption
        };

        console.log('Datos completos de la orden:', orderData);

        // Crear orden en el backend
        try {
          const orderResponse = await ordersAPI.create(user.id, orderData);
          console.log('Respuesta completa del servidor:', orderResponse);
          const newOrder = orderResponse.data;

          console.log('Respuesta de orden creada:', newOrder);
          console.log('Order ID:', newOrder?.id);

          if (newOrder && newOrder.id) {
            // Simular pago con 70% de probabilidad de éxito, 30% de fracaso
            const paymentSuccess = Math.random() < 0.7;
            console.log('Probabilidad de pago:', paymentSuccess ? '70% éxito' : '30% fracaso');

            if (paymentSuccess) {
              // Pago exitoso
              console.log('Pago exitoso (70% de probabilidad)');

              // Completar la orden (cambiar estado a COMPLETED)
              try {
                await ordersAPI.completeOrder(newOrder.id);
                console.log('Orden completada:', newOrder.id);
              } catch (completeError) {
                console.warn('Error al completar orden:', completeError);
                // Continuar aunque falle
              }

              // Limpiar carrito completamente
              cart.forEach(item => {
                for (let i = 0; i < item.cantidad; i++) {
                  eliminarDelCarrito(item.id);
                }
              });

              // Notificar a otras partes de la app que las órdenes y productos cambiaron
              window.dispatchEvent(new CustomEvent('ordersUpdated'));
              window.dispatchEvent(new CustomEvent('productsUpdated'));

              // Redirigir a página de éxito
              navigate(`/checkout/success/${newOrder.id}`);
            } else {
              // Pago fallido (30% de probabilidad) - actualizar estado de la orden a Failed
              console.log('Pago fallido (30% de probabilidad)');
              
              try {
                // Crear una actualización manual del estado a "Failed" usando PUT
                await ordersAPI.updateOrderStatus(newOrder.id, 'Failed');
                console.log('Orden marcada como fallida:', newOrder.id);
              } catch (failError) {
                console.warn('Error al marcar orden como fallida:', failError);
                // Continuar aunque falle la actualización
              }
              
              navigate("/checkout/error");
            }
          } else {
            console.error('No hay ID en la orden:', newOrder);
            navigate("/checkout/error");
          }
        } catch (createOrderError) {
          console.error('Error al crear la orden en el backend:', createOrderError);
          console.error('Detalles del error:', createOrderError.response?.data || createOrderError.message);
          navigate("/checkout/error");
        }
      } else {
        // Fallback: crear orden en dataStore si no hay usuario
        const order = {
          userId: "guest",
          items: cart.map(item => ({
            ...item,
            precioFinalGuardado: (item.precioFinal || item.precio || 0)
          })),
          shippingInfo: shippingInfo,
          paymentInfo: {
            ...paymentInfo,
            cardNumber: paymentInfo.cardNumber.slice(-4)
          },
          subtotal: subtotal,
          shippingCost: shippingCost,
          iva: iva,
          total: total,
          status: "completed",
          deliveryOption: shippingInfo.deliveryOption
        };

        const newOrder = dataStore.createOrder(order);
        cart.forEach(item => {
          for (let i = 0; i < item.cantidad; i++) {
            eliminarDelCarrito(item.id);
          }
        });

        window.dispatchEvent(new CustomEvent('ordersUpdated'));
        window.dispatchEvent(new CustomEvent('productsUpdated'));

        navigate(`/checkout/success/${newOrder.id}`);
      }
    } catch (error) {
      console.error("Error al procesar la orden:", error);
      navigate("/checkout/error");
    } finally {
      setIsProcessing(false);
    }
  };

  // Esta validación ahora se hace en useEffect, pero mantenemos como fallback
  if (cart.length === 0) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Verificando carrito...</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-lg-8">
          <h2 className="mb-4">Checkout</h2>

          <form onSubmit={handleSubmit}>
            {/* Información de envío */}
            <div className="card mb-4">
              <div className="card-header">
                <h5 className="mb-0">Información de Envío</h5>
              </div>
              <div className="card-body">
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Nombre *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.firstName ? 'is-invalid' : ''}`}
                      name="firstName"
                      value={shippingInfo.firstName}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.firstName && <div className="invalid-feedback">{errors.firstName}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Apellido *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.lastName ? 'is-invalid' : ''}`}
                      name="lastName"
                      value={shippingInfo.lastName}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.lastName && <div className="invalid-feedback">{errors.lastName}</div>}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Email *</label>
                    <input
                      type="email"
                      className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                      name="email"
                      value={shippingInfo.email}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Teléfono *</label>
                    <input
                      type="tel"
                      className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
                      name="phone"
                      value={shippingInfo.phone}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.phone && <div className="invalid-feedback">{errors.phone}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Dirección *</label>
                  <input
                    type="text"
                    className={`form-control ${errors.address ? 'is-invalid' : ''}`}
                    name="address"
                    value={shippingInfo.address}
                    onChange={handleShippingChange}
                    placeholder="Calle, número, departamento"
                    required
                  />
                  {errors.address && <div className="invalid-feedback">{errors.address}</div>}
                </div>

                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Ciudad *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                      name="city"
                      value={shippingInfo.city}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.city && <div className="invalid-feedback">{errors.city}</div>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Región *</label>
                    <select
                      className={`form-select ${errors.region ? 'is-invalid' : ''}`}
                      name="region"
                      value={shippingInfo.region}
                      onChange={handleShippingChange}
                      required
                    >
                      <option value="">Seleccionar región</option>
                      <option value="arica">Arica y Parinacota</option>
                      <option value="tarapaca">Tarapacá</option>
                      <option value="antofagasta">Antofagasta</option>
                      <option value="atacama">Atacama</option>
                      <option value="coquimbo">Coquimbo</option>
                      <option value="valparaiso">Valparaíso</option>
                      <option value="metropolitana">Metropolitana</option>
                      <option value="ohiggins">O'Higgins</option>
                      <option value="maule">Maule</option>
                      <option value="nuble">Ñuble</option>
                      <option value="biobio">Biobío</option>
                      <option value="araucania">Araucanía</option>
                      <option value="losrios">Los Ríos</option>
                      <option value="loslagos">Los Lagos</option>
                      <option value="aysen">Aysén</option>
                      <option value="magallanes">Magallanes</option>
                    </select>
                    {errors.region && <div className="invalid-feedback">{errors.region}</div>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Código Postal *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.postalCode ? 'is-invalid' : ''}`}
                      name="postalCode"
                      value={shippingInfo.postalCode}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.postalCode && <div className="invalid-feedback">{errors.postalCode}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Opción de entrega *</label>
                  {deliveryOptions.map(option => (
                    <div key={option.value} className="form-check">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="deliveryOption"
                        value={option.value}
                        checked={shippingInfo.deliveryOption === option.value}
                        onChange={handleShippingChange}
                        required
                      />
                      <label className="form-check-label">
                        {option.label} - ${option.cost.toLocaleString()}
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Información de pago */}
            <div className="card mb-4">
              <div className="card-header">
                <h5 className="mb-0">Información de Pago</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label className="form-label">Número de Tarjeta *</label>
                  <input
                    type="text"
                    className={`form-control ${errors.cardNumber ? 'is-invalid' : ''}`}
                    name="cardNumber"
                    value={paymentInfo.cardNumber}
                    onChange={handlePaymentChange}
                    placeholder="1234 5678 9012 3456"
                    maxLength="19"
                    required
                  />
                  {errors.cardNumber && <div className="invalid-feedback">{errors.cardNumber}</div>}
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Fecha de Expiración *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.expiryDate ? 'is-invalid' : ''}`}
                      name="expiryDate"
                      value={paymentInfo.expiryDate}
                      onChange={handlePaymentChange}
                      placeholder="MM/AA"
                      maxLength="5"
                      required
                    />
                    {errors.expiryDate && <div className="invalid-feedback">{errors.expiryDate}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">CVV *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.cvv ? 'is-invalid' : ''}`}
                      name="cvv"
                      value={paymentInfo.cvv}
                      onChange={handlePaymentChange}
                      placeholder="123"
                      maxLength="4"
                      required
                    />
                    {errors.cvv && <div className="invalid-feedback">{errors.cvv}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Nombre en la Tarjeta *</label>
                  <input
                    type="text"
                    className={`form-control ${errors.cardName ? 'is-invalid' : ''}`}
                    name="cardName"
                    value={paymentInfo.cardName}
                    onChange={handlePaymentChange}
                    placeholder="Como aparece en la tarjeta"
                    required
                  />
                  {errors.cardName && <div className="invalid-feedback">{errors.cardName}</div>}
                </div>
              </div>
            </div>

            <div className="d-flex gap-2 mb-4">
              <button
                type="submit"
                className="btn btn-success btn-lg flex-fill"
                disabled={isProcessing}
              >
                {isProcessing ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Procesando Pago...
                  </>
                ) : (
                  `Pagar $${total.toLocaleString()}`
                )}
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-lg"
                onClick={() => navigate("/carrito")}
              >
                Volver al Carrito
              </button>
            </div>
          </form>
        </div>

        {/* Resumen de compra */}
        <div className="col-lg-4">
          <div className="card" style={{ position: 'sticky', top: '20px' }}>
            <div className="card-header">
              <h5 className="mb-0">Resumen de Compra</h5>
            </div>
            <div className="card-body">
              {user && user.hasDuocDiscount && (
                <div className="alert mb-3" style={{ background: 'rgba(57, 255, 20, 0.1)', border: '1px solid var(--accent)', color: 'var(--text)' }}>
                  <i className="bi bi-star-fill me-2"></i>
                  ¡Descuento DUOC UC aplicado! 20% OFF en todos los productos
                </div>
              )}

              {cart.map(item => (
                <div key={item.id} className="d-flex justify-content-between mb-2">
                  <span>{item.nombre} x{item.cantidad}</span>
                  <span>${((item.precioOriginal || item.precio || 0) * item.cantidad).toLocaleString()}</span>
                </div>
              ))}

              <hr />

              <div className="d-flex justify-content-between mb-2">
                <span>Subtotal:</span>
                <span>${subtotal.toLocaleString()}</span>
              </div>

              {user && user.hasDuocDiscount && descuentoDuoc > 0 && (
                <div className="d-flex justify-content-between mb-2">
                  <span style={{ color: 'var(--accent)' }}>Descuento DUOC (20%):</span>
                  <span style={{ color: 'var(--accent)' }}>-${descuentoDuoc.toLocaleString()}</span>
                </div>
              )}

              <div className="d-flex justify-content-between mb-2">
                <span>IVA (19%):</span>
                <span>${iva.toLocaleString()}</span>
              </div>

              <div className="d-flex justify-content-between mb-2">
                <span>Envío:</span>
                <span>${shippingCost.toLocaleString()}</span>
              </div>

              <hr />

              <div className="d-flex justify-content-between mb-3">
                <strong>Total:</strong>
                <strong className="text-success">${total.toLocaleString()}</strong>
              </div>

              <div className="alert alert-info small">
                <i className="bi bi-info-circle me-1"></i>
                Los pagos se procesan de forma segura. Tu información está protegida.
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;