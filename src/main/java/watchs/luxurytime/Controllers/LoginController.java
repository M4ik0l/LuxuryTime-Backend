package watchs.luxurytime.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import watchs.luxurytime.Entities.Utente;
import watchs.luxurytime.Services.LoginService;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/sync")
    public ResponseEntity<Utente> syncUser(@AuthenticationPrincipal Jwt principal) {
        String email = principal.getClaimAsString("email");
        String nome = principal.getClaimAsString("given_name");
        String cognome = principal.getClaimAsString("family_name");
        String numeroTelefonico = principal.getClaimAsString("phone_number");

        Utente currUser = loginService.addOrgetUtente(
                nome,
                cognome,
                email,
                numeroTelefonico != null ? numeroTelefonico : ""
        );
        return ResponseEntity.ok(currUser);
    }
}