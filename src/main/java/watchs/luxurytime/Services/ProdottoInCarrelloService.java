package watchs.luxurytime.Services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Entities.ProdottoInCarrello;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Repositories.ProdottoInCarrelloRepository;
import watchs.luxurytime.Repositories.ProdottoRepository;
import watchs.luxurytime.Repositories.UtenteRepository;
import watchs.luxurytime.Support.exceptions.ProdottoEsauritoException;
import watchs.luxurytime.Support.exceptions.ProdottoInesistenteException;
import watchs.luxurytime.Support.exceptions.UtenteInesistenteException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdottoInCarrelloService {

    private final ProdottoInCarrelloRepository prodottoInCarrelloRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    private static final int MINUTI_SCADENZA_CARRELLO = 10;

    public ProdottoInCarrelloService(ProdottoInCarrelloRepository prodottoInCarrelloRepository, UtenteRepository utenteRepository, ProdottoRepository prodottoRepository){
        this.prodottoInCarrelloRepository = prodottoInCarrelloRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<ProdottoInCarrello> getAllProdottiInCarrello(){
        return prodottoInCarrelloRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public ProdottoInCarrello getProdottoInCarrello(int id){
        return prodottoInCarrelloRepository.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void eliminaCarrelloUtente(Utente u) throws UtenteInesistenteException {
        List<ProdottoInCarrello> carrello = getCarrello(u.getEmail());
        prodottoInCarrelloRepository.deleteAll(carrello);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<ProdottoInCarrello> getCarrello(String email) throws UtenteInesistenteException {
        Utente utente = utenteRepository.findByEmail(email).orElseThrow(UtenteInesistenteException::new);
        List<ProdottoInCarrello> carrello = prodottoInCarrelloRepository.findByUtenteAndOrdineIsNull(utente);
        boolean carrelloModificato = false;

        for (ProdottoInCarrello pic : carrello) {
            Prodotto prod = pic.getProdotto();
            java.math.BigDecimal prezzoReale;
            if (prod.getPrezzoScontato() != null && prod.getPrezzoScontato().compareTo(java.math.BigDecimal.ZERO) > 0) {
                prezzoReale = prod.getPrezzoScontato();
            } else {
                prezzoReale = prod.getPrezzo();
            }
            if (pic.getPrezzo().compareTo(prezzoReale) != 0) {
                pic.setPrezzo(prezzoReale);
                carrelloModificato = true;
            }
        }
        if (carrelloModificato) {
            prodottoInCarrelloRepository.saveAll(carrello);
        }

        return carrello;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void rimuoviProdottoInCarrello(int idCarrello, String emailDalToken) {
        ProdottoInCarrello pic = prodottoInCarrelloRepository.findById(idCarrello);
        if (pic != null) {
            // Controllo di sicurezza: verifico che il carrello appartenga davvero all'utente del Token JWT
            if (pic.getUtente() == null || !pic.getUtente().getEmail().equalsIgnoreCase(emailDalToken)) {
                throw new AccessDeniedException("Non sei autorizzato a modificare il carrello di un altro utente.");
            }
            Prodotto prod = pic.getProdotto();
            prod.setQuantita(prod.getQuantita() + pic.getQuantita());
            prodottoRepository.save(prod);
            prodottoInCarrelloRepository.delete(pic);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void diminuisciQuantitaProdottoInCarrello(int idCarrello, String emailDalToken) {
        ProdottoInCarrello pic = prodottoInCarrelloRepository.findById(idCarrello);
        if (pic != null) {
            // Controllo di sicurezza: verifico che il carrello appartenga davvero all'utente del Token JWT
            if (pic.getUtente() == null || !pic.getUtente().getEmail().equalsIgnoreCase(emailDalToken)) {
                throw new AccessDeniedException("Non sei autorizzato a modificare il carrello di un altro utente.");
            }
            if (pic.getQuantita() <= 1) {
                rimuoviProdottoInCarrello(idCarrello, emailDalToken);
            } else {
                pic.setQuantita(pic.getQuantita() - 1);
                Prodotto prod = pic.getProdotto();
                prod.setQuantita(prod.getQuantita() + 1);
                prodottoRepository.save(prod);
                prodottoInCarrelloRepository.save(pic);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void add(String email, Prodotto prod, int quantita) throws UtenteInesistenteException, ProdottoInesistenteException, ProdottoEsauritoException {
        if (email == null)
            throw new UtenteInesistenteException();
        if (!prodottoRepository.existsById(prod.getId()))
            throw new ProdottoInesistenteException();
        Prodotto prodottoAggiornato = prodottoRepository.findById(prod.getId());
        if (prodottoAggiornato == null) {
            throw new ProdottoInesistenteException();
        }
        if (quantita > prodottoAggiornato.getQuantita()) {
            throw new ProdottoEsauritoException();
        }

        Utente u = utenteRepository.findByEmail(email).orElseThrow(UtenteInesistenteException::new);
        ProdottoInCarrello pic = prodottoInCarrelloRepository.findByUtenteAndProdottoAndOrdine(u, prodottoAggiornato, null);

        java.math.BigDecimal prezzoReale;
        if (prodottoAggiornato.getPrezzoScontato() != null && prodottoAggiornato.getPrezzoScontato().compareTo(java.math.BigDecimal.ZERO) > 0) {
            prezzoReale = prodottoAggiornato.getPrezzoScontato();
        } else {
            prezzoReale = prodottoAggiornato.getPrezzo();
        }
        if (pic != null) {
            pic.setQuantita(pic.getQuantita() + quantita);
            pic.setPrezzo(prezzoReale);
            pic.setDataAggiunta(java.time.LocalDateTime.now());
        } else {
            pic = new ProdottoInCarrello(prodottoAggiornato, quantita, prezzoReale, u);
            pic.setPrezzo(prezzoReale);
        }
        prodottoAggiornato.setQuantita(prodottoAggiornato.getQuantita() - quantita);
        prodottoRepository.save(prodottoAggiornato);
        prodottoInCarrelloRepository.save(pic);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void pulisciCarrelliScaduti() {
        LocalDateTime limiteScadenza = LocalDateTime.now().minusMinutes(MINUTI_SCADENZA_CARRELLO);
        List<ProdottoInCarrello> carrelliAttivi = prodottoInCarrelloRepository.findAll();
        int rimossi = 0;
        for (ProdottoInCarrello pic : carrelliAttivi) {
            if (pic.getOrdine() == null && pic.getDataAggiunta() != null && pic.getDataAggiunta().isBefore(limiteScadenza)) {
                Prodotto prod = pic.getProdotto();
                prod.setQuantita(prod.getQuantita() + pic.getQuantita());
                prodottoRepository.save(prod);
                prodottoInCarrelloRepository.delete(pic);
                rimossi++;
            }
        }
        if (rimossi > 0) {
            System.out.println("Cron Job: Rimosse " + rimossi + " voci scadute dai carrelli e ripristinate le quantità in inventario.");
        }
    }
}