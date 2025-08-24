package controller;

import model.Projet;
import model.Tache;
import model.Utilisateur;
import dao.ProjetDAO;
import dao.TacheDAO;
import dao.UtilisateurDAO;
import java.util.List;
import javax.swing.JOptionPane;

public class ProjetController {
    private final ProjetDAO projetDAO;
    private final TacheDAO tacheDAO;
    private final UtilisateurDAO utilisateurDAO;
    
    public ProjetController() {
        this.projetDAO = new ProjetDAO();
        this.tacheDAO = new TacheDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    // Méthodes pour les projets
    public boolean ajouterProjet(Projet projet) {
        if (projet == null) {
            showError("Le projet ne peut pas être null");
            return false;
        }
        
        if (!validerProjet(projet)) {
            return false;
        }
        
        try {
            projetDAO.ajouterProjet(projet);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de l'ajout du projet: " + e.getMessage());
            return false;
        }
    }
    
    public List<Projet> listerProjets() {
        return projetDAO.listerProjets();
    }
    
    public boolean supprimerProjet(int id) {
        if (id <= 0) {
            showError("ID de projet invalide");
            return false;
        }
        
        try {
            // Supprimer d'abord les tâches associées
            tacheDAO.supprimerTachesParProjet(id);
            // Puis supprimer le projet
            projetDAO.supprimerProjet(id);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la suppression du projet: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modifierProjet(Projet projet) {
        if (projet == null) {
            showError("Le projet ne peut pas être null");
            return false;
        }
        
        if (!validerProjet(projet)) {
            return false;
        }
        
        try {
            projetDAO.modifierProjet(projet);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la modification du projet: " + e.getMessage());
            return false;
        }
    }
    
    public Projet getProjetById(int id) {
        if (id <= 0) {
            showError("ID de projet invalide");
            return null;
        }
        return projetDAO.getProjetById(id);
    }
    
    public Projet getProjetByName(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            showError("Le nom du projet ne peut pas être vide");
            return null;
        }
        return projetDAO.getProjetByName(nom);
    }
    
    public List<Projet> rechercherProjets(String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            return listerProjets(); // Retourner tous les projets si recherche vide
        }
        return projetDAO.rechercherProjets(terme);
    }
    
    public List<Projet> filtrerProjetsParStatut(Projet.StatutProjet statut) {
        if (statut == null) {
            showError("Le statut ne peut pas être null");
            return listerProjets();
        }
        return projetDAO.filtrerProjetsParStatut(statut);
    }
    
    // Méthodes pour les tâches
    public boolean ajouterTache(Tache tache) {
        if (tache == null) {
            showError("La tâche ne peut pas être null");
            return false;
        }
        
        try {
            tacheDAO.ajouterTache(tache);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de l'ajout de la tâche: " + e.getMessage());
            return false;
        }
    }
    
    public List<Tache> listerTaches() {
        return tacheDAO.listerTaches();
    }
    
    public List<Tache> getTachesByProjet(int projetId) {
        if (projetId <= 0) {
            showError("ID de projet invalide");
            return null;
        }
        return tacheDAO.getTachesByProjet(projetId);
    }
    
    public boolean modifierTache(Tache tache) {
        if (tache == null) {
            showError("La tâche ne peut pas être null");
            return false;
        }
        
        try {
            tacheDAO.modifierTache(tache);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la modification de la tâche: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerTache(int id) {
        if (id <= 0) {
            showError("ID de tâche invalide");
            return false;
        }
        
        try {
            tacheDAO.supprimerTache(id);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la suppression de la tâche: " + e.getMessage());
            return false;
        }
    }
    
    public Tache getTacheById(int id) {
        if (id <= 0) {
            showError("ID de tâche invalide");
            return null;
        }
        return tacheDAO.getTacheById(id);
    }
    
    public boolean updateStatutTache(int tacheId, Tache.StatutTache statut) {
        if (tacheId <= 0) {
            showError("ID de tâche invalide");
            return false;
        }
        
        if (statut == null) {
            showError("Le statut ne peut pas être null");
            return false;
        }
        
        try {
            tacheDAO.updateStatutTache(tacheId, statut);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la mise à jour du statut: " + e.getMessage());
            return false;
        }
    }
    
    public boolean assignerTacheAProjet(int tacheId, int projetId) {
        if (tacheId <= 0 || projetId <= 0) {
            showError("ID de tâche ou projet invalide");
            return false;
        }
        
        try {
            tacheDAO.assignerTacheAProjet(tacheId, projetId);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de l'assignation de la tâche: " + e.getMessage());
            return false;
        }
    }
    
    // Méthodes pour les utilisateurs
    public boolean ajouterUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            showError("L'utilisateur ne peut pas être null");
            return false;
        }
        
        try {
            utilisateurDAO.ajouterUtilisateur(utilisateur);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
            return false;
        }
    }
    
    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurDAO.listerUtilisateurs();
    }
    
    public Utilisateur getUtilisateurById(int id) {
        if (id <= 0) {
            showError("ID d'utilisateur invalide");
            return null;
        }
        return utilisateurDAO.getUtilisateurById(id);
    }
    
    public Utilisateur getUtilisateurByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            showError("L'email ne peut pas être vide");
            return null;
        }
        return utilisateurDAO.getUtilisateurByEmail(email);
    }
    
    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            showError("L'utilisateur ne peut pas être null");
            return false;
        }
        
        try {
            utilisateurDAO.modifierUtilisateur(utilisateur);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la modification de l'utilisateur: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerUtilisateur(int id) {
        if (id <= 0) {
            showError("ID d'utilisateur invalide");
            return false;
        }
        
        try {
            utilisateurDAO.supprimerUtilisateur(id);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            return false;
        }
    }
    
    // Méthodes utilitaires
    public List<Utilisateur> getChefsDeProjet() {
        return utilisateurDAO.getChefsDeProjet();
    }
    
    public int getNombreProjetsUtilisateur(int userId) {
        if (userId <= 0) {
            showError("ID d'utilisateur invalide");
            return 0;
        }
        return projetDAO.getNombreProjetsUtilisateur(userId);
    }
    
    public int getNombreTachesUtilisateur(int userId) {
        if (userId <= 0) {
            showError("ID d'utilisateur invalide");
            return 0;
        }
        return tacheDAO.getNombreTachesUtilisateur(userId);
    }
    
    // Méthodes pour les exports
    public boolean exporterProjetCSV(int projetId, String filePath) {
        if (projetId <= 0) {
            showError("ID de projet invalide");
            return false;
        }
        
        if (filePath == null || filePath.trim().isEmpty()) {
            showError("Chemin du fichier invalide");
            return false;
        }
        
        try {
            projetDAO.exporterProjetCSV(projetId, filePath);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de l'export CSV: " + e.getMessage());
            return false;
        }
    }
    
    public boolean importerTachesCSV(int projetId, String filePath) {
        if (projetId <= 0) {
            showError("ID de projet invalide");
            return false;
        }
        
        if (filePath == null || filePath.trim().isEmpty()) {
            showError("Chemin du fichier invalide");
            return false;
        }
        
        try {
            tacheDAO.importerTachesCSV(projetId, filePath);
            return true;
        } catch (Exception e) {
            showError("Erreur lors de l'import CSV: " + e.getMessage());
            return false;
        }
    }
    
    // Méthodes privées utilitaires
    private boolean validerProjet(Projet projet) {
        if (projet.getNom() == null || projet.getNom().trim().isEmpty()) {
            showError("Le nom du projet est requis");
            return false;
        }
        
        if (projet.getDateDebut() == null) {
            showError("La date de début est requise");
            return false;
        }
        
        if (projet.getDateFin() == null) {
            showError("La date de fin est requise");
            return false;
        }
        
        if (projet.getDateDebut().after(projet.getDateFin())) {
            showError("La date de début doit être avant la date de fin");
            return false;
        }
        
        if (projet.getStatut() == null) {
            showError("Le statut du projet est requis");
            return false;
        }
        
        if (projet.getIdUtilisateur() <= 0) {
            showError("L'ID de l'utilisateur responsable est invalide");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}