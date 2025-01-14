package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.DetalleOrdenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;

import java.util.List;

@Repository
public class DetalleOrdenRepositoryImpl implements DetalleOrdenRepository {
    @Autowired
    Sql2o sql2o;

    @Override
    public DetalleOrdenEntity findById(Long id) {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM detalleordenes WHERE iddetalle = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(DetalleOrdenEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("No se encontró detalle de la orden", e);
        }
    }

    @Override
    public List<DetalleOrdenEntity> findByOrdenId(Long ordenId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM detalleordenes WHERE idorden = :id LIMIT :limit OFFSET :offset")
                    .addParameter("id", ordenId)
                    .addParameter("limit", pageSize)
                    .addParameter("offset", offset)
                    .executeAndFetch(DetalleOrdenEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron los detalles de la orden", e);
        }
    }

    @Override
    public int count(Long ordenId) {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT count(*) FROM detalleordenes WHERE idorden = :id")
                    .addParameter("id", ordenId)
                    .executeScalar(Integer.class);
        }
    }

    @Override
    public List<DetalleOrdenEntity> findByOrdenIdNormal(Long ordenId) {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM detalleordenes WHERE idorden = :id")
                    .addParameter("id", ordenId)
                    .executeAndFetch(DetalleOrdenEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron los detalles de la orden", e);
        }
    }

    @Override
    public List<DetalleOrdenEntity> findAll() {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM detalleordenes")
                    .executeAndFetch(DetalleOrdenEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("No se encontró detalle de la orden", e);
        }
    }

    @Override
    public void save(DetalleOrdenEntity detalleOrden) {
        try (org.sql2o.Connection con = sql2o.beginTransaction()) {
            con.createQuery("INSERT INTO public.detalleordenes(idorden, idproducto, cantidad, preciounitario) VALUES (:idorden,:idproducto,:cantidad,:preciounitario);")
                    .addParameter("idorden", detalleOrden.getIdOrden())
                    .addParameter("idproducto", detalleOrden.getIdProducto())
                    .addParameter("cantidad", detalleOrden.getCantidad())
                    .addParameter("preciounitario", detalleOrden.getPrecioUnitario())
                    .executeUpdate();
            Long generatedId = con.createQuery("SELECT currval('detalleordenes_iddetalle_seq')")
                    .executeScalar(Long.class);
            detalleOrden.setIdProducto(generatedId);
            con.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Error al insertar el producto", ex);
        }
    }

    @Override
    public void update(DetalleOrdenEntity detalleOrden) {
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery("UPDATE detalleordenes SET "+
                            "idorden = :idorden, " +
                            "idproducto = :idproducto, " +
                            "cantidad = :cantidad, " +
                            "preciounitario = :preciounitario " +
                            "WHERE iddetalle = :iddetalle")
                    .addParameter("idorden", detalleOrden.getIdOrden())
                    .addParameter("idproducto", detalleOrden.getIdProducto())
                    .addParameter("cantidad", detalleOrden.getCantidad())
                    .addParameter("preciounitario", detalleOrden.getPrecioUnitario())
                    .addParameter("iddetalle", detalleOrden.getIdDetalle())
                    .executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error al actualizar el producto", ex);
        }
    }

    @Override
    public void delete(DetalleOrdenEntity detalleOrden) {
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery("DELETE FROM detalleordenes WHERE iddetalle = :id")
                    .addParameter("id", detalleOrden.getIdOrden())
                    .executeUpdate();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery("DELETE FROM detalleordenes WHERE iddetalle = :id")
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }
}
