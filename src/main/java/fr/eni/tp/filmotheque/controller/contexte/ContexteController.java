package fr.eni.tp.filmotheque.controller.contexte;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import fr.eni.tp.filmotheque.bll.contexte.ContexteService;
import fr.eni.tp.filmotheque.bo.Membre;

@Controller
@RequestMapping("/contexte")
@SessionAttributes({ "membreEnSession" })
public class ContexteController {

	private ContexteService contexteService;

	public ContexteController(ContexteService contexteService) {
		this.contexteService = contexteService;
	}

	// Pour mettre un membre en session par défaut -- Sinon Error Session
	@ModelAttribute("membreEnSession")
	public Membre membreEnSession() {
		return new Membre();
	}

	@GetMapping("/session")
	public String connexion(@ModelAttribute("membreEnSession") Membre mEnSession,
			@RequestParam(name = "email", required = false, defaultValue = "jtrillard@campus-eni.fr") String email) {
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

	@GetMapping("/cloture")
	public String findSession(SessionStatus status) {
		status.setComplete();
		return "redirect:/films";
	}

	@GetMapping
	public String choixContexte() {
		return "contexte/view-contexte";
	}
}
