package fr.eni.tp.filmotheque.dao.impl;

import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dao.MembreDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MembreDAOImpl implements MembreDAO {

    private static final String READ_ID = "SELECT id, nom, prenom, email, admin FROM MEMBRE WHERE id = :id";
    private static final String READ_MAIL = "SELECT id, nom, prenom, email, admin FROM MEMBRE WHERE email = :email";
    private NamedParameterJdbcTemplate jdbcTemplate;

    public MembreDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Membre read(long id) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", id);
        return jdbcTemplate.queryForObject(READ_ID, namedParameters, new MembreRowMapper());
    }

    @Override
    public Membre read(String email) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);

        return jdbcTemplate.queryForObject(READ_MAIL, namedParameters, new MembreRowMapper());
    }

    class MembreRowMapper implements RowMapper<Membre> {
        @Override
        public Membre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Membre m = new Membre();
            m.setId(rs.getLong("id"));
            m.setPseudo(rs.getString("email"));
            m.setNom(rs.getString("nom"));
            m.setPrenom(rs.getString("prenom"));
            m.setAdmin(rs.getBoolean("admin"));
            return m;
        }
    }
}
