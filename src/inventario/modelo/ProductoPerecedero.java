package inventario.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Producto con fecha de vencimiento (RF10). */
public class ProductoPerecedero extends Producto {

    private LocalDate fechaVencimiento;

    @Override
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    public long diasParaVencer() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

    @Override
    public String getTipo() { return "PERECEDERO"; }

    @Override
    public String getDetalleEspecial() {
        if (fechaVencimiento == null) return "-";
        return estaVencido() ? "VENCIDO (" + fechaVencimiento + ")" : "Vence " + fechaVencimiento;
    }

    @Override
    public String validarReglas() {
        return fechaVencimiento == null ? "El producto perecedero requiere fecha de vencimiento." : null;
    }
}
