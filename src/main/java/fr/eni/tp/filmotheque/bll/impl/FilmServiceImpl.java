package fr.eni.tp.filmotheque.bll.impl;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.*;
import fr.eni.tp.filmotheque.dao.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class FilmServiceImpl implements FilmService {

    private FilmDAO filmDAO;
    private GenreDAO genreDAO;
    private ParticipantDAO participantDAO;
    private AvisDAO avisDAO;
    private MembreDAO membreDAO;

    public FilmServiceImpl(FilmDAO filmDAO, GenreDAO genreDAO, ParticipantDAO participantDAO, AvisDAO avisDAO, MembreDAO membreDAO) {
        this.filmDAO = filmDAO;
        this.genreDAO = genreDAO;
        this.participantDAO = participantDAO;
        this.avisDAO = avisDAO;
        this.membreDAO = membreDAO;
    }

    @Override
    public List<Film> consulterFilms() {
//        System.out.println(filmDAO.findAll());
        List<Film> films = filmDAO.findAll();

        if (films != null)
            films.forEach(this::chargerGenreEtRealisateurByFilm);

        return films;
    }

    @Override
    public Film consulterFilmParId(long id) {
        Film film = filmDAO.read(id);

        if (film != null) {
            chargerGenreEtRealisateurByFilm(film);
            List<Participant> acteurs = participantDAO.findActeurs(film.getId());
            if (acteurs != null)
                film.setActeurs(acteurs);

            List<Avis> avis = avisDAO.findByFilm(id);
            if (avis != null) {
                avis.forEach(this::chargerMembreByAvis);
                film.setAvis(avis);
            }
        }

        return film;
    }

    private void chargerMembreByAvis(Avis a) {
        Membre membre = membreDAO.read(a.getMembre().getId());
        a.setMembre(membre);
    }

    private void chargerGenreEtRealisateurByFilm(Film film) {
        Participant realisateur = participantDAO.read(film.getRealisateur().getId());
        Genre genre = genreDAO.read(film.getGenre().getId());

        film.setRealisateur(realisateur);
        film.setGenre(genre);
    }

    @Override
    public List<Genre> consulterGenres() {
        return genreDAO.findAll();
    }

    @Override
    public Genre consulterGenreParId(long id) {
        return genreDAO.read(id);
    }

    @Override
    public List<Participant> consulterParticipants() {
        return participantDAO.findAll();
    }

    @Override
    public Participant consulterParticipantParId(long id) {
        return participantDAO.read(id);
    }

    @Override
    public void creerFilm(Film film) {
        filmDAO.create(film);
    }

    @Override
    public String consulterTitreFilm(long idFilm) {
        return filmDAO.findTitre(idFilm);
    }

    @Override
    public void publierAvis(Avis avis, long idFilm) {
        avisDAO.create(avis, idFilm);
    }

    @Override
    public List<Avis> consulterAvis(long idFilm) {
        return null;
    }
}
