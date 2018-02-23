/**
 * This is group 23 Java GUI program solving following questions:
 *
 * For grade E
 * 7. Display all product types.
 * 8. Display all brands of a selected product type, such as ost (cheese) or mjölk (milk).
 * 9. Loyal customer registration.
 *
 * For grade B
 * 22. User interaction using Java-FX components.
 * 23. Display store stock when user is selecting a store.
 *
 */
package Projekt.Icookop;

// Importing SQL, randomization and GUI (JavaFX) functionality
import java.io.File;
import java.sql.*;
import java.util.Random;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Icookop extends Application {

    // DB connection variable
    static protected Connection con;

    // Tableview variables
    private ObservableList<ObservableList> data;
    private TableView tableview;

    // Stage/Scene variables
    private Stage window;
    private Scene scene1, scene2;
    private ChoiceBox<String> choiceBox1, choiceBox2;

    // Searches for method start()
    public static void main(String[] args) {

        launch(args);
    }

    // Method for establishing a DB connection
    public void connect() {

        // Relative path to Access database, project can be run from anywhere.
        File dbfile = new File("Icooköp_v8.accdb");
        String relativepath = dbfile.getAbsolutePath();

        // Local Access DB static variables
        String URL = "jdbc:ucanaccess://"+relativepath;
        String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
        try
        {
            // Register the driver with DriverManager
            Class.forName(driver);
            // Create a connection to the database
            con = DriverManager.getConnection(URL);
            // Set the auto commit of the connection to false.
            // An explicit commit will be required in order to accept
            // any changes done to the DB through this connection.
            con.setAutoCommit(false);
            // Some logging
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Method for displaying all available product types
    public void createStatement(){

        data = FXCollections.observableArrayList();

        // Local variables
        ResultSet rs;
        Statement stmt;
        String query = "SELECT namn FROM Produktgrupp ORDER BY namn ASC";

        try{

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            //Clearing tables of old data
            tableview.getItems().clear();
            tableview.getColumns().clear();


            // Table columns added dynamically using ResultSet metadata.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){

                // We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }

                });

                tableview.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");

            }

            // Data added to ObservableList
            while(rs.next()){

                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){

                    //Iterate Column
                    row.add(rs.getString(i));
                }

                data.add(row);
            }

            // Data added to TableView
            tableview.setItems(data);

            // Close the variable stmt and release all resources bound to it
            // Any ResultSet associated to the Statement will be automatically closed too.
            stmt.close();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");

        }
    }

    // Method for presenting all available brands of a selected product.
    public void preparedStatement(String ptype) {

        data = FXCollections.observableArrayList();

        // Local variables, query statement saved to variable query.
        ResultSet rs;
        PreparedStatement stmt;
        String query = "SELECT DISTINCT Märke.namn FROM " +
                "Märke, Märkesprodukt, Produkt, Produktbeskrivning, Produktgrupp " +
                "WHERE Märke.[märke_id]=Märkesprodukt.[märke_id] " +
                "AND Märkesprodukt.[produkt_id]=Produkt.[produkt_id] " +
                "AND Produkt.[pbeskrivning_id]=Produktbeskrivning.[pbeskrivning_id] " +
                "AND Produktbeskrivning.[pgrupp_id]=Produktgrupp.[pgrupp_id] " +
                "AND Produktgrupp.[namn]= ?";

        try{

            // Create a statement associated to the connection con.
            // The new statement is placed in the variable stmt.
            // Provide the value for the first ? in the SQL statement.
            // Execute query and save ResultSet to variable rs.
            stmt = con.prepareStatement(query);
            stmt.setString(1, ptype);
            rs = stmt.executeQuery();

            // Clearing tables of old data
            tableview.getItems().clear();
            tableview.getColumns().clear();

            // Table columns added dynamically using ResultSet metadata.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){

                // We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableview.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }

            // Data added to ObservableList
            while(rs.next()){

                // Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){

                    // Iterate Column
                    row.add(rs.getString(i));
                }
                // Console printout
                System.out.println("Row [1] added "+row );
                data.add(row);
            }

            // Data added to TableView
            tableview.setItems(data);

            // Close the variable stmt and release all resources bound to it
            // Any ResultSet associated to the Statement will be automatically closed too.
            stmt.close();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");

        }
    }

    // Method for checking store stock and printing it to TableView
    public void storeStock(String store){

        data = FXCollections.observableArrayList();

        //Local variables
        ResultSet rs;
        PreparedStatement stmt;
        //Store query string to variable
        String query = "SELECT Förpackning.[streckkod], LagerfördVara.[antalIButik], LagerfördVara.[maxantal] FROM Förpackning, LagerfördVara, Butik WHERE Förpackning.[streckkod]=LagerfördVara.[streckkod] AND LagerfördVara.[butik_id]=Butik.[butik_id] AND Butik.[namn]=?";

        try{

            // The new statement is placed in the variable stmt.
            // Provide the value for the first ? in the SQL statement.
            // Execute query and save ResultSet to variable rs.
            stmt = con.prepareStatement(query);
            stmt.setString(1, store);
            rs = stmt.executeQuery();

            // Clearing tables of old data
            tableview.getItems().clear();
            tableview.getColumns().clear();

            // Table columns added dynamically using ResultSet metadata.
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){

                // We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableview.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }

            // Data added to ObservableList
            while(rs.next()){

                // Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){

                    // Iterate Column
                    row.add(rs.getString(i));
                }
                // Console printout
                System.out.println("Row [1] added "+row );
                data.add(row);
            }

            // Data added to TableView
            tableview.setItems(data);

            // Close the variable stmt and release all resources bound to it
            // Any ResultSet associated to the Statement will be automatically closed too.
            stmt.close();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on building Data");

        }
    }

    public boolean checkPnr(String param) {

        // Local variables
        String query;
        ResultSet rs;
        PreparedStatement stmt;
        boolean result = false;

        try {
            // Set the SQL statement into the query variable
            query = "SELECT personnummer FROM Stamkund WHERE personnummer = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, param);

            // Execute the SQL statement that is prepared in the variable stmt
            // and store the result in the variable rs.
            rs = stmt.executeQuery();

            // Sets result to true if personnummer exists
            result = rs.next();

            // Close the variable stmt and release all resources bound to it
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Data check error");
        }
        return result;
    }

    // Method for generating and returning a randomized card number in the range of 1000-9999.
    // The range can be edited in the insertStamkund method.
    public int randomNumber(int min, int max) {

        // Local variables
        String query;
        ResultSet rs;
        PreparedStatement stmt;
        int internalNumber;

        // A random number is generated in the selected range min-max (1000-9999)
        Random rand = new Random();
        internalNumber = rand.nextInt((max - min) + 1) + min;

        try {

            // Set the SQL statement into the query variable
            // We check if our generated number exists in the database.
            query = "SELECT kortnummer FROM Stamkund WHERE kortnummer = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, internalNumber);

            // Execute the SQL statement that is prepared in the variable stmt
            // and store the result in the variable rs.
            // If we receive a ResultSet, boolean result is set to 1, and the generated number exists.
            rs = stmt.executeQuery();
            boolean result = rs.next();

            // If result is true, we call ourselves for a new number
            if (result) {
                return randomNumber(min, max);
            }

            // Close the variable stmt and release all resources bound to it
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Data Insert error");
        }
        // If result is false, returns the unique card number
        return internalNumber;
    }

    // insertStamkund takes six strings as input and calls randomNumber for an integer
    public int insertStamkund(String pnrParam,String fnameParam,String snameParam,
                              String addrParam,String mailParam,String cellParam) {

        // Call randomNumber() and stores the value to cardParam.
        int cardParam = randomNumber(1000,9999);

        // Set the SQL statement into the query variable
        String query = "INSERT INTO Stamkund (kortnummer,personnummer,förnamn," +
                "efternamn,adress,epost,mobilnummer) VALUES (?,?,?,?,?,?,?)";

        try {
            // Create a statement associated to the connection and the query.
            // The new statement is placed in the variable stmt.
            PreparedStatement stmt = con.prepareStatement(query);

            // Provide the values for the ?'s in the SQL statement, from 1 to 7.
            stmt.setInt(1, cardParam);
            stmt.setString(2, pnrParam);
            stmt.setString(3, fnameParam);
            stmt.setString(4, snameParam);
            stmt.setString(5, addrParam);
            stmt.setString(6, mailParam);
            stmt.setString(7, cellParam);

            // Execute the SQL statement that is prepared in the variable stmt
            stmt.executeUpdate();

            // Close the variable stmt and release all resources bound to it
            stmt.close();

            // Commit the changes made to the database.
            con.commit();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Data Insert");
            }

        // Returns card number for user display
        return cardParam;
    }

    // This method saves any changes to the database and closes the connection.
    // Messages are only printed to the console.
    public void closeProgram(){

        try {
            System.out.println("Programmet avslutas...");
            con.commit();
            System.out.println("Skriver ändringar...");
            con.close();
            System.out.println("Stänger anslutningen...");
            window.close();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Could not properly close program");
        }
    }

    // Method for fetching all product types to drop down menu
    public void getProductTypes(){

        // Local variables
        ResultSet rs;
        Statement stmt;
        String query = "SELECT namn FROM Produktgrupp ORDER BY namn ASC";

        try{

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while(rs.next()){
                choiceBox1.getItems().add(rs.getString("namn"));
            }
            stmt.close();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error fetching data");
        }
    }

    // Method for fetching all stores to drop down menu
    public void getStores(){

        // Local variables
        ResultSet rs;
        Statement stmt;
        String query = "SELECT namn FROM Butik ORDER BY namn ASC";

        try{

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while(rs.next()){
                choiceBox2.getItems().add(rs.getString("namn"));
            }
            stmt.close();
        }

        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error fetching data");
        }
    }

    public void start(Stage primaryStage) throws Exception {

        // Connecting to Access DB
        connect();

        // Declaration of variable window as primaryStage
        window = primaryStage;
        window.setTitle("Icooköp");
        primaryStage.setTitle("Icooköp");

        // Calls method closeProgram() also when terminating the program
        window.setOnCloseRequest(e -> closeProgram());

        //---------------------SCENE1--------------------------

        // Scene 1 Welcome Label
        Label labelScene1 = new Label();
        labelScene1.setText("Välkommen till Icooköp beta v0.2!");
        labelScene1.setFont(Font.font ("Verdana", 20));

        // Scene 1 left menu with spacing
        VBox scene1leftMenu = new VBox(15);
        scene1leftMenu.setPadding(new Insets(20, 20, 20, 20));

        // Calls closeProgram()
        Button s1q1 = new Button("Avsluta");
        s1q1.setOnAction(e -> closeProgram());

        // Displays all available product types
        Button s1b1 = new Button("Visa produkttyper");
        s1b1.setOnAction(e -> createStatement());

        // TableView object for displaying database data.
        tableview = new TableView();

        // TextField for user input of a selected product.
        Label s1l1 = new Label();
        s1l1.setText("Visa märken till produktgrupp");

        // Calls preparedStatement with product parameter
        choiceBox1 = new ChoiceBox<>();
        getProductTypes();
        choiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> preparedStatement(newValue));

        // Button for switching to scene 2 (insertStamkund)
        Button s1b3 = new Button("Lägg till stamkund");
        s1b3.setOnAction(e -> window.setScene(scene2));

        // Label for drop down menu
        Label s1l2 = new Label();
        s1l2.setText("Se butiksaldo");

        // Creating ChoiceBox (drop down) with all the stores.
        // When choosing a store, storeStock() method is called for that store.
        choiceBox2 = new ChoiceBox<>();
        getStores();
        choiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> storeStock(newValue));

        // Adding buttons, labels, text fields and choicebox (drop down) to left menu
        scene1leftMenu.getChildren().addAll(labelScene1,s1q1,s1b1,s1l1,choiceBox1,s1b3,s1l2,choiceBox2);

        // Creating BorderPane object and aligning left and center content
        BorderPane Borderpane1 = new BorderPane();
        Borderpane1.setLeft(scene1leftMenu);
        Borderpane1.setCenter(tableview);

        // Setting scene 1 using BorderPane1. Both scenes have identical dimensions.
        scene1 = new Scene(Borderpane1,700,500);



        //------------------- SCENE2 (insert Stamkund) --------------------

        // HBox for scene 2 created and a Label for it.
        HBox scene2topMenu = new HBox();
        Label labelScene2 = new Label();
        labelScene2.setText("Lägg till stamkund");
        labelScene2.setFont(Font.font ("Verdana", 20));

        // Left menu VBox created.
        VBox scene2leftMenu = new VBox(15);

        // Scene 2 Main manu and Quit Button created
        Button s2b1 = new Button("Huvudmeny");
        s2b1.setOnAction(e -> window.setScene(scene1));
        Button s2q1 = new Button("Avsluta");
        s2q1.setOnAction(e -> closeProgram());

        // GridPane for scene 2 created with spacing
        GridPane s2grid = new GridPane();
        s2grid.setPadding(new Insets(10, 10, 10, 10));
        s2grid.setVgap(15);
        s2grid.setHgap(10);

        // Label displaying required input fields
        Label oblLabel = new Label("* = Obligatoriskt fält");
        GridPane.setConstraints(oblLabel, 0, 0);

        // Label for prnParam
        Label pnrLabel = new Label("*Personnummer:");
        GridPane.setConstraints(pnrLabel, 0, 1);

        // pnrParam is forced by regular expression to "6 digits"-"dash"-"4 digits"
        FormattedTextField pnrParam = new FormattedTextField("^\\d{6}-\\d{4}$");
        pnrParam.setPromptText("XXXXXX-XXXX");
        GridPane.setConstraints(pnrParam, 1, 1);

        // Label for fnameParam
        Label fnameLabel = new Label("*Förnamn:");
        GridPane.setConstraints(fnameLabel, 0, 2);

        // First name TextField
        TextField fnameParam = new TextField();
        fnameParam.setPromptText("t.ex. Arne");
        GridPane.setConstraints(fnameParam, 1, 2);

        // Label for snameParam
        Label snameLabel = new Label("*Efternamn:");
        GridPane.setConstraints(snameLabel, 0, 3);

        // Surname TextField
        TextField snameParam = new TextField();
        snameParam.setPromptText("t.ex. Andersson");
        GridPane.setConstraints(snameParam, 1, 3);

        // Label for addrParam
        Label addrLabel = new Label("*Adress:");
        GridPane.setConstraints(addrLabel, 0, 4);

        // Address TextField
        TextField addrParam = new TextField();
        addrParam.setPromptText("t.ex. Storgatan 1");
        GridPane.setConstraints(addrParam, 1, 4);

        // Label for mailParam
        Label mailLabel = new Label("E-post:");
        GridPane.setConstraints(mailLabel, 0, 5);

        // Mail TextField
        TextField mailParam = new TextField();
        mailParam.setPromptText("din@epost.nu");
        GridPane.setConstraints(mailParam, 1, 5);

        // Label for cellParam
        Label cellLabel = new Label("Mobilnummer:");
        GridPane.setConstraints(cellLabel, 0, 6);

        // cellParam is forced by regular expression to "3 digits"-"dash"-"7 digits"
        FormattedTextField cellParam = new FormattedTextField("^\\d{3}-\\d{7}$");
        cellParam.setPromptText("XXX-XXXXXXX");
        GridPane.setConstraints(cellParam, 1, 6);

        // This Label displays user information such as errors and confirmations.
        Label executeLabel = new Label();
        GridPane.setConstraints(executeLabel, 1, 8);

        // Adding Button "Registrera" with a lot of functionality
        Button s2b2 = new Button("Registrera");
        GridPane.setConstraints(s2b2, 1, 7);
        s2b2.setOnAction(new EventHandler<ActionEvent>() {
                             @Override
                             public void handle(ActionEvent event) {

                                 // First if-statement checks for data in required fields.
                                 // Required: pnrParam, fnameParam, lnameParam and addrParam.
                                 if ((pnrParam.getText() != null && !pnrParam.getText().isEmpty())&&
                                         (fnameParam.getText() != null && !fnameParam.getText().isEmpty())&&
                                         (snameParam.getText() != null && !snameParam.getText().isEmpty())&&
                                         (addrParam.getText() != null && !addrParam.getText().isEmpty())){

                                     // Second if-statement calls checkPnr() to see if pnrParam (Personnummer)
                                     // is unique in database. If unique, checkPnr() returns False.
                                     // If !checkPnr = True, all data is valid and we run insertStamkund
                                     if (!checkPnr(pnrParam.getText())) {

                                         // Calls insertStamkund() with data from all TextFields.
                                         // Saves card number to integer cardParam.
                                         int cardParam = insertStamkund(pnrParam.getText(),fnameParam.getText(),
                                                 snameParam.getText(),addrParam.getText(),mailParam.getText(),
                                                 cellParam.getText());

                                         // Converts cardParam value to String c.
                                         // Prints a welcoming and customer card number.
                                         String c = String.valueOf(cardParam);
                                         executeLabel.setText("Välkommen som stamkund hos Icooköp!\n" +
                                                 "Ditt kortnummer är: " + c);

                                         // Clear TextFields
                                         pnrParam.clear();
                                         fnameParam.clear();
                                         snameParam.clear();
                                         addrParam.clear();
                                         mailParam.clear();
                                         cellParam.clear();
                                     }

                                     // If !checkPnr = False, pnrParam is already in database and user is notified.
                                     else{
                                         executeLabel.setText("Personnumret finns redan registrerat!\n" +
                                                 "Välj ett annat tack.");
                                     }

                                 }

                                 // If any required fields are empty, user is notified.
                                 else {
                                     executeLabel.setText("Fyll i alla obligatoriska fält!");
                                 }
                             }
                         });

        // Button for clearing data in all TextFields and executeLabel
        Button s2b3 = new Button("Rensa fält");
        GridPane.setConstraints(s2b3, 0, 7);
        s2b3.setOnAction(new EventHandler<ActionEvent>() {
                             @Override
                             public void handle(ActionEvent event) {
                                 pnrParam.clear();
                                 fnameParam.clear();
                                 snameParam.clear();
                                 addrParam.clear();
                                 mailParam.clear();
                                 cellParam.clear();
                                 executeLabel.setText(null);
                             }
                         }
            );

        // Adding buttons to left and top menus
        scene2leftMenu.getChildren().addAll(s2b1,s2q1);
        scene2topMenu.getChildren().add(labelScene2);

        // Adding Labels and TextFields to scene 2 center grid
        s2grid.getChildren().addAll(oblLabel,pnrLabel,pnrParam,fnameLabel,
                fnameParam,snameLabel,snameParam,addrLabel,addrParam,mailLabel,
                mailParam,cellLabel,cellParam,s2b2,s2b3,executeLabel);

        // Creating BorderPane object and aligning left, top and center content
        BorderPane Borderpane2 = new BorderPane();
        Borderpane2.setLeft(scene2leftMenu);
        Borderpane2.setTop(scene2topMenu);
        Borderpane2.setCenter(s2grid);

        // Setting scene 2 using BorderPane2. Both scenes have identical dimensions.
        scene2 = new Scene(Borderpane2,700,500);

        // Setting primary scene and display window
        window.setScene(scene1);
        window.show();

    }
}
