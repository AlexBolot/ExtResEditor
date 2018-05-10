package fr.unice.polytech.si3.qgl.ise;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


/*................................................................................................................................
 . Copyright (c)
 .
 . The App	 Class was Coded by : Alexandre BOLOT
 .
 . Last Modified : 11/04/17 23:27
 .
 . Contact : bolotalex06@gmail.com
 ...............................................................................................................................*/

public class App extends Application
{
    public static void main (String[] args)
    {
      launch(args);
    }
    
    @Override
    public void start (Stage primaryStage) throws Exception
    {
        try
        {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("main-view.fxml")));
            primaryStage.setTitle("Home");
            primaryStage.setScene(new Scene(root, 600, 580));
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }
    }
}
