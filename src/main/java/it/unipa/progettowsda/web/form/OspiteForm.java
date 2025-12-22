package it.unipa.progettowsda.web.form;

import it.unipa.progettowsda.domain.entity.enumerazioni.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class OspiteForm {

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    // IMPORTANTE: Obbligatorio e deve essere nel passato
    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascita;

    @NotBlank(message = "Il luogo di nascita è obbligatorio")
    private String luogoNascita;

    @NotBlank(message = "La cittadinanza è obbligatoria")
    private String cittadinanza;

    private TipoDocumento tipoDoc; // Obbligatorio solo per capogruppo (gestito nel controller)
    private String numeroDoc;      // Obbligatorio solo per capogruppo (gestito nel controller)

    // --- GETTER E SETTER ---
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

    public String getNumeroDoc() { return numeroDoc; }
    public void setNumeroDoc(String numeroDoc) { this.numeroDoc = numeroDoc; }
}