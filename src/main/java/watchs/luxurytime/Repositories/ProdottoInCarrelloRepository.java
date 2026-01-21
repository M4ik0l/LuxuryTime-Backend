package watchs.luxurytime.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import watchs.luxurytime.Entities.Ordine;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Entities.ProdottoInCarrello;
import watchs.luxurytime.Entities.Utente;
import java.util.List;

@Repository
public interface ProdottoInCarrelloRepository extends JpaRepository<ProdottoInCarrello, Integer> {

    ProdottoInCarrello findById(int id);

    ProdottoInCarrello findByUtenteAndProdottoAndOrdine(Utente u, Prodotto p, Ordine o);

    List<ProdottoInCarrello> findByUtenteAndOrdineIsNull(Utente utente);

}
