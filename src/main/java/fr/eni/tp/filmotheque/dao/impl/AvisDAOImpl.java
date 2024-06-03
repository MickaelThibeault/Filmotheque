package fr.eni.tp.filmotheque.dao.impl;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dao.AvisDAO;
import fr.eni.tp.filmotheque.dao.FilmDAO;
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
public class AvisDAOImpl implements AvisDAO {

    private static final String CREATE = "INSERT INTO AVIS (note, commentaire, id_membre, id_film) VALUES (:note, :commentaire, :id_membre, :id_film)";
    private static final String FIND_BY_FILM = "SELECT id, note, commentaire, id_membre FROM AVIS WHERE id_film = :idFilm";
    private final String COUNT_AVIS = "SELECT count(id) FROM AVIS where id_membre = :idMembre and id_film= :idFilm";

    private NamedParameterJdbcTemplate jdbcTemplate;
    private FilmDAO filmDAO;

    public AvisDAOImpl(NamedParameterJdbcTemplate jdbcTemplate, FilmDAO filmDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDAO = filmDAO;
    }

    @Override
    public void create(Avis avis, long idFilm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("note", avis.getNote());
        parameters.addValue("commentaire", avis.getCommentaire());
        parameters.addValue("id_membre", avis.getMembre().getId());
        parameters.addValue("id_film", idFilm);

        jdbcTemplate.update(CREATE, parameters, keyHolder);
        if (keyHolder != null && keyHolder.getKey() != null) {
            avis.setId(keyHolder.getKey().longValue());
        }

//        Film film = filmDAO.read(idFilm);
//        if (film != null)
//            film.getAvis().add(avis);
    }

    @Override
    public List<Avis> findByFilm(long idFilm) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("idFilm", idFilm);

        return jdbcTemplate.query(FIND_BY_FILM, namedParameters, new AvisRowMapper());
    }

    @Override
    public int countAvis(long idFilm, long idMembre) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("idFilm", idFilm);
        namedParameters.addValue("idMembre", idMembre);
        return jdbcTemplate.queryForObject(COUNT_AVIS, namedParameters, Integer.class);
    }

    class AvisRowMapper implements RowMapper<Avis> {
        @Override
        public Avis mapRow(ResultSet rs, int rowNum) throws SQLException {
            Avis a = new Avis();
            a.setId(rs.getLong("id"));
            a.setNote(rs.getInt("note"));
            a.setCommentaire(rs.getString("commentaire"));

            Membre membre = new Membre();
            membre.setId(rs.getInt("id_membre"));
            a.setMembre(membre);
            return a;
        }
    }
}
