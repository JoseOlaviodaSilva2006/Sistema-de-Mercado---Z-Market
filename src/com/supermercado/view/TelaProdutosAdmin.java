package com.supermercado.view;

import com.supermercado.controller.LoginController;
import com.supermercado.controller.ProdutoController;
import com.supermercado.dao.ProdutoDAO;
import com.supermercado.model.Produto;
import com.supermercado.util.ComponentScaler;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaProdutosAdmin extends JFrame {

    private final ProdutoController produtoController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nomeField, precoField, estoqueField;
    private JButton addButton, editButton, deleteButton, clearButton, logoutButton;

    public TelaProdutosAdmin() {
        this.produtoController = new ProdutoController(new ProdutoDAO());

        setTitle("Zé Market - Painel do Administrador");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        atualizarTabela();
        setVisible(true);
    }

    private void initComponents() {
        // Layout principal
        setLayout(new BorderLayout(10, 10));
        
        // Painel Superior (Formulário)
        JPanel formPanel = createFormPanel();
        
        // Painel Central (Tabela)
        JScrollPane tablePanel = createTablePanel();
        
        // Painel Inferior (Botões de Ação)
        JPanel actionPanel = createActionPanel();

        // Adiciona os painéis à janela
        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Habilita o redimensionamento proporcional dos componentes
        new com.supermercado.util.ComponentScaler(this).enableScaling();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gerenciar Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Coluna 0: Labels
        gbc.gridx = 0;
        gbc.weightx = 0; // Rótulos não devem expandir
        gbc.gridy = 0; panel.add(new JLabel("Nome:"), gbc);
        gbc.gridy = 1; panel.add(new JLabel("Preço:"), gbc);
        gbc.gridy = 2; panel.add(new JLabel("Estoque:"), gbc);

        // Coluna 1: Fields
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Campos de texto devem expandir
        gbc.gridy = 0; nomeField = new JTextField(20); panel.add(nomeField, gbc);
        gbc.gridy = 1; precoField = new JTextField(); panel.add(precoField, gbc);
        gbc.gridy = 2; estoqueField = new JTextField(); panel.add(estoqueField, gbc);
        
        // Coluna 2: Botão Adicionar
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0; // Botão não deve expandir horizontalmente
        addButton = new JButton("Adicionar");
        addButton.addActionListener(e -> adicionarProduto());
        panel.add(addButton, gbc);

        return panel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                preencherFormulario();
            }
        });
        return new JScrollPane(table);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        editButton = new JButton("Salvar Edição");
        editButton.addActionListener(e -> editarProduto());
        
        deleteButton = new JButton("Remover");
        deleteButton.addActionListener(e -> removerProduto());
        
        clearButton = new JButton("Limpar Formulário");
        clearButton.addActionListener(e -> limparFormulario());

        logoutButton = new JButton("Deslogar");
        logoutButton.addActionListener(e -> deslogar());
        
        panel.add(clearButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(logoutButton);
        return panel;
    }

    private void atualizarTabela() {
        try {
            tableModel.setRowCount(0);
            List<Produto> produtos = produtoController.listarProdutos();
            for (Produto p : produtos) {
                tableModel.addRow(new Object[]{p.getId(), p.getNome(), "R$ " + String.format("%.2f", p.getPreco()), p.getQuantidadeEstoque()});
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
            // Remove o "R$ " antes de preencher o campo
            String precoStr = tableModel.getValueAt(selectedRow, 2).toString().replace("R$ ", "").replace(",", ".");
            precoField.setText(precoStr);
            estoqueField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    private void adicionarProduto() {
        try {
            String nome = nomeField.getText();
            double preco = Double.parseDouble(precoField.getText().replace(",", "."));
            int estoque = Integer.parseInt(estoqueField.getText());
            
            produtoController.cadastrarProduto(nome, preco, estoque);
            
            JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!");
            atualizarTabela();
            limparFormulario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e estoque devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nome = nomeField.getText();
            double preco = Double.parseDouble(precoField.getText().replace(",", "."));
            int estoque = Integer.parseInt(estoqueField.getText());

            produtoController.editarProduto(id, nome, preco, estoque);

            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            atualizarTabela();
            limparFormulario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e estoque devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                produtoController.removerProduto(id);
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!");
                atualizarTabela();
                limparFormulario();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deslogar() {
        // Apenas fecha a janela atual e abre a de login
        dispose();
        new TelaLogin();
    }
}