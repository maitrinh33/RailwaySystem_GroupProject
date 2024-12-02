module railway.railwaysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;  // Required for working with SQL


    opens Controllers to javafx.fxml;
    exports Controllers;
}
