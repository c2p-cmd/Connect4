package com.c2p.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootNode = loader.load();

		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar = creteMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		Pane menuPane = (Pane) rootNode.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootNode);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four!");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public MenuBar creteMenu() {

		// File Menu
		Menu fileMenu = new Menu("File");

		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(actionEvent -> controller.resetGame(true));

		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(actionEvent -> controller.resetGame(false));

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Exit Game");
		exitGame.setOnAction(actionEvent -> {
			// Exiting javaFX App
			Platform.exit();
			System.exit(0);
		});

		// add items to menu item
		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		// Help Menu
		Menu helpMenu = new Menu("Help");

		MenuItem aboutGame = new MenuItem("About Connect4");
		aboutGame.setOnAction(actionEvent -> aboutConnectFour());

		SeparatorMenuItem anotherSeparator = new SeparatorMenuItem();
		MenuItem aboutMe = new MenuItem("About Developer");
		aboutMe.setOnAction(actionEvent -> aboutMe());

		// adding items to menu item
		helpMenu.getItems().addAll(aboutGame, anotherSeparator, aboutMe);

		// Creating MenuBar object and adding menus to it
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		// returning created object
		return menuBar;
	}

	private void aboutMe() {
		// Showing AlertType.INFORMATION
		Alert aboutDev = new Alert(Alert.AlertType.INFORMATION);
		aboutDev.setTitle("About The Developer");
		aboutDev.setHeaderText("Sharan Thakur");
		aboutDev.setContentText("I am a newbie Java Developer this is my first attempt\nat making a javaFX game.\nHope you enjoy it");

		aboutDev.show();
	}

	private void aboutConnectFour() {
		// Showing AlertType.INFORMATION
		Alert aboutGameBox = new Alert(Alert.AlertType.INFORMATION);
		aboutGameBox.setTitle("About Connect Four");
		aboutGameBox.setHeaderText("How To Play?");
		aboutGameBox.setContentText("Connect Four is a two-player connection game\n in which the players first choose a color and\n then take turns dropping colored discs\n from the top into a seven-column,\n six-row vertically suspended grid.\n The pieces fall straight down,\n occupying the next available\n space within the column.\n The objective of the game is to be\n the first to form a horizontal, vertical,\n or diagonal line of four of one's own discs.\nConnect Four is a solved game.\nThe first player can always win by playing the right moves.\n");
		aboutGameBox.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
