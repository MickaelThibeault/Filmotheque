package fr.eni.tp.filmotheque.dao;

import fr.eni.tp.filmotheque.bo.Avis;

import java.util.List;

public interface AvisDAO {
    public void create(Avis avis, long idFilm);
    public List<Avis> findByFilm(long idFilm);
}
