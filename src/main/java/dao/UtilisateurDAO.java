package dao;

import model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UtilisateurDAO {
    public boolean ajouterUtilisateur(Utilisateur utilisateur) {
        String sql = "INSERT INTO Utilisateur(nom, email, mot_de_passe, role) VALUES(?,?,?,?)";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getEmail());
            pstmt.setString(3, utilisateur.getMotDePasse()); // Mot de passe déjà hashé
            pstmt.setString(4, utilisateur.getRole().name());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        utilisateur.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de l'ajout de l'utilisateur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public List<Utilisateur> listerUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT id, nom, email, role FROM Utilisateur ORDER BY nom";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setEmail(rs.getString("email"));
                u.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    public Utilisateur getUtilisateurById(int id) {
        String sql = "SELECT id, nom, email, mot_de_passe, role FROM Utilisateur WHERE id = ?";
        Utilisateur utilisateur = null;
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    utilisateur = new Utilisateur();
                    utilisateur.setId(rs.getInt("id"));
                    utilisateur.setNom(rs.getString("nom"));
                    utilisateur.setEmail(rs.getString("email"));
                    utilisateur.setMotDePasse(rs.getString("mot_de_passe")); // Récupération du hash
                    utilisateur.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }

    public Utilisateur getUtilisateurByEmail(String email) {
        String sql = "SELECT id, nom, email, mot_de_passe, role FROM Utilisateur WHERE email = ?";
        Utilisateur utilisateur = null;
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    utilisateur = new Utilisateur();
                    utilisateur.setId(rs.getInt("id"));
                    utilisateur.setNom(rs.getString("nom"));
                    utilisateur.setEmail(rs.getString("email"));
                    utilisateur.setMotDePasse(rs.getString("mot_de_passe"));
                    utilisateur.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }

    public List<Utilisateur> getChefsDeProjet() {
        List<Utilisateur> chefs = new ArrayList<>();
        String sql = "SELECT id, nom, email, role FROM Utilisateur WHERE role = 'CHEF_DE_PROJET'";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setEmail(rs.getString("email"));
                u.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
                chefs.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chefs;
    }

    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        String sql = "UPDATE Utilisateur SET nom = ?, email = ?, role = ? WHERE id = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getEmail());
            pstmt.setString(3, utilisateur.getRole().name());
            pstmt.setInt(4, utilisateur.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la modification de l'utilisateur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean modifierMotDePasse(int userId, String nouveauMotDePasseHash) {
        String sql = "UPDATE Utilisateur SET mot_de_passe = ? WHERE id = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nouveauMotDePasseHash);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerUtilisateur(int id) {
        if (utilisateurAvecProjets(id)) {
            JOptionPane.showMessageDialog(null, 
                "Impossible de supprimer : l'utilisateur est responsable de projets", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean utilisateurAvecProjets(int userId) {
        String sql = "SELECT COUNT(*) FROM Projet WHERE id_utilisateur = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Par sécurité, on empêche la suppression en cas d'erreur
        }
    }

    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM Utilisateur WHERE email = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifierAuthentification(String email, String motDePasseHash) {
        String sql = "SELECT mot_de_passe FROM Utilisateur WHERE email = ?";
        
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String motDePasseStocke = rs.getString("mot_de_passe");
                    return motDePasseStocke != null && motDePasseStocke.equals(motDePasseHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}