package dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.apache.commons.dbcp2.BasicDataSource;

public class ConnexionBDD {
    private static BasicDataSource dataSource;

    static {
        try {
            // Configuration du pool de connexions
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/gestion_projets");
            dataSource.setUsername("root");
            dataSource.setPassword("");
            
            // Configuration du pool
            dataSource.setInitialSize(5);       // 5 connexions initiales
            dataSource.setMaxTotal(20);         // Maximum de 20 connexions
            dataSource.setMaxIdle(10);          // Maximum de 10 connexions inactives
            dataSource.setMinIdle(5);           // Minimum de 5 connexions inactives
            dataSource.setMaxWaitMillis(10000); // 10 secondes d'attente max
            
            // Options de validation
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(true);
            dataSource.setTestWhileIdle(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur d'initialisation de la base de données: " + e.getMessage(), 
                "Erreur Critique", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static Connection getConnexion() throws SQLException {
        return dataSource.getConnection();
    }

    public static void fermerConnexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // Retourne la connexion au pool
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors de la fermeture de la connexion: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}