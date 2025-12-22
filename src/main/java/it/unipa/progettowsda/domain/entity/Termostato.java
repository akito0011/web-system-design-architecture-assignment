package it.unipa.progettowsda.domain.entity;
import it.unipa.progettowsda.domain.entity.enumerazioni.ModalitaTermostato;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@Table(name = "termostato")
public class Termostato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_termostato")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_camera", nullable = false)
    private Camera camera;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('OFF', 'COOL', 'HEAT') DEFAULT 'OFF'")
    private ModalitaTermostato modalita = ModalitaTermostato.OFF;

    // Mappiamo DECIMAL(3,1)
    @DecimalMin("16.0") @DecimalMax("28.0")
    @Column(name = "temp", precision = 3, scale = 1)
    private BigDecimal temperatura;

    public Termostato() {}

    public Termostato(Camera camera) {
        this.camera = camera;
        this.temperatura = new BigDecimal("22.0"); // Default confortevole
    }

    public Integer getId() { return id; }

    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public ModalitaTermostato getModalita() { return modalita; }
    public void setModalita(ModalitaTermostato modalita) { this.modalita = modalita; }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }


}
