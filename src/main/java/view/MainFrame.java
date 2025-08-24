package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    
    public MainFrame() {
        super("Gestion de Projets");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        tabbedPane = new JTabbedPane();
        
        // Ajout des onglets
        tabbedPane.addTab("Projets", new ProjetPanel());
        tabbedPane.addTab("Tâches", new TachePanel());
        tabbedPane.addTab("Utilisateurs", new UtilisateurPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Logiciel de Gestion de Projets v1.0\n© 2025", 
                "À propos", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}