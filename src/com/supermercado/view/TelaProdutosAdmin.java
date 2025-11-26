package com.supermercado.view;

import com.supermercado.controller.LoginController;
import com.supermercado.controller.ProdutoController;
import com.supermercado.dao.ProdutoDAO;
import com.supermercado.model.Produto;
import com.supermercado.util.ComponentScaler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TelaProdutosAdmin extends JFrame {

    private final ProdutoController produtoController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nomeField, precoField, estoqueField;

    // Definição da paleta de cores
    private static final Color COR_FUNDO = new Color(245, 245, 245);
    private static final Color COR_PRINCIPAL = new Color(0, 123, 255);
    private static final Color COR_SECUNDARIA = new Color(231, 241, 255);
    private static final Color COR_TEXTO = new Color(51, 51, 51);
    private static final Color COR_BOTAO_SUCESSO = new Color(40, 167, 69);
    private static final Color COR_BOTAO_PERIGO = new Color(220, 53, 69);
    private static final Color COR_BOTAO_AVISO = new Color(255, 193, 7);

    public TelaProdutosAdmin() {
        this.produtoController = new ProdutoController(new ProdutoDAO());

        setTitle("Zé Market - Painel do Administrador");
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        atualizarTabela();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(COR_FUNDO);
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        // Painel Superior (Formulário)
        JPanel formPanel = createFormPanel();
        
        // Painel Central (Tabela)
        JScrollPane tablePanel = createTablePanel();
        
        // Painel Inferior (Botões de Ação)
        JPanel actionPanel = createActionPanel();

        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        new ComponentScaler(this).enableScaling();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COR_FUNDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_PRINCIPAL, 1), "Gerenciar Produto", 
            0, 0, new Font("Roboto", Font.BOLD, 18), COR_PRINCIPAL));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(createLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; nomeField = createTextField(25); panel.add(nomeField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; panel.add(createLabel("Preço:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.5; precoField = createTextField(10); panel.add(precoField, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0; panel.add(createLabel("Estoque:"), gbc);
        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 0.5; estoqueField = createTextField(10); panel.add(estoqueField, gbc);

        return panel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        configurarTabela(table);
        
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                preencherFormulario();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return scrollPane;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setBackground(COR_FUNDO);
        
        JButton addButton = createButton("Adicionar", COR_BOTAO_SUCESSO);
        addButton.addActionListener(e -> adicionarProduto());
        
        JButton editButton = createButton("Salvar Edição", COR_PRINCIPAL);
        editButton.addActionListener(e -> editarProduto());
        
        JButton deleteButton = createButton("Remover", COR_BOTAO_PERIGO);
        deleteButton.addActionListener(e -> removerProduto());
        
        JButton clearButton = createButton("Limpar", COR_BOTAO_AVISO);
        clearButton.addActionListener(e -> limparFormulario());

        JButton logoutButton = createButton("Deslogar", COR_TEXTO);
        logoutButton.addActionListener(e -> deslogar());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(logoutButton);
        return panel;
    }

    private void configurarTabela(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.setGridColor(new Color(220, 220, 220));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 16));
        header.setBackground(COR_SECUNDARIA);
        header.setForeground(COR_PRINCIPAL);
        header.setBorder(BorderFactory.createLineBorder(COR_PRINCIPAL));
        
        table.setSelectionBackground(COR_PRINCIPAL);
        table.setSelectionForeground(Color.WHITE);
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
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void atualizarTabela() {
        try {
            tableModel.setRowCount(0);
            List<Produto> produtos = produtoController.listarProdutos();
            for (Produto p : produtos) {
                tableModel.addRow(new Object[]{p.getId(), p.getNome(), String.format("R$ %.2f", p.getPreco()), p.getQuantidadeEstoque()});
            }
        } catch (RuntimeException e) {
            exibirErro("Erro ao carregar produtos: " + e.getMessage());
        }
    }
    
    private void limparFormulario() {
        nomeField.setText("");
        precoField.setText("");
        estoqueField.setText("");
        table.clearSelection();
    }
    
    private void preencherFormulario() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            nomeField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            String precoStr = tableModel.getValueAt(selectedRow, 2).toString().replace("R$ ", "").replace(",", ".");
            precoField.setText(precoStr);
            estoqueField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    private void adicionarProduto() {
        if (nomeField.getText().trim().isEmpty() || precoField.getText().trim().isEmpty() || estoqueField.getText().trim().isEmpty()) {
            exibirAviso("Todos os campos devem ser preenchidos.");
            return;
        }
        try {
            String nome = nomeField.getText();
            double preco = Double.parseDouble(precoField.getText().replace(",", "."));
            int estoque = Integer.parseInt(estoqueField.getText());
            
            produtoController.cadastrarProduto(nome, preco, estoque);
            
            JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();
            limparFormulario();
        } catch (NumberFormatException ex) {
            exibirErro("Preço e estoque devem ser números válidos.");
        } catch (RuntimeException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void editarProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            exibirAviso("Selecione um produto para editar.");
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nome = nomeField.getText();
            double preco = Double.parseDouble(precoField.getText().replace(",", "."));
            int estoque = Integer.parseInt(estoqueField.getText());

            produtoController.editarProduto(id, nome, preco, estoque);

            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();
            limparFormulario();
        } catch (NumberFormatException ex) {
            exibirErro("Preço e estoque devem ser números válidos.");
        } catch (RuntimeException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void removerProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            exibirAviso("Selecione um produto para remover.");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este produto?", "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                produtoController.removerProduto(id);
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                atualizarTabela();
                limparFormulario();
            } catch (RuntimeException ex) {
                exibirErro(ex.getMessage());
            }
        }
    }

    private void deslogar() {
        dispose();
        new TelaLogin();
    }
    
    private void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    private void exibirAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
