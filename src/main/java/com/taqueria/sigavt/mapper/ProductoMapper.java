package com.taqueria.sigavt.mapper;

import com.taqueria.sigavt.dto.ProductoDTO;
import com.taqueria.sigavt.model.Categoria;
import com.taqueria.sigavt.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoDTO aDTO(Producto producto) {
        if (producto == null) return null;

        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setUnidadMedida(producto.getUnidadMedida());
        dto.setPrecio(producto.getPrecio());
        dto.setActivo(producto.getActivo());

        if (producto.getCategoria() != null) {
            dto.setIdCategoria(producto.getCategoria().getIdCategoria());
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }

        return dto;
    }

    // Se convierte lo que llega en JSON (Cliente) hacia la BD
    public Producto aEntidad(ProductoDTO dto) {
        if (dto == null) return null;

        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setPrecio(dto.getPrecio());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        if (dto.getIdCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(dto.getIdCategoria());
            producto.setCategoria(categoria);
        }

        return producto;
    }
}