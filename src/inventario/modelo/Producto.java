package inventario.modelo;

import java.time.LocalDate;

/**
 * Clase abstracta base de los productos (herencia + polimorfismo, RF01/RF10).
 * Cada subclase define su tipo y sus reglas propias de manejo.
 */
public abstract class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;
    private int stockMinimo;
    private Categoria categoria;
    private Proveedor proveedor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    /** Valor del tipo tal como se guarda en la base de datos. */
    public abstract String getTipo();

    /** Texto con el dato propio de cada tipo (vencimiento o garantía). */
    public abstract String getDetalleEspecial();

    /** Regla propia de cada tipo. Devuelve el mensaje de error o null si es válido. */
    public abstract String validarReglas();

    /** Solo los perecederos tienen fecha de vencimiento. */
    public LocalDate getFechaVencimiento() { return null; }

    /** Solo los no perecederos tienen garantía. */
    public Integer getMesesGarantia() { return null; }

    /** RF08: el producto está agotándose. */
    public boolean estaBajoMinimo() { return stock <= stockMinimo; }

    public double valorInventario() { return precio * stock; }

    @Override
    public String toString() { return codigo + " - " + nombre; }
}
