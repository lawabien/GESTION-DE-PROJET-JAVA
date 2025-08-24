package dao;

import model.Tache;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class TacheDAO {
    private static final String TABLE_NAME = "Tache";

    public boolean ajouterTache(Tache tache) {
        String sql = String.format(
            "INSERT INTO %s (titre, description, statut, priorite, date_creation, date_echeance, id_projet, id_utilisateur) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Validation des données
            if (!tache.estValide()) {
                throw new IllegalArgumentException("Données de la tâche invalides");
            }

            pstmt.setString(1, tache.getTitre());
            pstmt.setString(2, tache.getDescription());
            pstmt.setString(3, tache.getStatut().name());
            pstmt.setString(4, tache.getPriorite().name());
            pstmt.setDate(5, new java.sql.Date(tache.getDateCreation().getTime()));
            pstmt.setDate(6, tache.getDateEcheance() != null ? 
                new java.sql.Date(tache.getDateEcheance().getTime()) : null);
            pstmt.setInt(7, tache.getIdProjet());
            pstmt.setInt(8, tache.getIdUtilisateur());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tache.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            gérerErreurSQL("l'ajout de la tâche", e);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<Tache> listerTaches() {
        return listerTachesAvecRequete(String.format("SELECT * FROM %s", TABLE_NAME));
    }

    public List<Tache> getTachesByProjet(int projetId) {
        return listerTachesAvecRequete(
            String.format("SELECT * FROM %s WHERE id_projet = ?", TABLE_NAME), 
            projetId);
    }

    public boolean modifierTache(Tache tache) {
        String sql = String.format(
            "UPDATE %s SET titre = ?, description = ?, statut = ?, priorite = ?, " +
            "date_echeance = ?, id_projet = ?, id_utilisateur = ? WHERE id = ?", 
            TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!tache.estValide()) {
                throw new IllegalArgumentException("Données de la tâche invalides");
            }

            pstmt.setString(1, tache.getTitre());
            pstmt.setString(2, tache.getDescription());
            pstmt.setString(3, tache.getStatut().name());
            pstmt.setString(4, tache.getPriorite().name());
            pstmt.setDate(5, tache.getDateEcheance() != null ? 
                new java.sql.Date(tache.getDateEcheance().getTime()) : null);
            pstmt.setInt(6, tache.getIdProjet());
            pstmt.setInt(7, tache.getIdUtilisateur());
            pstmt.setInt(8, tache.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            gérerErreurSQL("la modification de la tâche", e);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean supprimerTache(int id) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            gérerErreurSQL("la suppression de la tâche", e);
            return false;
        }
    }

    public Tache getTacheById(int id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? créerTacheDepuisResultSet(rs) : null;
            }
        } catch (SQLException e) {
            gérerErreurSQL("la récupération de la tâche", e);
            return null;
        }
    }
    
    public boolean supprimerTachesParProjet(int projetId) {
        String sql = String.format("DELETE FROM %s WHERE id_projet = ?", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, projetId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            gérerErreurSQL("la suppression des tâches du projet", e);
            return false;
        }
    }

    public boolean updateStatutTache(int tacheId, Tache.StatutTache statut) {
        String sql = String.format("UPDATE %s SET statut = ? WHERE id = ?", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut.name());
            pstmt.setInt(2, tacheId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            gérerErreurSQL("la mise à jour du statut", e);
            return false;
        }
    }

    public boolean assignerTacheAProjet(int tacheId, int projetId) {
        String sql = String.format("UPDATE %s SET id_projet = ? WHERE id = ?", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, projetId);
            pstmt.setInt(2, tacheId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            gérerErreurSQL("l'assignation de la tâche", e);
            return false;
        }
    }

    public int getNombreTachesUtilisateur(int userId) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE id_utilisateur = ?", TABLE_NAME);
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            gérerErreurSQL("le comptage des tâches", e);
            return 0;
        }
    }

    // Méthodes privées utilitaires
    private List<Tache> listerTachesAvecRequete(String sql, Object... params) {
        List<Tache> taches = new ArrayList<>();
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    taches.add(créerTacheDepuisResultSet(rs));
                }
            }
        } catch (SQLException e) {
            gérerErreurSQL("la récupération des tâches", e);
        }
        return taches;
    }

    private Tache créerTacheDepuisResultSet(ResultSet rs) throws SQLException {
        Tache tache = new Tache();
        tache.setId(rs.getInt("id"));
        tache.setTitre(rs.getString("titre"));
        tache.setDescription(rs.getString("description"));
        tache.setStatut(Tache.StatutTache.valueOf(rs.getString("statut")));
        tache.setPriorite(Tache.Priorite.valueOf(rs.getString("priorite")));
        tache.setDateCreation(rs.getDate("date_creation"));
        tache.setDateEcheance(rs.getDate("date_echeance"));
        tache.setIdProjet(rs.getInt("id_projet"));
        tache.setIdUtilisateur(rs.getInt("id_utilisateur"));
        return tache;
    }

    private void gérerErreurSQL(String operation, SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            String.format("Erreur lors de %s: %s", operation, e.getMessage()), 
            "Erreur SQL", 
            JOptionPane.ERROR_MESSAGE);
    }

    public void importerTachesCSV(int projetId, String filePath) {
        // Implémentation non fait
        throw new UnsupportedOperationException("Import CSV non implémenté");
    }
}