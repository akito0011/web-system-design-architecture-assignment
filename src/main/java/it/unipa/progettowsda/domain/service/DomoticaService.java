package it.unipa.progettowsda.domain.service;

import it.unipa.progettowsda.domain.entity.*;
import it.unipa.progettowsda.domain.entity.enumerazioni.ModalitaTermostato;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoLuce;
import it.unipa.progettowsda.domain.repository.*;
import it.unipa.progettowsda.domain.entity.enumerazioni.Azione;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DomoticaService {

    private final LuceRepository luceRepo;
    private final TapparellaRepository tapparellaRepo;
    private final TermostatoRepository termostatoRepo;
    private final CameraRepository cameraRepo;
    private final PrenotazioneRepository prenotazioneRepo; // Aggiunto per risolvere il problema dell'ID

    public DomoticaService(LuceRepository luceRepo, TapparellaRepository tapparellaRepo,
                           TermostatoRepository termostatoRepo, CameraRepository cameraRepo,
                           PrenotazioneRepository prenotazioneRepo) {
        this.luceRepo = luceRepo;
        this.tapparellaRepo = tapparellaRepo;
        this.termostatoRepo = termostatoRepo;
        this.cameraRepo = cameraRepo;
        this.prenotazioneRepo = prenotazioneRepo;
    }

    private Camera getCameraDaPrenotazione(Integer idPrenotazione) {
        Prenotazione p = prenotazioneRepo.findById(idPrenotazione)
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata: " + idPrenotazione));

        if (p.getCamera() == null) {
            throw new EntityNotFoundException("Nessuna camera associata alla prenotazione " + idPrenotazione);
        }
        return p.getCamera();
    }

    public Tapparella getTapparellaByPrenotazioneId(Integer idPrenotazione) {
        Camera camera = getCameraDaPrenotazione(idPrenotazione);
        // Assumiamo 1 tapparella per camera (o prendiamo la prima)
        return tapparellaRepo.findByCameraId(camera.getId()).stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nessuna tapparella nella camera " + camera.getId()));
    }

    public Termostato getTermostatoByPrenotazioneId(Integer idPrenotazione) {
        Camera camera = getCameraDaPrenotazione(idPrenotazione);
        // Assumiamo 1 termostato per camera
        return termostatoRepo.findByCameraId(camera.getId()).stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nessun termostato nella camera " + camera.getId()));
    }

    // --- LETTURA DISPOSITIVI
    public List<Luce> getLuci(Integer idCamera) {
        return luceRepo.findByCameraId(idCamera);
    }

    public List<Tapparella> getTapparelle(Integer idCamera) {
        return tapparellaRepo.findByCameraId(idCamera);
    }

    public Termostato getTermostato(Integer idCamera) {
        return termostatoRepo.findByCameraId(idCamera).stream().findFirst().orElse(null);
    }

    // --- AZIONI MODIFICA STATO

    @Transactional
    public void switchLuce(Integer idLuce) {
        Luce luce = luceRepo.findById(idLuce).orElseThrow();
        if (luce.getStato() == StatoLuce.ON) {
            luce.setStato(StatoLuce.valueOf("OFF"));
        } else {
            luce.setStato(StatoLuce.valueOf("ON"));
        }
        luceRepo.save(luce);
    }

    @Transactional
    public void regolaTapparella(Integer idTapparella, Azione azione) {
        Tapparella t = tapparellaRepo.findById(idTapparella)
                .orElseThrow(() -> new EntityNotFoundException("Tapparella non trovata"));

        final int STEP = 10;
        int livelloAttuale = t.getLivello();
        int nuovoLivello;

        if (azione == Azione.INCREMENTA) {
            nuovoLivello = livelloAttuale + STEP;
        } else {
            nuovoLivello = livelloAttuale - STEP;
        }

        nuovoLivello = Math.min(100, Math.max(0, nuovoLivello));

        t.setLivello(nuovoLivello);
        tapparellaRepo.save(t);
    }

    @Transactional
    public void regolaTemperatura(Integer idTermostato, Azione azione) {
        Termostato t = termostatoRepo.findById(idTermostato)
                .orElseThrow(() -> new EntityNotFoundException("Termostato non trovato"));

        if (t.getModalita() == ModalitaTermostato.OFF) {
            throw new IllegalStateException("Termostato spento: impossibile cambiare temperatura.");
        }

        final BigDecimal STEP = new BigDecimal("0.5");
        BigDecimal maxTemp;
        BigDecimal minTemp;

        if (t.getModalita() == ModalitaTermostato.HEAT) {
            minTemp = new BigDecimal("10.0");
            maxTemp = new BigDecimal("30.0");
        } else {
            minTemp = new BigDecimal("16.0");
            maxTemp = new BigDecimal("32.0");
        }

        BigDecimal tempAttuale = t.getTemperatura();
        BigDecimal nuovaTemp;

        if (azione == Azione.INCREMENTA) {
            nuovaTemp = tempAttuale.add(STEP);
        } else {
            nuovaTemp = tempAttuale.subtract(STEP);
        }

        if (nuovaTemp.compareTo(maxTemp) > 0) {
            nuovaTemp = maxTemp;
        } else if (nuovaTemp.compareTo(minTemp) < 0) {
            nuovaTemp = minTemp;
        }

        t.setTemperatura(nuovaTemp);
        termostatoRepo.save(t);
    }

    @Transactional
    public void cambiaModalita(Integer idTermostato, ModalitaTermostato nuovaModalita) {
        Termostato t = termostatoRepo.findById(idTermostato)
                .orElseThrow(() -> new EntityNotFoundException("Termostato non trovato"));

        if (nuovaModalita == ModalitaTermostato.HEAT && t.getModalita() != ModalitaTermostato.HEAT) {
            t.setTemperatura(new BigDecimal("20.0"));
        } else if (nuovaModalita == ModalitaTermostato.COOL && t.getModalita() != ModalitaTermostato.COOL) {
            t.setTemperatura(new BigDecimal("24.0"));
        }

        t.setModalita(nuovaModalita);
        termostatoRepo.save(t);
    }

    public Tapparella getTapparellaById(Integer id) {
        return tapparellaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tapparella non trovata"));
    }

    public Termostato getTermostatoById(Integer id) {
        return termostatoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Termostato non trovato"));
    }

    public List<Luce> getLuciByPrenotazioneId(Integer idPrenotazione) {
        Camera camera = getCameraDaPrenotazione(idPrenotazione);
        // findByCameraId restituisce una lista vuota se non ci sono luci, non lancia eccezioni
        return luceRepo.findByCameraId(camera.getId());
    }
}