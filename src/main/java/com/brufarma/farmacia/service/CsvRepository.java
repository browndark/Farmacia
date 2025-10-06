package com.brufarma.farmacia.service;

import com.brufarma.farmacia.model.Fornecedor;
import com.brufarma.farmacia.model.Medicamento;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvRepository {
    private final File arquivo;

    public CsvRepository(File arquivo) {
        this.arquivo = arquivo;
    }

    public List<Medicamento> carregar() {
        List<Medicamento> lista = new ArrayList<>();
        if (!arquivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {

            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                // Mantém campos vazios (ex.: ...;;...)
                String[] c = linha.split(";", -1);

                // Campos mínimos (8): até 'controlado'
                if (c.length < 8) continue; // ignora linha inválida

                String codigo          = c[0];
                String nome            = c[1];
                String descricao       = c[2];
                String principioAtivo  = c[3];
                LocalDate validade     = c[4].isBlank() ? null : LocalDate.parse(c[4]);
                int quantidade         = c[5].isBlank() ? 0 : Integer.parseInt(c[5]);
                BigDecimal preco       = c[6].isBlank() ? BigDecimal.ZERO : new BigDecimal(c[6]);
                boolean controlado     = c[7].isBlank() ? false : Boolean.parseBoolean(c[7]);

                Fornecedor f;
                if (c.length >= 14) {
                    f = new Fornecedor(
                            c[8],  // cnpj
                            c[9],  // razao
                            c[10], // tel
                            c[11], // email
                            c[12], // cidade
                            c[13]  // estado
                    );
                } else {
                    // cria fornecedor “vazio” se vier só 8 colunas
                    f = new Fornecedor("", "", "", "", "", "");
                }

                lista.add(new Medicamento(
                        codigo, nome, descricao, principioAtivo, validade,
                        quantidade, preco, controlado, f
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void salvarTodos(List<Medicamento> lista) {
        try {
            arquivo.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arquivo, false), StandardCharsets.UTF_8))) {
                for (Medicamento m : lista) {
                    Fornecedor f = (m.getFornecedor() == null)
                            ? new Fornecedor("", "", "", "", "", "")
                            : m.getFornecedor();

                    String linha = String.join(";",
                            nv(m.getCodigo()),
                            nv(m.getNome()),
                            nv(m.getDescricao()),
                            nv(m.getPrincipioAtivo()),
                            m.getDataValidade() == null ? "" : m.getDataValidade().toString(),
                            String.valueOf(m.getQuantidadeEstoque()),
                            m.getPreco() == null ? "" : m.getPreco().toString(),
                            String.valueOf(m.isControlado()),
                            nv(f.getCnpj()),
                            nv(f.getRazaoSocial()),
                            nv(f.getTelefone()),
                            nv(f.getEmail()),
                            nv(f.getCidade()),
                            nv(f.getEstado())
                    );
                    bw.write(linha);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String nv(String s) { return s == null ? "" : s; }
}
