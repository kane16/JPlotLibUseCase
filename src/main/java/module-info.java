module pl.delukesoft.jplotlibusecase {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires jplotlib;

  opens pl.delukesoft.jplotlibusecase to javafx.fxml;
  exports pl.delukesoft.jplotlibusecase;
}