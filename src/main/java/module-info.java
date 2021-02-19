module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires itextpdf;
    requires java.mail;
    requires org.apache.pdfbox;
    requires com.google.zxing;
    requires com.google.zxing.javase;

    opens org.example to javafx.fxml;
    exports org.example;
}