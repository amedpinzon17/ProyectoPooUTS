package inventario.modelo;

import java.time.LocalDateTime;

/** Compra o venta de un producto (RF06). */
public class Movimiento {

    public enum Tipo { COMPRA, VENTA }

    private int id;
    private int idProducto;
    private int idUsuario;
    private String nombreUsuario;
    private String nombreProducto;
    private Tipo tipo;
    private int cantidad;
    private double precioUnitario;
    private LocalDateTime fecha;
    private String observacion;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public double getTotal() { return cantidad * precioUnitario; }
}
