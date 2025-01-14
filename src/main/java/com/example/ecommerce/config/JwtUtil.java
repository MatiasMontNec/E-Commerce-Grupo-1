package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
public class JwtUtil {

    // Este es el código secreto que está en la fase de firma del JWT
    // En un ambiente de producción, este valor debe ser guardado en un lugar seguro
    private static String SECRET = "yo";
    private static Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    private static String dbName = System.getenv("DB_NAME");
    // Este metodo crea un JWT con el nombre de usuario
    public String create(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(dbName)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60)) // Modifica este valor para cambiar la duración del token
                ).sign(ALGORITHM);

    }

    // Este metodo verifica si un JWT es válido
    public boolean isValid(String jwt) {
        try {
            JWT.require(ALGORITHM).build().verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            System.out.println("Token inválido: " + jwt);
            return false;
        }
    }


    // Este metodo extrae el nombre de usuario de un JWT
    public String getUsername(String jwt){
        return JWT.require(ALGORITHM)
                .build()
                .verify(jwt)
                .getSubject();
    }
}