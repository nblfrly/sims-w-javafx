module com.simsw {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires xstream;
    requires javafx.base;

    exports com.simsw;
    exports com.simsw.controller;

    opens com.simsw to javafx.fxml;
    opens com.simsw.controller to javafx.fxml;
    opens com.simsw.model to javafx.base, xstream;
    

}