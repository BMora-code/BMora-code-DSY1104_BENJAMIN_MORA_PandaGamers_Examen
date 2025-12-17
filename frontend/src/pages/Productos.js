import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import ProductoCard from "../components/ProductoCard";
import { productsAPI, offersAPI } from "../api";

const Productos = () => {
  const [productos, setProductos] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const location = useLocation();

  useEffect(() => {
    const updateProductos = async () => {
      try {
        console.log('Actualizando productos desde el backend...');
        const response = await productsAPI.getAll();
        const allProducts = response.data || [];
        console.log('Raw products from backend:', allProducts);
        console.log('Cantidad de productos recibidos:', allProducts.length);

        // Intentar obtener ofertas públicas y mezclarlas con los productos
        try {
          const offersResp = await offersAPI.getPublic();
          const offers = Array.isArray(offersResp.data) ? offersResp.data : (offersResp.data || []);
          const offersMap = {};
          offers.forEach(o => { if (o && o.productId) offersMap[String(o.productId)] = o; });

          const merged = allProducts.map(p => {
            const pid = String(p.id);
            const offer = offersMap[pid];
            if (offer) {
              // conservar precio original y aplicar precio de oferta
              const original = p.price;
              const discountedPrice = (typeof offer.price !== 'undefined' && offer.price !== null)
                ? Number(offer.price)
                : Math.round(original * (1 - (offer.discount || 0) / 100));
              return { ...p, originalPrice: original, price: discountedPrice, offer: { discount: offer.discount, ofertaId: offer.id, price: discountedPrice } };
            }
            return p;
          });

          setProductos(merged);
        } catch (err) {
          console.warn('No se pudieron obtener ofertas públicas:', err);
          setProductos(allProducts);
        }
      } catch (error) {
        console.error('Error fetching products from backend:', error);
        console.log('Backend no disponible, mostrando lista vacía');
        setProductos([]);
      }
    };

    // Verificar si hay parámetros de URL para filtrar por categoría
    const urlParams = new URLSearchParams(location.search);
    const categoryParam = urlParams.get('cat');
    if (categoryParam) {
      setSelectedCategory(categoryParam);
    }

    // Cargar productos inicialmente
    updateProductos();

    // Escuchar cambios en las ofertas para actualizar la lista de productos
    const handleOfertasUpdate = () => {
      console.log('Evento ofertasUpdated recibido en Productos.js');
      updateProductos();
    };

    window.addEventListener('ofertasUpdated', handleOfertasUpdate);

    return () => {
      window.removeEventListener('ofertasUpdated', handleOfertasUpdate);
    };
  }, [location.search]);

  // Filtrar productos basado en búsqueda y categoría
  const filteredProductos = productos.filter(producto => {
    const name = (producto.name || '').toString();
    const description = (producto.description || '').toString();
    const matchesSearch = name.toLowerCase().includes((searchTerm || '').toLowerCase()) ||
                         description.toLowerCase().includes((searchTerm || '').toLowerCase());
    const matchesCategory = selectedCategory === "" || (producto.category || '') === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  // Obtener categorías únicas
  // Incluir una categoría por defecto para productos sin categoría
  const categories = [...new Set(productos.map(p => (p.category && p.category !== '') ? p.category : 'Sin categoría'))];

  // Agrupar productos por categoría (y ordenar por nombre dentro de cada categoría)
  const productosPorCategoria = categories.reduce((acc, category) => {
    acc[category] = productos
      .filter(producto => ((producto.category && producto.category !== '') ? producto.category : 'Sin categoría') === category)
      .slice() // copia para no mutar el original
      .sort((a, b) => (a.name || '').toString().localeCompare((b.name || '').toString(), 'es'));
    return acc;
  }, {});

  // Lista filtrada ordenada (cuando se usa el filtro de categoría)
  const sortedFilteredProductos = filteredProductos.slice().sort((a, b) =>
    (a.name || '').toString().localeCompare((b.name || '').toString(), 'es')
  );

  return (
    <div className="container mt-3">
      <h2 className="mb-4">Productos</h2>

      {/* Barra de búsqueda y filtros */}
      <div className="row mb-3">
        <div className="col-md-6">
          <input
            type="text"
            className="form-control"
            placeholder="Buscar productos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="col-md-6">
          <select
            className="form-select"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          >
            <option value="">Todas las categorías</option>
            {categories.map(category => (
              <option key={category} value={category}>{category}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Mostrar productos por secciones si no hay filtro de categoría */}
      {selectedCategory === "" ? (
        categories.map(category => {
          const productosFiltrados = productosPorCategoria[category].filter(producto => {
            const name = (producto.name || '').toString().toLowerCase();
            const description = (producto.description || '').toString().toLowerCase();
            const term = (searchTerm || '').toString().toLowerCase();
            return name.includes(term) || description.includes(term);
          });

          if (productosFiltrados.length === 0) return null;

          return (
            <div key={category} className="mb-3">
              <h3 className="mb-2">{category}</h3>
              <div className="row gx-3 gy-3">
                {productosFiltrados.map(producto => (
                  <div key={producto.id} className="col-lg-3 col-md-4 col-sm-6 mb-3 text-start" style={{ paddingLeft: '8px' }}>
                    <ProductoCard producto={producto} />
                  </div>
                ))}
              </div>
            </div>
          );
        })
      ) : (
        /* Grid de productos filtrados por categoría (ordenado) */
        <div className="row gx-3 gy-3">
          {sortedFilteredProductos.map(producto => (
            <div key={producto.id} className="col-lg-3 col-md-4 col-sm-6 mb-3 text-start" style={{ paddingLeft: '8px' }}>
              <ProductoCard producto={producto} />
            </div>
          ))}
        </div>
      )}

      {filteredProductos.length === 0 && (
        <div className="text-center mt-4">
          <p>No se encontraron productos que coincidan con tu búsqueda.</p>
        </div>
      )}
    </div>
  );
};

export default Productos;
