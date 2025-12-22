package it.unipa.progettowsda.domain.entity;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "struttura", uniqueConstraints = {
        // Qui sto imponendo un vincolo d'integrità per rendere unici indirizzo e città
        @UniqueConstraint(
                name = "uq_struttura_reale",
                columnNames = {"indirizzo", "citta"}
        )
})
public class Struttura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_struttura")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 250)
    private String indirizzo;

    @Column(nullable = false, length = 50)
    private String citta;

    // qua definisco la lista di servizi offerti dalla struttura
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "`ServiziStruttura`", // Nome della tabella di raccordo SQL
            joinColumns = @JoinColumn(name = "id_struttura"), // Colonna FK verso Struttura
            inverseJoinColumns = @JoinColumn(name = "id_servizio") // Colonna FK verso Servizio
    )
    private Set<Servizio> serviziDisponibili = new HashSet<>();


    // --- Costruttori, Getter e Setter ---

    public Struttura() {}

    public Struttura(String nome, String indirizzo, String citta) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
    }

    public Integer getId() { return id;}

    public String getCitta() { return citta;}
    public void setCitta(String citta) { this.citta = citta;}

    public String getIndirizzo() { return indirizzo;}
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo;}

    public String getNome() { return nome;}
    public void setNome(String nome) { this.nome = nome;}

    //faccio anche per serviziDisponibili
    public Set<Servizio> getServiziDisponibili() { return serviziDisponibili; }
    public void setServiziDisponibili(Set<Servizio> serviziDisponibili) { this.serviziDisponibili = serviziDisponibili; }

}
