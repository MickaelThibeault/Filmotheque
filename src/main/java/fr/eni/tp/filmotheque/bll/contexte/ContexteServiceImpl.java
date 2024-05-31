package fr.eni.tp.filmotheque.bll.contexte;

import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dao.MembreDAO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ContexteServiceImpl implements ContexteService{

    private MembreDAO membreDAO;

    public ContexteServiceImpl(MembreDAO membreDAO) {
        this.membreDAO = membreDAO;
    }

    @Override
    public Membre charger(String email) {
        return membreDAO.read(email);
    }
}
