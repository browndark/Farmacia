module com.brufarma.farmacia {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // FXMLLoader precisa refletir os controllers
    opens com.brufarma.farmacia.controller to javafx.fxml;

    // (se algum FXML ou recurso estiver no pacote raiz)
    opens com.brufarma.farmacia to javafx.fxml, javafx.graphics;

    // TableView usa reflexão sobre os models
    opens com.brufarma.farmacia.model to javafx.base;

    // Exports opcionais
    exports com.brufarma.farmacia;
    exports com.brufarma.farmacia.controller;
}
