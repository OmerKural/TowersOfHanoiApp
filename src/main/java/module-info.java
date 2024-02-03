module de.tum.in.ase.towersofhanoiapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.tum.in.ase.towersofhanoiapp to javafx.fxml;
    exports de.tum.in.ase.towersofhanoiapp;
}