package it.unipa.progettowsda.domain.service;

import it.unipa.progettowsda.domain.entity.*;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoCamera; // Import necessario
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione;
import it.unipa.progettowsda.domain.repository.*;
import it.unipa.progettowsda.web.form.OspiteForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepo;
    private final CameraRepository cameraRepo;
    private final UtenteRepository utenteRepo;
    private final ServizioRepository servizioRepo;
    private final PrenotazioneServizioRepository prenServizioRepo;
    private final OspiteRepository ospiteRepo;
    private final StrutturaRepository strutturaRepo;

    private static final BigDecimal TASSA_SOGGIORNO = new BigDecimal("2.00");

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepo,
            CameraRepository cameraRepo,
            UtenteRepository utenteRepo,
            ServizioRepository servizioRepo,
            PrenotazioneServizioRepository prenServizioRepo,
            OspiteRepository ospiteRepo,
            StrutturaRepository strutturaRepo) {
        this.prenotazioneRepo = prenotazioneRepo;
        this.cameraRepo = cameraRepo;
        this.utenteRepo = utenteRepo;
        this.servizioRepo = servizioRepo;
        this.prenServizioRepo = prenServizioRepo;
        this.ospiteRepo = ospiteRepo;
        this.strutturaRepo = strutturaRepo;
    }

    // --- METODI UTILITY / CLIENTI ---

    @Transactional
    public void aggiungiNota(Integer idPrenotazione, String nuovaNota) {
        Prenotazione p = prenotazioneRepo.findById(idPrenotazione)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        String noteAttuali = p.getNoteCliente() == null ? "" : p.getNoteCliente();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        p.setNoteCliente(noteAttuali + "[" + timestamp + "] " + nuovaNota + "\n");
        prenotazioneRepo.save(p);
    }

    public List<Prenotazione> getPrenotazioneUtente(Integer idUtente) {
        return prenotazioneRepo.findByUtenteId(idUtente);
    }

    public Prenotazione findById(Integer id) {
        return prenotazioneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
    }

    public List<Camera> cercaCamereLibere(String citta, LocalDate in, LocalDate out, int ospiti) {
        // La query SQL gestisce la logica temporale, ignorando lo stato fisico attuale
        return cameraRepo.findCamereDisponibili(citta, ospiti, in, out);
    }

    public List<Camera> cercaCamereLibereInStruttura(Integer idStruttura, LocalDate in, LocalDate out, int ospiti) {
        Struttura s = strutturaRepo.findById(idStruttura)
                .orElseThrow(() -> new RuntimeException("Struttura non trovata"));
        List<Camera> tutte = cameraRepo.findCamereDisponibili(s.getCitta(), ospiti, in, out);
        return tutte.stream()
                .filter(c -> c.getStruttura().getId().equals(idStruttura))
                .toList();
    }

    // --- METODO CREA PRENOTAZIONE ---
    @Transactional
    public Prenotazione creaPrenotazione(Integer idUtente, Integer idCamera, LocalDate checkin, LocalDate checkout,
            int ospitiTotali, int ospitiEsentiDichiarati, Map<Long, Integer> serviziMap) {
        Utente utente = utenteRepo.findById(idUtente).orElseThrow();
        Camera camera = cameraRepo.findById(idCamera).orElseThrow();

        // NOTA: Non cambiamo lo stato della camera qui.
        // La camera potrebbe essere OCCUPATA da qualcun altro oggi, ma libera per le
        // date future di questa prenotazione.

        long notti = ChronoUnit.DAYS.between(checkin, checkout);
        if (notti < 1)
            notti = 1;

        BigDecimal totale = camera.getPrezzoBase().multiply(BigDecimal.valueOf(notti));
        int paganti = Math.max(0, ospitiTotali - ospitiEsentiDichiarati);
        BigDecimal costoTassa = TASSA_SOGGIORNO.multiply(BigDecimal.valueOf(paganti))
                .multiply(BigDecimal.valueOf(notti));
        totale = totale.add(costoTassa);

        Prenotazione p = new Prenotazione();
        p.setUtente(utente);
        p.setCamera(camera);
        p.setDataCheckin(checkin);
        p.setDataCheckout(checkout);
        p.setNumOspiti(ospitiTotali);
        p.setNumOspitiEsentiDichiarati(ospitiEsentiDichiarati);
        p.setStato(StatoPrenotazione.CONFERMATA);

        List<PrenotazioneServizio> listaServizi = new ArrayList<>();
        if (serviziMap != null) {
            for (Map.Entry<Long, Integer> entry : serviziMap.entrySet()) {
                Servizio serv = servizioRepo.findById(Math.toIntExact(entry.getKey())).orElseThrow();
                BigDecimal costo = serv.getPrezzo().multiply(BigDecimal.valueOf(entry.getValue()));
                totale = totale.add(costo);

                PrenotazioneServizio ps = new PrenotazioneServizio();
                ps.setPrenotazione(p);
                ps.setServizio(serv);
                ps.setQuantita(entry.getValue());
                ps.setPrezzoPagato(serv.getPrezzo());
                ps.setDataAcquisto(LocalDateTime.now());
                listaServizi.add(ps);
            }
        }
        p.setPrezzoPagato(totale);
        p = prenotazioneRepo.save(p);
        prenServizioRepo.saveAll(listaServizi);
        return p;
    }

    // --- METODO CHECK-IN ---
    @Transactional
    public void eseguiCheckIn(Integer idPrenotazione, List<OspiteForm> listaOspiti) {
        Prenotazione p = prenotazioneRepo.findById(idPrenotazione)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata con ID: " + idPrenotazione));

        LocalDate oggi = LocalDate.now();
        int contatoreEsentiReali = 0;

        for (OspiteForm form : listaOspiti) {
            Ospite o = new Ospite();
            o.setPrenotazione(p);
            o.setNome(form.getNome());
            o.setCognome(form.getCognome());
            o.setDataNascita(form.getDataNascita());
            o.setLuogoNascita(form.getLuogoNascita());
            o.setCittadinanza(form.getCittadinanza());
            if (form.getTipoDoc() != null) {
                o.setTipoDoc(form.getTipoDoc());
                o.setNumeroDoc(form.getNumeroDoc());
            }

            if (form.getDataNascita() != null) {
                int eta = Period.between(form.getDataNascita(), oggi).getYears();
                if (eta < 12 || eta > 85) {
                    o.setEsenteTassa(true);
                    contatoreEsentiReali++;
                } else {
                    o.setEsenteTassa(false);
                }
            } else {
                o.setEsenteTassa(false);
            }
            ospiteRepo.save(o);
        }

        if (contatoreEsentiReali != p.getNumOspitiEsentiDichiarati()) {
            long notti = Math.max(1, ChronoUnit.DAYS.between(p.getDataCheckin(), p.getDataCheckout()));
            int pagantiDichiarati = Math.max(0, p.getNumOspiti() - p.getNumOspitiEsentiDichiarati());
            BigDecimal tassaVecchia = TASSA_SOGGIORNO.multiply(BigDecimal.valueOf(pagantiDichiarati))
                    .multiply(BigDecimal.valueOf(notti));

            int pagantiReali = Math.max(0, p.getNumOspiti() - contatoreEsentiReali);
            BigDecimal tassaNuova = TASSA_SOGGIORNO.multiply(BigDecimal.valueOf(pagantiReali))
                    .multiply(BigDecimal.valueOf(notti));

            p.setPrezzoPagato(p.getPrezzoPagato().subtract(tassaVecchia).add(tassaNuova));
            p.setNumOspitiEsentiDichiarati(contatoreEsentiReali);
        }

        p.setStato(StatoPrenotazione.IN_CORSO);

        // MODIFICA: Ora che l'ospite è entrato fisicamente, la camera diventa OCCUPATA
        Camera c = p.getCamera();
        c.setStato(StatoCamera.OCCUPATA);
        cameraRepo.save(c);

        prenotazioneRepo.save(p);
    }

    // --- METODI DI GESTIONE ---
    @Transactional
    public void eseguiCheckOut(Integer id) {
        Prenotazione p = prenotazioneRepo.findById(id).orElseThrow();
        p.setStato(StatoPrenotazione.TERMINATA);

        // MODIFICA: L'ospite se ne va, la camera deve essere pulita
        Camera c = p.getCamera();
        c.setStato(StatoCamera.DA_PULIRE);
        cameraRepo.save(c);

        prenotazioneRepo.save(p);
    }

    @Transactional
    public void cancellaPrenotazione(Integer id) {
        Prenotazione p = prenotazioneRepo.findById(id).orElseThrow();
        if (p.getStato() == StatoPrenotazione.TERMINATA)
            throw new RuntimeException("Già terminata");

        // MODIFICA IMPORTANTE:
        // Se la prenotazione è CONFERMATA (futura), non tocchiamo la camera.
        // Se fosse IN_CORSO (cioè l'ospite è già dentro), allora sì, dovremmo liberarla
        // o metterla da pulire.

        if (p.getStato() == StatoPrenotazione.IN_CORSO) {
            // Se stiamo cancellando una prenotazione mentre l'ospite è dentro (es. Cacciato
            // via)
            Camera c = p.getCamera();
            c.setStato(StatoCamera.DA_PULIRE);
            cameraRepo.save(c);
        }

        // Se è CONFERMATA (non ancora arrivato), non facciamo nulla sulla Camera.
        // La disponibilità temporale si libera automaticamente cambiando lo stato della
        // prenotazione.

        p.setStato(StatoPrenotazione.CANCELLATA);
        prenotazioneRepo.save(p);
    }

    @Transactional
    public void annullaPrenotazioneLatoCliente(Integer idPrenotazione, Integer idUtenteConnesso) {
        Prenotazione p = prenotazioneRepo.findById(idPrenotazione)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        if (!p.getUtente().getId().equals(idUtenteConnesso)) {
            throw new RuntimeException("Non hai i permessi per cancellare questa prenotazione.");
        }

        if (p.getStato() != StatoPrenotazione.CONFERMATA) {
            throw new RuntimeException("Non è possibile cancellare una prenotazione già in corso o terminata.");
        }

        // Usiamo il metodo admin. Essendo stato CONFERMATA, la camera non verrà toccata
        // fisicamente,
        // ma la prenotazione diventerà CANCELLATA, liberando lo slot temporale per le
        // ricerche.
        cancellaPrenotazione(idPrenotazione);
    }

    // --- QUERY REPORT E DASHBOARD ---
    public List<Prenotazione> getArriviDiOggi() {
        return prenotazioneRepo.findByDataCheckinAndStato(LocalDate.now(), StatoPrenotazione.CONFERMATA);
    }

    public List<Prenotazione> getPartenzeDiOggi() {
        return prenotazioneRepo.findByDataCheckoutAndStato(LocalDate.now(), StatoPrenotazione.IN_CORSO);
    }

    public List<Prenotazione> getOspitiInCasa() {
        return prenotazioneRepo.findByStato(StatoPrenotazione.IN_CORSO);
    }

    public List<Prenotazione> getTutteLePrenotazioni() {
        return prenotazioneRepo.findAll(org.springframework.data.domain.Sort
                .by(org.springframework.data.domain.Sort.Direction.DESC, "dataCheckin"));
    }

    // --- GENERAZIONE REPORT XML QUESTURA ---
    public String generaReportQuestura() {
        LocalDate oggi = LocalDate.now();
        List<Prenotazione> tutte = prenotazioneRepo.findAll();

        List<Prenotazione> lista = tutte.stream()
                .filter(p -> p.getStato() != StatoPrenotazione.CANCELLATA)
                .filter(p -> !p.getDataCheckin().isAfter(oggi) && !p.getDataCheckout().isBefore(oggi))
                .collect(Collectors.toList());

        Map<String, List<Prenotazione>> mappaStrutture = lista.stream()
                .collect(Collectors.groupingBy(p -> p.getCamera().getStruttura().getNome()));

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<ReportQuestura>\n");
        xml.append("  <DataReport>").append(oggi).append("</DataReport>\n");

        for (Map.Entry<String, List<Prenotazione>> entry : mappaStrutture.entrySet()) {
            String nomeStruttura = entry.getKey();
            List<Prenotazione> prenotazioniStruttura = entry.getValue();

            xml.append("  <Struttura>").append(nomeStruttura).append("</Struttura>\n");
            xml.append("  <Ospiti>\n");

            for (Prenotazione p : prenotazioniStruttura) {
                List<Ospite> ospiti = p.getOspiti();
                if (ospiti != null && !ospiti.isEmpty()) {
                    xml.append("    <Gruppo>\n");

                    // Capogruppo (primo ospite)
                    Ospite capogruppo = ospiti.get(0);
                    xml.append("      <Capogruppo>\n");
                    xml.append("        <Nome>").append(capogruppo.getNome()).append("</Nome>\n");
                    xml.append("        <Cognome>").append(capogruppo.getCognome()).append("</Cognome>\n");
                    xml.append("        <Cittadinanza>")
                            .append(capogruppo.getCittadinanza() != null ? capogruppo.getCittadinanza() : "N.D.")
                            .append("</Cittadinanza>\n");
                    xml.append("        <LuogoNascita>")
                            .append(capogruppo.getLuogoNascita() != null ? capogruppo.getLuogoNascita() : "N.D.")
                            .append("</LuogoNascita>\n");
                    xml.append("        <DataNascita>").append(capogruppo.getDataNascita()).append("</DataNascita>\n");
                    xml.append("        <TipoDocumento>")
                            .append(capogruppo.getTipoDoc() != null ? capogruppo.getTipoDoc() : "N.D.")
                            .append("</TipoDocumento>\n");
                    xml.append("        <NumeroDocumento>")
                            .append(capogruppo.getNumeroDoc() != null ? capogruppo.getNumeroDoc() : "N.D.")
                            .append("</NumeroDocumento>\n");
                    xml.append("      </Capogruppo>\n");

                    // Altri ospiti
                    if (ospiti.size() > 1) {
                        xml.append("      <AltriOspiti>\n");
                        for (int i = 1; i < ospiti.size(); i++) {
                            Ospite o = ospiti.get(i);
                            xml.append("        <Ospite>\n");
                            xml.append("          <Nome>").append(o.getNome()).append("</Nome>\n");
                            xml.append("          <Cognome>").append(o.getCognome()).append("</Cognome>\n");
                            xml.append("          <Cittadinanza>")
                                    .append(o.getCittadinanza() != null ? o.getCittadinanza() : "N.D.")
                                    .append("</Cittadinanza>\n");
                            xml.append("          <LuogoNascita>")
                                    .append(o.getLuogoNascita() != null ? o.getLuogoNascita() : "N.D.")
                                    .append("</LuogoNascita>\n");
                            xml.append("          <DataNascita>").append(o.getDataNascita()).append("</DataNascita>\n");
                            xml.append("        </Ospite>\n");
                        }
                        xml.append("      </AltriOspiti>\n");
                    }

                    xml.append("    </Gruppo>\n");
                }
            }
            xml.append("  </Ospiti>\n");
        }

        xml.append("</ReportQuestura>");
        return xml.toString();
    }

    // --- GENERAZIONE REPORT XML TASSA SOGGIORNO ---
    public String generaReportTassaSoggiorno() {
        LocalDate oggi = LocalDate.now();
        List<Prenotazione> tutte = prenotazioneRepo.findAll();

        List<Prenotazione> lista = tutte.stream()
                .filter(p -> p.getStato() != StatoPrenotazione.CANCELLATA)
                .filter(p -> !p.getDataCheckin().isAfter(oggi) && !p.getDataCheckout().isBefore(oggi))
                .collect(Collectors.toList());

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<ReportTassaSoggiorno>\n");
        xml.append("  <DataReport>").append(oggi).append("</DataReport>\n");
        xml.append("  <Versamenti>\n");

        for (Prenotazione p : lista) {
            xml.append("    <Pernottamento>\n");

            // Nome e cognome del capogruppo
            String capogruppoNome = (p.getUtente() != null)
                    ? p.getUtente().getNome() + " " + p.getUtente().getCognome()
                    : "N.D.";
            xml.append("      <Capogruppo>").append(capogruppoNome).append("</Capogruppo>\n");
            xml.append("      <TotaleOspiti>").append(p.getNumOspiti()).append("</TotaleOspiti>\n");

            // Esenzioni raggruppate per tipo
            List<Ospite> ospiti = p.getOspiti();
            Map<String, Integer> esenzioniPerTipo = new java.util.HashMap<>();

            if (ospiti != null) {
                for (Ospite o : ospiti) {
                    if (Boolean.TRUE.equals(o.getEsenteTassa())) {
                        String tipoEsenzione = "Altro";
                        if (o.getDataNascita() != null) {
                            int eta = Period.between(o.getDataNascita(), oggi).getYears();
                            if (eta < 12)
                                tipoEsenzione = "Minore 12 anni";
                            else if (eta > 85)
                                tipoEsenzione = "Over 85";
                        }
                        esenzioniPerTipo.merge(tipoEsenzione, 1, Integer::sum);
                    }
                }
            }

            if (!esenzioniPerTipo.isEmpty()) {
                xml.append("      <Esenzioni>\n");
                for (Map.Entry<String, Integer> e : esenzioniPerTipo.entrySet()) {
                    xml.append("        <Esenzione>\n");
                    xml.append("          <Tipo>").append(e.getKey()).append("</Tipo>\n");
                    xml.append("          <Quantita>").append(e.getValue()).append("</Quantita>\n");
                    xml.append("        </Esenzione>\n");
                }
                xml.append("      </Esenzioni>\n");
            }

            xml.append("    </Pernottamento>\n");
        }

        xml.append("  </Versamenti>\n");
        xml.append("</ReportTassaSoggiorno>");
        return xml.toString();
    }
}