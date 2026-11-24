package cl.fortinity.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "boletas")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;


    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";


    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();


    public Boleta() {
    }


    public Long getId() {
        return id;

    }


    public Usuario getUsuario() {
        return usuario;

    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

    }


    public BigDecimal getTotal() {
        return total;

    }


    public void setTotal(BigDecimal total) {
        this.total = total;

    }


    public String getEstado() {
        return estado;

    }


    public void setEstado(String estado) {
        this.estado = estado;

    }


    public LocalDateTime getFecha() {
        return fecha;
        
    }
}