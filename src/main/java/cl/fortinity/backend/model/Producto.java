package cl.fortinity.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 150)
    private String nombre;


    @Column(columnDefinition = "TEXT")
    private String descripcion;


    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;


    @Column(nullable = false)
    private int descuento = 0;


    @Column(nullable = false)
    private int stock = 0;


    @Column(length = 1000)
    private String imagen;


    @Column(length = 100)
    private String plataforma;


    @Column(length = 150)
    private String desarrollador;


    @Column(nullable = false)
    private boolean destacado = false;


    @Column(nullable = false)
    private boolean activo = true;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;


    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();


    public Producto() {
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


    public String getDescripcion() {
        return descripcion;

    }


    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;

    }


    public BigDecimal getPrecio() {
        return precio;

    }


    public void setPrecio(BigDecimal precio) {
        this.precio = precio;

    }


    public int getDescuento() {
        return descuento;

    }


    public void setDescuento(int descuento) {
        this.descuento = descuento;

    }


    public int getStock() {
        return stock;

    }


    public void setStock(int stock) {
        this.stock = stock;

    }


    public String getImagen() {
        return imagen;

    }


    public void setImagen(String imagen) {
        this.imagen = imagen;

    }


    public String getPlataforma() {
        return plataforma;

    }


    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;

    }


    public String getDesarrollador() {
        return desarrollador;

    }


    public void setDesarrollador(String desarrollador) {
        this.desarrollador = desarrollador;

    }


    public boolean isDestacado() {
        return destacado;

    }


    public void setDestacado(boolean destacado) {
        this.destacado = destacado;

    }


    public boolean isActivo() {
        return activo;

    }


    public void setActivo(boolean activo) {
        this.activo = activo;

    }


    public Categoria getCategoria() {
        return categoria;

    }


    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;

    }


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;

    }
}