package it.unipa.progettowsda.domain.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "StoricoPulizie")
public class StoricoPulizie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pulizia")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_camera", nullable = false)
    private Camera camera;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_staff", nullable = false)
    private Utente staff;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra = LocalDateTime.now(); // Default a adesso

    @Column(columnDefinition = "TEXT")
    private String note;

    public StoricoPulizie() {}

    public StoricoPulizie(Camera camera, Utente staff, String note) {
        this.camera = camera;
        this.staff = staff;
        this.note = note;
        this.dataOra = LocalDateTime.now();
    }

    public Integer getId() { return id; }

    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public Utente getStaff() { return staff; }
    public void setStaff(Utente staff) { this.staff = staff; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}