package com.supermercado.view;

import com.supermercado.controller.UsuarioController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class TelaCadastroUsuario extends JDialog {

    private final UsuarioController usuarioController;
    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JCheckBox adminCheckBox;
    private final JFrame owner;

    // Definição da paleta de cores
    private static final Color COR_FUNDO = new Color(245, 245, 245);
    private static final Color COR_PRINCIPAL = new Color(0, 123, 255);
    private static final Color COR_TEXTO = new Color(51, 51, 51);
    private static final Color COR_BOTAO_SUCESSO = new Color(40, 167, 69);

    public TelaCadastroUsuario(JFrame owner, UsuarioController usuarioController) {
        super(owner, "Zé Market - Cadastro de Usuário", true);
        this.owner = owner;
        this.usuarioController = usuarioController;

        initComponents();

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COR_FUNDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Cadastro de Novo Usuário");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(COR_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 1: Nome
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(createLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField = createTextField(20);
        panel.add(nomeField, gbc);

        // Linha 2: CPF
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(createLabel("CPF:"), gbc);
        gbc.gridx = 1;
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfFormatter);
            cpfField.setFont(new Font("Roboto", Font.PLAIN, 14));
            cpfField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
            ));
        } catch (ParseException e) {
            cpfField = new JFormattedTextField(); // Fallback
        }
        panel.add(cpfField, gbc);
        
        // Linha 3: Admin
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(createLabel("É Administrador?"), gbc);
        gbc.gridx = 1;
        adminCheckBox = new JCheckBox();
        adminCheckBox.setBackground(COR_FUNDO);
        panel.add(adminCheckBox, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(COR_FUNDO);
        
        JButton cadastrarButton = createButton("Cadastrar", COR_BOTAO_SUCESSO);
        cadastrarButton.addActionListener(e -> cadastrarUsuario());
        
        JButton voltarButton = createButton("Voltar", COR_TEXTO);
        voltarButton.addActionListener(e -> dispose());
        
        buttonPanel.add(voltarButton);
        buttonPanel.add(cadastrarButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
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
            new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void cadastrarUsuario() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText().replaceAll("[^0-9]", "");
        boolean isAdmin = adminCheckBox.isSelected();

        // # TRATAMENTO DE EXCEÇÃO (VIEW)
        // Captura erros de validação (ex: CPF inválido) ou de banco de dados (ex: CPF já existe)
        // que vêm das camadas de controller e DAO.
        try {
            usuarioController.cadastrarUsuario(nome, cpf, isAdmin);
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        // O erro é exibido ao usuário em uma caixa de diálogo.
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
