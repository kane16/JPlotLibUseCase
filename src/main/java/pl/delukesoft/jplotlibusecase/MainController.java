package pl.delukesoft.jplotlibusecase;

import static java.util.stream.Collectors.toList;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import pl.delukesoft.jplotlib.builder.PlotDataBuilder;
import pl.delukesoft.jplotlib.exception.InvalidPlotTypeProvided;
import pl.delukesoft.jplotlib.model.enums.ColumnType;
import pl.delukesoft.jplotlib.model.enums.PlotType;
import pl.delukesoft.jplotlib.model.input.PlotInfo;
import pl.delukesoft.jplotlib.model.input.SeriesInfo;
import pl.delukesoft.jplotlib.model.output.PlotData;
import pl.delukesoft.jplotlib.plotops.functions.GroupingFunction;


public class MainController {

  final CategoryAxis xAxis = new CategoryAxis();
  final NumberAxis yAxis = new NumberAxis();
  @FXML
  private Label JPlotLibWelcomeLabel;
  @FXML
  private Button uploadFileButton;
  @FXML
  private ChoiceBox<String> aggregationTypeChoice;
  @FXML
  private ChoiceBox<String> plotTypeChoice;
  @FXML
  private LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
  @FXML
  private ChoiceBox<String> xField;
  @FXML
  private ChoiceBox<String> yField;
  @FXML
  private GridPane pane;
  private JFXSnackbar snackbar;
  private Map<String, FieldState> availableFieldsMap;
  private String filePath;

  @FXML
  public void initialize() {
    var plotTypes = Arrays.stream(PlotType.values())
        .map(PlotType::toString)
        .collect(toList());
    var groupingFunctions = Arrays.stream(GroupingFunction.values())
        .map(GroupingFunction::toString)
        .collect(toList());
    snackbar = new JFXSnackbar(pane);
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
    if (xField.getValue() != null && yField.getValue() != null) {
      drawChartWhenFieldsChosen();
    }
  }

  public void onAggregationChanged() {
    xField.setDisable(false);
    yField.setDisable(false);
    if (xField.getValue() != null && yField.getValue() != null) {
      drawChartWhenFieldsChosen();
    }
  }

  public void uploadCsv() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose CSV file");
    chooser.getExtensionFilters().add(new ExtensionFilter("Comma separated values", "*.csv"));
    File file = chooser.showOpenDialog(pane.getScene().getWindow());
    if (file != null) {
      plotTypeChoice.setDisable(false);
      plotTypeChoice.setValue("STANDARD");
      filePath = file.getAbsolutePath();
      List<String> columns = PlotDataBuilder.builder()
          .withFilePath(file.getAbsolutePath()).extractColumns();
      this.availableFieldsMap = new HashMap<>();
      columns.forEach(column -> availableFieldsMap.put(column, FieldState.FREE));
      xField.setDisable(false);
      yField.setDisable(false);
      xField.getItems().addAll(columns);
      yField.getItems().addAll(columns);
    }
  }

  private void adjustColumnsForComboBox(ChoiceBox<String> field) {
    List<String> fieldsToRemove = this.availableFieldsMap.entrySet()
        .stream()
        .filter(entry -> entry.getValue().equals(FieldState.OCCUPIED) && !entry.getKey()
            .equals(field.getValue()))
        .map(Entry::getKey)
        .collect(toList());
    List<String> fieldsToAdd = this.availableFieldsMap.entrySet()
        .stream()
        .filter(entry -> entry.getValue().equals(FieldState.FREE) && !field.getItems()
            .contains(entry.getKey()))
        .map(Entry::getKey)
        .collect(toList());
    field.getItems().removeAll(fieldsToRemove);
    field.getItems().addAll(fieldsToAdd);
  }

  public void onXFieldChanged() {
    refreshItems();
    adjustColumnsForComboBox(yField);
    if (xField.getValue() != null && yField.getValue() != null) {
      drawChartWhenFieldsChosen();
    }
  }

  public void onYFieldChanged() {
    refreshItems();
    adjustColumnsForComboBox(xField);
    if (xField.getValue() != null && yField.getValue() != null) {
      drawChartWhenFieldsChosen();
    }
  }

  private void refreshItems() {
    for (Entry<String, FieldState> field : this.availableFieldsMap.entrySet()) {
      if (field.getKey().equals(xField.getValue()) || field.getKey().equals(yField.getValue())) {
        this.availableFieldsMap.put(field.getKey(), FieldState.OCCUPIED);
      } else {
        this.availableFieldsMap.put(field.getKey(), FieldState.FREE);
      }
    }
  }

  private void drawChartWhenFieldsChosen() {
    String xFieldName = xField.getValue();
    String yFieldName = yField.getValue();
    PlotType plotType = PlotType.valueOf(plotTypeChoice.getValue());
    Optional<GroupingFunction> aggregation = Optional.ofNullable(aggregationTypeChoice.getValue())
        .map(GroupingFunction::valueOf);
    PlotInfo plotInfo = aggregation.map(groupingFunction -> new PlotInfo(
        plotType,
        new SeriesInfo(xFieldName, ColumnType.STRING),
        new SeriesInfo(yFieldName, ColumnType.INTEGER),
        groupingFunction
    )).orElse(new PlotInfo(
        plotType,
        new SeriesInfo(xFieldName, ColumnType.STRING),
        new SeriesInfo(yFieldName, ColumnType.INTEGER)
    ));
    try {
      PlotData plotData = PlotDataBuilder.builder()
          .withFilePath(filePath)
          .withPlotInfo(plotInfo)
          .build();
      XYChart.Series<String, Number> dataSeries = new Series<>();
      for (Entry<String, List<Number>> entry : plotData.getArgsWithValuesMap().entrySet()) {
        dataSeries.getData()
            .add(new XYChart.Data<>(entry.getKey(), entry.getValue().get(0)));
      }
      dataSeries.setName("Name");
      chart.getData().clear();
      chart.getData().addAll(dataSeries);
      chart.setAnimated(false);
    } catch (InvalidPlotTypeProvided invalidPlotTypeProvided) {
      Label label = new Label("Invalid Plot type provided");
      label.setPrefHeight(50.0);
      label.setTextFill(Color.RED);
      SnackbarEvent event = new SnackbarEvent(label, Duration.seconds(2L), null);
      snackbar.enqueue(event);
      System.out.println("Couldn't draw chart, invalid plot type");
    }

  }

  private enum FieldState {
    FREE,
    OCCUPIED
  }

}