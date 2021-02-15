package com.c2p.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7, ROWS = 6;
	private static final double CIRCLE_DIAMETER = 80.0;
	private static final String DISC_1 = "#24303E", DISC_2 = "#4CAA88";

	private static String PLAYER_1 = "Player One";
	private static String PLAYER_2 = "Player Two";

	private boolean isPlayerOneTurn = true;
	private boolean isAllowedToInsert = true;

	private final Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];// for storing whether a disc exists or not

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneTextField, playerTwoTextField;

	@FXML
	public Button setNamesButton;

	public void createPlayground() {

		Shape rectanglesWithHoles = createGameStructureGrid();
		rootGridPane.add(rectanglesWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}

		setNamesButton.setOnAction(event -> inputNames());
	}

	private void inputNames() {
		String playerOne = playerOneTextField.getText();
		String playerTwo = playerTwoTextField.getText();
		if (playerOne != null) {
			PLAYER_1 = playerOne;
			playerNameLabel.setText(PLAYER_1);
		}
		if (playerTwo != null) {
			PLAYER_2 = playerTwo;
		}
	}

	private Shape createGameStructureGrid() {
		Circle circle = new Circle();
		circle.setRadius(CIRCLE_DIAMETER / 2);
		circle.setCenterX(CIRCLE_DIAMETER / 2);
		circle.setCenterY(CIRCLE_DIAMETER / 2);
		circle.setSmooth(true);

		Shape rectanglesWithHoles = new Rectangle( (COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {

				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

				rectanglesWithHoles = Shape.subtract(rectanglesWithHoles, circle);
			}
		}

		rectanglesWithHoles.setFill(Color.WHITE);
		return rectanglesWithHoles;
	}

	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectList = new ArrayList<>();
		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rect = new Rectangle(CIRCLE_DIAMETER, (ROWS+1)*CIRCLE_DIAMETER);
			rect.setFill(Color.TRANSPARENT);
			rect.setTranslateX( col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4 );
			// on hover property
			rect.setOnMouseEntered(mouseEvent -> rect.setFill(Color.valueOf("#eeeeee5c")));
			rect.setOnMouseExited(mouseEvent -> rect.setFill(Color.TRANSPARENT));
			// on click property
			final int column = col;
			rect.setOnMouseClicked(mouseEvent -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDiscs(new Disc(isPlayerOneTurn), column);
				}
			});

			rectList.add(rect);
		}
		return rectList;
	}

	private void insertDiscs(Disc disc, int column) {

		//to check if there is space in the array
		int rowPtr = ROWS-1;
		while (rowPtr >= 0) {
			if ( getDiscIfPresent(rowPtr, column) == null) {
				break;
			}
			rowPtr--;
		}
		// to check if the column is not full
		if (rowPtr < 0) {
			return;
		}

		insertedDiscsArray[rowPtr][column] = disc;
		insertedDiscsPane.getChildren().add(disc);
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4) );

		final int row = rowPtr;
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), disc);
		transition.setToY(rowPtr * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4) );
		transition.setOnFinished(actionEvent -> {

			if ( gameEnded(row,column) ) {
				gameOver();
				return;
			}

			isAllowedToInsert = true;
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText( isPlayerOneTurn ? PLAYER_1 : PLAYER_2 );
		});

		transition.play();
	}

	private Disc getDiscIfPresent(int row, int column) { // in order to prevent array out of bounds exception

		if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0) {
			return null;
		}
		return insertedDiscsArray[row][column];
	}

	private void gameOver() {
		String winner = (isPlayerOneTurn) ? PLAYER_1 : PLAYER_2;

		Alert winnerBox = new Alert(Alert.AlertType.INFORMATION);
		winnerBox.setTitle("Connect Four");
		winnerBox.setHeaderText("The Winner Is " + winner);
		winnerBox.setContentText("Want to play again? ");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		winnerBox.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() -> {

			Optional<ButtonType> clickedBtn = winnerBox.showAndWait();
			if (clickedBtn.isPresent() && clickedBtn.get() == yesBtn) {
				resetGame(false);
			} else {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame(boolean isNewGame) {

		insertedDiscsPane.getChildren().clear();    // removes all inserted discs from pane (visual).
		// set inserted discs array to null
		for (Disc[] discs : insertedDiscsArray) {
			Arrays.fill(discs, null);
		}

		isAllowedToInsert = true; // let new game to played
		isPlayerOneTurn = true; // let player one start game
		if (isNewGame) {
			PLAYER_1 = "Player One";
			PLAYER_2 = "Player Two";
			playerOneTextField.setText(null);
			playerTwoTextField.setText(null);
		}
		playerNameLabel.setText(PLAYER_1); // default to player one

		createPlayground(); // new playground freshly

	}

	private boolean gameEnded(int row, int column) {

		// Vertical Criteria:
		// the combination of points maybe for ex- last disc inserted = (2,3)
		// so vertical criteria - 0,3 1,3 2,3 3,3 4,3 5,3 store it in class Point2D as x,y coordinates
		List<Point2D> verticalPts = IntStream.rangeClosed(row - 3, row + 3) // collect integers in range 0,5
										.mapToObj( r -> new Point2D(r, column) ) // will give us the combination 0,3 1,3 2,3 3,3 4,3 5,3
										.collect(Collectors.toList()); // converting to list

		List<Point2D> horizontalPts = IntStream.rangeClosed(column - 3, column + 3) // collect integers in range 0,6
				.mapToObj( c -> new Point2D(row, c) ) // will give us the combination 2,0 2,1 2,2 2,3 2,4 2,5 2,6
				.collect(Collectors.toList()); // converting to list

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
											.mapToObj(i -> startPoint1.add(i, -i))
											.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
											.mapToObj(i -> startPoint2.add(i, i))
											.collect(Collectors.toList());

		return checkCombinations(verticalPts) || checkCombinations(horizontalPts)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
	}

	private boolean checkCombinations(List<Point2D> pts) {

		int chain = 0;
		for (Point2D p : pts) {
			int rowIndex = (int) p.getX();
			int columnIndex = (int) p.getY();

			Disc disc = getDiscIfPresent( rowIndex, columnIndex );

			if (disc != null && disc.isPlayerOneMove() == isPlayerOneTurn) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}

		}
		return false;
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOne;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOne = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(DISC_1) : Color.valueOf(DISC_2));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}

		public boolean isPlayerOneMove() {
			return isPlayerOne;
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}
