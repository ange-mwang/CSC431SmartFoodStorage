package Fridge;
import java.util.*;
import java.sql.*;
import java.net.*;
import java.io.*;

public class Storage {

    private boolean isInitialized = false;
    private static final int maxRecipes = 100;
    private String url;
    private String user;
    private String pass;
    private Connection connection;

    public Storage() {
        url = "jdbc:mysql://localhost:3306/STORAGE";
        user = "root";
        pass = "pass";
        try {
            connection = DriverManager.getConnection(this.url, this.user, this.pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        if (this.isInitialized) return;
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Food " + 
                "(name VARCHAR(255), " +
                "timeAdded TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY ( name, timeAdded ))");
            System.out.println("Connected to Database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addItem(String foodName) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("INSERT INTO Food (name) VALUES ('" + foodName + "')");
            System.out.println("Inserted '" + foodName + "'into table");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getRecipes() {
        try {
            Statement statement = this.connection.createStatement();
            ResultSet itemResults = statement.executeQuery("SELECT DISTINCT name FROM Food");    
            String items = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=9beccd47350341a5bf107a22cb6456e8&number=" + this.maxRecipes + "&ingredients=";
            while(itemResults.next()) {
                items += itemResults.getString("name") + ",";
                System.out.println("Found: " + itemResults.getString("name"));
            }
            System.out.println("Request: " + items);
            URL reqUrl = new URL(items.substring(0, items.length() - 1));
            HttpURLConnection conn = (HttpURLConnection)reqUrl.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuffer res = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    res.append(line);
                }
                input.close();

                System.out.println(res.toString());
            } else {
                System.out.println("Something went wrong, GET request failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
