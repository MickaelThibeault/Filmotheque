package fr.eni.tp.filmotheque.controller;

import fr.eni.tp.filmotheque.exceptions.BusinessCode;
import fr.eni.tp.filmotheque.exceptions.BusinessException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Membre;

@Controller
@RequestMapping("/avis")
@SessionAttributes({ "membreEnSession" })
public class AvisController {

	private FilmService filmService;

	public AvisController(FilmService filmService) {
		this.filmService = filmService;
	}

	@GetMapping("/creer")
	public String creerUnAvis(@ModelAttribute("membreEnSession") Membre mEnSession,
			@RequestParam(name = "idFilm", required = true) long idFilm, Model model) {
		if (mEnSession != null && mEnSession.getId() >= 1) {
			if (idFilm > 0) {
				String titreFilm = filmService.consulterTitreFilm(idFilm);
				model.addAttribute("titreFilm", titreFilm);
				model.addAttribute("idFilm", idFilm);
				model.addAttribute("avis", new Avis());

				return "view-avis";
			}
		}
		return "redirect:/films";
	}

	@PostMapping("/creer")
	public String creerUnAvis(@ModelAttribute("membreEnSession") Membre mEnSession,
							  @RequestParam(name = "idFilm", required = true) long idFilm,  @Valid @ModelAttribute("avis") Avis avis,
							  BindingResult br, Model model) {
		if (mEnSession != null && mEnSession.getId() >= 1) {
			avis.setMembre(mEnSession);

				try {
					filmService.publierAvis(avis, idFilm);
					return "redirect:/films";
				} catch (BusinessException e) {
					datas(model, idFilm);
					e.getClefsExternalisations().forEach(key -> {
						ObjectError error = new ObjectError("globalError", key);
						br.addError(error);
					});
					return "view-avis";
				}
		} else {
			System.out.println("Aucun membre en session");
			ObjectError error = new ObjectError("globalError", BusinessCode.VALIDATION_MEMBRE);
			br.addError(error);
			return "view-avis";
		}
	}

	private void datas(Model model, long idFilm) {
		String titreFilm = filmService.consulterTitreFilm(idFilm);
		model.addAttribute("titreFilm", titreFilm);
		model.addAttribute("idFilm", idFilm);
	}


}
