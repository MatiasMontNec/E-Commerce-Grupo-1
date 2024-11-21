package com.example.ecommerce.services;

import com.example.ecommerce.entities.ProductoEntity;
import com.example.ecommerce.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    ProductoRepository productoRepository;

    public ProductoEntity getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public List<ProductoEntity> getAllProductos() {
        return productoRepository.findAll();
    }

    public void saveProducto(ProductoEntity producto) {
        productoRepository.save(producto);
    }

    public void updateProducto(ProductoEntity producto) {
        if (producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo para actualizar.");
        }
        ProductoEntity existingProducto = productoRepository.findById(producto.getIdProducto());
        if (producto.getNombre() == null){
            producto.setNombre(existingProducto.getNombre());
        }
        if (producto.getDescripcion() == null){
            producto.setDescripcion(existingProducto.getDescripcion());
        }
        if (producto.getEstado() == null){
            producto.setEstado(existingProducto.getEstado());
        }
        if (producto.getPrecio() == null){
            producto.setPrecio(existingProducto.getPrecio());
        }
        if (producto.getStock() == null){
            producto.setStock(existingProducto.getStock());
        }
        if (producto.getIdCategoria() == null){
            producto.setIdCategoria(existingProducto.getIdCategoria());
        }
        productoRepository.update(producto);
    }

    public void deleteProductoById(Long id) {
        ProductoEntity producto = productoRepository.findById(id);
        if (producto == null) {
            throw new IllegalArgumentException("El id del producto no existe");
        }
        productoRepository.delete(producto);
    }

    public void deleteProducto(ProductoEntity producto) {
        ProductoEntity existingProducto = productoRepository.findById(producto.getIdProducto());
        if (existingProducto == null) {
            throw new IllegalArgumentException("El id del producto no existe");
        }
        productoRepository.delete(producto);
    }

}
