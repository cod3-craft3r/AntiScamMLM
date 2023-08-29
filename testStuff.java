import java.sql.*;
import java.util.ArrayList;

public class testStuff {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/random", "root", "cocksucker");

            String qString="SELECT * FROM sample WHERE name=?;";
            PreparedStatement q=con.prepareStatement(qString);
            q.setString(1, "archit");

            ResultSet rs=q.executeQuery();
            rs=null;
            System.out.println(rs);
            // while(rs.next()) {
            //     System.out.println("name- "+rs.getString(1)+"\tage- "+rs.getInt(2)+"\tlast name- "+rs.getString(3));
            // }
        }
        catch(Exception e) {
            System.out.println("kucch gadbad hai");
            System.out.println(e);
        }

        // ArrayList<String> l=new ArrayList<>();
        // l.add("archit");
        // l.add("null");
        // l.add("sharma");
        // l.add(null);

        // System.out.println(l.indexOf(null));
    }
}

// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.FlowPane;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;

// public class testStuff extends Application {

//     Stage window;

//     @Override
//     public void start(Stage primaryStage) throws Exception {
//         window = primaryStage;
//         window.setTitle("Example App");

//         FlowPane f = new FlowPane();
//         Label lbl = new Label("HOME");
//         f.getChildren().add(lbl);
//         f.setAlignment(Pos.TOP_CENTER);
//         Scene scene2 = new Scene(f,300,300);

//         // Back button
//         Button backButton = new Button("Back");
//         backButton.setOnAction(e -> window.setScene(scene2));

//         // Form
//         Label nameLabel = new Label("Name:");
//         TextField nameTextField = new TextField();
//         Label emailLabel = new Label("Email:");
//         TextField emailTextField = new TextField();

//         VBox formLayout = new VBox(10);
//         formLayout.setPadding(new Insets(20, 20, 20, 20));
//         formLayout.getChildren().addAll(nameLabel, nameTextField, emailLabel, emailTextField);

//         // Main layout
        // BorderPane layout = new BorderPane();
        // layout.setTop(backButton);
        // layout.setCenter(formLayout);

//         Scene scene = new Scene(layout, 400, 300);
//         window.setScene(scene);
//         window.show();
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

// public class testStuff extends Application {

//     @Override
//     public void start(Stage primaryStage) throws Exception {
//         // Create a scene and set it on the stage
//         StackPane root = new StackPane(new Button("Hello, world!"));
//         Scene scene = new Scene(root, 400, 400);
//         primaryStage.setScene(scene);

//         // Get the current scene set on the stage
//         Scene currentScene = primaryStage.getScene();
//         System.out.println(currentScene.equals(scene)); // Output: true

//         primaryStage.show();
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }