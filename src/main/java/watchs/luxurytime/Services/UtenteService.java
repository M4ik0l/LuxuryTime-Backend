package watchs.luxurytime.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.Indirizzo;
import watchs.luxurytime.Entities.Ordine;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Repositories.IndirizzoRepository;
import watchs.luxurytime.Repositories.UtenteRepository;
import watchs.luxurytime.Support.exceptions.OrdineInesistenteException;
import watchs.luxurytime.Support.exceptions.UtenteEsistenteException;
import watchs.luxurytime.Support.exceptions.UtenteInesistenteException;

import java.util.List;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final IndirizzoRepository indirizzoRepository;
    private final ProdottoInCarrelloService prodottoInCarrelloService;
    private final OrdineService ordineService;

    public UtenteService(UtenteRepository utenteRepository, IndirizzoRepository indirizzoRepository, ProdottoInCarrelloService prodottoInCarrelloService, OrdineService ordineService) {
        this.utenteRepository = utenteRepository;
        this.indirizzoRepository = indirizzoRepository;
        this.prodottoInCarrelloService = prodottoInCarrelloService;
        this.ordineService = ordineService;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Utente registraUtente(Utente u) throws UtenteEsistenteException {
        if (utenteRepository.existsByEmail(u.getEmail())){
            throw new UtenteEsistenteException();
        }
        return utenteRepository.save(u);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Utente> ricercaAvanzata(String nome, String cognome, String email) {
        if (nome == null && cognome == null && email == null) {
            throw new IllegalArgumentException("Almeno un parametro di ricerca è richiesto");
        }
        return utenteRepository.ricercaUtente(nome, cognome, email);
    }

    @Transactional
    public Utente aggiornaTelefono(int idUtente, String nuovoTelefono) throws UtenteInesistenteException {
        Utente utente = utenteRepository.findById(idUtente).orElseThrow(() -> new UtenteInesistenteException("Impossibile trovare l'utente con ID: " + idUtente));
        utente.setNumeroTelefonico(nuovoTelefono);
        return utenteRepository.save(utente);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void eliminaUtente(int id) throws UtenteInesistenteException, OrdineInesistenteException {
        Utente u = utenteRepository.findById(id).orElseThrow(UtenteInesistenteException::new);
        //elimino i prodotti contenuti nel carrello dell'utente
        prodottoInCarrelloService.eliminaCarrelloUtente(u);
        //elimino gli ordini effettuati dall'utente
        List<Ordine> ordini = u.getStoricoOrdini();
        if (ordini != null) {
            for(Ordine o : ordini){
                ordineService.eliminaOrdine(o.getId());
            }
        }
        //elimino gli indirizzi dell'utente
        List<Indirizzo> indirizzi = indirizzoRepository.findAllByUtente(u);
        indirizzoRepository.deleteAll(indirizzi);
        //elimino l'utente
        utenteRepository.delete(u);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Utente aggiornaUtente(Utente datiAggiornati) throws UtenteInesistenteException {
        // Cerco l'utente nel DB tramite l'ID
        Utente u = utenteRepository.findById(datiAggiornati.getId()).orElseThrow(() -> new UtenteInesistenteException("Impossibile trovare l'utente con ID: " + datiAggiornati.getId()));
        // Aggiorno i campi
        u.setNome(datiAggiornati.getNome());
        u.setCognome(datiAggiornati.getCognome());
        u.setEmail(datiAggiornati.getEmail());
        u.setNumeroTelefonico(datiAggiornati.getNumeroTelefonico());
        // Salvo e restituisco l'utente aggiornato
        return utenteRepository.save(u);
    }

    public List<Utente> findAll() {
        return utenteRepository.findAll();
    }

    public List<Indirizzo> getIndirizziUtente(String email) throws UtenteInesistenteException {
        Utente u = utenteRepository.findByEmail(email).orElseThrow(UtenteInesistenteException::new);
        return indirizzoRepository.findAllByUtente(u);
    }
}