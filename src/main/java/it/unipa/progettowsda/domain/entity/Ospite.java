package it.unipa.progettowsda.domain.entity;

import it.unipa.progettowsda.domain.entity.enumerazioni.TipoDocumento;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ospite")
public class Ospite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ospite")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_prenotazione", nullable = false)
    private Prenotazione prenotazione;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 50)
    private String cognome;

    @Column(name = "data_nascita", nullable = false)
    private LocalDate dataNascita;

    @Column(name = "luogo_nascita", nullable = false, length = 100)
    private String luogoNascita;

    @Column(nullable = false, length = 50)
    private String cittadinanza;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_doc")
    private TipoDocumento tipoDoc;

    @Column(name = "numero_doc", length = 50)
    private String numeroDocumento;

    @Column(name = "esente", nullable = false)
    private Boolean esenteTassa = false;


    public Ospite() {} //lascio solo il costruttore vuoto visto che ci sono molti parametri
    //poi dopo che istanzio l'ospite inizializzo gli attributi con setter/getter oppure i Builder(? poi studio cosa sono)

    public Integer getId() { return id; }

    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione prenotazione) { this.prenotazione = prenotazione; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getLuogoNascita() { return luogoNascita; }
    public void setLuogoNascita(String luogoNascita) { this.luogoNascita = luogoNascita; }

    public String getCittadinanza() { return cittadinanza; }
    public void setCittadinanza(String cittadinanza) { this.cittadinanza = cittadinanza; }

    public TipoDocumento getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(TipoDocumento tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getNumeroDoc() { return numeroDocumento; }
    public void setNumeroDoc(String numeroDoc) { this.numeroDocumento = numeroDoc; }

    public Boolean getEsenteTassa() { return esenteTassa; }
    public void setEsenteTassa(Boolean esenteTassa) { this.esenteTassa = esenteTassa; }
}
