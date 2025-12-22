package it.unipa.progettowsda.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PrenotazioneServizio")
public class PrenotazioneServizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenotazione_servizio")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_prenotazione", nullable = false)
    private Prenotazione prenotazione;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_servizio", nullable = false)
    private Servizio servizio;

    @Column(name = "prezzo_pagato", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoPagato;

    @Column(nullable = false)
    private Integer quantita;

    @Column(name = "data_acquisto")
    private LocalDateTime dataAcquisto = LocalDateTime.now();

    public PrenotazioneServizio() {
    }

    public PrenotazioneServizio(Prenotazione prenotazione, Servizio servizio, Integer quantita, BigDecimal prezzoPagato) {
        this.prenotazione = prenotazione;
        this.servizio = servizio;
        this.quantita = quantita;
        this.prezzoPagato = prezzoPagato;
        this.dataAcquisto = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }
    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }

    public Servizio getServizio() {
        return servizio;
    }

    public void setServizio(Servizio servizio) {
        this.servizio = servizio;
    }

    public BigDecimal getPrezzoPagato() {
        return prezzoPagato;
    }

    public void setPrezzoPagato(BigDecimal prezzoPagato) {
        this.prezzoPagato = prezzoPagato;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public LocalDateTime getDataAcquisto() {
        return dataAcquisto;
    }

    public void setDataAcquisto(LocalDateTime dataAcquisto) {
        this.dataAcquisto = dataAcquisto;
    }
}