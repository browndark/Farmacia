package com.brufarma.farmacia.service;

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
                .sorted(Comparator.comparing(Medicamento::getDataValidade))
                .collect(Collectors.toList());
    }

    public List<Medicamento> estoqueBaixo() {
        return service.listarTodos().stream()
                .filter(m -> m.getQuantidadeEstoque() < 5)
                .collect(Collectors.toList());
    }
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
