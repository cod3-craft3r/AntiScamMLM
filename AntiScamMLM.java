import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;

@Deprecated
class NoSuchMemberException extends Exception {
    @Override
    public String toString() {
        return "No such member exists in the system currently, first add this member then continue adding referrals";
    }
}

class MemberExistsException extends Exception {
    @Override
    public String toString() {
        return "this member already exists";
    }
}

class ReferralsLimitException extends Exception {
    @Override
    public String toString() {
        return "this member already exists";
    }
}

class DBMSConnection {
    // later merge validate_member & add_member functions into one; they return int & that determines whether validation went wrong or error occurred or success
    private final String url;
    private final String username;
    private final String passcode;

    public DBMSConnection(String host, String name, String code) {
        this.url = host;
        this.username = name;
        this.passcode = code;
    }

    public Connection new_connection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(url, username, passcode);
        } catch (Exception e) {
            System.out.println("connection to the database can't be established at the moment");
        }

        return con;
    }

    public boolean validate_member(String name) {
        Connection con = new_connection();
        PreparedStatement query=null;

        try {
            String queryString = "SELECT * FROM members WHERE name=?;";
            query = con.prepareStatement(queryString);

            query.setString(1, name);
            ResultSet rs = query.executeQuery();
            if(!rs.next()){
                destroy_connection(con, query);
                return true;
            }
            else if(rs.next()){
                destroy_connection(con, query);
                return false;
            }
        }
        catch(SQLException e) {
            System.out.println("some error occurred while validating member");
        }

        destroy_connection(con, query);
        return false;
    }

    public boolean add_member(String name) throws MemberExistsException {
        Connection con = new_connection();
        boolean member_dne = validate_member(name);
        PreparedStatement query=null;

        try {
            if(member_dne) {    // new member
                String queryString = "INSERT INTO members(name) VALUES(?);";
                query = con.prepareStatement(queryString);

                query.setString(1, name);
                int res = query.executeUpdate();
                if(res > 0){
                    destroy_connection(con, query);
                    return true;
                }
                else{
                    destroy_connection(con, query);
                    return false;
                }
            }
        }
        catch(Exception e) {
            System.out.println("some error occurred while adding member to the database");
        }
        if(!member_dne)
            throw new MemberExistsException();

        destroy_connection(con, query);
        return false;
    }

    public ResultSet get_member_details(String memberName) {
        Connection con = new_connection();
        ResultSet rs = null;
        PreparedStatement query=null;

        try {
            String queryString = "SELECT * FROM members WHERE name=?;";
            query = con.prepareStatement(queryString);
            query.setString(1, memberName);
            rs = query.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("ResultSet is empty");
            } else {
                return rs;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("some error occurred while validating referal");
        }

        destroy_connection(con, query);
        return rs;
    }

    public boolean add_referral(String memberName, String refName) throws ReferralsLimitException, NoSuchMemberException {
        Connection con = new_connection();
        ResultSet rs = null;
        // boolean limit = false;
        PreparedStatement q=null;
        try {
            rs = get_member_details(memberName);

            if(!rs.isBeforeFirst()) {
                throw new NoSuchMemberException();
            }
            else {
                ArrayList<String> lst = new ArrayList<>();

                if(rs.next()){
                    for (int i = 3; i <= 7; i++)    // 3-7 are referals for each member so this loop is constant
                        lst.add(rs.getString(i));
                }

                int index = lst.indexOf(null)+1;

                if(index != 1) {
                    if(lst.contains(refName))
                        return false;
                }

                if(index <= 5) {
                    System.out.println(index);
                    StringBuilder qString = new StringBuilder("UPDATE members SET ");
                    qString.append("ref"+index+"=? WHERE name=?;");

                    q = con.prepareStatement(qString.toString());
                    q.setString(1, refName);
                    q.setString(2, memberName);
                    q.executeUpdate();
                }

                if(index == 5) {
                    destroy_connection(con, q);
                    throw new ReferralsLimitException();
                }

                destroy_connection(con, q);
                return true;
            }
        }
        catch(SQLException e) {
            System.out.println("some error occurred while adding referral to the database");
        }

        // destroy_connection(con, q);
        return false;
    }

    public boolean remove_member_upon_referral_limit(String memberName) {
        Connection con = new_connection();
        PreparedStatement query=null;

        try {
            String queryString = "DELETE FROM members WHERE name=?;";
            query = con.prepareStatement(queryString);

            query.setString(1, memberName);
            int res = query.executeUpdate();
            if(res > 0) {
                destroy_connection(con, query);
                return true;
            }
        }
        catch(SQLException e) {
            System.out.println("cannot delete member from database");
        }

        destroy_connection(con, query);
        return false;
    }

    public void destroy_connection(Connection con, PreparedStatement stmt) {
        try {
            stmt.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("connection to the database can't be closed properly, this may have serious repercussions later");
        }
    }
}

public class AntiScamMLM extends Application {
    Scene scene;
    Button back;

    class Member {
        Label nameLabel;
        TextField nameField;
        Button btn;
        
        public void show_member_form(Stage primaryStage) {
            back = new Button("back");
            nameLabel = new Label("enter name of member");
            nameField = new TextField();
            btn = new Button("add member");
    
            btn.setOnAction(actionEvent -> {
                String name = nameField.getText();
                if(name.length() == 0) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning!");
                    alert.setHeaderText("Member name empty");
                    alert.setContentText("Please enter the member's name");
                    alert.showAndWait();
                }
                else {
                    DBMSConnection dbmsCon = new DBMSConnection("jdbc:mysql://localhost:3306/projects", "root", "cocksucker");
    
                    try {
                        boolean result = dbmsCon.add_member(name);
                        if(!result) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Error!");
                            alert.setHeaderText("Cannot add member");
                            alert.setContentText("Member couldn't be added to the database, try again later");
                            alert.showAndWait();
                        }
                        else if(result) {
                            Alert alert = new Alert(AlertType.CONFIRMATION);
                            alert.setTitle("Done!");
                            alert.setHeaderText("Added "+name);
                            alert.setContentText("Member added successfully to the database");
                            alert.showAndWait();
                        }
                    }
                    catch(MemberExistsException mee) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Member exists!");
                        alert.setHeaderText(name+" already exists in the system");
                        alert.setContentText("Please check name again, or add another member");
                        alert.showAndWait();
                    }
                }
            });

            back.setOnAction(e -> primaryStage.setScene(scene));
    
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(20, 20, 20, 20));
            vbox.getChildren().addAll(nameLabel, nameField, btn);
            vbox.setAlignment(Pos.CENTER);

            BorderPane layout = new BorderPane();
            layout.setTop(back);
            layout.setCenter(vbox);

            Scene scene1 = new Scene(layout);
            primaryStage.setHeight(500);
            primaryStage.setWidth(350);
            primaryStage.setScene(scene1);
            primaryStage.show();
        }
    }
    
    class Referal {
        Label nameLabel;
        TextField nameField;
        Label refLabel;
        TextField refField;
        Button btn;
        
        public void show_referal_form(Stage primaryStage) {
            back = new Button("back");
            nameLabel = new Label("enter name of member");
            nameField = new TextField();
            refLabel = new Label("enter name of referral");
            refField = new TextField();
            btn = new Button("add referral");
    
            btn.setOnAction(actionEvent -> {
                String name = nameField.getText();
                String ref = refField.getText();
                
                if(name.length() == 0) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning!");
                    alert.setHeaderText("Member name empty");
                    alert.setContentText("Please enter the member's name");
                    alert.showAndWait();
                }
                else if(ref.length() == 0) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning!");
                    alert.setHeaderText("Referral name empty");
                    alert.setContentText("Please enter the referral's name");
                    alert.showAndWait();
                }
                else {
                    DBMSConnection dbmsCon = new DBMSConnection("jdbc:mysql://localhost:3306/projects", "root", "cocksucker");
    
                    try {
                        boolean result = dbmsCon.add_referral(name, ref);
                        if(!result) {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Referral already added!");
                            alert.setHeaderText("Add another referral");
                            alert.setContentText("Another referral with same username already exists, please add another referral");
                            alert.showAndWait();
                        }
                        else {
                            try {
                                dbmsCon.add_member(ref);
                            } catch (MemberExistsException e) {
                                // 
                            }
                            Alert alert = new Alert(AlertType.CONFIRMATION);
                            alert.setTitle("Done!");
                            alert.setHeaderText("Added a referral for "+name);
                            alert.setContentText("Referral added for the specified member");
                            alert.showAndWait();
                        }
                    }
                    catch(NoSuchMemberException mdne) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Member doesn't exist!");
                        alert.setHeaderText(name+" doesn't exist");
                        alert.setContentText("{$ADD COMMENT HERE}");
                        alert.showAndWait();
                    }
                    catch(ReferralsLimitException rle) {
                        dbmsCon.remove_member_upon_referral_limit(name);
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Max Referrals!");
                        alert.setHeaderText("Referral limit reached");
                        alert.setContentText("A member can only have upto 5 referrals, this member has now exited from the system");
                        alert.showAndWait();
                    }
                }
            });

            back.setOnAction(e -> primaryStage.setScene(scene));
    
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(20, 20, 20, 20));
            vbox.getChildren().addAll(nameLabel, nameField, refLabel, refField, btn);
            vbox.setAlignment(Pos.CENTER);

            BorderPane layout = new BorderPane();
            layout.setTop(back);
            layout.setCenter(vbox);

            Scene scene1 = new Scene(layout);
            primaryStage.setHeight(500);
            primaryStage.setWidth(350);
            primaryStage.setScene(scene1);
            primaryStage.show();
        }
    }    

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button("add member");
        Button btn2 = new Button("add referal");

        btn1.setOnAction(actionEvent -> {
            Member m = new Member();
            m.show_member_form(primaryStage);
        });

        btn2.setOnAction(actionEvent -> {
            Referal r = new Referal();
            r.show_referal_form(primaryStage);
        });


        FlowPane f = new FlowPane();
        f.getChildren().addAll(btn1, btn2);
        scene = new Scene(f);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Anti-Scam MLM");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}