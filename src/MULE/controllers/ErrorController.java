package MULE.controllers;

import javafx.fxml.FXML;

// Created by David on 10/16/2015.
public class ErrorController {
    @FXML
    public void back() {
        ScreenNavigator.getInstance().loadPlayer();
    }
}
