package com.example.ecommerce.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenEntity {
    private Long idOrden;
    private LocalDateTime fechaOrden;
    private String estado;
    private Long idCliente;
    private BigDecimal total;
}