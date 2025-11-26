package com.supermercado.view;

import com.supermercado.controller.LoginController;
import com.supermercado.controller.UsuarioController;
import com.supermercado.dao.UsuarioDAO;
import com.supermercado.model.Usuario;
import com.supermercado.util.ComponentScaler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class TelaLogin extends JFrame {

    private final LoginController loginController;
    private JFormattedTextField cpfField;
    private JTextField nomeField;

    // Definição da paleta de cores
    private static final Color COR_FUNDO = new Color(245, 245, 245);
    private static final Color COR_PRINCIPAL = new Color(0, 123, 255);
    private static final Color COR_TEXTO = new Color(51, 51, 51);
    private static final Color COR_BOTAO_SECUNDARIO = new Color(108, 117, 125);

    public TelaLogin() {
        this.loginController = new LoginController(new UsuarioDAO());

        setTitle("Zé Market - Login");
        setMinimumSize(new Dimension(450, 350));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        
        pack();
        setVisible(true);

        new ComponentScaler(this).enableScaling();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COR_FUNDO);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Zé Market", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 36));
        titleLabel.setForeground(COR_PRINCIPAL);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COR_FUNDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: CPF
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("CPF:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfFormatter);
            cpfField.setFont(new Font("Roboto", Font.PLAIN, 14));
             cpfField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 12, 8, 12)
            ));
        } catch (ParseException e) {
            cpfField = new JFormattedTextField(); // Fallback
        }
        formPanel.add(cpfField, gbc);

        // Linha 1: Nome
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Nome:"), gbc);

        gbc.gridx = 1;
        nomeField = createTextField(20);
        formPanel.add(nomeField, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(COR_FUNDO);
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 5, 5, 5);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;

        JButton loginButton = createButton("Login", COR_PRINCIPAL);
        loginButton.addActionListener(e -> realizarLogin());
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        btnGbc.weightx = 1;
        buttonPanel.add(loginButton, btnGbc);
        getRootPane().setDefaultButton(loginButton);

        JButton registerButton = createButton("Cadastrar-se", COR_BOTAO_SECUNDARIO);
        registerButton.addActionListener(e -> abrirTelaCadastro());
        btnGbc.gridy = 1;
        buttonPanel.add(registerButton, btnGbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto", Font.BOLD, 14));
        label.setForeground(COR_TEXTO);
        return label;
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Roboto", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void realizarLogin() {
        String cpf = cpfField.getText().replaceAll("[^0-9]", "");
        String nome = nomeField.getText();
        
        try {
            Usuario usuario = loginController.autenticar(cpf, nome);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (usuario.isAdmin()) {
                    new TelaProdutosAdmin();
                } else {
                    new TelaCompraCliente();
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
