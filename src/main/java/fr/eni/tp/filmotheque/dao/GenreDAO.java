package fr.eni.tp.filmotheque.dao;

import fr.eni.tp.filmotheque.bo.Genre;

import java.util.List;

public interface GenreDAO {
    public Genre read(long id);
    public List<Genre> findAll();
}
