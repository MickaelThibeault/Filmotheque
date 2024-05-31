package fr.eni.tp.filmotheque.dao.impl;

import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.dao.FilmDAO;
import fr.eni.tp.filmotheque.dao.GenreDAO;
import fr.eni.tp.filmotheque.dao.ParticipantDAO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FilmDAOImpl implements FilmDAO {

//    private static final String READ = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM WHERE id= ?;";
    private static final String READ = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM WHERE id= :id;";
    private static final String FIND_ALL = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM";
//    private static final String FIND_TITRE = "SELECT titre FROM FILM WHERE id = ?";
    private static final String FIND_TITRE = "SELECT titre FROM FILM WHERE id = :id";
    private static final String CREATE = "INSERT INTO FILM (titre, annee, duree, synopsis, id_realisateur, id_genre) VALUES (:titre, :annee, :duree, :synopsis, :id_realisateur, :id_genre)";

//    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private ParticipantDAO participantDAO;
    private GenreDAO genreDAO;

    public FilmDAOImpl(NamedParameterJdbcTemplate jdbcTemplate, ParticipantDAO participantDAO, GenreDAO genreDAO) {
//        this.jdbcTemplate = jdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.participantDAO = participantDAO;
        this.genreDAO = genreDAO;
    }

    @Override
    public void create(Film film) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("titre", film.getTitre());
        namedParameters.addValue("annee", film.getAnnee());
        namedParameters.addValue("synopsis", film.getSynopsis());
        namedParameters.addValue("id_realisateur", film.getRealisateur().getId());
        namedParameters.addValue("id_genre", film.getGenre().getId());

        jdbcTemplate.update(CREATE, namedParameters, keyHolder);

        if (keyHolder != null && keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().longValue());
        }
    }

    @Override
    public Film read(long id) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", id);
        Film film = jdbcTemplate.queryForObject(READ, namedParameters, new FilmRowMapper());
        return film;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(FIND_ALL, new FilmRowMapper());
        System.out.println(films);
        return films;
    }

    @Override
    public String findTitre(long id) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", id);
        String titre = jdbcTemplate.queryForObject(FIND_TITRE, namedParameters, String.class);
        return titre;
    }

    class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setTitre(rs.getString("titre"));
            f.setAnnee(rs.getInt("annee"));
            f.setDuree(rs.getInt("duree"));
            f.setSynopsis(rs.getString("synopsis"));

            // Associations
            Participant realisateur = new Participant();
            realisateur.setId(rs.getLong("id_realisateur"));
            f.setRealisateur(realisateur);
//            f.setRealisateur(participantDAO.read(rs.getInt("id_realisateur")));

            Genre genre = new Genre();
            genre.setId(rs.getLong("id_genre"));
            f.setGenre(genre);
//            f.setGenre(genreDAO.read(rs.getInt("id_genre")));

            return f;
        }
    }
}
