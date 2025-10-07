package com.brufarma.farmacia.controller;
import com.brufarma.farmacia.model.Fornecedor;
import com.brufarma.farmacia.model.Medicamento;
import com.brufarma.farmacia.service.CsvRepository;
import com.brufarma.farmacia.service.MedicamentoService;
import com.brufarma.farmacia.service.RelatorioService;
import com.brufarma.farmacia.util.Validacao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MedicamentoController {

    @FXML private TextField txtCodigo, txtNome, txtDescricao, txtPrincipio, txtQtd, txtPreco;
    @FXML private TextField txtCnpj, txtRazao, txtTelefone, txtEmail, txtCidade, txtEstado, txtBusca;
    @FXML private DatePicker dpValidade;
    @FXML private CheckBox chkControlado;

    @FXML private TableView<Medicamento> tabela;
    @FXML private TableColumn<Medicamento, String> colCodigo, colNome, colFornecedor;
    @FXML private TableColumn<Medicamento, Integer> colQtd;
    @FXML private TableColumn<Medicamento, String> colPreco;

    @FXML private Label lblStatus;

    private MedicamentoService service;
    private RelatorioService relatorios;

    @FXML
    public void initialize() {
        // Caminho simples para dev/local. (Se empacotar, troque para getResourceAsStream no repo)
        File csv = new File("src/main/resources/data/medicamentos.csv");
        service = new MedicamentoService(new CsvRepository(csv));
        relatorios = new RelatorioService(service);

        // Colunas da tabela (com null-safety)
        colCodigo.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue() == null ? "" : c.getValue().getCodigo()
                ));
        colNome.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue() == null ? "" : c.getValue().getNome()
                ));
        colQtd.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue() == null ? 0 : c.getValue().getQuantidadeEstoque()
                ).asObject());
        colPreco.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        (c.getValue() == null || c.getValue().getPreco() == null)
                                ? "" : c.getValue().getPreco().toString()
                ));
        colFornecedor.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        (c.getValue() != null && c.getValue().getFornecedor() != null)
                                ? c.getValue().getFornecedor().getRazaoSocial() : ""
                ));

        atualizarTabela();
        status("Pronto.");
    }

    private void atualizarTabela() {
        tabela.setItems(FXCollections.observableArrayList(service.listarTodos()));
    }

    // === AÇÕES DA TELA ===

    @FXML
    public void onCadastrar() {
        try {
            Medicamento m = lerFormulario();
            if (service.cadastrar(m)) {
                status("Cadastrado.");
                atualizarTabela();
                limpar();
            } else {
                status("Já existe medicamento com esse código.");
            }
        } catch (Exception e) {
            status("Erro ao cadastrar: " + e.getMessage());
        }
    }

    @FXML
    public void onBuscar() {
        String cod = txtBusca.getText();
        service.buscarPorCodigo(cod).ifPresentOrElse(m -> {
            preencherFormulario(m);
            status("Encontrado.");
        }, () -> status("Não encontrado."));
    }

    @FXML
    public void onAtualizar() {
        try {
            Medicamento m = lerFormulario();
            if (service.atualizar(m)) {
                status("Atualizado.");
                atualizarTabela();
            } else {
                status("Código não encontrado.");
            }
        } catch (Exception e) {
            status("Erro ao atualizar: " + e.getMessage());
        }
    }

    @FXML
    public void onExcluir() {
        String cod = txtBusca.getText();
        if (service.excluir(cod)) {
            status("Excluído.");
            atualizarTabela();
            limpar();
        } else {
            status("Código não encontrado.");
        }
    }

    // === RELATÓRIO: Vencem ≤30 dias ===

    @FXML
    public void onVencem30d() {
        var lista = relatorios.proximosAoVencimento(); // List<Medicamento>
        String msg = lista.isEmpty()
                ? "Nenhum medicamento vence em até 30 dias."
                : lista.stream()
                .map(m -> {
                    LocalDate hoje = LocalDate.now();
                    long dias = ChronoUnit.DAYS.between(hoje, m.getDataValidade());
                    if (dias < 0) dias = 0; // evita negativo se já venceu
                    return m.getNome() + " (" + m.getCodigo() + ") — vence em "
                            + dias + " dia(s) [" + m.getDataValidade() + "]";
                })
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Relatório");
        a.setHeaderText("Vencem em até 30 dias");
        a.setContentText(msg);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    // === UTILITÁRIOS ===

    private Medicamento lerFormulario() {
        if (!Validacao.naoVazio(txtCodigo.getText()))
            throw new IllegalArgumentException("Código é obrigatório.");

        int qtd = Validacao.parseInt(txtQtd.getText(), "Qtd");
        BigDecimal preco = Validacao.parseBig(txtPreco.getText(), "Preço");

        Fornecedor f = new Fornecedor(
                txtCnpj.getText(), txtRazao.getText(), txtTelefone.getText(),
                txtEmail.getText(), txtCidade.getText(), txtEstado.getText()
        );

        return new Medicamento(
                txtCodigo.getText().trim(),
                txtNome.getText().trim(),
                txtDescricao.getText().trim(),
                txtPrincipio.getText().trim(),
                dpValidade.getValue(),
                qtd,
                preco,
                chkControlado.isSelected(),
                f
        );
    }

    private void preencherFormulario(Medicamento m) {
        txtCodigo.setText(m.getCodigo());
        txtNome.setText(m.getNome());
        txtDescricao.setText(m.getDescricao());
        txtPrincipio.setText(m.getPrincipioAtivo());
        dpValidade.setValue(m.getDataValidade());
        txtQtd.setText(String.valueOf(m.getQuantidadeEstoque()));
        txtPreco.setText(m.getPreco() == null ? "" : m.getPreco().toString());
        chkControlado.setSelected(m.isControlado());

        if (m.getFornecedor() != null) {
            txtCnpj.setText(m.getFornecedor().getCnpj());
            txtRazao.setText(m.getFornecedor().getRazaoSocial());
            txtTelefone.setText(m.getFornecedor().getTelefone());
            txtEmail.setText(m.getFornecedor().getEmail());
            txtCidade.setText(m.getFornecedor().getCidade());
            txtEstado.setText(m.getFornecedor().getEstado());
        }
        txtBusca.setText(m.getCodigo());
    }

    private void limpar() {
        txtCodigo.clear(); txtNome.clear(); txtDescricao.clear(); txtPrincipio.clear();
        dpValidade.setValue(null);
        txtQtd.clear(); txtPreco.clear(); chkControlado.setSelected(false);
        txtCnpj.clear(); txtRazao.clear(); txtTelefone.clear(); txtEmail.clear();
        txtCidade.clear(); txtEstado.clear();
    }

    private void status(String s) {
        lblStatus.setText(s);
    }
}
