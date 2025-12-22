package it.unipa.progettowsda.web.controller;

import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.service.PrenotazioneService;
import it.unipa.progettowsda.domain.service.StaffService;
import it.unipa.progettowsda.domain.service.UtenteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;
    private final UtenteService utenteService;
    private final PrenotazioneService prenotazioneService;

    public StaffController(StaffService staffService, UtenteService utenteService, PrenotazioneService prenotazioneService) {
        this.staffService = staffService;
        this.utenteService = utenteService;
        this.prenotazioneService = prenotazioneService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Recuperiamo i dati dell'utente loggato per il benvenuto
        Utente staff = utenteService.findByEmail(userDetails.getUsername());

        model.addAttribute("utente", staff);

        // Lista camere da pulire (per la card gialla)
        model.addAttribute("camereSporche", staffService.getCamereDaPulire());

        // Lista camere occupate (per la tabella rossa)
        model.addAttribute("prenotazioniAttive", prenotazioneService.getOspitiInCasa());

        // IMPORTANTE: Lista storico pulizie (per la tabella nera in basso)
        model.addAttribute("storicoPulizie", staffService.getStoricoCompleto());

        return "staff/dashboard";
    }

    @PostMapping("/segna-pulita")
    public String pulisciCamera(@RequestParam Integer idCamera,
                                @AuthenticationPrincipal UserDetails userDetails, // <--- AGGIUNTO: Serve per sapere CHI pulisce
                                RedirectAttributes redirectAttributes) {
        try {
            // 1. Recupero l'utente staff loggato
            Utente staff = utenteService.findByEmail(userDetails.getUsername());

            // 2. Passo ID Camera e ID Staff al service (che ora richiede entrambi)
            staffService.segnaCameraComePulita(idCamera, staff.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Camera " + idCamera + " pulita e registrata nello storico.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }
}