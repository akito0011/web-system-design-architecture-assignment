package it.unipa.progettowsda.web.controller;

import it.unipa.progettowsda.domain.entity.Prenotazione;
import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.service.PrenotazioneService;
import it.unipa.progettowsda.domain.service.StaffService;
import it.unipa.progettowsda.domain.service.UtenteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class GestoreController {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService; // AGGIUNTO
    private final StaffService staffService;

    public GestoreController(PrenotazioneService prenotazioneService,
                             UtenteService utenteService,
                             StaffService staffService) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.staffService = staffService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        LocalDate oggi = LocalDate.now();
        List<Prenotazione> arrivi = prenotazioneService.getArriviDiOggi();
        List<Prenotazione> partenze = prenotazioneService.getPartenzeDiOggi();
        List<Prenotazione> inCasaGrezzi = prenotazioneService.getOspitiInCasa();

        List<Prenotazione> inCasaReali = inCasaGrezzi.stream()
                .filter(p -> !oggi.isBefore(p.getDataCheckin()) && !oggi.isAfter(p.getDataCheckout()))
                .collect(Collectors.toList());

        List<Prenotazione> tutte = prenotazioneService.getTutteLePrenotazioni();

        model.addAttribute("arrivi", arrivi);
        model.addAttribute("partenze", partenze);
        model.addAttribute("inCasa", inCasaReali);
        model.addAttribute("tuttePrenotazioni", tutte);
        model.addAttribute("dataOggi", oggi);

        return "gestore/dashboard";
    }

    // --- GESTIONE STAFF (NUOVA SEZIONE) ---

    @GetMapping("/gestione-staff")
    public String mostraGestioneStaff(Model model) {
        // Ora utenteService ha il metodo getTuttoLoStaff()
        model.addAttribute("listaStaff", utenteService.getTuttoLoStaff());
        model.addAttribute("nuovoStaff", new Utente());
        return "gestore/gestione-staff";
    }

    @PostMapping("/crea-staff")
    public String creaStaff(@ModelAttribute Utente nuovoStaff, RedirectAttributes redirectAttributes) {
        try {
            // Ora utenteService ha il metodo creaStaff()
            utenteService.creaStaff(nuovoStaff);
            redirectAttributes.addFlashAttribute("successMessage", "Membro dello staff creato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/admin/gestione-staff";
    }

    @PostMapping("/elimina-staff")
    public String eliminaStaff(@RequestParam Integer idUtente, RedirectAttributes redirectAttributes) {
        try {
            // Ora utenteService ha il metodo eliminaUtente()
            utenteService.eliminaUtente(idUtente);
            redirectAttributes.addFlashAttribute("successMessage", "Utente eliminato.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossibile eliminare: " + e.getMessage());
        }
        return "redirect:/admin/gestione-staff";
    }

    // --- ALTRE AZIONI ESISTENTI ---

    @GetMapping("/dettaglio-prenotazione/{id}")
    public String dettaglioPrenotazione(@PathVariable Integer id, Model model) {
        try {
            Prenotazione p = prenotazioneService.findById(id);
            model.addAttribute("prenotazione", p);
            return "gestore/dettaglio-prenotazione";
        } catch (RuntimeException e) {
            return "redirect:/admin/dashboard?error=PrenotazioneNonTrovata";
        }
    }

    @PostMapping("/conferma-checkout")
    public String forzaCheckOut(@RequestParam Integer idPrenotazione) {
        try {
            prenotazioneService.eseguiCheckOut(idPrenotazione);
            return "redirect:/admin/dashboard?success=CheckoutForzato";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/cancella-prenotazione")
    public String cancellaPrenotazione(@RequestParam Integer idPrenotazione) {
        try {
            prenotazioneService.cancellaPrenotazione(idPrenotazione);
            return "redirect:/admin/dashboard?success=PrenotazioneCancellata";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=" + e.getMessage();
        }
    }

    @GetMapping("/checkin-manuale/{id}")
    public String checkInManuale(@PathVariable Integer id, Model model) {
        try {
            Prenotazione p = prenotazioneService.findById(id);
            return "redirect:/admin/dettaglio-prenotazione/" + id;
        } catch (RuntimeException e) {
            return "redirect:/admin/dashboard?error=ImpossibileFareCheckinIDNonValido";
        }
    }

    @GetMapping("/download-report")
    public ResponseEntity<String> downloadReportXml() {
        String xmlContent = prenotazioneService.generaReportXmlGiornaliero();
        String filename = "report_prenotazioni_" + LocalDate.now() + ".xml";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_XML)
                .body(xmlContent);
    }

    @GetMapping("/storico-pulizie")
    public String vediStoricoPulizie(Model model) {
        // Recupera la lista dal service e la passa alla vista
        model.addAttribute("storico", staffService.getStoricoCompleto());
        return "gestore/storico-pulizie"; // Assicurati che il file HTML sia in templates/gestore/
    }
}