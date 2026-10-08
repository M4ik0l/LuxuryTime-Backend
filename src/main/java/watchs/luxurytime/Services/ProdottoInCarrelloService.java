package watchs.luxurytime.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Entities.ProdottoNelCarrello;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Repositories.ProdottoNelCarrelloRepository;
import watchs.luxurytime.Repositories.ProdottoRepository;
import watchs.luxurytime.Repositories.UtenteRepository;

import java.util.List;

@Service
public class ProdottoNelCarrelloService {

    private ProdottoNelCarrelloRepository prodottoNelCarrelloRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<ProdottoNelCarrello> getAllProdottiNelCarrello(){
        return prodottoNelCarrelloRepository.findAll();
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public ProdottoNelCarrello getProdottoInCarrello(int id){
        return prodottoNelCarrelloRepository.findById(id);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void eliminaCarrelloUtente(Utente u) throws UtenteInesistenteException {
        //verifico esistenza utente
        if(!utenteRepository.existsByEmail(u.getEmail())){
            throw new UtenteInesistenteException();
        }

        // prendo il carrello corrente dell'utente
        List<ProdottoNelCarrello> carrello = getCarrello(u.getEmail());

        //elimino i prodotti nel carrello dell'utente
        for(ProdottoNelCarrello prodotto : carrello){
            prodottoNelCarrelloRepository.delete(prodotto);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<ProdottoNelCarrello> getCarrello(String email) throws UtenteInesistenteException {
        if(! utenteRepository.existsByEmail(email))
            throw new UtenteInesistenteException();
        Utente utente = utenteRepository.findByEmail(email);
        return  utente.getCarrello();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void rimuoviProdottoInCarrello(ProdottoNelCarrello pic){
        prodottoNelCarrelloRepository.delete(pic);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void diminuisciQuantitaProdottoInCarrello(String email, ProdottoInCarrello pic) throws UtenteInesistenteException, ProdottoInesistenteException {
        if(!utenteRepository.existsByEmail(email)){
            throw new UtenteInesistenteException();
        }
        if(!prodottoNelCarrelloRepository.existsById(pic.getId())){
            throw new ProdottoInesistenteException();
        }
        List<ProdottoNelCarrello> carrello = getCarrello(email);
        for(ProdottoNelCarrello p : carrello){
            if(p.getId() == pic.getId()){
                if(p.getQuantita()==1)
                    rimuoviProdottoInCarrello(p);
                else
                    p.setQuantita(p.getQuantita()-1);
            }
        }
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED)
    public void add(String email, Prodotto prod, int quantita) throws UtenteInesistenteException, ProdottoInesistenteException, ProdottoEsauritoException {
        if( email==null || !utenteRepository.existsByEmail(email))
            throw new UtenteInesistenteException();
        if(!prodottoRepository.existsById(prod.getId()))
            throw new ProdottoInesistenteException();

        ProdottoInCarrello pic;

        //prima controllo se il prodotto è presente nel carrello dell'utente
        Utente u = utenteRepository.findByEmail(email);
        if(prodottoNelCarrelloRepository.findByUtenteAndProdottoAndOrdine(u,prod,null)!=null){
            pic = prodottoNelCarrelloRepository.findByUtenteAndProdottoAndOrdine(u,prod,null);
            if( pic.getQuantita()+quantita>prod.getQuantita())
                throw new ProdottoEsauritoException();
            pic.setQuantita(pic.getQuantita()+quantita);
        }
        //questa parte gestisce il caso in cui il prodotto non è presente nel carrello
        else{
            if(quantita>prod.getQuantita())
                throw new ProdottoEsauritoException();
            pic = new ProdottoNelCarrello(prod, quantita, prod.getPrezzo(),u);
            prodottoNelCarrelloRepository.save(pic);
        }

    }

}
