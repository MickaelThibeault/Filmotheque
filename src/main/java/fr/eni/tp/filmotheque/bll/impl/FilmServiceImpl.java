package fr.eni.tp.filmotheque.bll.impl;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.*;
import fr.eni.tp.filmotheque.dao.*;
import fr.eni.tp.filmotheque.exceptions.BusinessCode;
import fr.eni.tp.filmotheque.exceptions.BusinessException;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
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
        BusinessException be = new BusinessException();
        boolean isValid = true;

        isValid &= validationFilm(film, be);
        isValid &= validationFilmUnique(film.getTitre(), be);
        isValid &= validationTitreFilm(film.getTitre(), be);
        isValid &= validationTitreFilmMax(film.getTitre(), be);
        isValid &= validationAnneeFilmMin(film.getAnnee(), be);
        isValid &= validationGenreFilm(film.getGenre(), be);
        isValid &= validationRealisateurFilm(film.getRealisateur(), be);
        isValid &= validationIdRealisateurFilm(film.getRealisateur(), be);
        isValid &= validationGenreFilm(film.getGenre(), be);
        isValid &= validationIdGenreFilm(film.getGenre(), be);
        isValid &= validationDureeFilm(film.getDuree(), be);
        isValid &= validationSynopsisFilmSize(film.getSynopsis(), be);
        isValid &= validationSynopsisFilm(film.getSynopsis(), be);
        isValid &= validerActeurs(film.getActeurs(), be);

        if (isValid) {
            filmDAO.create(film);

            long idFilm = film.getId();
            film.getActeurs()
                    .forEach(p -> {
                        participantDAO.createActeur(p.getId(), idFilm);
                    });
        } else {
            throw be;
        }

    }

    @Override
    public String consulterTitreFilm(long idFilm) {
        return filmDAO.findTitre(idFilm);
    }

    @Override
    public void publierAvis(Avis avis, long idFilm) {
        BusinessException be = new BusinessException();

        boolean isValid = validerAvis(avis, be);
        isValid &= validerNote(avis.getNote(), be);
        isValid &= validerCommentaire(avis.getCommentaire(), be);
        isValid &= validerMembre(avis.getMembre(), be);
        isValid &= validerIdFilm(idFilm, be);
        isValid &= validerAvisUnique(idFilm, avis.getMembre().getId(), be);

        if (isValid) {
            try {
                avisDAO.create(avis, idFilm);
            } catch (DataAccessException e) {
                be.add(BusinessCode.BLL_AVIS_CREER_ERREUR);
                throw be;
            }
        } else {
            throw be;
        }
    }




    @Override
    public List<Avis> consulterAvis(long idFilm) {
        return null;
    }

    private boolean validationFilm(Film film, BusinessException be) {
        if (film == null) {
            be.add(BusinessCode.VALIDATION_FILM_NULL);
            return false;
        }
        return true;
    }

    private boolean validationFilmUnique(String titre, BusinessException be) {
        try {
            boolean titreExist = filmDAO.findTitre(titre);
            if (titreExist) {
                be.add(BusinessCode.VALIDATION_FILM_UNIQUE);
                return false;
            }

        } catch (DataAccessException e) {
            be.add(BusinessCode.VALIDATION_FILM_UNIQUE);
            return false;
        }
        return true;
    }

    private boolean validationTitreFilm(String titre, BusinessException be) {
        if (titre == null || titre.isBlank()) {
            be.add(BusinessCode.VALIDATION_FILM_TITRE_BLANK);
            return false;
        }
        return true;
    }

    private boolean validationTitreFilmMax(String titre, BusinessException be) {
        if (titre.length() > 250) {
            be.add(BusinessCode.VALIDATION_FILM_TITRE_MAX);
            return false;
        }
        return true;
    }

    private boolean validationAnneeFilmMin(Integer annee, BusinessException be) {
        if (annee < 1900) {
            be.add(BusinessCode.VALIDATION_FILM_ANNEE);
            return false;
        }
        return true;
    }

    private boolean validationGenreFilm(Genre genre, BusinessException be) {
        if (genre == null) {
            be.add(BusinessCode.VALIDATION_FILM_GENRE_NULL);
            return false;
        }
        return true;
    }

    private boolean validationIdGenreFilm(Genre genre, BusinessException be) {
        if (genre.getId() == 0) {
            be.add(BusinessCode.VALIDATION_FILM_IDGENRE);
            return false;
        }
        try {
            Genre genreEnBase = genreDAO.read(genre.getId());
            if (genreEnBase == null) {
                be.add(BusinessCode.VALIDATION_FILM_IDGENRE);
                return false;
            }
        } catch (DataAccessException e) {
            // Impossible de trouver le genre
            be.add(BusinessCode.VALIDATION_FILM_IDGENRE);
            return false;
        }
        return true;
    }

    private boolean validationRealisateurFilm(Participant realisateur, BusinessException be) {
        if (realisateur == null) {
            be.add(BusinessCode.VALIDATION_FILM_REALISATEUR);
            return false;
        }
        return true;
    }

    private boolean validationIdRealisateurFilm(Participant participant, BusinessException be) {
        if (participant.getId() <= 0) {
            be.add(BusinessCode.VALIDATION_FILM_IDREALISATEUR);
            return false;
        }

        // Valider que l'identifiant du Réalisateur existe en base
        try {
            Participant participantEnBase = participantDAO.read(participant.getId());
            if (participantEnBase == null) {
                be.add(BusinessCode.VALIDATION_FILM_IDREALISATEUR);
                return false;
            }
        } catch (DataAccessException e) {
            // Impossible de trouver le réalisateur
            be.add(BusinessCode.VALIDATION_FILM_IDREALISATEUR);
            return false;
        }

        return true;
    }

    private boolean validationDureeFilm(int duree, BusinessException be) {
        if (duree <= 0) {
            be.add(BusinessCode.VALIDATION_FILM_DUREE_MIN);
            return false;
        }
        return true;
    }

    private boolean validationSynopsisFilmSize(String synopsis, BusinessException be) {
        if (synopsis.length() > 250 || synopsis.length() < 20) {
            be.add(BusinessCode.VALIDATION_FILM_SYNOPSIS_SIZE);
            return false;
        }
        return true;
    }

    private boolean validationSynopsisFilm(String synopsis, BusinessException be) {
        if (synopsis == null || synopsis.isBlank()) {
            be.add(BusinessCode.VALIDATION_FILM_SYNOPSIS);
            return false;
        }
        return true;
    }

    private boolean validerActeurs(List<Participant> acteurs, BusinessException be) {
        if (acteurs == null || acteurs.isEmpty()) {
            return true;// Pas de validation si aucun acteur dans la liste
        }

        // Valider que l'identifiant des acteurs est valide
        for (Participant participant : acteurs) {
            if (participant.getId() <= 0) {
                be.add(BusinessCode.VALIDATION_FILM_ACTEUR_ID_INCONNU);
                return false;
            } else {
                // Valider que l'identifiant de l'acteur existe en base
                try {
                    Participant participantEnBase = participantDAO.read(participant.getId());
                    if (participantEnBase == null) {
                        be.add(BusinessCode.VALIDATION_FILM_ACTEUR_ID_INCONNU);
                        return false;
                    }
                } catch (DataAccessException e) {
                    // Impossible de trouver l'acteur
                    be.add(BusinessCode.VALIDATION_FILM_ACTEUR_ID_INCONNU);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validerAvis(Avis a, BusinessException be) {
        if (a == null) {
            be.add(BusinessCode.VALIDATION_AVIS_NULL);
            return false;
        }
        return true;
    }

    // Note donnée de 0 à 5
    private boolean validerNote(int note, BusinessException be) {
        if (note < 0 || note > 5) {
            be.add(BusinessCode.VALIDATION_AVIS_NOTE);
            return false;
        }
        return true;
    }

    // Commentaire associé (entre 1 et 250 caractères)
    private boolean validerCommentaire(String commentaire, BusinessException be) {
        if (commentaire == null || commentaire.isBlank()) {
            be.add(BusinessCode.VALIDATION_AVIS_COMMENTAIRE_BLANK);
            return false;
        }
        if (commentaire.length() < 1 || commentaire.length() > 250) {
            be.add(BusinessCode.VALIDATION_AVIS_COMMENTAIRE_LENGTH);
            return false;
        }
        return true;
    }

    // Publier un avis si et seulement si un membre est connecté
    private boolean validerMembre(Membre membre, BusinessException be) {
        if (membre == null) {
            be.add(BusinessCode.VALIDATION_AVIS_MEMBRE_NULL);
            return false;
        }

        // Valider que l'identifiant du membre est valide
        if (membre.getId() <= 0) {
            be.add(BusinessCode.VALIDATION_AVIS_MEMBRE_ID_INCONNU);
            return false;
        }

        // Valider que l'identifiant du membre existe en base
        try {
            Membre membreEnBase = membreDAO.read(membre.getId());
            if (membreEnBase == null) {
                be.add(BusinessCode.VALIDATION_AVIS_MEMBRE_INCONNU);
                return false;
            }

            // Valider que le membre correspond à celui de la base
            // Pour valider l'égalité du membre en session et de celui en base
            // Il faut valider tous les champs sauf le mot de passe
            // Redéfinir la méthode equals dans la classe Membre
            if (!membreEnBase.equals(membre)) {
                be.add(BusinessCode.VALIDATION_AVIS_MEMBRE_INCONNU);
                return false;
            }

        } catch (DataAccessException e) {
            be.add(BusinessCode.VALIDATION_AVIS_MEMBRE_INCONNU);
            return false;
        }

        return true;
    }

    // Valider l'identifiant du film associé
    private boolean validerIdFilm(long idFilm, BusinessException be) {
        if (idFilm < 0) {
            be.add(BusinessCode.VALIDATION_FILM_ID_INCONNU);
            return false;
        }
        try {
            String titre = filmDAO.findTitre(idFilm);
            if (titre == null) {
                be.add(BusinessCode.VALIDATION_FILM_INCONNU);
                return false;
            }
        } catch (DataAccessException e) {
            be.add(BusinessCode.VALIDATION_FILM_INCONNU);
            return false;
        }

        return true;
    }

    // Valider que le membre n'a pas donné d'avis sur le film
    private boolean validerAvisUnique(long idFilm, long idMembre, BusinessException be) {
        try {
            int count = avisDAO.countAvis(idFilm, idMembre);
            if (count > 0) {
                be.add(BusinessCode.VALIDATION_AVIS_UNIQUE);
                return false;
            }
        } catch (DataAccessException e) {
            be.add(BusinessCode.VALIDATION_AVIS_UNIQUE);
            return false;
        }

        return true;
    }

}
