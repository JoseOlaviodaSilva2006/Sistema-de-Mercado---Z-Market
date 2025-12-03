package com.supermercado.controller;

import com.supermercado.dao.ProdutoDAO;
import com.supermercado.model.Produto;
import java.sql.SQLException;
import java.util.List;

public class ProdutoController {

    private final ProdutoDAO produtoDAO;

    public ProdutoController(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void cadastrarProduto(String nome, double preco, int quantidade) {
        if (nome == null || nome.trim().isEmpty() || preco < 0 || quantidade < 0) {
            throw new IllegalArgumentException("Dados do produto inválidos.");
        }
        // # TRATAMENTO DE EXCEÇÃO (CONTROLLER)
        // O padrão se repete: o controller captura a exceção específica do banco (SQLException)
        // e a transforma em uma exceção genérica (RuntimeException) para a View tratar.
        try {
            Produto produto = new Produto(0, nome, preco, quantidade);
            produtoDAO.salvar(produto);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    public void editarProduto(int id, String nome, double preco, int quantidade) {
        if (id <= 0 || nome == null || nome.trim().isEmpty() || preco < 0 || quantidade < 0) {
            throw new IllegalArgumentException("Dados do produto inválidos.");
        }
        try {
            Produto produto = new Produto(id, nome, preco, quantidade);
            produtoDAO.atualizar(produto);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao editar produto: " + e.getMessage());
        }
    }

    public void removerProduto(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do produto inválido.");
        }
        try {
            produtoDAO.remover(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage());
        }
    }

    public List<Produto> listarProdutos() {
        try {
            return produtoDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage());
        }
    }
}