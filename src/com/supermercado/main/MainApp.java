package com.supermercado.main;

import com.supermercado.util.SQLiteDatabaseSetup;
import com.supermercado.view.TelaLogin;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class MainApp {
    
    public static void main(String[] args) {
        try {
            // Prepara o banco de dados antes de iniciar a aplicação
            SQLiteDatabaseSetup.createTables();
        } catch (RuntimeException e) {
            // Se houver um erro crítico no banco de dados, exibe uma mensagem e fecha a app.
            JOptionPane.showMessageDialog(null, "Erro crítico ao inicializar o banco de dados:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Encerra a aplicação
        }

        // Inicia a interface gráfica na Event Dispatch Thread (EDT) para garantir a segurança das threads.
        SwingUtilities.invokeLater(() -> new TelaLogin());
    }
}