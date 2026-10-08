package watchs.luxurytime.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "utente", schema = "luxurytime_db")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Basic
    @Column(name = "cognome", nullable = false, length = 50)
    private String cognome;

    @Basic
    @Column(name = "email", nullable = false, length = 90)
    private String email;

    @Basic
    @Column(name = "numero_telefonico", nullable = true, length = 20)
    private String numeroTelefonico;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProdottoInCarrello> carrello;

    @OneToMany(mappedBy = "acquirente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ordine> storicoOrdini;

    public Utente(String nome, String cognome, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public Utente(String nome, String cognome, String email, String numeroTelefonico){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.numeroTelefonico = numeroTelefonico;
    }

    public Utente(){
    }
}