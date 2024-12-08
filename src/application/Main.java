package application;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private File selectedFile;
    private HuffmanCompress compressor;
    private boolean isCompressed = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Huffman Compression and Decompression");
        primaryStage.setScene(createMainPane(primaryStage));
        primaryStage.show();
    }

    private Scene createMainPane(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(20));
        mainPane.setStyle("-fx-background-color: #5F9EA0;"); // Updated theme color

        VBox topPane = new VBox(10);
        topPane.setAlignment(Pos.CENTER);

        Image icon = new Image("compressIcon.png"); // Replace with your icon image path
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(350);
        iconView.setFitHeight(210);

        Label mainTitle = new Label("Huffman Compression and Decompression");
        mainTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;"); // White text color

        topPane.getChildren().addAll(iconView, mainTitle);
        mainPane.setTop(topPane);

        VBox centerPane = new VBox(20);
        centerPane.setAlignment(Pos.CENTER);

        Button compressButton = new Button("Compress");
        Button decompressButton = new Button("Decompress");

        compressButton.setPrefWidth(200);
        decompressButton.setPrefWidth(200);

        // Button Styles
        compressButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        decompressButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");

        centerPane.getChildren().addAll(compressButton, decompressButton);
        mainPane.setCenter(centerPane);

        compressButton.setOnAction(event -> {
            resetCompressPaneState(); // Reset the compression scene's state
            primaryStage.setScene(createCompressPane(primaryStage));
        });

        decompressButton.setOnAction(event -> {
            primaryStage.setScene(createDecompressPane(primaryStage));
        });
        return new Scene(mainPane, 700, 600);
    }

    private void resetCompressPaneState() {
        selectedFile = null;
        isCompressed = false;
    }

    private Scene createCompressPane(Stage primaryStage) {
        BorderPane compressPane = new BorderPane();
        compressPane.setPadding(new Insets(20));
        compressPane.setStyle("-fx-background-color: #5F9EA0;");

        // Top Section: Title
        Label titleLabel = new Label("File Compression");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;"); // White text
        VBox topBox = new VBox(titleLabel);
        topBox.setAlignment(Pos.CENTER);
        compressPane.setTop(topBox);

        // Center Section: Compression Controls
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        // Choose File
        HBox chooseFileBox = new HBox(10);
        chooseFileBox.setAlignment(Pos.CENTER);

        Button chooseFileButton = new Button("Choose File");
        Label fileNameLabel = new Label(selectedFile != null ? selectedFile.getName() : "No file selected");
        fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;"); // White text

        chooseFileButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 14px;");

        chooseFileBox.getChildren().addAll(chooseFileButton, fileNameLabel);

        // Start Compression
        Button startCompressButton = new Button("Start Compression");
        Label compressStatusLabel = new Label(isCompressed ? "Compression Successful!" : "");
        compressStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;"); // White text
        startCompressButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 14px;");

        centerBox.getChildren().addAll(chooseFileBox, startCompressButton, compressStatusLabel);

        // Bottom Section: Action Buttons
        HBox bottomBox = new HBox(15);
        bottomBox.setAlignment(Pos.CENTER);

        Button showStatisticsButton = new Button("Show Statistics");
        Button showHuffmanTableButton = new Button("Show Huffman Table");
        Button showHeaderButton = new Button("Show Header");
        Button backButton = new Button("Back");

        // Enable action buttons only after compression
        showStatisticsButton.setDisable(!isCompressed);
        showHuffmanTableButton.setDisable(!isCompressed);
        showHeaderButton.setDisable(!isCompressed);

        // Styling for Bottom Buttons
        for (Button button : new Button[]{showStatisticsButton, showHuffmanTableButton, showHeaderButton, backButton}) {
            button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
        }

        // Events
        chooseFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose File");
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            fileNameLabel.setText(selectedFile != null ? selectedFile.getName() : "No file selected");
            compressStatusLabel.setText(""); // Reset status on new file selection
            isCompressed = false; // Reset compression state
            showStatisticsButton.setDisable(true);
            showHuffmanTableButton.setDisable(true);
            showHeaderButton.setDisable(true);
        });

        startCompressButton.setOnAction(event -> {
            if (selectedFile != null) {
            	
                compressor = new HuffmanCompress(selectedFile.getAbsolutePath());
                isCompressed = true;

                // Enable action buttons after compression
                showStatisticsButton.setDisable(false);
                showHuffmanTableButton.setDisable(false);
                showHeaderButton.setDisable(false);

                compressStatusLabel.setText("Compression Successful!");
            } else {
                compressStatusLabel.setText("Please select a file first.");
            }
        });

        showStatisticsButton.setOnAction(event -> primaryStage.setScene(createStatisticsPane(primaryStage)));
        showHuffmanTableButton.setOnAction(event -> primaryStage.setScene(createHuffmanTablePane(primaryStage)));
        showHeaderButton.setOnAction(event -> {
            if (isCompressed) {
                // Create a new scene to display the header
                primaryStage.setScene(createHeaderDisplayScene(primaryStage));
            }
        });


        backButton.setOnAction(event -> {
            primaryStage.setScene(createMainPane(primaryStage)); // Go back to the main menu
        });

        bottomBox.getChildren().addAll(backButton, showStatisticsButton, showHuffmanTableButton, showHeaderButton);

        compressPane.setCenter(centerBox);
        compressPane.setBottom(bottomBox);

        return new Scene(compressPane, 800, 600);
    }

    private Scene createHeaderDisplayScene(Stage primaryStage) {
        // Create the raw header content
        StringBuilder headerContent = new StringBuilder();

        // Add file extension
        headerContent.append("File Extension: ").append(compressor.getOriginalFileExtension()).append("\n");

        // Add original file size
        headerContent.append("Original File Size: ").append(compressor.getOriginalFileSize()).append("\n");

        // Add header size
        headerContent.append("Header Size: ").append(compressor.getActualHeaderSizeInBits()).append(" bits\n");

        // Add Huffman tree structure
        headerContent.append("Huffman Tree Structure: ").append(compressor.CreateHuffmanCodingTree()).append("\n");

        // Create a VBox for layout
        VBox headerPane = new VBox(20);
        headerPane.setPadding(new Insets(20));
        headerPane.setAlignment(Pos.CENTER);
        headerPane.setStyle("-fx-background-color: #5F9EA0;");

        // Title Label
        Label headerTitle = new Label("Compressed File Header");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        // TextArea to display the header
        TextArea headerTextArea = new TextArea();
        headerTextArea.setWrapText(true);
        headerTextArea.setEditable(false);
        headerTextArea.setPrefSize(700, 400);
        headerTextArea.setStyle("-fx-font-size: 14px; -fx-control-inner-background: #FFFFFF; -fx-border-color: #4682B4;");
        headerTextArea.appendText(headerContent.toString()); // Append the header content

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        backButton.setOnAction(event -> primaryStage.setScene(createCompressPane(primaryStage)));

        // Add components to the VBox
        headerPane.getChildren().addAll(headerTitle, headerTextArea, backButton);

        // Create and return the scene
        return new Scene(headerPane, 800, 600);
    }

    private Scene createStatisticsPane(Stage primaryStage) {
        VBox statisticsPane = new VBox(15); // Increased spacing for better layout
        statisticsPane.setPadding(new Insets(20));
        statisticsPane.setAlignment(Pos.CENTER);
        statisticsPane.setStyle("-fx-background-color: #5F9EA0;"); // Updated background color

        // Title Label
        Label statisticLabel = new Label("Compression Statistics");
        statisticLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;"); // White text for better contrast

        // Statistic Labels
        Label numCharacters = new Label("Number of Distinct Characters in the file: " + compressor.getNumberOfChars());
        Label frequencies = new Label("Frequency of Characters in the file: " + compressor.getFrequenciesSum());
        Label originalSize = new Label("Original File Size: " + (compressor.getOriginalFileSize()*8) + " bits");
        Label compressedSize = new Label("Compressed File Size: " + compressor.getCompressedFileSize() + " bits");
        Label compressionRatio = new Label("Compression Ratio: "
                + String.format("%.2f", 100.0 * (1 - (double) compressor.getCompressedFileSize() / (compressor.getOriginalFileSize()*8))) + "%");

        for (Label label : new Label[]{numCharacters,frequencies, originalSize, compressedSize, compressionRatio}) {
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFFFFF;"); // Uniform styling for all labels
        }

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        backButton.setOnAction(event -> primaryStage.setScene(createCompressPane(primaryStage)));

        // Layout arrangement
        statisticsPane.getChildren().addAll(statisticLabel, numCharacters,frequencies, originalSize, compressedSize, compressionRatio, backButton);

        return new Scene(statisticsPane, 800, 600);
    }
    private Scene createHuffmanTablePane(Stage primaryStage) {
        // Create a VBox to hold the table and the back button
        VBox huffmanTablePane = new VBox(20);
        huffmanTablePane.setPadding(new Insets(20));
        huffmanTablePane.setAlignment(Pos.CENTER);
        huffmanTablePane.setStyle("-fx-background-color: #5F9EA0;"); // Match the background color with other scenes

        // Title Label
        Label tableTitle = new Label("Huffman Table");
        tableTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;"); // White text for consistency

        // Create a TableView
        TableView<readData> huffmanTable = new TableView<>();
        huffmanTable.setPlaceholder(new Label("No data available")); // Remove extra rows
        huffmanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Adjust columns to fit table width
        huffmanTable.setStyle("-fx-border-color: #4682B4; -fx-border-width: 2px;"); // Style the table border

        // Table Styling
        huffmanTable.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #E0E0E0);"
                + "-fx-border-radius: 5px; -fx-border-color: #4682B4;"
                + "-fx-table-cell-border-color: transparent;"); // Table background gradient

        // Create table columns
        TableColumn<readData, Character> charColumn = new TableColumn<>("Character");
        charColumn.setCellValueFactory(new PropertyValueFactory<>("character"));
        charColumn.setPrefWidth(120);
        charColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;"); // Styling for column headers

        TableColumn<readData, Integer> freqColumn = new TableColumn<>("Frequency");
        freqColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        freqColumn.setPrefWidth(120);
        freqColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

        TableColumn<readData, String> codeColumn = new TableColumn<>("Huffman Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setPrefWidth(200);
        codeColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

        TableColumn<readData, Integer> lengthColumn = new TableColumn<>("Length");
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        lengthColumn.setPrefWidth(120);
        lengthColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

        TableColumn<readData, Integer> lengthFreqColumn = new TableColumn<>("Length * Frequency");
        lengthFreqColumn.setCellValueFactory(new PropertyValueFactory<>("lengthFreq"));
        lengthFreqColumn.setPrefWidth(180);
        lengthFreqColumn.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

        // Add columns to the table
        huffmanTable.getColumns().addAll(charColumn, freqColumn, codeColumn, lengthColumn, lengthFreqColumn);

        // Populate table with data
        ObservableList<readData> data = FXCollections.observableArrayList();
        int totalBits = 0;
        int totalFrequencies = 0;

        // Loop through characters and populate rows
        for (int i = 0; i < 256; i++) {
            int freq = compressor.getFrequencies()[i];
            String code = compressor.getHuff()[i];
            int length = compressor.getLengths()[i];

            if (freq > 0) { // Only include characters with non-zero frequency
                int lengthFreq = freq * length;
                totalBits += lengthFreq;
                totalFrequencies += freq;
                data.add(new readData((char) i, freq, code, length, lengthFreq));
            }
        }

        // Add total row
        data.add(new readData('Σ', totalFrequencies, "-", 0, totalBits)); // Total size in bits

        // Set data to the table
        huffmanTable.setItems(data);

        // Row Styling
        huffmanTable.setRowFactory(tv -> new TableRow<readData>() {
            @Override
            protected void updateItem(readData item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getCharacter() == 'Σ') {
                    setStyle("-fx-background-color: #FFD700; -fx-font-weight: bold;"); // Golden color for total row
                } else {
                    setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #F5F5F5);"); // Gradient for normal rows
                }
            }
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        backButton.setOnAction(event -> primaryStage.setScene(createCompressPane(primaryStage)));

        // Add title, table, and back button to the VBox
        huffmanTablePane.getChildren().addAll(tableTitle, huffmanTable, backButton);

        return new Scene(huffmanTablePane, 900, 650); // Adjusted size for better layout
    }



    private Scene createDecompressPane(Stage primaryStage) {
        BorderPane decompressPane = new BorderPane();
        decompressPane.setPadding(new Insets(20));
        decompressPane.setStyle("-fx-background-color: #5F9EA0;");

        // Top Section: Title
        Label titleLabel = new Label("File Decompression");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        VBox topBox = new VBox(titleLabel);
        topBox.setAlignment(Pos.CENTER);
        decompressPane.setTop(topBox);

        // Center Section: Information Label
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Label decompressLabel = new Label("Select a compressed file (.huf) to decompress.");
        decompressLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFFFFF;");

        Button chooseFileButton = new Button("Choose File");
        Label fileNameLabel = new Label("No file selected");
        fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        chooseFileButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
        
        // File chooser action
        chooseFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File to Decompress");
            
            // Restrict to .huf files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Huffman Compressed Files (*.huf)", "*.huf");
            fileChooser.getExtensionFilters().add(extFilter);
            
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileNameLabel.setText(selectedFile.getName());
                // Perform decompression
                new HuffmanDecompress(selectedFile.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Decompression Complete!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        centerBox.getChildren().addAll(decompressLabel, chooseFileButton, fileNameLabel);
        decompressPane.setCenter(centerBox);

        // Bottom Section: Back Button
        HBox bottomBox = new HBox(15);
        bottomBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        backButton.setOnAction(event -> primaryStage.setScene(createMainPane(primaryStage)));

        bottomBox.getChildren().add(backButton);
        decompressPane.setBottom(bottomBox);

        return new Scene(decompressPane, 800, 600);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
