Farmácia — Estoque de Medicamentos

Este projeto é uma aplicação JavaFX que gerencia o estoque de medicamentos, com funcionalidades de CRUD (Criar, Ler, Atualizar e Excluir), além de relatórios utilizando Stream API do Java para filtrar e agrupar informações de medicamentos. A persistência dos dados é feita com o uso de um arquivo CSV.

Funcionalidades
Botões e Funções

Cadastrar

Descrição: Cadastra um novo medicamento no sistema. Para isso, são preenchidos os campos como Código, Nome, Descrição, Princípio Ativo, Quantidade, Preço e informações sobre o Fornecedor (CNPJ, Razão Social, Telefone, etc.).

Validações:

O código do medicamento deve ser único (7 caracteres).

A quantidade deve ser um número positivo.

O preço deve ser um valor numérico e maior que zero.

O CNPJ do fornecedor deve ter 14 dígitos.

Fluxo: Ao clicar em Cadastrar, o medicamento é adicionado ao arquivo CSV e a tabela é atualizada automaticamente.

Buscar

Descrição: Busca um medicamento no sistema pelo Código.

Fluxo: O código é digitado e, ao clicar em Buscar, o sistema procura o medicamento no arquivo CSV e exibe as informações do item encontrado.

Atualizar

Descrição: Permite atualizar as informações de um medicamento já cadastrado, como quantidade, preço ou descrição.

Fluxo: O medicamento é buscado e, se encontrado, o usuário pode alterar os dados desejados. A tabela e o arquivo CSV são atualizados.

Excluir

Descrição: Exclui um medicamento do sistema.

Fluxo: O medicamento é buscado pelo Código e, se encontrado, é removido do arquivo CSV e da tabela exibida.

Vencem ≤30d

Descrição: Exibe um alerta com a lista de medicamentos que vencem em até 30 dias.

Fluxo: O sistema filtra os medicamentos e exibe, em um alerta, todos os medicamentos cuja validade seja dentro dos próximos 30 dias. A contagem de dias restantes para o vencimento também é exibida.

Relatórios

ProximosAoVencimento

Descrição: Filtra os medicamentos cuja validade esteja dentro dos próximos 30 dias.

Stream utilizada:

filter(): Filtra medicamentos com data de validade dentro do limite de 30 dias.

sorted(): Ordena os medicamentos pela data de validade.

Collectors.toList(): Converte o resultado em uma lista.

Código:

public List<Medicamento> proximosAoVencimento() {
LocalDate hoje = LocalDate.now();
LocalDate limite = hoje.plusDays(30);
return service.listarTodos().stream()
.filter(m -> m.getDataValidade() != null)
.filter(m -> !m.getDataValidade().isAfter(limite))
.sorted(Comparator.comparing(Medicamento::getDataValidade))
.collect(Collectors.toList());
}


Estoque Baixo

Descrição: Filtra os medicamentos com quantidade em estoque menor que 5.

Stream utilizada:

filter(): Filtra medicamentos com a quantidade de estoque menor que 5.

collect(): Coleta o resultado em uma lista.

Código:

public List<Medicamento> estoqueBaixo() {
return service.listarTodos().stream()
.filter(m -> m.getQuantidadeEstoque() < 5)
.collect(Collectors.toList());
}


Valor Total por Fornecedor

Descrição: Calcula o valor total de medicamentos por fornecedor, multiplicando o preço pela quantidade de cada item.

Stream utilizada:

groupingBy(): Agrupa medicamentos por fornecedor.

reducing(): Soma o valor total de medicamentos por fornecedor (preço * quantidade).

Código:

public Map<String, Double> valorTotalPorFornecedor() {
return service.listarTodos().stream()
.collect(Collectors.groupingBy(
m -> m.getFornecedor() == null ? "—" : m.getFornecedor().getRazaoSocial(),
Collectors.summingDouble(m -> m.getPreco().doubleValue() * m.getQuantidadeEstoque())
));
}


Controlados vs Não Controlados

Descrição: Contabiliza a quantidade de medicamentos controlados e não controlados.

Stream utilizada:

groupingBy(): Agrupa medicamentos por Controlado ou Não controlado.

counting(): Conta a quantidade de medicamentos em cada grupo.

Código:

public Map<String, Long> controladosVsNao() {
return service.listarTodos().stream()
.collect(Collectors.groupingBy(m -> m.isControlado() ? "Controlados" : "Não controlados",
Collectors.counting()));
}

Como Rodar o Projeto

Requisitos:

Java 17 ou superior instalado.

Configuração de JavaFX para o seu IDE (IntelliJ, Eclipse, etc.).

Executando o Projeto:

Abra no seu IDE de preferência.

Compile e execute a classe MainApp que inicializa a aplicação JavaFX.

Acessando Funcionalidades:

Ao abrir a interface, você verá a tabela de medicamentos e os botões para Cadastrar, Buscar, Atualizar, Excluir e Gerar Relatórios.

O botão Vencem ≤30d gera um relatório exibindo medicamentos com validade dentro dos próximos 30 dias.

Observações:

Dados Persistidos: Todas as operações de cadastro, atualização, exclusão são refletidas diretamente no arquivo CSV src/main/resources/data/medicamentos.csv.

Streams: A aplicação usa Stream API para filtrar, ordenar e agrupar os dados. Isso facilita a manipulação de grandes volumes de dados no sistema.