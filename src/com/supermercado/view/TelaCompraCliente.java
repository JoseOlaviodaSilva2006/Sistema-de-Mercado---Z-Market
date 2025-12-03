package com.supermercado.view;

import com.supermercado.controller.CompraController;
import com.supermercado.controller.LoginController;
import com.supermercado.controller.ProdutoController;
import com.supermercado.dao.ProdutoDAO;
import com.supermercado.model.Produto;
import com.supermercado.model.Usuario;
import com.supermercado.util.ComponentScaler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TelaCompraCliente extends JFrame {

    private final ProdutoController produtoController;
    private final CompraController compraController;
    private JTable tabelaProdutos, tabelaCarrinho;
    private DefaultTableModel modelProdutos, modelCarrinho;
    private JLabel totalLabel;
    private JTextField quantidadeField;

    // Definição da paleta de cores
    private static final Color COR_FUNDO = new Color(245, 245, 245); // Cinza claro
    private static final Color COR_PRINCIPAL = new Color(0, 123, 255); // Azul
    private static final Color COR_SECUNDARIA = new Color(231, 241, 255); // Azul claro
    private static final Color COR_TEXTO = new Color(51, 51, 51); // Cinza escuro
    private static final Color COR_BOTAO_SUCESSO = new Color(40, 167, 69); // Verde
    private static final Color COR_BOTAO_PERIGO = new Color(220, 53, 69); // Vermelho

    public TelaCompraCliente() {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        this.produtoController = new ProdutoController(produtoDAO);
        this.compraController = new CompraController(produtoDAO);
        
        setTitle("Zé Market - Compras - Cliente: " + LoginController.getUsuarioLogado().getNome());
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        atualizarTabelaProdutos();
        setVisible(true);
    }

    private void initComponents() {
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COR_FUNDO);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Bem-vindo ao Zé Market!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 32));
        titleLabel.setForeground(COR_TEXTO);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Split Pane para dividir produtos e carrinho
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(10);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(criarPainelProdutos());
        splitPane.setRightComponent(criarPainelCarrinho());

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        // # PARTE RESPONSIVA
        // A linha abaixo ativa a funcionalidade de responsividade para esta tela.
        new ComponentScaler(this).enableScaling();
    }
    
    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_PRINCIPAL, 1), "Produtos Disponíveis", 0, 0, new Font("Roboto", Font.BOLD, 18), COR_PRINCIPAL));
        
        modelProdutos = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço", "Estoque"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(modelProdutos);
        configurarTabela(tabelaProdutos);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);
        
        // Painel para adicionar ao carrinho
        JPanel painelAdicionar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        painelAdicionar.setBackground(COR_FUNDO);
        
        quantidadeField = new JTextField("1", 5);
        quantidadeField.setPreferredSize(new Dimension(80, 30));
        
        JButton adicionarButton = criarBotao("Adicionar ao Carrinho", COR_PRINCIPAL, "resources/icons/add.png");
        adicionarButton.addActionListener(e -> adicionarAoCarrinho());
        
        painelAdicionar.add(new JLabel("Qtd:"));
        painelAdicionar.add(quantidadeField);
        painelAdicionar.add(adicionarButton);
        
        painel.add(painelAdicionar, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private JPanel criarPainelCarrinho() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_BOTAO_SUCESSO, 1), "Meu Carrinho", 0, 0, new Font("Roboto", Font.BOLD, 18), COR_BOTAO_SUCESSO));
        
        modelCarrinho = new DefaultTableModel(new Object[]{"Produto", "Qtd", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCarrinho = new JTable(modelCarrinho);
        configurarTabela(tabelaCarrinho);
        painel.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        
        // Painel inferior do carrinho
        JPanel painelSul = new JPanel(new BorderLayout(10, 0));
        painelSul.setBackground(COR_FUNDO);
        painelSul.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        totalLabel = new JLabel("Total: R$ 0.00");
        totalLabel.setFont(new Font("Roboto", Font.BOLD, 22));
        totalLabel.setForeground(COR_TEXTO);
        painelSul.add(totalLabel, BorderLayout.WEST);
        
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setBackground(COR_FUNDO);
        
        JButton removerButton = criarBotao("Remover", COR_BOTAO_PERIGO, "resources/icons/remove.png");
        removerButton.addActionListener(e -> removerDoCarrinho());
        
        JButton finalizarButton = criarBotao("Finalizar Compra", COR_BOTAO_SUCESSO, "resources/icons/finish.png");
        finalizarButton.addActionListener(e -> finalizarCompra());

        JButton deslogarButton = criarBotao("Deslogar", COR_TEXTO, "resources/icons/logout.png");
        deslogarButton.addActionListener(e -> deslogar());
        
        botoes.add(removerButton);
        botoes.add(finalizarButton);
        botoes.add(new JSeparator(SwingConstants.VERTICAL));
        botoes.add(deslogarButton);
        
        painelSul.add(botoes, BorderLayout.EAST);
        painel.add(painelSul, BorderLayout.SOUTH);
        
        return painel;
    }

    private void configurarTabela(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 16));
        header.setBackground(COR_SECUNDARIA);
        header.setForeground(COR_PRINCIPAL);
        header.setBorder(BorderFactory.createLineBorder(COR_PRINCIPAL));
        
        table.setSelectionBackground(COR_PRINCIPAL);
        table.setSelectionForeground(Color.WHITE);
    }
    
    private JButton criarBotao(String texto, Color cor, String iconPath) {
        JButton button = new JButton(texto);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBackground(cor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // A lógica para carregar ícones foi removida para evitar erros,
        // pois os arquivos de imagem não foram encontrados no projeto.
        // O parâmetro iconPath é mantido para não quebrar as chamadas do método,
        // mas não é mais utilizado.
        
        return button;
    }
    
    private void atualizarTabelaProdutos() {
        // # TRATAMENTO DE EXCEÇÃO (VIEW)
        // Captura erros que podem ocorrer ao buscar a lista de produtos no banco de dados.
        try {
            modelProdutos.setRowCount(0);
            List<Produto> produtos = produtoController.listarProdutos();
            for (Produto p : produtos) {
                if (p.getQuantidadeEstoque() > 0) {
                    modelProdutos.addRow(new Object[]{p.getId(), p.getNome(), String.format("R$ %.2f", p.getPreco()), p.getQuantidadeEstoque()});
                }
            }
        } catch (RuntimeException e) {
            exibirErro("Erro ao carregar produtos: " + e.getMessage());
        }
    }
    
    private void atualizarTabelaCarrinho() {
        modelCarrinho.setRowCount(0);
        Map<Produto, Integer> itens = compraController.getCarrinho().getItens();
        for (Map.Entry<Produto, Integer> entry : itens.entrySet()) {
            Produto p = entry.getKey();
            int qtd = entry.getValue();
            modelCarrinho.addRow(new Object[]{p.getNome(), qtd, String.format("R$ %.2f", p.getPreco() * qtd)});
        }
        totalLabel.setText(String.format("Total: R$ %.2f", compraController.getCarrinho().calcularTotal()));
    }
    
    private void adicionarAoCarrinho() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            exibirAviso("Selecione um produto para adicionar.");
            return;
        }

        // # TRATAMENTO DE EXCEÇÃO (VIEW)
        // Captura erros de conversão de número (se o usuário digitar um texto em "Qtd")
        // e também erros de regras de negócio (ex: estoque insuficiente) vindos do controller.
        try {
            String text = quantidadeField.getText();
            int quantidade = Integer.parseInt(text);

            if (quantidade <= 0) {
                exibirAviso("A quantidade deve ser maior que zero.");
                return;
            }

            int id = (int) tabelaProdutos.getValueAt(selectedRow, 0);
            Produto produto = produtoController.listarProdutos().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            
            if (produto == null) {
                exibirErro("Produto não encontrado.");
                return;
            }

            compraController.adicionarAoCarrinho(produto, quantidade);
            atualizarTabelaCarrinho();
            quantidadeField.setText("1"); // Reseta o campo

        } catch (NumberFormatException e) {
            exibirErro("A quantidade inserida é inválida. Por favor, insira um número válido.");
        } catch (RuntimeException ex) {
            exibirErro(ex.getMessage());
        }
    }
    
    private void removerDoCarrinho() {
        int selectedRow = tabelaCarrinho.getSelectedRow();
        if (selectedRow == -1) {
            exibirAviso("Selecione um item do carrinho para remover.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este item?", "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
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
            exibirAviso("O carrinho está vazio.");
            return;
        }
        
        double total = compraController.getCarrinho().calcularTotal();
        String mensagem = String.format("Deseja finalizar a compra no valor de R$ %.2f?", total);
        int confirm = JOptionPane.showConfirmDialog(this, mensagem, "Confirmação de Compra", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // # TRATAMENTO DE EXCEÇÃO (VIEW)
            // Captura erros que podem acontecer durante a atualização do estoque no banco de dados.
            // Por exemplo, se o estoque de um produto acabou enquanto o usuário ainda estava comprando.
            try {
                Usuario usuarioLogado = LoginController.getUsuarioLogado();
                String notaFiscal = compraController.gerarNotaFiscal(usuarioLogado);
                compraController.finalizarCompra();
                
                JTextArea textArea = new JTextArea(notaFiscal);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));
                JOptionPane.showMessageDialog(this, scrollPane, "Compra Realizada com Sucesso!", JOptionPane.INFORMATION_MESSAGE);
                
                atualizarTabelaProdutos();
                atualizarTabelaCarrinho();
                
            } catch (RuntimeException ex) {
                exibirErro("Não foi possível finalizar a compra.\n" + ex.getMessage());
                atualizarTabelaProdutos();
            }
        }
    }
    
    private void deslogar() {
        dispose();
        new TelaLogin();
    }
    
    // Métodos utilitários para feedback visual
    private void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    private void exibirAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}