package inventario.modelo;

/** Producto sin vencimiento, con meses de garantía. */
public class ProductoNoPerecedero extends Producto {

    private int mesesGarantia;

    @Override
    public Integer getMesesGarantia() { return mesesGarantia; }
    public void setMesesGarantia(int mesesGarantia) { this.mesesGarantia = mesesGarantia; }

    @Override
    public String getTipo() { return "NO_PERECEDERO"; }

    @Override
    public String getDetalleEspecial() {
        return mesesGarantia > 0 ? "Garantía " + mesesGarantia + " meses" : "Sin garantía";
    }

    @Override
    public String validarReglas() {
        return mesesGarantia < 0 ? "Los meses de garantía no pueden ser negativos." : null;
    }
}
