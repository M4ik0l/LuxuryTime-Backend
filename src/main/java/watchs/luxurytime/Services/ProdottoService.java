package watchs.luxurytime.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Entities.ProdottoInCarrello;
import watchs.luxurytime.Repositories.ProdottoInCarrelloRepository;
import watchs.luxurytime.Repositories.ProdottoRepository;
import watchs.luxurytime.Repositories.UtenteRepository;
import watchs.luxurytime.Support.exceptions.ProdottoEsistenteException;
import watchs.luxurytime.Support.exceptions.ProdottoInesistenteException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoInCarrelloRepository prodottoInCarrelloRepository;

    public ProdottoService(ProdottoRepository prodottoRepository, UtenteRepository utenteRepository, ProdottoInCarrelloRepository prodottoInCarrelloRepository){
        this.prodottoRepository = prodottoRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoInCarrelloRepository = prodottoInCarrelloRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public Prodotto getProdottoById(int id) throws ProdottoInesistenteException{
        if(!prodottoRepository.existsById(id))
            throw new ProdottoInesistenteException();
        return prodottoRepository.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Prodotto registraProdotto(Prodotto p) throws ProdottoEsistenteException {
        if (prodottoRepository.existsByMarcaAndModelloAndRiferimento(p.getMarca(), p.getModello(), p.getRiferimento())){
            throw new ProdottoEsistenteException();
        }
        // Salvo semplicemente il prodotto
        return prodottoRepository.save(p);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Prodotto aggiornaProdotto(Prodotto prodAggiornato) throws ProdottoInesistenteException {
        // Ricerca per ID
        Prodotto p = prodottoRepository.findById(prodAggiornato.getId());
        if(p == null){
            throw new ProdottoInesistenteException();
        }
        // Aggiorniamo i dati base
        p.setNome(prodAggiornato.getNome());
        p.setMarca(prodAggiornato.getMarca());
        p.setModello(prodAggiornato.getModello());
        if (prodAggiornato.getPrezzo() != null) {
            p.setPrezzo(prodAggiornato.getPrezzo());
        }
        // Aggiungo il prezzo scontato
        p.setPrezzoScontato(prodAggiornato.getPrezzoScontato());
        if (prodAggiornato.getQuantita() != null) {
            p.setQuantita(prodAggiornato.getQuantita());
        }
        // Riga per le foto
        p.setImmagini(prodAggiornato.getImmagini());
        // Aggiorno la scheda tecnica
        p.setAnno(prodAggiornato.getAnno());
        p.setEdizioneLimitata(prodAggiornato.isEdizioneLimitata());
        p.setGenere(prodAggiornato.getGenere());
        p.setMovimento(prodAggiornato.getMovimento());
        p.setMaterialeCassa(prodAggiornato.getMaterialeCassa());
        p.setMaterialeCinturino(prodAggiornato.getMaterialeCinturino());
        p.setDiametroCassa(prodAggiornato.getDiametroCassa());
        p.setColoreQuadrante(prodAggiornato.getColoreQuadrante());
        p.setTipoVetro(prodAggiornato.getTipoVetro());
        p.setResistenzaAcqua(prodAggiornato.getResistenzaAcqua());
        p.setCirconferenzaPolso(prodAggiornato.getCirconferenzaPolso());
        // Dettagli extra
        p.setRiferimento(prodAggiornato.getRiferimento());
        p.setCollezione(prodAggiornato.getCollezione());
        p.setCondizione(prodAggiornato.getCondizione());
        p.setGaranzia(prodAggiornato.getGaranzia());
        p.setCertificato(prodAggiornato.getCertificato());
        p.setChiusura(prodAggiornato.getChiusura());

        return prodottoRepository.save(p);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void eliminaProdotto(int id) throws ProdottoInesistenteException {
        if(!prodottoRepository.existsById(id)){
            throw new ProdottoInesistenteException();
        }
        Prodotto prodotto = prodottoRepository.findById(id);
        // Invece di caricare tutti gli utenti del DB, uso la relazione per svuotare i carrelli che contengono questo prodotto
        List<ProdottoInCarrello> carrelliAssociati = prodotto.getProdottiNelCarrello();
        if (carrelliAssociati != null && !carrelliAssociati.isEmpty()) {
            prodottoInCarrelloRepository.deleteAll(carrelliAssociati);
        }
        // Elimino definitivamente il prodotto
        prodottoRepository.deleteById(id);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Prodotto> visualizzazionePaginata(int pageNumber, int pageSize, String ordinamento){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(ordinamento));
        Page<Prodotto> pagedResult = prodottoRepository.findAll(pageable);
        if(!pagedResult.hasContent())
            return new LinkedList<>();
        return pagedResult.getContent();
    }

    public List<Prodotto> getProdotti() {
        return prodottoRepository.findAll();
    }

    public List<Prodotto> ricercaAvanzataDinamica(Prodotto filtro) {
        return prodottoRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filtro.getMarca() != null) {
                predicates.add(cb.like(cb.lower(root.get("marca")), "%" + filtro.getMarca().toLowerCase() + "%"));
            }
            if (filtro.getModello() != null) {
                predicates.add(cb.like(cb.lower(root.get("modello")), "%" + filtro.getModello().toLowerCase() + "%"));
            }
            if (filtro.getAnno() != null) {
                predicates.add(cb.like(cb.lower(root.get("anno")), "%" + filtro.getAnno() + "%"));
            }
            if (filtro.getGenere() != null) {
                predicates.add(cb.like(cb.lower(root.get("genere")), "%" + filtro.getGenere() + "%"));
            }
            if (filtro.getMovimento() != null) {
                predicates.add(cb.like(cb.lower(root.get("movimento")), "%" + filtro.getMovimento() + "%"));
            }
            if (filtro.getMaterialeCassa() != null) {
                predicates.add(cb.like(cb.lower(root.get("materiale_cassa")), "%" + filtro.getMaterialeCassa() + "%"));
            }
            if (filtro.getMaterialeCinturino() != null) {
                predicates.add(cb.like(cb.lower(root.get("materiale_cinturino")), "%" + filtro.getMaterialeCinturino() + "%"));
            }
            if (filtro.getDiametroCassa() != null) {
                predicates.add(cb.like(cb.lower(root.get("diametro_cassa")), "%" + filtro.getDiametroCassa().toLowerCase() + "%"));
            }
            if (filtro.getResistenzaAcqua() != null) {
                predicates.add(cb.like(cb.lower(root.get("resistenza_acqua")), "%" + filtro.getResistenzaAcqua().toLowerCase() + "%"));
            }
            if (filtro.getColoreQuadrante() != null) {
                predicates.add(cb.equal(root.get("coloreQuadrante"), filtro.getColoreQuadrante()));
            }
            if (filtro.getTipoVetro() != null) {
                predicates.add(cb.equal(root.get("tipoVetro"), filtro.getTipoVetro()));
            }
            if (filtro.getCirconferenzaPolso() != null) {
                predicates.add(cb.like(cb.lower(root.get("circonferenza_polso")), "%" + filtro.getCirconferenzaPolso().toLowerCase() + "%"));
            }
            if (filtro.getCondizione() != null) {
                predicates.add(cb.like(cb.lower(root.get("condizione")), "%" + filtro.getCondizione().toLowerCase() + "%"));
            }
            if (filtro.getGaranzia() != null) {
                predicates.add(cb.like(cb.lower(root.get("garanzia")), "%" + filtro.getGaranzia().toLowerCase() + "%"));
            }
            if (filtro.getCollezione() != null) {
                predicates.add(cb.like(cb.lower(root.get("collezione")), "%" + filtro.getCollezione().toLowerCase() + "%"));
            }
            if (filtro.getCertificato() != null) {
                predicates.add(cb.like(cb.lower(root.get("certificato")), "%" + filtro.getCertificato().toLowerCase() + "%"));
            }
            if (filtro.getChiusura() != null) {
                predicates.add(cb.like(cb.lower(root.get("chiusura")), "%" + filtro.getChiusura().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void toggleVisibilita(int id) throws ProdottoInesistenteException {
        Prodotto p = prodottoRepository.findById(id);
        if(p == null){
            throw new ProdottoInesistenteException();
        }
        p.setAttivo(!p.isAttivo());
        prodottoRepository.save(p);
    }


}