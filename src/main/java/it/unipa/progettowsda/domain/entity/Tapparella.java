package it.unipa.progettowsda.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "tapparella")
public class Tapparella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tapparella")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_camera", nullable = false)
    private Camera camera;

    @Column(nullable = false, length = 50)
    private String nome;

//qua uso Integer per tinyint e sfrutto la validazione java
    @Min(0) @Max(100)
    @Column(nullable = false)
    private Integer livello = 0; // 0 = Chiusa, 100 = Aperta

    public Tapparella() {}

    public Tapparella(Camera camera, String nome) {
        this.camera = camera;
        this.nome = nome;
        this.livello = 0;
    }

    public Integer getId() { return id; }

    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getLivello() { return livello; }
    public void setLivello(Integer livello) {
        if (livello < 0 || livello > 100) {
            throw new IllegalArgumentException("Il livello deve essere tra 0 e 100");
        }
        this.livello = livello;
    }
}