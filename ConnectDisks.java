import java.util.InputMismatchException;
import java.util.Scanner;

public class ConnectDisks {

	/** required number of disks to have in a line */
	private static int CONNECT_COUNT;
	/** number of rows */
	private static int ROW_COUNT;
	/** number of columns */
	private static int COLUMN_COUNT;

	/** no victory status */
	private static final int CONTINUE = -1;
	/** draw status */
	private static final int DRAW = 0;

	/** Identifying string for player */
	private static String PLAYER_1_NAME = "Player One";
	/** Identifying string for player */
	private static String PLAYER_2_NAME = "Player Two";

	/** defines an unowned cell */
	private static final int UNOWNED = 0;
	/** Identifying integer for player */
	private static final int PLAYER_1 = 1;
	/** Identifying integer for player */
	private static final int PLAYER_2 = 2;

	/** row x column */
	private static int[][] gameGrid;

	public static void main(String[] args) {
		// prompt user to setup game
		Scanner reader = new Scanner(System.in);

		// prompt users for names
		System.out.print(PLAYER_1_NAME
				+ ": What would like your username to be? ");
		PLAYER_1_NAME = reader.nextLine().trim();

		System.out.print(PLAYER_2_NAME
				+ ": What would like your username to be? ");
		PLAYER_2_NAME = reader.nextLine().trim();

		// get setup variables
		final int minimum = 4;
		System.out.print("How many rows? (" + minimum + " + ): ");
		ROW_COUNT = reader.nextInt();
		if (ROW_COUNT < minimum) {
			reader.close();
			throw new IllegalArgumentException("Invalid Row Count");
		}

		System.out.print("How many columns? (" + minimum + " + ): ");
		COLUMN_COUNT = reader.nextInt();
		if (COLUMN_COUNT < minimum) {
			reader.close();
			throw new IllegalArgumentException("Invalid Column Count");
		}

		final int maximumConnectCount = COLUMN_COUNT < ROW_COUNT ? COLUMN_COUNT
				: ROW_COUNT;
		System.out.print("How many should be connected to win? ("
				+ minimum
				+ (minimum == maximumConnectCount ? "" : " - "
						+ maximumConnectCount) + "): ");
		CONNECT_COUNT = reader.nextInt();
		if (CONNECT_COUNT < minimum || CONNECT_COUNT > maximumConnectCount) {
			reader.close();
			throw new IllegalArgumentException("Invalid Connect Count");
		}

		// init variables
		gameGrid = new int[ROW_COUNT][COLUMN_COUNT];
		boolean playerOnesTurn = true;
		printGameGrid();

		// game loop
		while (true) {

			// prompt user for column index
			System.out.println((playerOnesTurn ? PLAYER_1_NAME : PLAYER_2_NAME)
					+ " - Drop a disk at column 0 - " + (COLUMN_COUNT - 1)
					+ ": ");

			// get column input
			int column;
			// catch invalid input
			try {
				column = reader.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Try again.");
				continue;
			}

			// catch out of bounds column index
			if (column >= COLUMN_COUNT || column < 0) {
				System.out.println("No such column. Try again.");
				continue;
			}

			// check that row is not full and play
			if (!dropDisk(column, playerOnesTurn)) {
				System.out.println("That row is full. Try again.");
				continue;
			}

			// check victory
			if (determineVictoryStatus() != CONTINUE)
				break;

			// change turns
			playerOnesTurn = !playerOnesTurn;

			// reprint the grid for the players to view
			printGameGrid();
		}

		reader.close();
		printGameGrid();

		// Print victory status
		int victory = determineVictoryStatus();
		if (victory == PLAYER_1)
			System.out.println(PLAYER_1_NAME + " has won!");
		else if (victory == PLAYER_2)
			System.out.println(PLAYER_2_NAME + " has won!");
		else
			System.out.println("The game ended in a draw.");
	}

	/**
	 * Output the gameGrid to the console
	 */
	private static void printGameGrid() {
		// print an index for players to view
		for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++)
			System.out.print(" " + columnIndex);
		System.out.println();

		// iterate through each column
		for (int row = 0; row < ROW_COUNT; row++) {
			// iterate through each row
			for (int column = 0; column < COLUMN_COUNT; column++) {
				// print a pipe and each row element
				int characterInt = gameGrid[row][column];
				char playerSymbol = ' ';
				// if player's names both start with the same letter, use '1'
				// and '2'
				if (PLAYER_1_NAME.charAt(0) == PLAYER_2_NAME.charAt(0)) {
					if (characterInt == PLAYER_1)
						playerSymbol = '1';
					else if (characterInt == PLAYER_2)
						playerSymbol = '2';
				} else {
					// get first letter of player's name
					if (characterInt == PLAYER_1)
						playerSymbol = PLAYER_1_NAME.charAt(0);
					else if (characterInt == PLAYER_2)
						playerSymbol = PLAYER_2_NAME.charAt(0);
				}
				System.out.print("|" + playerSymbol);
			}
			// end the line with a pipe
			System.out.println('|');
		}
		// add a blank line
		System.out.println();
	}

	/**
	 * Try to play a disk for the specified player at the specified column
	 * 
	 * @param column
	 * @param playerOnesTurn
	 * @return true if able to play a disk in the specified column
	 */
	private static boolean dropDisk(int column, boolean playerOnesTurn) {
		boolean playSucessful = false;

		// iterate through each row backwards
		for (int row = ROW_COUNT - 1; row >= 0; row--) {
			// if the spot is unowned, add one disk
			if (gameGrid[row][column] == UNOWNED) {
				gameGrid[row][column] = playerOnesTurn ? PLAYER_1 : PLAYER_2;
				playSucessful = true;
				break;
			}
		}
		// return true if able to play a disk in the specified column
		return playSucessful;
	}

	/**
	 * Check if there is a winner or if the grid is full
	 * 
	 * @return the player that won, CONTINUE or DRAW
	 */
	private static int determineVictoryStatus() {
		// check horizontal victory
		int count = 0;
		int currentPlayer = 0;

		for (int row = 0; row < gameGrid.length; row++)
			for (int column = 0; column < gameGrid[row].length; column++) {
				if (gameGrid[row][column] == UNOWNED) {
					count = 0;
					continue;
				}
				// else
				if (currentPlayer == gameGrid[row][column]) {
					count++;
				} else {
					currentPlayer = gameGrid[row][column];
					count = 1;
				}
				if (count >= CONNECT_COUNT)
					return currentPlayer;
			}

		// check vertical
		count = 0;
		currentPlayer = 0;

		for (int column = 0; column < gameGrid[0].length; column++)
			for (int row = 0; row < gameGrid.length; row++) {
				if (gameGrid[row][column] == UNOWNED) {
					count = 0;
					continue;
				}
				// else
				if (currentPlayer == gameGrid[row][column]) {
					count++;
				} else {
					currentPlayer = gameGrid[row][column];
					count = 1;
				}
				if (count >= CONNECT_COUNT)
					return currentPlayer;
			}

		// check left up to right diagonal
		for (int row = CONNECT_COUNT - 1; row < gameGrid.length; row++)
			for (int column = 0; column < gameGrid[row].length
					- (CONNECT_COUNT - 1); column++) {
				if (gameGrid[row][column] == UNOWNED)
					continue;
				boolean isDiagonal = true;
				for (int i = 1; i < CONNECT_COUNT; i++)
					if (gameGrid[row][column] != gameGrid[row - i][column + i])
						isDiagonal = false;

				if (isDiagonal)
					return gameGrid[row][column];
			}

		// check left down to right diagonal
		for (int row = 0; row < gameGrid.length - (CONNECT_COUNT - 1); row++)
			for (int column = 0; column < gameGrid[row].length
					- (CONNECT_COUNT - 1); column++) {
				if (gameGrid[row][column] == UNOWNED)
					continue;
				boolean isDiagonal = true;
				for (int i = 1; i < CONNECT_COUNT; i++)
					if (gameGrid[row][column] != gameGrid[row + i][column + i])
						isDiagonal = false;

				if (isDiagonal)
					return gameGrid[row][column];
			}

		// check draw ie. grid is full and this method has not returned yet
		boolean isDraw = true;
		for (int row = 0; row < gameGrid.length; row++) {
			for (int column = 0; column < gameGrid[row].length; column++) {
				if (gameGrid[row][column] == UNOWNED) {
					isDraw = false;
					break;
				}
			}
			if (!isDraw)
				break;
		}

		// return if draw
		if (isDraw)
			return DRAW;

		// return that no victory condition has been reached
		return CONTINUE;
	}

}
