package it.unipa.progettowsda.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "servizio")
public class Servizio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servizio")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    // RELAZIONE MANY-TO-MANY (Lato Inverso)
    // "mappedBy" si riferisce al nome del campo 'serviziDisponibili' nella classe Struttura.
    // Usiamo Set per evitare duplicati e migliorare le performance di Hibernate.
    @ManyToMany(mappedBy = "serviziDisponibili")
    private Set<Struttura> strutture = new HashSet<>();

    // --- COSTRUTTORI ---

    public Servizio() {
    }

    public Servizio(String nome, BigDecimal prezzo) {
        this.nome = nome;
        this.prezzo = prezzo;
    }

    // --- GETTER E SETTER ---

    public Integer getId() { return id; }

    public String getNome() { return nome;}
    public void setNome(String nome) { this.nome = nome;}

    public BigDecimal getPrezzo() { return prezzo;}
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo;}

    public Set<Struttura> getStrutture() { return strutture;}
    public void setStrutture(Set<Struttura> strutture) { this.strutture = strutture;}

    //qua faccio override di equals ed hashcode per il set (serve per l'unicità)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Verifica se l'oggetto è un'istanza di Servizio
        if (!(o instanceof Servizio servizio)) return false;

        // Due servizi sono uguali se hanno lo stesso ID (e l'ID non è null)
        return id != null && Objects.equals(id, servizio.id);
    }

    @Override
    public int hashCode() {
        // Ritornare una costante è una best practice per le Entity JPA
        // per garantire consistenza anche prima che l'ID venga generato dal DB.
        return 31;
    }

    @Override
    public String toString() {
        return "Servizio{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", prezzo=" + prezzo +
                '}';
    }
}
