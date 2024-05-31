package fr.eni.tp.filmotheque.dao.impl;

import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.dao.GenreDAO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.security.spec.NamedParameterSpec;
import java.util.List;

@Repository
public class GenreDAOImpl implements GenreDAO {

    private static final String FIND_ALL = "SELECT id, titre FROM GENRE;";
//    private static final String READ = "SELECT id, titre FROM GENRE WHERE id= ?;";
    private static final String READ = "SELECT id, titre FROM GENRE WHERE id= :id;";
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public GenreDAOImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Genre read(long id) {
//        Genre genre = jdbcTemplate.queryForObject(READ, new BeanPropertyRowMapper<>(Genre.class), id);

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", id);
        Genre genre = namedParameterJdbcTemplate.queryForObject(READ, namedParameters, new BeanPropertyRowMapper<>(Genre.class));
        return genre;
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genres = jdbcTemplate.query(FIND_ALL, new BeanPropertyRowMapper<>(Genre.class));
        return genres;
    }
}
