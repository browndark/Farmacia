package com.brufarma.farmacia.service;
import com.brufarma.farmacia.model.Medicamento;
import com.brufarma.farmacia.util.Validacao;


import java.util.*;


public class MedicamentoService {
    private final CsvRepository repo;
    private final List<Medicamento> cache;


    public MedicamentoService(CsvRepository repo) {
        this.repo = repo;
        this.cache = new ArrayList<>(repo.carregar());
    }


    public List<Medicamento> listarTodos() { return new ArrayList<>(cache); }


    public Optional<Medicamento> buscarPorCodigo(String codigo) {
        return cache.stream().filter(m -> m.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }


    public boolean cadastrar(Medicamento m) {
        if (!Validacao.codigoValido(m.getCodigo())) return false;
        if (buscarPorCodigo(m.getCodigo()).isPresent()) return false;
        cache.add(m);
        repo.salvarTodos(cache);
        return true;
    }


    public boolean atualizar(Medicamento m) {
        Optional<Medicamento> opt = buscarPorCodigo(m.getCodigo());
        if (opt.isEmpty()) return false;
        int i = cache.indexOf(opt.get());
        cache.set(i, m);
        repo.salvarTodos(cache);
        return true;
    }


    public boolean excluir(String codigo) {
        boolean removed = cache.removeIf(x -> x.getCodigo().equalsIgnoreCase(codigo));
        if (removed) repo.salvarTodos(cache);
        return removed;
    }
}
