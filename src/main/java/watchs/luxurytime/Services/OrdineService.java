package watchs.luxurytime.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.*;
import watchs.luxurytime.Repositories.*;
import watchs.luxurytime.Support.exceptions.*;

import java.util.Date;
import java.util.List;

@Service
public class OrdineService {

    private final OrdineRepository ordineRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;
    private final IndirizzoRepository indirizzoRepository;
    private final ProdottoInCarrelloRepository prodottoInCarrelloRepository;

    @Autowired
    public OrdineService(OrdineRepository ordineRepository, UtenteRepository utenteRepository, ProdottoRepository prodottoRepository, IndirizzoRepository indirizzoRepository, ProdottoInCarrelloRepository prodottoInCarrelloRepository){
        this.ordineRepository = ordineRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
        this.indirizzoRepository = indirizzoRepository;
        this.prodottoInCarrelloRepository = prodottoInCarrelloRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Ordine> getOrdiniUtente(String email) throws UtenteInesistenteException {
        Utente utente = utenteRepository.findByEmail(email).orElseThrow(UtenteInesistenteException::new);
        List<Ordine> ordini = ordineRepository.findByAcquirente(utente);
        for (Ordine ordine : ordini) {
            ordine.getCarrello().size();
        }
        return ordini;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void eliminaOrdine(int id) throws OrdineInesistenteException {
        if(!ordineRepository.existsById(id)){
            throw new OrdineInesistenteException();
        }
        Ordine o = ordineRepository.findById(id);
        if(o.getCarrello()!= null){
            prodottoInCarrelloRepository.deleteAll(o.getCarrello());
        }
        ordineRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Ordine aggiornaOrdine(Ordine ordine) throws OrdineInesistenteException {
        if(!ordineRepository.existsById(ordine.getId())){
            throw new OrdineInesistenteException();
        }
        return ordineRepository.save(ordine);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Ordine> ricercaData(Date data){
        return ordineRepository.findByData(data);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Ordine> ricercaPerPeriodoUtente(Date inizio, Date fine, Utente utente){
        return ordineRepository.findInPeriodAcquirente(inizio, fine, utente);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Ordine> findAll(){
        return ordineRepository.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public Ordine effettuaOrdine(String email, Indirizzo indirizzoSpedizione, Integer quantitaAttesa)
            throws CarrelloVuotoException, UtenteInesistenteException, TelefonoMancanteException, PrezzoCambiatoException {
        Utente u = utenteRepository.findByEmail(email).orElseThrow(UtenteInesistenteException::new);

        if (u.getNumeroTelefonico() == null || u.getNumeroTelefonico().isBlank()) {
            throw new TelefonoMancanteException();
        }

        // Prendiamo solo i prodotti attualmente nel carrello
        List<ProdottoInCarrello> carrelloAttuale = prodottoInCarrelloRepository.findByUtenteAndOrdineIsNull(u);
        if (carrelloAttuale.isEmpty())
            throw new CarrelloVuotoException();

        // CONTROLLO ANTI-RACE CONDITION (Due browser aperti o click simultanei Elimina + Acquista)
        if (quantitaAttesa != null) {
            int quantitaRealeNelDb = carrelloAttuale.stream().mapToInt(ProdottoInCarrello::getQuantita).sum();
            if (quantitaRealeNelDb != quantitaAttesa) {
                throw new PrezzoCambiatoException();
            }
        }

        indirizzoSpedizione.setUtente(u);
        List<Indirizzo> esistenti = indirizzoRepository.findAllByUtente(u);
        Indirizzo spedizioneDaUsare = null;
        if (esistenti != null) {
            for (Indirizzo esistente : esistenti) {
                if (esistente.equals(indirizzoSpedizione)) {
                    spedizioneDaUsare = esistente;
                    break;
                }
            }
        }
        if (spedizioneDaUsare == null) {
            spedizioneDaUsare = indirizzoRepository.save(indirizzoSpedizione);
        }

        // Creo l'ordine
        Ordine ordine = new Ordine();
        ordine.setData(new Date());
        ordine.setAcquirente(u);
        ordine.setSpedizione(spedizioneDaUsare);
        ordine.setStato("In Lavorazione");

        for (ProdottoInCarrello pic : carrelloAttuale) {
            pic.setOrdine(ordine);
        }
        ordine.setCarrello(carrelloAttuale);
        return ordineRepository.save(ordine);
    }

    @Transactional
    public Ordine aggiornaStato(int id, String nuovoStato) throws OrdineInesistenteException {
        Ordine ordine = ordineRepository.findById(id);
        if(ordine == null) {
            throw new OrdineInesistenteException();
        }
        ordine.setStato(nuovoStato);
        return ordineRepository.save(ordine);
    }
}