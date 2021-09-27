package pl.delukesoft.jplotlibusecase;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pl.delukesoft.jplotlib.builder.PlotDataBuilder;
import pl.delukesoft.jplotlib.model.enums.PlotType;
import pl.delukesoft.jplotlib.plotops.functions.GroupingFunction;


public class MainController {

  @FXML
  private Label JPlotLibWelcomeLabel;

  @FXML
  private Button uploadFileButton;

  @FXML
  private ChoiceBox<String> aggregationTypeChoice;

  @FXML
  private ChoiceBox<String> plotTypeChoice;

  @FXML
  private LineChart<String, Number> chart;

  @FXML
  private ChoiceBox<String> xField;

  @FXML
  private ChoiceBox<String> yField;

  @FXML
  private GridPane pane;

  @FXML
  public void initialize() {
    var plotTypes = Arrays.stream(PlotType.values())
        .map(PlotType::toString)
        .collect(toList());
    var groupingFunctions = Arrays.stream(GroupingFunction.values())
        .map(GroupingFunction::toString)
        .collect(toList());
    plotTypeChoice.getItems().addAll(plotTypes);
    aggregationTypeChoice.getItems().addAll(groupingFunctions);
  }

  public void onPlotTypeChanged() {
    if (plotTypeChoice.getValue().equals(PlotType.AGGREGATION.toString())) {
      aggregationTypeChoice.setVisible(true);
      if (aggregationTypeChoice.getValue() == null) {
        xField.setDisable(true);
        yField.setDisable(true);
      } else {
        xField.setDisable(false);
        yField.setDisable(false);
      }
    } else {
      aggregationTypeChoice.setVisible(false);
      aggregationTypeChoice.setValue(null);
      xField.setDisable(false);
      yField.setDisable(false);
    }
  }

  public void onAggregationChanged() {
    xField.setDisable(false);
    yField.setDisable(false);
  }

  public void uploadCsv() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose CSV file");
    chooser.getExtensionFilters().add(new ExtensionFilter("Comma separated values", "*.csv"));
    File file = chooser.showOpenDialog(pane.getScene().getWindow());
    if (file != null) {
      System.out.printf("Chosen file: %s", file.getAbsolutePath());
      System.out.println();
      plotTypeChoice.setDisable(false);
      plotTypeChoice.setValue("STANDARD");
      List<String> columns = PlotDataBuilder.builder()
          .withFilePath(file.getAbsolutePath()).extractColumns();
      xField.setDisable(false);
      yField.setDisable(false);
      xField.getItems().addAll(columns);
      yField.getItems().addAll(columns);
    } else {
      System.out.println("File has not been chosen");
    }
  }
}