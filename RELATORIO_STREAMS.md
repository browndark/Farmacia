# Relatório — Processamento com Stream API (Branch 2)


Este documento descreve os **processos** implementados com `java.util.stream`.


## 1. Filtrar medicamentos próximos ao vencimento (30 dias)
**Objetivo:** identificar itens que precisam de atenção.
**Código-chave:**
```java
LocalDate limite = LocalDate.now().plusDays(30);
lista.stream()
.filter(m -> m.getDataValidade()!=null && !m.getDataValidade().isAfter(limite))
.sorted(Comparator.comparing(Medicamento::getDataValidade))
.toList();

Operadores usados: filter, sorted, toList.

Filtrar medicamentos com estoque baixo (<5)

Código-chave:
lista.stream().filter(m -> m.getQuantidadeEstoque() < 5).toList();
Operadores: filter.

3. Calcular valor total do estoque por fornecedor

Ideia: preço * quantidade por item e somar por fornecedor. Código-chave:

lista.stream()
.filter(m -> m.getFornecedor()!=null)
.collect(Collectors.groupingBy(
m -> m.getFornecedor().getRazaoSocial(),
Collectors.reducing(BigDecimal.ZERO,
m -> m.getPreco().multiply(BigDecimal.valueOf(m.getQuantidadeEstoque())),
BigDecimal::add)));

Operadores: filter, collect, groupingBy, reducing.

. Listar controlados vs não controlados (contagem)

Código-chave:
lista.stream()
.collect(Collectors.groupingBy(
m -> m.isControlado()?"Controlados":"Não controlados",
Collectors.counting()));
Operadores: collect, groupingBy, counting.

Observações

Todos os relatórios usam a lista em memória do MedicamentoService carregada do CSV.

Após qualquer CRUD, o CSV é regravado.

### `README.md` (append do Branch 2)
```md
## Relatórios (Branch 2)
- **Próximos ao vencimento (30 dias)**
- **Estoque baixo (<5)**
- **Valor total por fornecedor**
- **Controlados vs não controlados**


Veja `RELATORIO_STREAMS.md` para os códigos e descrição.

CSV (exemplo de linha)
ABC1234;Dipirona;Analgésico;Metamizol;2026-01-10;10;12.90;false;12345678000199;Farmabras;11999999999;contato@farmabras.com;São Paulo;SP
