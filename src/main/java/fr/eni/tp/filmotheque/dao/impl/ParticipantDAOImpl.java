package fr.eni.tp.filmotheque.dao.impl;

import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.dao.ParticipantDAO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ParticipantDAOImpl implements ParticipantDAO {

//    private static final String READ = "SELECT id, nom, prenom FROM PARTICIPANT WHERE id= ?;";
    private static final String READ = "SELECT id, nom, prenom FROM PARTICIPANT WHERE id= :id;";
    private static final String FIND_ALL = "SELECT id, nom, prenom FROM PARTICIPANT;";
//    private static final String FIND_ACTEURS =
//            "SELECT id, nom, prenom FROM PARTICIPANT p " +
//            "INNER JOIN ACTEURS a ON p.id=a.id_participent " +
//            "WHERE a.id_film= ?;";
    private static final String FIND_ACTEURS =
            "SELECT id, nom, prenom FROM PARTICIPANT p " +
                    "INNER JOIN ACTEURS a ON p.id=a.id_participent " +
                    "WHERE a.id_film= :idFilm;";
    private static final String CREATEACTEUR = "INSERT INTO ACTEURS (id_film, id_participant) VALUES (:idFilm, :idParticipant);";
//    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ParticipantDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Participant read(long id) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("id", id);
        Participant participant = jdbcTemplate.queryForObject(READ, namedParameters, new BeanPropertyRowMapper<>(Participant.class));
        return participant;
    }

    @Override
    public List<Participant> findAll() {
        List<Participant> participants = jdbcTemplate.query(FIND_ALL, new BeanPropertyRowMapper<>(Participant.class));
        return participants;
    }

    @Override
    public List<Participant> findActeurs(long idFilm) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate.getJdbcTemplate())
                .withProcedureName("FindActeurs")
                .returningResultSet("acteurs", new BeanPropertyRowMapper<>(Participant.class));
        MapSqlParameterSource in = new MapSqlParameterSource();

        in.addValue("idFilm", idFilm);

        Map<String, Object> out = simpleJdbcCall.execute(in);

        if (out.get("acteurs") != null) {
            List<Participant> acteurs = (List<Participant>) out.get("acteurs");
            return acteurs;
        }
        return null;
    }

    @Override
    public void createActeur(long idParticipant, long idFilm) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("idFilm", idFilm);
        namedParameters.addValue("idParticipant", idParticipant);

        jdbcTemplate.update(CREATEACTEUR, namedParameters);

    }
}
