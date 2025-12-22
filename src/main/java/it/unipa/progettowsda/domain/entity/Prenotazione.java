package it.unipa.progettowsda.domain.entity;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prenotazione")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenotazione")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente; // Chi prenota

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_camera", nullable = false)
    private Camera camera;

    @Column(nullable = false)
    private LocalDate dataCheckin;

    @Column(nullable = false)
    private LocalDate dataCheckout;

    @Column(nullable = false)
    private Integer numOspiti;

    @Column(name = "prezzo_pagato", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoPagato;

    @Column(columnDefinition = "TEXT")
    private String noteCliente;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('CONFERMATA', 'IN_CORSO', 'TERMINATA', 'CANCELLATA') DEFAULT 'CONFERMATA'")
    private StatoPrenotazione stato = StatoPrenotazione.CONFERMATA;

    @OneToMany(mappedBy = "prenotazione", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ospite> ospiti = new ArrayList<>();

    @Column(name = "num_ospiti_esenti_dichiarati", nullable = false)
    private Integer numOspitiEsentiDichiarati = 0;

    // Costruttori, Getter e Setter...

    public Integer getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public LocalDate getDataCheckout() {
        return dataCheckout;
    }

    public void setDataCheckout(LocalDate dataCheckout) {
        this.dataCheckout = dataCheckout;
    }

    public LocalDate getDataCheckin() {
        return dataCheckin;
    }

    public void setDataCheckin(LocalDate dataCheckin) {
        this.dataCheckin = dataCheckin;
    }

    public Integer getNumOspiti() {
        return numOspiti;
    }

    public void setNumOspiti(Integer numOspiti) {
        this.numOspiti = numOspiti;
    }

    public BigDecimal getPrezzoPagato() { return prezzoPagato; }
    public void setPrezzoPagato(BigDecimal prezzoPagato) { this.prezzoPagato = prezzoPagato; }

    public String getNoteCliente() {
        return noteCliente;
    }

    public void setNoteCliente(String noteCliente) {
        this.noteCliente = noteCliente;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    public List<Ospite> getOspiti() {return ospiti;}

    public void setOspiti(List<Ospite> ospiti) {this.ospiti = ospiti;}

    public void addOspite(Ospite ospite) {
        ospiti.add(ospite);
        ospite.setPrenotazione(this);
    }
    public Integer getNumOspitiEsentiDichiarati() { return numOspitiEsentiDichiarati; }
    public void setNumOspitiEsentiDichiarati(Integer num) { this.numOspitiEsentiDichiarati = num; }

}

