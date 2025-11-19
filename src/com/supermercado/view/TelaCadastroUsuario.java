package com.supermercado.view;

import com.supermercado.controller.UsuarioController;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class TelaCadastroUsuario extends JDialog {

    private final UsuarioController usuarioController;
    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JCheckBox adminCheckBox;
    private final JFrame owner;

    public TelaCadastroUsuario(JFrame owner, UsuarioController usuarioController) {
        super(owner, "Zé Market - Cadastro de Usuário", true);
        this.owner = owner;
        this.usuarioController = usuarioController;

        // O JDialog modal já bloqueia a janela owner, não é preciso um listener complexo.
        // A janela owner voltará a ter foco quando este dialog for fechado.

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // Rótulo não expande
        panel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Campo de texto expande
        nomeField = new JTextField(20);
        panel.add(nomeField, gbc);

        // Linha 1: CPF
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; // Rótulo não expande
        panel.add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Campo de texto expande
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfFormatter);
        } catch (ParseException e) {
            cpfField = new JFormattedTextField(); // Fallback
        }
        panel.add(cpfField, gbc);
        
        // Linha 2: Admin
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0; // Rótulo não expande
        panel.add(new JLabel("É Administrador?"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0; // Checkbox também pode expandir para preencher o espaço
        adminCheckBox = new JCheckBox();
        panel.add(adminCheckBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(e -> cadastrarUsuario());
        
        JButton voltarButton = new JButton("Voltar");
        voltarButton.addActionListener(e -> dispose()); // Apenas fecha o dialog
        
        buttonPanel.add(voltarButton);
        buttonPanel.add(cadastrarButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void cadastrarUsuario() {
        String nome = nomeField.getText();
        // Remove a máscara do CPF antes de enviar para o controller
        String cpf = cpfField.getText().replaceAll("[^0-9]", "");
        boolean isAdmin = adminCheckBox.isSelected();

        try {
            usuarioController.cadastrarUsuario(nome, cpf, isAdmin);
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            dispose(); // Apenas fecha o dialog, a tela de login abaixo receberá o foco.
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}