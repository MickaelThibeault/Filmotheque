package fr.eni.tp.filmotheque.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;

@Controller
@RequestMapping("/films")
@SessionAttributes({ "genresEnSession", "membreEnSession" })
public class FilmController {

	private FilmService filmService;

	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}

	@GetMapping
	public String afficherFilms(Model model) {
		List<Film> films = filmService.consulterFilms();
		model.addAttribute("films", films);

		return "view-films";
	}

	@GetMapping("/detail")
	public String afficherUnFilm(@RequestParam(name = "id", required = true) long id, Model model) {
		if (id > 0) {
			Film film = filmService.consulterFilmParId(id);
			if (film != null) {
				// Ajout de l'instance dans le modèle
				model.addAttribute("film", film);
				return "view-film-detail";
			} else {
				System.out.println("Film inconnu");
			}
		} else {
			System.out.println("Identifiant inconnu");
		}
		// Par défaut redirection vers l'url pour afficher les films
		return "redirect:/films";

	}

	/**
	 * Gestion de la liste des genres en session
	 * 
	 * Création d'une méthode qui charge les données dans le modèle
	 * avec @ModelAttribute
	 * 
	 * Injection de l'attribut en session avec @SessionAttributes
	 */
	@ModelAttribute("genresEnSession")
	public List<Genre> chargerGenres() {
		System.out.println("Chargement en Session - GENRES");
		return filmService.consulterGenres();
	}
	
	@GetMapping("/creer")
	public String creerFilm() {
		return "view-film-form";
	}


}
