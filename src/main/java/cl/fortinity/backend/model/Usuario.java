package cl.fortinity.backend.model;


import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String nombre;


    @Column(nullable = false, unique = true, length = 150)
    private String email;


    @JsonIgnore
    @Column(nullable = false)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.CLIENTE;


    @Column(nullable = false)
    private boolean activo = true;


    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();


    public Usuario() {
    }


    public Long getId() {
        return id;

    }


    public String getNombre() {
        return nombre;

    }


    public void setNombre(String nombre) {
        this.nombre = nombre;

    }


    public String getEmail() {
        return email;

    }


    public void setEmail(String email) {
        this.email = email;

    }


    public String getPassword() {
        return password;

    }


    public void setPassword(String password) {
        this.password = password;

    }


    public Rol getRol() {
        return rol;

    }


    public void setRol(Rol rol) {
        this.rol = rol;

    }


    public boolean isActivo() {
        return activo;

    }


    public void setActivo(boolean activo) {
        this.activo = activo;

    }


    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;

    }
}