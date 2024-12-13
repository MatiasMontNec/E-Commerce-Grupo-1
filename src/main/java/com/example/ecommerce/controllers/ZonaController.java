package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.RepartidorEntity;
import com.example.ecommerce.services.ZonaService;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/zonas")
public class ZonaController {

    @Autowired
    ZonaService zonaService;

    @GetMapping("/repartidores-zona")
    public ResponseEntity<List<RepartidorEntity>> getByZona(Long idzona) {
        List<RepartidorEntity> repartidores = zonaService.getByZona(idzona);
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/mapeo-zonas")
    public ResponseEntity<?> getZonasWithGeoJSON(
            @RequestParam(value = "idzona", required = false) Long idzona) {
        if (idzona != null) {
            // Buscar una sola zona por su ID
            Optional<Map<String, Object>> zona = zonaService.getZonaWithGeoJSONById(idzona);
            if (zona.isPresent()) {
                return ResponseEntity.ok(zona.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Zona no encontrada"));
            }
        }

        // Si no se pasa el ID, devolver todas las zonas
        List<Map<String, Object>> zonas = zonaService.getAllZonasWithGeoJSON();
        return ResponseEntity.ok(zonas);
    }

    @PostMapping("/point-in-zona")
    public ResponseEntity<Boolean> isPointInZona(@RequestBody Map<String, Object> request) {
        Geometry zona = (Geometry) request.get("zona"); // Recibe la geometría de la zona
        Point point = (Point) request.get("point");     // Recibe el punto a verificar
        boolean isInside = zonaService.pointInZona(zona, point);
        return ResponseEntity.ok(isInside);
    }


}