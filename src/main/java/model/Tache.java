package model;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Tache {
    private int id;
    private String titre;
    private String description;
    private StatutTache statut;
    private Priorite priorite;
    private Date dateCreation;
    private Date dateEcheance;
    private int idProjet;
    private int idUtilisateur;

    public enum StatutTache {
        A_FAIRE("À faire"), 
        EN_COURS("En cours"), 
        TERMINE("Terminé"), 
        BLOQUE("Bloqué");

        private final String libelle;

        StatutTache(String libelle) {
            this.libelle = libelle;
        }

        @Override
        public String toString() {
            return libelle;
        }
    }

    public enum Priorite {
        BASSE("Basse"), 
        MOYENNE("Moyenne"), 
        HAUTE("Haute"), 
        CRITIQUE("Critique");

        private final String libelle;

        Priorite(String libelle) {
            this.libelle = libelle;
        }

        @Override
        public String toString() {
            return libelle;
        }
    }

    // Constructeurs
    public Tache() {
        this.dateCreation = new Date(); // Date de création par défaut = maintenant
    }

    public Tache(String titre, String description, StatutTache statut, Priorite priorite, 
                Date dateEcheance, int idProjet, int idUtilisateur) {
        this();
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.priorite = priorite;
        this.dateEcheance = dateEcheance;
        this.idProjet = idProjet;
        this.idUtilisateur = idUtilisateur;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { 
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide");
        }
        this.titre = titre; 
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public StatutTache getStatut() { return statut != null ? statut : StatutTache.A_FAIRE; }
    public void setStatut(StatutTache statut) { this.statut = statut != null ? statut : StatutTache.A_FAIRE; }
    
    public Priorite getPriorite() { return priorite != null ? priorite : Priorite.MOYENNE; }
    public void setPriorite(Priorite priorite) { this.priorite = priorite != null ? priorite : Priorite.MOYENNE; }
    
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { 
        this.dateCreation = dateCreation != null ? dateCreation : new Date(); 
    }
    
    public Date getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(Date dateEcheance) { this.dateEcheance = dateEcheance; }
    
    public int getIdProjet() { return idProjet; }
    public void setIdProjet(int idProjet) { this.idProjet = idProjet; }
    
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    // Méthodes utilitaires
    public String getDateCreationFormatee() {
        return formatDate(dateCreation);
    }

    public String getDateEcheanceFormatee() {
        return formatDate(dateEcheance);
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    @Override
    public String toString() {
        return String.format("%s (Priorité: %s, Statut: %s, Échéance: %s)", 
               titre, 
               getPriorite().toString(), 
               getStatut().toString(), 
               getDateEcheanceFormatee());
    }

    // Validation
    public boolean estValide() {
        return titre != null && !titre.trim().isEmpty() &&
               idProjet > 0 &&
               idUtilisateur > 0;
    }
}