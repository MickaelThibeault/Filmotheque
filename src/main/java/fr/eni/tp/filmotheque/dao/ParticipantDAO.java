package fr.eni.tp.filmotheque.dao;

import fr.eni.tp.filmotheque.bo.Participant;

import java.util.List;

public interface ParticipantDAO {
    public Participant read(long id);
    public List<Participant> findAll();
    public List<Participant> findActeurs(long idFilm);
    public void createActeur(long idParticipant, long idFilm);
}
