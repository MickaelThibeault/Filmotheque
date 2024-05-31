package fr.eni.tp.filmotheque.dao;

import fr.eni.tp.filmotheque.bo.Membre;

public interface MembreDAO {

    public Membre read(long id);
    public Membre read(String email);
}
