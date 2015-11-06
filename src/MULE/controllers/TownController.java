package MULE.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

// Created by Ethan on 9/18/2015.
public class TownController {

    @FXML
    void usePub(MouseEvent event) {
        Game.instance.gamble();

    }

    @FXML
    void useAssay(MouseEvent event) {
        Game.instance.useAssay();

    }

    @FXML
    void useLand(MouseEvent event) {
        Game.instance.useLandOffice();

    }
    @FXML
    private void pauseMusic(ActionEvent e) {
        Game.instance.pauseMusic();
    }
    @FXML
    private void playMusic(ActionEvent e) {
        Game.instance.playMusic();
    }
    public void showMap() {
        ScreenNavigator.getInstance().loadMain();
    }

    public void leaveTown(MouseEvent event) {
        Game.instance.leaveTown();
        ScreenNavigator.getInstance().loadMap();
    }

    @FXML
    void enterStore(MouseEvent event) {
        Game.instance.enterStore();
    }

    @FXML
    private void handleLoadGame(ActionEvent e) {
        Game.instance.loadGame();
    }
    @FXML
    private void handleSaveGame(ActionEvent e) {
        Game.instance.saveGame();
    }
    @FXML
    private void closeGame(ActionEvent e) {
        Game.instance.timer.stopTime();
        System.exit(0);
    }
}
