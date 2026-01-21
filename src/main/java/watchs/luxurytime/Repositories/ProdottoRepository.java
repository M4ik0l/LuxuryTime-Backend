package watchs.luxurytime.Repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import watchs.luxurytime.Entities.Prodotto;

public interface ProdottoRepository extends JpaRepository<Prodotto, Integer>, JpaSpecificationExecutor<Prodotto> {

    Prodotto findById(int id);

    boolean existsByNome(String nome);

    boolean existsByMarcaAndModelloAndRiferimento(String marca, String modello, String riferimento);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prodotto p WHERE p.id = :id")
    Prodotto findByIdWithLock(@Param("id") int id);

}

