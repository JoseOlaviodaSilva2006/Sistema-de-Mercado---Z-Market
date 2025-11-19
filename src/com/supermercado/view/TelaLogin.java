package com.supermercado.view;

import com.supermercado.controller.LoginController;
import com.supermercado.controller.UsuarioController;
import com.supermercado.dao.UsuarioDAO;
import com.supermercado.model.Usuario;
import com.supermercado.util.ComponentScaler;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class TelaLogin extends JFrame {

    private final LoginController loginController;
    private JFormattedTextField cpfField;
    private JTextField nomeField;

    public TelaLogin() {
        this.loginController = new LoginController(new UsuarioDAO());

        setTitle("Zé Market - Login");
        setMinimumSize(new Dimension(400, 250));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Zé Market", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: CPF
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // Rótulo não deve expandir
        formPanel.add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Campo de texto expande
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfFormatter);
        } catch (ParseException e) {
            cpfField = new JFormattedTextField(); // Fallback
        }
        formPanel.add(cpfField, gbc);

        // Linha 1: Nome
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; // Rótulo não deve expandir
        formPanel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Campo de texto expande
        nomeField = new JTextField();
        formPanel.add(nomeField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> realizarLogin());
        buttonPanel.add(loginButton);
        getRootPane().setDefaultButton(loginButton); // Define o botão de login como padrão

        JButton registerButton = new JButton("Cadastrar-se");
        registerButton.addActionListener(e -> abrirTelaCadastro());
        buttonPanel.add(registerButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        pack();
        setVisible(true);

        // Habilita o redimensionamento proporcional dos componentes
        new ComponentScaler(this).enableScaling();
    }

    private void realizarLogin() {
        // Remove a máscara do CPF antes de enviar para o controller
        String cpf = cpfField.getText().replaceAll("[^0-9]", "");
        String nome = nomeField.getText();
        
        try {
            Usuario usuario = loginController.autenticar(cpf, nome);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
                dispose();
                if (usuario.isAdmin()) {
                    new TelaProdutosAdmin(); // O construtor já chama setVisible(true)
                } else {
                    new TelaCompraCliente(); // O construtor já chama setVisible(true)
                }
            } else {
                JOptionPane.showMessageDialog(this, "CPF ou Nome inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaCadastro() {
        new TelaCadastroUsuario(this, new UsuarioController(new UsuarioDAO()));
    }
}