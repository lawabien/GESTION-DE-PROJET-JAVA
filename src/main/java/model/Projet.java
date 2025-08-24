package model;

import java.util.Date;

public class Projet {
    private int id;
    private String nom;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private StatutProjet statut;
    private int idUtilisateur;

    public enum StatutProjet {
        EN_ATTENTE, EN_COURS, TERMINE, ANNULE
    }

    // Constructeurs
    public Projet() {}

    public Projet(String nom, String description, Date dateDebut, Date dateFin, StatutProjet statut, int idUtilisateur) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.idUtilisateur = idUtilisateur;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public StatutProjet getStatut() { return statut; }
    public void setStatut(StatutProjet statut) { this.statut = statut; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    @Override
    public String toString() {
        return nom;
    }
}