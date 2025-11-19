package com.supermercado.view;

import com.supermercado.controller.CompraController;
import com.supermercado.controller.LoginController;
import com.supermercado.controller.ProdutoController;
import com.supermercado.dao.ProdutoDAO;
import com.supermercado.model.Produto;
import com.supermercado.model.Usuario;
import com.supermercado.util.ComponentScaler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TelaCompraCliente extends JFrame {

    private final ProdutoController produtoController;
    private final CompraController compraController;
    private JTable tabelaProdutos, tabelaCarrinho;
    private DefaultTableModel modelProdutos, modelCarrinho;
    private JLabel totalLabel;

    public TelaCompraCliente() {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        this.produtoController = new ProdutoController(produtoDAO);
        this.compraController = new CompraController(produtoDAO);
        
        setTitle("Zé Market - Compras - Cliente: " + LoginController.getUsuarioLogado().getNome());
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        atualizarTabelaProdutos();
        setVisible(true);
    }

    private void initComponents() {
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Bem-vindo ao Zé Market!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Split Pane para dividir produtos e carrinho
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        splitPane.setLeftComponent(criarPainelProdutos());
        splitPane.setRightComponent(criarPainelCarrinho());

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        // Habilita o redimensionamento proporcional dos componentes
        new com.supermercado.util.ComponentScaler(this).enableScaling();
    }
    
    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis"));
        
        modelProdutos = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço", "Estoque"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(modelProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);
        
        JButton adicionarButton = new JButton("Adicionar ao Carrinho");
        adicionarButton.addActionListener(e -> adicionarAoCarrinho());
        painel.add(adicionarButton, BorderLayout.SOUTH);
        
        return painel;
    }
    
        private JPanel criarPainelCarrinho() {
    
            JPanel painel = new JPanel(new BorderLayout(5, 5));
    
            painel.setBorder(BorderFactory.createTitledBorder("Meu Carrinho"));
    
            
    
            modelCarrinho = new DefaultTableModel(new Object[]{"Produto", "Qtd", "Subtotal"}, 0) {
    
                @Override public boolean isCellEditable(int row, int column) { return false; }
    
            };
    
            tabelaCarrinho = new JTable(modelCarrinho);
    
            tabelaCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
            painel.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
    
            
    
            // Painel inferior do carrinho
    
            JPanel painelSul = new JPanel(new BorderLayout());
    
            totalLabel = new JLabel("Total: R$ 0.00");
    
            totalLabel.setFont(new Font("Serif", Font.BOLD, 18));
    
            totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
            painelSul.add(totalLabel, BorderLayout.WEST); // Alterado para WEST
    
            
    
            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
            botoes.add(new JButton("Remover") {{ addActionListener(e -> removerDoCarrinho()); }});
    
            botoes.add(new JButton("Finalizar Compra") {{ addActionListener(e -> finalizarCompra()); }});
    
            botoes.add(new JSeparator(SwingConstants.VERTICAL));
    
            botoes.add(new JButton("Deslogar") {{ addActionListener(e -> deslogar()); }});
    
            
    
            painelSul.add(botoes, BorderLayout.EAST);
    
            painel.add(painelSul, BorderLayout.SOUTH);
    
            
    
            return painel;
    
        }
    
    
    
        private void atualizarTabelaProdutos() {
    
            try {
    
                modelProdutos.setRowCount(0);
    
                List<Produto> produtos = produtoController.listarProdutos();
    
                for (Produto p : produtos) {
    
                    if (p.getQuantidadeEstoque() > 0) {
    
                        modelProdutos.addRow(new Object[]{p.getId(), p.getNome(), p.getPreco(), p.getQuantidadeEstoque()});
    
                    }
    
                }
    
            } catch (RuntimeException e) {
    
                JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    
            }
    
        }
    
    
    
        private void atualizarTabelaCarrinho() {
    
            modelCarrinho.setRowCount(0);
    
            Map<Produto, Integer> itens = compraController.getCarrinho().getItens();
    
            for (Map.Entry<Produto, Integer> entry : itens.entrySet()) {
    
                Produto p = entry.getKey();
    
                int qtd = entry.getValue();
    
                modelCarrinho.addRow(new Object[]{p.getNome(), qtd, p.getPreco() * qtd});
    
            }
    
            totalLabel.setText(String.format("Total: R$ %.2f", compraController.getCarrinho().calcularTotal()));
    
        }
    
        
    
        private void adicionarAoCarrinho() {
    
            int selectedRow = tabelaProdutos.getSelectedRow();
    
            if (selectedRow == -1) {
    
                JOptionPane.showMessageDialog(this, "Selecione um produto para adicionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
    
                return;
    
            }
    
            
    
            try {
    
                int id = (int) modelProdutos.getValueAt(selectedRow, 0);
    
                Produto produto = produtoController.listarProdutos().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    
    
    
                if (produto == null) {
    
                    JOptionPane.showMessageDialog(this, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
    
                    return;
    
                }
    
    
    
                String qtdStr = JOptionPane.showInputDialog(this, "Digite a quantidade:", "Adicionar ao Carrinho", JOptionPane.PLAIN_MESSAGE);
    
                if (qtdStr == null) return; // Cancelado pelo usuário
    
    
    
                int quantidade = Integer.parseInt(qtdStr);
    
                compraController.adicionarAoCarrinho(produto, quantidade);
    
                atualizarTabelaCarrinho();
    
    
    
            } catch (NumberFormatException ex) {
    
                JOptionPane.showMessageDialog(this, "Por favor, digite um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
    
            } catch (RuntimeException ex) {
    
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    
            }
    
        }
    
        
    
        private void removerDoCarrinho() {
    
            int selectedRow = tabelaCarrinho.getSelectedRow();
    
            if (selectedRow == -1) {
    
                JOptionPane.showMessageDialog(this, "Selecione um item do carrinho para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
    
                return;
    
            }
    
            
    
            String nomeProduto = (String) modelCarrinho.getValueAt(selectedRow, 0);
    
            
    
            compraController.getCarrinho().getItens().keySet().stream()
    
                .filter(p -> p.getNome().equals(nomeProduto))
    
                .findFirst()
    
                .ifPresent(produtoParaRemover -> {
    
                    compraController.removerDoCarrinho(produtoParaRemover);
    
                    atualizarTabelaCarrinho();
    
                });
    
        }
    
    
    
        private void finalizarCompra() {
    
            if (compraController.getCarrinho().getItens().isEmpty()) {
    
                JOptionPane.showMessageDialog(this, "O carrinho está vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
    
                return;
    
            }
    
    
    
            double total = compraController.getCarrinho().calcularTotal();
    
            String mensagem = String.format("Deseja finalizar a compra no valor de R$ %.2f?", total);
    
            int confirm = JOptionPane.showConfirmDialog(this, mensagem, "Confirmação", JOptionPane.YES_NO_OPTION);
    
    
    
            if (confirm == JOptionPane.YES_OPTION) {
    
                try {
    
                    Usuario usuarioLogado = LoginController.getUsuarioLogado();
    
                    String notaFiscal = compraController.gerarNotaFiscal(usuarioLogado);
    
                    
    
                    compraController.finalizarCompra();
    
    
    
                    JTextArea textArea = new JTextArea(notaFiscal);
    
                    textArea.setEditable(false);
    
                    JScrollPane scrollPane = new JScrollPane(textArea);
    
                    scrollPane.setPreferredSize(new Dimension(400, 300));
    
                    JOptionPane.showMessageDialog(this, scrollPane, "Compra Realizada com Sucesso!", JOptionPane.INFORMATION_MESSAGE);
    
                    
    
                    atualizarTabelaProdutos();
    
                    atualizarTabelaCarrinho();
    
                } catch (RuntimeException ex) {
    
                    JOptionPane.showMessageDialog(this, "Não foi possível finalizar a compra.\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    
                    atualizarTabelaProdutos(); // Re-sincroniza o estoque
    
                }
    
            }
    
        }
    
        
    
        private void deslogar() {
    
            dispose();
    
            new TelaLogin();
    
        }
    
    }
    
    