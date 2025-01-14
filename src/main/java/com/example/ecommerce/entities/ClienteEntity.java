package com.example.ecommerce.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {
    private Long idCliente;
    private String nombre;
    private String direccion;
    private String email;
    private String telefono;
    private String password;
    private String username;
    private String rol;
}
