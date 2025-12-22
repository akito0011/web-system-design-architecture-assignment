package it.unipa.progettowsda.domain.entity;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoLuce;
import jakarta.persistence.*;

@Entity
@Table(name = "luce")
public class Luce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_luce")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_camera", nullable = false)
    private Camera camera;

    @Column(nullable = false, length = 50)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoLuce stato = StatoLuce.OFF;

    public Luce() {}

    public Luce(Camera camera, String nome) {
        this.camera = camera;
        this.nome = nome;
    }

    public Integer getId() { return id; }

    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public StatoLuce getStato() { return stato; }
    public void setStato(StatoLuce stato) { this.stato = stato; }
}