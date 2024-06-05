package fr.eni.tp.filmotheque.controller.security;

import fr.eni.tp.filmotheque.bll.contexte.ContexteService;
import fr.eni.tp.filmotheque.bo.Membre;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.security.Principal;

@Controller
@SessionAttributes({ "membreEnSession" })
public class LoginController {

    private ContexteService contexteService;

    public LoginController(ContexteService contexteService) {
        this.contexteService = contexteService;
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/session")
    public String chargerMembreEnSession(@ModelAttribute("membreEnSession") Membre mEnSession,
                                         Principal principal) {
        String email = principal.getName();
        Membre aCharger = contexteService.charger(email);
        if (aCharger != null) {
            mEnSession.setId(aCharger.getId());
            mEnSession.setNom(aCharger.getNom());
            mEnSession.setPrenom(aCharger.getPrenom());
            mEnSession.setPseudo(aCharger.getPseudo());
            mEnSession.setAdmin(aCharger.isAdmin());
        } else {
            mEnSession.setId(0);
            mEnSession.setNom(null);
            mEnSession.setPrenom(null);
            mEnSession.setPseudo(null);
            mEnSession.setAdmin(false);
        }
        System.out.println(mEnSession);
        return "redirect:/films";
    }

    @ModelAttribute("membreEnSession")
    public Membre membreEnSession() {
        return new Membre();
    }
}
