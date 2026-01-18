package it.unipa.progettowsda.domain.entity;
import it.unipa.progettowsda.domain.entity.enumerazioni.Ruolo;
import jakarta.persistence.*;

@Entity
@Table(name = "Utente") // Specifica il nome della tabella nel DB
public class Utente {
    @Id //dichiaro che questo sarà
    @GeneratedValue(strategy = GenerationType.IDENTITY) // l'id con questo dico che la strategia di generazione dell'id con auto-incremento nel db
    @Column(name = "id_utente") // Mappa la colonna 'id_utente' del DB sul campo 'id' di Java
    private Integer id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 50)
    private String cognome;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING) //da jakarta.persistence prendo la libreria EnumType
    @Column(nullable = false)
    private Ruolo ruolo;


    public Utente() {
    }
    //costruttore completo
    public Utente(String password, String nome, String cognome, String email, Ruolo ruolo) {
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
    }

    // --- GETTER E SETTER ---
    public Integer getId() { return id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

}
