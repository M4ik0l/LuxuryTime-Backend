package watchs.luxurytime.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import watchs.luxurytime.Entities.Indirizzo;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Repositories.IndirizzoRepository;
import watchs.luxurytime.Repositories.UtenteRepository;
import watchs.luxurytime.Support.exceptions.IndirizzoEsistenteException;
import watchs.luxurytime.Support.exceptions.IndirizzoInesistenteException;
import watchs.luxurytime.Support.exceptions.UtenteInesistenteException;

import java.util.List;

@Service
public class IndirizzoService {

    private final IndirizzoRepository indirizzoRepository;
    private final UtenteRepository utenteRepository;

    public IndirizzoService(IndirizzoRepository indirizzoRepository, UtenteRepository utenteRepository) {
        this.indirizzoRepository = indirizzoRepository;
        this.utenteRepository = utenteRepository;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Indirizzo> getAllIndirizziUtente(String email) throws UtenteInesistenteException, IndirizzoInesistenteException {
        Utente u = utenteRepository.findByEmail(email).orElseThrow(UtenteInesistenteException::new);
        List<Indirizzo> indirizzi= indirizzoRepository.findAllByUtenteAndAttivoTrue(u);
        if(indirizzi.isEmpty())
            throw new IndirizzoInesistenteException();
        return indirizzi;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public List<Indirizzo> getIndirizzo(String citta, String via, int numeroC) throws IndirizzoInesistenteException {
        if(!indirizzoRepository.existsByCittaAndViaAndNumeroCivicoAndAttivoTrue(citta,via,numeroC))
            throw new IndirizzoInesistenteException();
        return indirizzoRepository.findByCittaAndViaAndNumeroCivico(citta,via,numeroC);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Indirizzo registraIndirizzo(Indirizzo indirizzo, int idUtente) throws UtenteInesistenteException, IndirizzoEsistenteException {
        Utente u = utenteRepository.findById(idUtente).orElseThrow(UtenteInesistenteException::new);
        // Verifico l'unicità dell'indirizzo
        if(indirizzoRepository.existsByCapAndViaAndNumeroCivico(indirizzo.getCap(), indirizzo.getVia(), indirizzo.getNumeroCivico())){
            List<Indirizzo> esistenti = indirizzoRepository.findByCittaAndViaAndNumeroCivico(indirizzo.getCitta(), indirizzo.getVia(), indirizzo.getNumeroCivico());
            for(Indirizzo i: esistenti){
                if(i.getUtente().equals(u) && i.isAttivo())
                    throw new IndirizzoEsistenteException();
                else if (i.getUtente().equals(u)){
                    i.setAttivo(true);
                    indirizzoRepository.save(i);
                    return i;
                }
            }
        }
        indirizzo.setUtente(u);
        indirizzo.setAttivo(true);
        // Salvo il nuovo indirizzo
        return indirizzoRepository.save(indirizzo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Indirizzo aggiornaIndirizzo(Indirizzo indirizzoAggiornato) throws IndirizzoInesistenteException{
        if(!indirizzoRepository.existsById(indirizzoAggiornato.getId()))
            throw new IndirizzoInesistenteException();
        Indirizzo nuovo= indirizzoRepository.findById(indirizzoAggiornato.getId());
        nuovo.setCap(indirizzoAggiornato.getCap());
        nuovo.setCitta(indirizzoAggiornato.getCitta());
        nuovo.setVia(indirizzoAggiornato.getVia());
        nuovo.setNumeroCivico(indirizzoAggiornato.getNumeroCivico());
        return indirizzoRepository.save(nuovo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void eliminaIndirizzo(int id) throws IndirizzoInesistenteException{
        if(!indirizzoRepository.existsById(id))
            throw new IndirizzoInesistenteException();
        Indirizzo i = indirizzoRepository.findById(id);
        i.setAttivo(false);
        indirizzoRepository.save(i);
    }
}