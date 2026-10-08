package watchs.luxurytime.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import watchs.luxurytime.Entities.Indirizzo;
import watchs.luxurytime.Entities.Utente;

import java.util.List;

@Repository
public interface IndirizzoRepository extends JpaRepository<Indirizzo, Integer> {

    List<Indirizzo> findAllByUtenteAndAttivoTrue(Utente u);

    List<Indirizzo> findAllByUtente(Utente u);

    boolean existsByCittaAndViaAndNumeroCivicoAndAttivoTrue(String citta, String via, int numeroCivico);

    boolean existsByCapAndViaAndNumeroCivico(int cap, String via, int numeroCivico);

    List<Indirizzo> findByCittaAndViaAndNumeroCivico(String citta, String via, int numeroCivico);


    Indirizzo findById(int id);

}
