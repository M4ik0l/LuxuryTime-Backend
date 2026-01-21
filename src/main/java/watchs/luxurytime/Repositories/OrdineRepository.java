package watchs.luxurytime.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import watchs.luxurytime.Entities.Ordine;
import watchs.luxurytime.Entities.Utente;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Integer> {

    Ordine findById(int id);

    List<Ordine> findByData(Date data);

    List<Ordine> findByAcquirente(Utente acquirente);

    @Query("SELECT o "+ "FROM Ordine o "+ "WHERE o.data >= :inizio AND o.data <= :fine AND o.acquirente= :acq")
    List<Ordine> findInPeriodAcquirente(@Param(value = "inizio")Date inizio, @Param(value = "fine")Date fine, @Param(value = "acq")Utente u);

}