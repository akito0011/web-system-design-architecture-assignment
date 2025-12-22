package it.unipa.progettowsda.web.controller;

import it.unipa.progettowsda.domain.entity.Camera;
import it.unipa.progettowsda.domain.entity.Struttura;
import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.repository.CameraRepository;
import it.unipa.progettowsda.domain.repository.StrutturaRepository;
import it.unipa.progettowsda.domain.service.PrenotazioneService;
import it.unipa.progettowsda.domain.service.UtenteService;
import it.unipa.progettowsda.web.form.RicercaForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;
    private final StrutturaRepository strutturaRepo;
    private final CameraRepository cameraRepo;
    private final UtenteService utenteService;

    public PrenotazioneController(PrenotazioneService prenotazioneService,
                                  StrutturaRepository strutturaRepo,
                                  CameraRepository cameraRepo,
                                  UtenteService utenteService) {
        this.prenotazioneService = prenotazioneService;
        this.strutturaRepo = strutturaRepo;
        this.cameraRepo = cameraRepo;
        this.utenteService = utenteService;
    }

    @GetMapping("/cerca")
    public String mostraFormRicerca(Model model) {
        model.addAttribute("ricercaForm", new RicercaForm());
        return "cliente/cerca";
    }

    @GetMapping("/risultati")
    public String mostraStrutture(
            @Valid @ModelAttribute("ricercaForm") RicercaForm form,
            BindingResult result,
            Model model) {

        if (form.getDataCheckin() != null && form.getDataCheckout() != null) {
            if (!form.getDataCheckout().isAfter(form.getDataCheckin())) {
                result.rejectValue("dataCheckout", "error.dates", "Il Check-out deve essere successivo al Check-in");
            }
        }

        if (result.hasErrors()) {
            return "cliente/cerca";
        }

        List<Camera> camereLibere = prenotazioneService.cercaCamereLibere(
                form.getCitta(), form.getDataCheckin(), form.getDataCheckout(), form.getNumOspiti()
        );

        Set<Struttura> struttureDisponibili = camereLibere.stream()
                .map(Camera::getStruttura)
                .collect(Collectors.toSet());

        if (struttureDisponibili.isEmpty()) {
            model.addAttribute("messaggioNoRisultati", "Nessuna struttura disponibile per i criteri selezionati.");
        }

        model.addAttribute("strutture", struttureDisponibili);

        return "cliente/risultati-strutture";
    }

    @GetMapping("/scelta-camera/{idStruttura}")
    public String mostraCamereStruttura(
            @PathVariable Integer idStruttura,
            @ModelAttribute("ricercaForm") RicercaForm form,
            Model model) {

        List<Camera> camereDisponibili = prenotazioneService.cercaCamereLibereInStruttura(
                idStruttura, form.getDataCheckin(), form.getDataCheckout(), form.getNumOspiti()
        );

        Struttura struttura = strutturaRepo.findById(idStruttura)
                .orElseThrow(() -> new RuntimeException("Struttura non trovata"));

        model.addAttribute("struttura", struttura);
        model.addAttribute("camere", camereDisponibili);

        return "cliente/scelta-camera";
    }

    @GetMapping("/riepilogo")
    public String riepilogo(
            @RequestParam Integer idCamera,
            @ModelAttribute("ricercaForm") RicercaForm form,
            Model model) {

        Camera camera = cameraRepo.findById(idCamera)
                .orElseThrow(() -> new RuntimeException("Camera non trovata"));

        long giorni = ChronoUnit.DAYS.between(form.getDataCheckin(), form.getDataCheckout());
        if (giorni < 1) giorni = 1;

        BigDecimal prezzoBaseCamera = camera.getPrezzoBase().multiply(BigDecimal.valueOf(giorni));

        int totaleOspiti = form.getNumOspiti() != null ? form.getNumOspiti() : 1;
        int ospitiEsenti = form.getOspitiEsenti() != null ? form.getOspitiEsenti() : 0;

        int ospitiPaganti = totaleOspiti - ospitiEsenti;
        if (ospitiPaganti < 0) ospitiPaganti = 0;

        BigDecimal tassaFissa = new BigDecimal("2.00");

        BigDecimal totaleTassa = tassaFissa
                .multiply(BigDecimal.valueOf(ospitiPaganti))
                .multiply(BigDecimal.valueOf(giorni));

        BigDecimal totaleComplessivo = prezzoBaseCamera.add(totaleTassa);

        model.addAttribute("camera", camera);
        model.addAttribute("giorni", giorni);
        model.addAttribute("totaleCamera", totaleComplessivo);
        model.addAttribute("serviziHotel", camera.getStruttura().getServiziDisponibili());

        return "cliente/riepilogo";
    }

    @PostMapping("/conferma-prenotazione")
    public String conferma(
            @RequestParam Integer idCamera,
            @ModelAttribute RicercaForm form,
            @RequestParam(required = false) List<Long> serviziScelti,
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Utente utente = utenteService.findByEmail(userDetails.getUsername());

        Map<Long, Integer> serviziMap = new HashMap<>();

        if (serviziScelti != null) {
            for (Long idServizio : serviziScelti) {
                String quantitaStr = request.getParameter("quantita-" + idServizio);
                int quantita = (quantitaStr != null && !quantitaStr.isEmpty()) ? Integer.parseInt(quantitaStr) : 1;
                serviziMap.put(idServizio, quantita);
            }
        }

        int numOspitiSicuro = form.getNumOspiti() != null ? form.getNumOspiti() : 1;
        int ospitiEsentiSicuro = form.getOspitiEsenti() != null ? form.getOspitiEsenti() : 0;

        prenotazioneService.creaPrenotazione(
                utente.getId(),
                idCamera,
                form.getDataCheckin(),
                form.getDataCheckout(),
                numOspitiSicuro,
                ospitiEsentiSicuro,
                serviziMap
        );

        return "redirect:/cliente/dashboard?success=true";
    }

    // --- NUOVO METODO PER ANNULLAMENTO LATO CLIENTE ---
    @PostMapping("/annulla-prenotazione")
    public String annullaPrenotazione(
            @RequestParam Integer idPrenotazione,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Utente utente = utenteService.findByEmail(userDetails.getUsername());
            prenotazioneService.annullaPrenotazioneLatoCliente(idPrenotazione, utente.getId());
            redirectAttributes.addFlashAttribute("success", "Prenotazione cancellata con successo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore: " + e.getMessage());
        }

        return "redirect:/cliente/dashboard";
    }
}