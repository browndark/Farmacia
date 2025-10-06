package com.brufarma.farmacia.util;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Validacao {
    public static boolean codigoValido(String codigo) { return codigo != null && codigo.matches("[A-Za-z0-9]{7}"); }
    public static boolean nomeValido(String nome) { return nome != null && nome.trim().length() >= 2; }
    public static boolean dataValidadeValida(LocalDate data) { return data != null && !data.isBefore(LocalDate.now()); }
    public static boolean quantidadeValida(int q) { return q >= 0; }
    public static boolean precoValido(BigDecimal p) { return p != null && p.compareTo(BigDecimal.ZERO) > 0; }
    public static boolean cnpjValido(String cnpj) { return cnpj != null && cnpj.matches("\\d{14}"); }

    public static boolean naoVazio(String s) { return s != null && !s.trim().isEmpty(); }

    public static int parseInt(String txt, String campo) {
        try { return Integer.parseInt(txt.trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Valor inválido em \"" + campo + "\"."); }
    }
    public static BigDecimal parseBig(String txt, String campo) {
        try { return new BigDecimal(txt.trim().replace(",", ".")); }
        catch (Exception e) { throw new IllegalArgumentException("Valor inválido em \"" + campo + "\"."); }
    }
}
