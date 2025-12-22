package it.unipa.progettowsda.domain.entity;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoCamera;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "camera")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_camera")
    private Integer id;

    // Relazione Many-to-One: Molte camere stanno in una Struttura
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_struttura", nullable = false)
    private Struttura struttura;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false)
    private int capienza;

    @Column(name = "prezzo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoBase;

    // Usiamo l'Enum per mappare la colonna ENUM del DB
    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoCamera stato = StatoCamera.LIBERA;

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Luce> luci = new ArrayList<>();

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tapparella> tapparelle = new ArrayList<>();

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Termostato> termostati = new ArrayList<>();

    // --- COSTRUTTORI ---

    public Camera() {
    }

    public Camera(Struttura struttura, String numero, int capienza, BigDecimal prezzoBase) {
        this.struttura = struttura;
        this.numero = numero;
        this.capienza = capienza;
        this.prezzoBase = prezzoBase;
    }

    // --- GETTER E SETTER ---
    // (Niente setId)

    public Integer getId() { return id; }

    public Struttura getStruttura() { return struttura; }
    public void setStruttura(Struttura struttura) { this.struttura = struttura; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public int getCapienza() { return capienza; }
    public void setCapienza(int capienza) { this.capienza = capienza; }

    public BigDecimal getPrezzoBase() { return prezzoBase; }
    public void setPrezzoBase(BigDecimal prezzoBase) { this.prezzoBase = prezzoBase; }

    public StatoCamera getStato() { return stato; }
    public void setStato(StatoCamera stato) { this.stato = stato; }

    //qua ho la sezione relativa alla domotica
    //prima faccio tutti i metodi per gestire le liste di dispositivi (inizializzarle e richiederle)
    public List<Luce> getLuci() {
        return luci;
    }

    public void setLuci(List<Luce> luci) {
        this.luci = luci;
    }

    public List<Tapparella> getTapparelle() {
        return tapparelle;
    }

    public void setTapparelle(List<Tapparella> tapparelle) {
        this.tapparelle = tapparelle;
    }

    public List<Termostato> getTermostati() {
        return termostati;
    }

    public void setTermostati(List<Termostato> termostati) {
        this.termostati = termostati;
    }

    //qua invece i metodi helper per aggiungere o rimuovere elmenti dalla lista
    //rimuovendo prima l'elemento dalla lista della camera, per poi annullare il riferimento alla camera del singolo dispositivo
    public void addLuce(Luce luce) {
        luci.add(luce);
        luce.setCamera(this);
    }

    public void removeLuce(Luce luce) {
        luci.remove(luce);
        luce.setCamera(null);
    }

    public void addTapparella(Tapparella tapparella) {
        tapparelle.add(tapparella);
        tapparella.setCamera(this);
    }

    public void removeTapparella(Tapparella tapparella) {
        tapparelle.remove(tapparella);
        tapparella.setCamera(null);
    }

    public void addTermostato(Termostato termostato) {
        termostati.add(termostato);
        termostato.setCamera(this);
    }

    public void removeTermostato(Termostato termostato) {
        termostati.remove(termostato);
        termostato.setCamera(null);
    }
}