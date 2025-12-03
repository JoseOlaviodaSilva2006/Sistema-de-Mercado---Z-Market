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

        // # TRATAMENTO DE EXCEÇÃO (CONTROLLER)
        // O controller tenta salvar o usuário através do DAO.
        try {
            Usuario novoUsuario = new Usuario(nome, cpf, isAdmin);
            usuarioDAO.salvar(novoUsuario);
            
        // Se o DAO lançar uma SQLException (ex: erro de conexão, CPF duplicado),
        // o controller a captura.
        } catch (SQLException e) {
            // Em vez de propagar a SQLException (que é uma exceção "checada"),
            // o controller a "traduz" para uma RuntimeException (exceção "não checada").
            // Isso simplifica o código na camada da View, que só precisa capturar RuntimeException.
            throw new RuntimeException(e.getMessage());
        }
    }
}