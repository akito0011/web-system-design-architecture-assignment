package it.unipa.progettowsda.web.form;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class RicercaForm {

    @NotBlank(message = "Devi inserire una città")
    private String citta;

    @NotNull(message = "Data obbligatoria")
    @FutureOrPresent(message = "Il check-in non può essere nel passato")
    private LocalDate dataCheckin;

    @NotNull(message = "Data obbligatoria")
    @Future(message = "Il check-out deve essere futuro")
    private LocalDate dataCheckout;

    @NotNull(message = "Inserisci il numero di ospiti")
    @Min(value = 1, message = "Almeno 1 ospite")
    @Max(value = 10, message = "Massimo 10 ospiti per ricerca")
    private Integer numOspiti;
    private Integer ospitiEsenti = 0; // Default 0

    // Getter e Setter
    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }
    public LocalDate getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(LocalDate dataCheckin) { this.dataCheckin = dataCheckin; }
    public LocalDate getDataCheckout() { return dataCheckout; }
    public void setDataCheckout(LocalDate dataCheckout) { this.dataCheckout = dataCheckout; }
    public Integer getNumOspiti() { return numOspiti; }
    public void setNumOspiti(Integer numOspiti) { this.numOspiti = numOspiti; }
    public Integer getOspitiEsenti() { return ospitiEsenti; }
    public void setOspitiEsenti(Integer ospitiEsenti) { this.ospitiEsenti = ospitiEsenti; }
}
