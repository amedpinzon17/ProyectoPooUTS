package inventario.modelo;

import java.time.LocalDateTime;

/** Usuario del sistema con su rol. */
public class Usuario {

    private int id;
    private String username;
    private String nombreCompleto;
    private String email;
    private Rol rol;
    private boolean activo = true;
    private LocalDateTime ultimoAcceso;
    private String salt;
    private String passwordHash;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean esAdministrador() {
        return rol != null && Rol.ADMINISTRADOR.equals(rol.getNombre());
    }

    public String getNombreRol() { return rol == null ? "" : rol.getNombre(); }

    @Override
    public String toString() { return username; }
}
