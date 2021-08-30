package pl.delukesoft.jplotlibusecase;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {

  @FXML
  private Label JPlotLibWelcomeLabel;

  @FXML
  private Button uploadFileButton;

  @FXML
  private ChoiceBox<String> aggregationType;

  @FXML
  private LineChart<String, Number> chart;

  @FXML
  private ChoiceBox<String> xField;

  @FXML
  private ChoiceBox<String> yField;

  @FXML
  private GridPane pane;

  public void uploadCsv(){
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose CSV file");
    chooser.getExtensionFilters().add(new ExtensionFilter("Comma separated values","*.csv"));
    File file = chooser.showOpenDialog(pane.getScene().getWindow());
    if(file != null){
      System.out.printf("Chosen file: %s", file.getAbsolutePath());
      System.out.println();
    }else {
      System.out.println("File has not been chosen");
    }
  }


}