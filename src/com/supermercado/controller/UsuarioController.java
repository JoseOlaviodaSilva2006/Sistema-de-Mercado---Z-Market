package com.supermercado.controller;

import com.supermercado.dao.UsuarioDAO;
import com.supermercado.model.Usuario;
import java.sql.SQLException;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public void cadastrarUsuario(String nome, String cpf, boolean isAdmin) {
        if (nome == null || nome.trim().isEmpty() || cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e CPF não podem ser vazios.");
        }
        
        // Validação simples do formato do CPF
        if (!cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido. Deve conter 11 dígitos numéricos.");
        }

        try {
            Usuario novoUsuario = new Usuario(nome, cpf, isAdmin);
            usuarioDAO.salvar(novoUsuario);
        } catch (SQLException e) {
            // Relança a exceção para ser tratada pela View
            throw new RuntimeException(e.getMessage());
        }
    }
}