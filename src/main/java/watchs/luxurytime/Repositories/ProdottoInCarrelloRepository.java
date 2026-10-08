package watchs.luxurytime.Repositories;

import org.springframework.stereotype.Repository;
import watchs.luxurytime.Entities.Prodotto;
import watchs.luxurytime.Entities.Utente;


@Repository
public interface ProdottoInCarrelloRepository extends JpaRepository<ProdottoInCarrello, Integer>  {

    @Override
    boolean existsById(int id);

    ProdottoInCarrello findById(int id);

    @Override
    void deleteById(int id);

    ProdottoInCarrello findByUtenteAndProdottoAndOrdine(Utente u, Prodotto p, Ordine o);

    ProdottoInCarrello findByProdottoAndOrdine(Prodotto p, Ordine o);


}
