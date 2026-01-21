package watchs.luxurytime.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import watchs.luxurytime.Entities.Utente;

import java.util.List;
import java.util.Optional; // <-- Import fondamentale

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Integer> {


    boolean existsByEmail(String email);

    Optional<Utente> findByEmail(String email);

    @Query("SELECT u FROM Utente u WHERE " +
            "(:nome IS NULL OR u.nome LIKE %:nome%) AND " +
            "(:cognome IS NULL OR u.cognome LIKE %:cognome%) AND " +
            "(:email IS NULL OR u.email = :email)")
    List<Utente> ricercaUtente(@Param(value = "nome") String nome,
                               @Param(value = "cognome") String cognome,
                               @Param(value = "email") String email);

}