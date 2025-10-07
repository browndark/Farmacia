package com.brufarma.farmacia.service;
<<<<<<< HEAD
import com.brufarma.farmacia.model.Medicamento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class RelatorioService {
    private final MedicamentoService service;


    public RelatorioService(MedicamentoService service) {
        this.service = service;
    }


    // 1) Próximos 30 dias
    public List<Medicamento> proximosAoVencimento() {
        LocalDate limite = LocalDate.now().plusDays(30);
        return service.listarTodos().stream()
                .filter(m -> m.getDataValidade()!=null && !m.getDataValidade().isAfter(limite))
=======

import com.brufarma.farmacia.model.Medicamento;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {
    private final MedicamentoService service;
    public RelatorioService(MedicamentoService service) { this.service = service; }

    // Vencem em até 30 dias
    public List<Medicamento> proximosAoVencimento() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);
        return service.listarTodos().stream()
                .filter(m -> m.getDataValidade() != null)
                .filter(m -> !m.getDataValidade().isAfter(limite))
>>>>>>> e3bb8bc7d66f36d78b2423bcafc56ff4db95dda3
                .sorted(Comparator.comparing(Medicamento::getDataValidade))
                .collect(Collectors.toList());
    }

<<<<<<< HEAD

    // 2) Estoque baixo (<5)
=======
>>>>>>> e3bb8bc7d66f36d78b2423bcafc56ff4db95dda3
    public List<Medicamento> estoqueBaixo() {
        return service.listarTodos().stream()
                .filter(m -> m.getQuantidadeEstoque() < 5)
                .collect(Collectors.toList());
    }
<<<<<<< HEAD


    // 3) Valor total por fornecedor
    public Map<String, BigDecimal> valorTotalPorFornecedor() {
        return service.listarTodos().stream()
                .filter(m -> m.getFornecedor()!=null)
                .collect(Collectors.groupingBy(
                        m -> m.getFornecedor().getRazaoSocial(),
                        Collectors.reducing(BigDecimal.ZERO,
                                m -> m.getPreco().multiply(BigDecimal.valueOf(m.getQuantidadeEstoque())),
                                BigDecimal::add)
                ));
    }


    // 4) Contagem por controlado vs não controlado
    public Map<String, Long> controladosVsNao() {
        return service.listarTodos().stream()
                .collect(Collectors.groupingBy(
                        m -> m.isControlado() ? "Controlados" : "Não controlados",
                        Collectors.counting()
                ));
    }
}
=======
    public Map<String, Double> valorTotalPorFornecedor() {
        return service.listarTodos().stream()
                .collect(Collectors.groupingBy(
                        m -> m.getFornecedor() == null ? "—" : m.getFornecedor().getRazaoSocial(),
                        Collectors.summingDouble(m -> m.getPreco().doubleValue() * m.getQuantidadeEstoque())
                ));
    }
    public Map<String, Long> controladosVsNao() {
        return service.listarTodos().stream()
                .collect(Collectors.groupingBy(m -> m.isControlado() ? "controlados" : "naoControlados",
                        Collectors.counting()));
    }
}
>>>>>>> e3bb8bc7d66f36d78b2423bcafc56ff4db95dda3
