import dao.ConnexionBDD;
import dao.UtilisateurDAO;
import model.Utilisateur;
import view.MainFrame;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List; 

public class ProjetManager {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Tester la connexion à la base de données
                testDatabaseConnection();
                
                // 2. Vérifier qu'il y a au moins un utilisateur admin
                verifierUtilisateurAdmin();
                
                // 3. Créer et afficher l'interface
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erreur d'initialisation: " + e.getMessage(),
                    "Erreur Critique",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static void testDatabaseConnection() throws SQLException {
        try (Connection conn = ConnexionBDD.getConnexion()) {
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Connexion à la base de données échouée");
            }
            System.out.println("[DEBUG] Connexion à la base de données réussie");
        }
    }

    private static void verifierUtilisateurAdmin() {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        List<Utilisateur> admins = utilisateurDAO.listerUtilisateurs().stream()
            .filter(u -> u.getRole() == Utilisateur.Role.ADMINISTRATEUR)
            .toList();
        
        if (admins.isEmpty()) {
            int response = JOptionPane.showConfirmDialog(null,
                "Aucun administrateur trouvé. Voulez-vous créer un compte admin par défaut?",
                "Configuration requise",
                JOptionPane.YES_NO_OPTION);
            
            if (response == JOptionPane.YES_OPTION) {
                creerAdminParDefaut(utilisateurDAO);
            } else {
                throw new IllegalStateException("L'application nécessite au moins un administrateur");
            }
        }
    }

    private static void creerAdminParDefaut(UtilisateurDAO utilisateurDAO) {
        try {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setEmail("admin@example.com");
            admin.setMotDePasse("admin123"); // Le mot de passe sera hashé automatiquement
            admin.setRole(Utilisateur.Role.ADMINISTRATEUR);
            
            if (utilisateurDAO.ajouterUtilisateur(admin)) {
                JOptionPane.showMessageDialog(null,
                    "Compte admin créé avec succès!\n" +
                    "Email: admin@example.com\n" +
                    "Mot de passe: admin123",
                    "Nouvel administrateur",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            throw new RuntimeException("Échec de création du compte admin: " + e.getMessage());
        }
    }
}
