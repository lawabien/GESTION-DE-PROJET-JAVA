package dao;

import model.Projet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ProjetDAO {
    public void ajouterProjet(Projet projet) {
        String sql = "INSERT INTO Projet(nom, description, date_debut, date_fin, statut, id_utilisateur) VALUES(?,?,?,?,?,?)";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, projet.getNom());
            pstmt.setString(2, projet.getDescription());
            pstmt.setDate(3, new java.sql.Date(projet.getDateDebut().getTime()));
            pstmt.setDate(4, new java.sql.Date(projet.getDateFin().getTime()));
            pstmt.setString(5, projet.getStatut().name());
            pstmt.setInt(6, projet.getIdUtilisateur());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        projet.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du projet", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Projet> listerProjets() {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM Projet ORDER BY date_debut DESC";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                projets.add(creerProjetDepuisResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    public void supprimerProjet(int id) {
        String sql = "DELETE FROM Projet WHERE id = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression du projet", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modifierProjet(Projet projet) {
        String sql = "UPDATE Projet SET nom = ?, description = ?, date_debut = ?, date_fin = ?, statut = ?, id_utilisateur = ? WHERE id = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, projet.getNom());
            pstmt.setString(2, projet.getDescription());
            pstmt.setDate(3, new java.sql.Date(projet.getDateDebut().getTime()));
            pstmt.setDate(4, new java.sql.Date(projet.getDateFin().getTime()));
            pstmt.setString(5, projet.getStatut().name());
            pstmt.setInt(6, projet.getIdUtilisateur());
            pstmt.setInt(7, projet.getId());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la modification du projet", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Projet getProjetById(int id) {
        String sql = "SELECT * FROM Projet WHERE id = ?";
        Projet projet = null;
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    projet = creerProjetDepuisResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projet;
    }
    
    public Projet getProjetByName(String nom) {
        String sql = "SELECT * FROM Projet WHERE nom = ?";
        Projet projet = null;
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    projet = creerProjetDepuisResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projet;
    }
    
    public List<Projet> rechercherProjets(String terme) {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM Projet WHERE nom LIKE ? OR description LIKE ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + terme + "%");
            pstmt.setString(2, "%" + terme + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    projets.add(creerProjetDepuisResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }
    
    public List<Projet> filtrerProjetsParStatut(Projet.StatutProjet statut) {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM Projet WHERE statut = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    projets.add(creerProjetDepuisResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }
    
    public int getNombreProjetsUtilisateur(int userId) {
        String sql = "SELECT COUNT(*) FROM Projet WHERE id_utilisateur = ?";
        int count = 0;
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    public void exporterProjetCSV(int projetId, String filePath) {
        Projet projet = getProjetById(projetId);
        if (projet == null) return;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // En-tête CSV
            writer.append("ID,Nom,Description,Date Début,Date Fin,Statut,Responsable\n");
            
            // Données du projet
            writer.append(String.valueOf(projet.getId())).append(",");
            writer.append(escapeCsv(projet.getNom())).append(",");
            writer.append(escapeCsv(projet.getDescription())).append(",");
            writer.append(sdf.format(projet.getDateDebut())).append(",");
            writer.append(sdf.format(projet.getDateFin())).append(",");
            writer.append(projet.getStatut().toString()).append(",");
            writer.append(String.valueOf(projet.getIdUtilisateur())).append("\n");
            
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'export CSV", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Projet creerProjetDepuisResultSet(ResultSet rs) throws SQLException {
        Projet p = new Projet();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setDateDebut(rs.getDate("date_debut"));
        p.setDateFin(rs.getDate("date_fin"));
        p.setStatut(Projet.StatutProjet.valueOf(rs.getString("statut")));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        return p;
    }
    
    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.contains(",") ? "\"" + input + "\"" : input;
    }
}