package it.unipa.progettowsda.domain.service;

import it.unipa.progettowsda.domain.entity.Camera;
import it.unipa.progettowsda.domain.entity.StoricoPulizie;
import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoCamera;
import it.unipa.progettowsda.domain.repository.CameraRepository;
import it.unipa.progettowsda.domain.repository.StoricoPulizieRepository;
import it.unipa.progettowsda.domain.repository.UtenteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffService {

    private final CameraRepository cameraRepo;
    private final StoricoPulizieRepository storicoRepo; // AGGIUNTO
    private final UtenteRepository utenteRepo; // AGGIUNTO

    public StaffService(CameraRepository cameraRepo,
                        StoricoPulizieRepository storicoRepo,
                        UtenteRepository utenteRepo) {
        this.cameraRepo = cameraRepo;
        this.storicoRepo = storicoRepo;
        this.utenteRepo = utenteRepo;
    }

    public List<Camera> getCamereDaPulire() {
        return cameraRepo.findByStato(StatoCamera.DA_PULIRE);
    }

    // NUOVO METODO per recuperare lo storico
    public List<StoricoPulizie> getStoricoCompleto() {
        return storicoRepo.findAll(Sort.by(Sort.Direction.DESC, "dataOra"));
    }

    @Transactional
    public void segnaCameraComePulita(Integer idCamera, Integer idStaff) {
        // --- STAMPE DI DEBUG ---
        System.out.println("DEBUG: Inizio pulizia");
        System.out.println("DEBUG: Parametro idCamera ricevuto: " + idCamera);
        System.out.println("DEBUG: Parametro idStaff ricevuto: " + idStaff);
        // -----------------------

        Camera c = cameraRepo.findById(idCamera)
                .orElseThrow(() -> new RuntimeException("Camera non trovata ID: " + idCamera));

        Utente staff = utenteRepo.findById(idStaff)
                .orElseThrow(() -> new RuntimeException("Utente staff non trovato ID: " + idStaff));

        // --- ALTRE STAMPE ---
        System.out.println("DEBUG: Trovata Camera: " + c.getNumero() + " (ID Reale: " + c.getId() + ")");
        System.out.println("DEBUG: Trovato Staff: " + staff.getNome() + " (ID Reale: " + staff.getId() + ")");
        // --------------------

        if (c.getStato() == StatoCamera.DA_PULIRE) {
            c.setStato(StatoCamera.LIBERA);
            cameraRepo.save(c);

            StoricoPulizie record = new StoricoPulizie();
            record.setCamera(c);
            record.setStaff(staff);
            record.setDataOra(LocalDateTime.now());

            // --- VERIFICA FINALE PRIMA DEL SAVE ---
            System.out.println("DEBUG: Sto salvando Storico -> CameraID: " + record.getCamera().getId() + " | StaffID: " + record.getStaff().getId());

            storicoRepo.save(record);
        } else {
            throw new RuntimeException("La camera non è in stato 'Da Pulire'.");
        }
    }


    // Serve per la dashboard staff (statistiche generali)
    public List<Camera> getTutteLeCamere() {
        return cameraRepo.findAll();
    }
}