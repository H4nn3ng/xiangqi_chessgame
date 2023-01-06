package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import de.tuberlin.sese.swtpp.gameserver.model.*;
//TODO: more imports from JVM allowed here

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XiangqiGame extends Game implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign red and black player
	private Player blackPlayer;
	private Player redPlayer;

	// internal representation of the game state
	// TODO: insert additional game data here
	private String gameState;
	private Piece[][] board;

 
	/************************
	 * constructors
	 ***********************/

	public XiangqiGame() {
		super();

		// TODO: initialization of game state can go here
		this.gameState = "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR";
		this.board = getBoardArray(this.gameState);

	}

	public String getType() {
		return "xiangqi";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.redPlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = redPlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (redGaveUp())
				gameInfo = "red gave up";
			else if (didRedDraw() && !didBlackDraw())
				gameInfo = "red called draw";
			else if (!didRedDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "red won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isRedNext() ? "r" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.redPlayer == player) {
				redPlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				redPlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false; 
	}

	/*
	 * ****************************************** Helpful stuff
	 */

	/**
	 *
	 * @return True if it's red player's turn
	 */
	public boolean isRedNext() {
		return nextPlayer == redPlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didRedDraw() {
		return redPlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean redGaveUp() {
		return redPlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		// Note: This method is for automatic testing. A regular game would not start at
		// some artificial state.
		// It can be assumed that the state supplied is a regular board that can be
		// reached during a game.
		this.board = getBoardArray(state);
	}

	@Override
	public String getBoard() {
		return  this.boardArrayToString(this.board);
	}

	/**
	 * @param moveString: format: "start-destination" example: "d1-e2"
	 * @param player      The player who tries the move
	 * @return boolean: true if move is valid, false if not
	 */
	@Override
	public boolean tryMove(String moveString, Player player) {
		if (nextPlayer != player) return false;
		if (!checkMoveString(moveString)) return false;
		String[] split = moveString.split("-");
		String startString = split[0];
		Piece piece = getPieceByIndexString(startString, this.board);
		String endString = split[1];
		Direction moveDirection = getMoveDirection(startString, endString);
		if (moveDirection == null) return false;
		int x1 = revertYIndex(startString.charAt(1));
        int y1 = letterToIndex(startString.charAt(0));
        int x2 = revertYIndex(endString.charAt(1));
        int y2 = letterToIndex(endString.charAt(0));
        // check if startfield is empty
 		if (this.board[x1][y1] == null) return false;
		int numberOfSteps = getNumberOfSteps(startString, endString);
		if (!piece.checkIfMoveIsValid(moveString, this.board, moveDirection, numberOfSteps, piece.getPlayerLetter())) return false;
		if(!movedCorrectPiece(x1, x2, y1, y2, piece))	return false;
		if(!deathLook(x1, x2, y1, y2, piece)) return false;
		if(moveIntoCheck(startString, endString, piece)) return false;
        doMove(x1, x2, y1, y2, piece);
		history.add(new Move(moveString, gameState, player));
		//isCheckmate(startString, endString, piece, player);
		checkAllMoves(piece, player);
		if (!isFinished()) changeTurn();
		return true;
	}
	
	private boolean movedCorrectPiece(int x1, int x2, int y1, int y2, Piece piece) {
		// Check if player tries to move the opponents piece
		if (isRedNext() != piece.isRed()) return false; 
		if (this.board[x2][y2] != null) {
			if(this.board[x2][y2].isRed() && isRedNext() || (!this.board[x2][y2].isRed() && !isRedNext())) return false;
		}	
		return true;
	}
	
	private void doMove(int x1, int x2, int y1, int y2, Piece piece) {
		this.board[x1][y1] = null;
        this.board[x2][y2] = piece;
        this.gameState = boardArrayToString(this.board); 
	}
	
	private boolean isValidMove(String moveString, Piece[][] board) {
		String[] split = moveString.split("-");
		String startString = split[0];
		String endString = split[1];
		int x2 = revertYIndex(endString.charAt(1));
		int y2 = letterToIndex(endString.charAt(0));
		Piece piece = getPieceByIndexString(startString, board);
		Direction moveDirection = getMoveDirection(startString, endString);
		int numberOfSteps = getNumberOfSteps(startString, endString);
		// invalid move; only horizontal/vertical/diagonal is allowed 
		if (moveDirection == null) return false;
		if (!piece.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, piece.getPlayerLetter())) return false;
		// don't beat your own piece
		if (board[x2][y2] != null) {
			if(board[x2][y2].isRed() && !isRedNext() || (!board[x2][y2].isRed() && isRedNext())) return false;
		}
		return true;
	} 
	private boolean isValidMove2(String moveString, Piece[][] board) {
		String[] split = moveString.split("-");
		String startString = split[0];
		String endString = split[1];
		Piece piece = getPieceByIndexString(startString, board);
		Direction moveDirection = getMoveDirection(startString, endString);
		int numberOfSteps = getNumberOfSteps(startString, endString);
		// invalid move; only horizontal/vertical/diagonal is allowed 
		if (moveDirection == null) return false;
		if (!piece.checkIfMoveIsValid(moveString, board, moveDirection, numberOfSteps, piece.getPlayerLetter())) return false;
		// don't beat your own piece
		return true;
	}
	
	private void checkAllMoves(Piece piece, Player player) {		
		boolean flag = false;
		for (int i = 0; i < this.board.length; i++) {
			Piece[] row = this.board[i];
			for (int j = 0; j < row.length; j++) {
				if (this.board[i][j] != null) {
					flag = checkAllMovesHelper(i, j, flag, piece);
				}	
			}
		}
		if(flag == false) this.regularGameEnd(player);
	}
	
	private boolean checkAllMovesHelper(int i, int j, boolean flag, Piece piece) {
		int x1 = rollbackYIndex(i);
		char y1 = indexToLetter(j);
		Piece piece2 = this.board[i][j];
		String startString = String.valueOf(y1) + String.valueOf(x1);
		if (getBlackPieceList().contains(this.board[i][j].getPieceLetter()) && piece.getPlayerLetter().equals("r")) {
			flag = tryToEscapeCheck(startString, piece2, flag);
		}
		
		if (getRedPieceList().contains(this.board[i][j].getPieceLetter()) && piece.getPlayerLetter().equals("b")) {
			flag = tryToEscapeCheck(startString, piece2, flag);
		}
		return flag;
	}
	
	private boolean tryToEscapeCheck(String startString, Piece piece2, boolean flag) {
		for (int i2 = 0; i2 < this.board.length; i2++) {
			Piece[] row2 = this.board[i2];
			for (int j2 = 0; j2 < row2.length; j2++) {
				int x2 = rollbackYIndex(i2);
				char y2 = indexToLetter(j2);
				String endString = String.valueOf(y2) + String.valueOf(x2);
				String moveString = startString + "-" + endString;
				flag = tryToEscapeCheckHelper(moveString, startString, endString, piece2, flag);
			}
		}
		return flag;
	}
	
	private boolean tryToEscapeCheckHelper(String moveString, String startString, String endString, Piece piece2, boolean flag) {
		if(isValidMove(moveString, this.board)) {  
			if(!isCheck(startString, endString, piece2)) {
				flag = true;
			} 
		}
		return flag;
	}
	
	private boolean isCheck(String startString, String endString, Piece piece) {
		int x1 = revertYIndex(startString.charAt(1));
        int y1 = letterToIndex(startString.charAt(0));
        int x2 = revertYIndex(endString.charAt(1));
        int y2 = letterToIndex(endString.charAt(0));
        
        Piece[][] board = this.board;
		Piece rollbackMove = board[x2][y2];
		board[x1][y1] = null;
        board[x2][y2] = piece;
        
		
		int[] blackGeneralIndex = getBoardIndexByPieceLetter("g", board);
		int[] redGeneralIndex = getBoardIndexByPieceLetter("G", board);
		
		boolean flag = false;
		int[] generalIndex;
		if (isRedNext()) {
			generalIndex = blackGeneralIndex;
		} else {
			generalIndex = redGeneralIndex;
		}
		
		flag = detectCheckSituation(generalIndex, flag);
		
		board[x1][y1] = piece;
        board[x2][y2] = rollbackMove;
		if (flag == true) return true;
		return false;
	}
	
	private boolean detectCheckSituation(int[] generalIndex, boolean flag) {
		for (int i = 0; i < board.length; i++) {
			Piece[] row = board[i];
			for (int j = 0; j < row.length; j++) {
				if (board[i][j] != null) {
					flag = detectCheckSituationHelper(i, j, generalIndex, flag);
				}	
			}
		}
		return flag;
	}
	
	private boolean detectCheckSituationHelper(int i, int j, int[] generalIndex, boolean flag) {
		int x_1 = rollbackYIndex(i);
		char y_1 = indexToLetter(j);

		int x_2 = rollbackYIndex(generalIndex[0]);
		char y_2 = indexToLetter(generalIndex[1]);
		
		String start_String = String.valueOf(y_1) + String.valueOf(x_1);
		String end_String = String.valueOf(y_2) + String.valueOf(x_2);
		String moveString = start_String + "-" + end_String;
		if (getBlackPieceList().contains(board[i][j].getPieceLetter()) && board[generalIndex[0]][generalIndex[1]].getPieceLetter().equals("G")) {
			if(isValidMove2(moveString, board)) {
				flag = true;
			}
		}
		if (getRedPieceList().contains(board[i][j].getPieceLetter()) && board[generalIndex[0]][generalIndex[1]].getPieceLetter().equals("g")) {
			if(isValidMove2(moveString, board)) {
				flag = true;
			}
		}
		return flag;
	}
	
	private boolean checkMoveString(String moveString) {
		String[] move = moveString.split("-");
		if (move.length != 2) return false;
		if (move[0].length() != 2) return false;
		if (move[1].length() != 2) return false;
		
		List<String> list = new ArrayList<String>(); 
		for(int i = 0; i < 10; i++) {
			list.add(String.valueOf(i));
		}
		
		List<String> charList = new ArrayList<String>(); 
		charList.add("a");
		charList.add("b");
		charList.add("c");
		charList.add("d");
		charList.add("e");
		charList.add("f");
		charList.add("g");
		charList.add("h");
		charList.add("i");
		if (!(list.contains(String.valueOf(move[0].charAt(1))))) return false;
		if (!(list.contains(String.valueOf(move[1].charAt(1))))) return false;

		if (!(charList.contains(String.valueOf(move[0].charAt(0))))) return false;
		if (!(charList.contains(String.valueOf(move[1].charAt(0))))) return false;

		
		return true;
	}
	
	private boolean deathLook(int x1, int x2, int y1, int y2, Piece piece) {

		Piece[][] board = this.board;
		Piece rollbackMove = board[x2][y2];
		board[x1][y1] = null;
        board[x2][y2] = piece;
        
		int[] blackGeneralIndex;
		int[] redGeneralIndex ;

		blackGeneralIndex = getBoardIndexByPieceLetter("g", board);
		redGeneralIndex = getBoardIndexByPieceLetter("G", board);
		boolean flag = false;
		if (blackGeneralIndex[1] == redGeneralIndex[1]) {
			int i = blackGeneralIndex[0];
			while (i + 1 < redGeneralIndex[0]) {
				if (board[i + 1][blackGeneralIndex[1]] != null) flag = true;
				i++;
			}
	
			if (flag == false) {
				board[x1][y1] = piece;
		        board[x2][y2] = rollbackMove;
				return false;
			}
		}
		board[x1][y1] = piece;
        board[x2][y2] = rollbackMove;
		return true;
	}
	
	private boolean moveIntoCheck(String startString, String endString, Piece piece) {
		int x1 = revertYIndex(startString.charAt(1));
		int y1 = letterToIndex(startString.charAt(0));
		int x2 = revertYIndex(endString.charAt(1));
		int y2 = letterToIndex(endString.charAt(0));
		
		int[] moveStringIndexes = new int[6];
		moveStringIndexes[0] = x1;
		moveStringIndexes[1] = y1;
		moveStringIndexes[2] = x2;
		moveStringIndexes[3] = y2;
		
		Piece[][] board = this.board;
		Piece rollbackMove = board[x2][y2];
		board[x1][y1] = null;
		board[x2][y2] = piece;
		
		int[] blackGeneralIndex = getBoardIndexByPieceLetter("g", board);
		int[] redGeneralIndex = getBoardIndexByPieceLetter("G", board);
		int[] generalIndex;
		if (!isRedNext()) {
			generalIndex = blackGeneralIndex;
		} else {
			generalIndex = redGeneralIndex;
		}
		
		 return iterateMoves(generalIndex, piece, rollbackMove, moveStringIndexes);
	}
	
	private boolean iterateMoves(int[] generalIndex, Piece piece, Piece rollbackMove, int[] moveStringIndexes) {		
		for (int i = 0; i < board.length; i++) {
			Piece[] row = board[i];
			for (int j = 0; j < row.length; j++) {
				if (board[i][j] != null) {
					
					String moveString = createMoveString(i, j, generalIndex);

					moveStringIndexes[4] = i;
					moveStringIndexes[5] = j;
					
					if (moveCausesCheck(moveStringIndexes, piece, rollbackMove, generalIndex, moveString)) return true; 
				}
			}
		}
		return false; 
	}
	 
	private String createMoveString(int i, int j, int[] generalIndex) {
		int x_1 = rollbackYIndex(i);
		char y_1 = indexToLetter(j);

		int x_2 = rollbackYIndex(generalIndex[0]);
		char y_2 = indexToLetter(generalIndex[1]);
		
		String start_String = String.valueOf(y_1) + String.valueOf(x_1);
		String end_String = String.valueOf(y_2) + String.valueOf(x_2);
		String moveString = start_String + "-" + end_String;
		return moveString;

	}
	
	private boolean moveCausesCheck(int[] moveStringIndexes, Piece piece, Piece rollbackMove, int[] generalIndex, String moveString) {
		int x1 = moveStringIndexes[0];
		int y1 = moveStringIndexes[1];
		int x2 = moveStringIndexes[2];
		int y2 = moveStringIndexes[3];
		

		if (getBlackPieceList().contains(board[moveStringIndexes[4]][moveStringIndexes[5]].getPieceLetter()) && board[generalIndex[0]][generalIndex[1]].getPieceLetter().equals("G")) {
			if(isValidMove(moveString, board)) {
				board[x1][y1] = piece;
		        board[x2][y2] = rollbackMove;
				return true;
			
			}
		}
		if (getRedPieceList().contains(board[moveStringIndexes[4]][moveStringIndexes[5]].getPieceLetter()) && board[generalIndex[0]][generalIndex[1]].getPieceLetter().equals("g")) {
			if(isValidMove(moveString, board)) {
				board[x1][y1] = piece;
		        board[x2][y2] = rollbackMove;
				return true;
			}
		} 
		return false;
	}
	
	
	private List<String> getRedPieceList() {
		List<String> redPieceList = new ArrayList<String>();
		redPieceList.add("A");
		redPieceList.add("C");
		redPieceList.add("E");
		redPieceList.add("G");
		redPieceList.add("H");
		redPieceList.add("R");
		redPieceList.add("S");
		return redPieceList;
	}
	
	
	private List<String> getBlackPieceList() {
		List<String> blackPieceList = new ArrayList<String>();
		blackPieceList.add("a");
		blackPieceList.add("c");
		blackPieceList.add("e");
		blackPieceList.add("g");
		blackPieceList.add("h");
		blackPieceList.add("r");
		blackPieceList.add("s");
		return blackPieceList;
	}
	
	
	
	private int getNumberOfSteps(String start, String end) {
		int x1 = revertYIndex(start.charAt(1));
		int y1 = letterToIndex(start.charAt(0));

		int x2 = revertYIndex(end.charAt(1));
		int y2 = letterToIndex(end.charAt(0));

		int verticalSteps = Math.abs(x1 - x2);
		int horizontalSteps = Math.abs(y1 - y2);

		if (verticalSteps >= horizontalSteps) {
			return verticalSteps;
		} else {
			return horizontalSteps;
		}
	}

	private void changeTurn() {
		if (nextPlayer == blackPlayer)
			nextPlayer = redPlayer;
		else
			nextPlayer = blackPlayer;
	}
	
	/**
     * Parses the given board string and tries to create a 2-dimensional array of Piece objects.
     * If a field is set to null, no piece stands on this field.
     *
     * @param boardString
     * @return
     * 
     */
	private Piece[][] getBoardArray(String boardString) {
        Piece[][] board = new Piece[10][9];
        String[] rows = boardString.split("/");
        for (int i = 0; i < rows.length; i++) {
        	getBoardArrayHelper(rows, i, board);
        }
        return board;
    }
	
	private Piece[][] getBoardArrayHelper(String[] rows, int i, Piece[][] board) {
		 String row = rows[i];

         char[] cols = row.toCharArray();
         // separate counter for j-index which will be increased for every new initialized field to keep second index in sync
         int pieceCounter = 0;
         for (int j = 0; j < cols.length; j++) {
             String col = String.valueOf(cols[j]);
             if (getColNumbers().contains(col)) {
             	for(int iterator = 0; iterator < Integer.parseInt(col); iterator++) {
                     pieceCounter++;
             	}
             } else {
                 board[i][pieceCounter] = new Piece(col);
                 pieceCounter++;
             }
         }
         return board;
	}
	
	
	private List<String> getColNumbers() {
		List<String> cols = new ArrayList<String>();
		cols.add("1");
		cols.add("2");
		cols.add("3");
		cols.add("4");
		cols.add("5");
		cols.add("6");
		cols.add("7");
		cols.add("8");
		cols.add("9");
		return cols;
	}


    private Piece getPieceByIndexString(String startString, Piece[][] boardArray) {

		int x1 = revertYIndex(startString.charAt(1));
		int y1 = letterToIndex(startString.charAt(0));
		
		if (boardArray[x1][y1] != null) return boardArray[x1][y1];

		return null;
	}
    
    private int[] getBoardIndexByPieceLetter(String piece, Piece[][] secondBoard) {
    	int[] indexes = new int[2];
		for (int i = 0; i < secondBoard.length; i++) {
			Piece[] row = secondBoard[i];
			for (int j = 0; j < row.length; j++) {
				if (secondBoard[i][j] != null) {
					getBoardIndexByPieceLetterHelper(indexes, i, j, piece, secondBoard);
				}
			}
		}
		return indexes;
	}
    
    private int[] getBoardIndexByPieceLetterHelper(int[] indexes, int i, int j, String piece, Piece[][] secondBoard) {
    	if (secondBoard[i][j].getPieceLetter().equals(piece)) {
			indexes[0] = i;
			indexes[1] = j;
		}
    	return indexes;
    }
    
    private String boardArrayToString(Piece[][] board) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < board.length; i++) {
			Piece[] row = board[i];

			for (int j = 0; j < row.length; j++) {
				Piece col = row[j];

				if (col == null) {
					sb.append("1");
				} else {
					sb.append(col.getPieceLetter());
				}
			}
			
			if (i != row.length) {
				sb.append("/");
			}
						
		}
		return aggregateBoardArray(sb.toString());
	}
    
    // helper for boardArrayToString
	private String aggregateBoardArray(String boardString) {
        String[] rows = boardString.split("/");
		StringBuilder newString = new StringBuilder();

        for (int i = 0; i < rows.length; i++) {
        	aggregateBoardArrayHelper(i, newString, rows);
		    if ( i != rows.length - 1) newString.append("/");
        }
		return newString.toString();
	}	
	
	private StringBuilder aggregateBoardArrayHelper(int i, StringBuilder newString, String[] rows) {
		String row = rows[i];
		char[] rowArray = row.toCharArray();
		int j = 0;
		while (j < rowArray.length) {
			int i1 = 0;
			if (String.valueOf(rowArray[j]).equals("1")) {
				while (String.valueOf(rowArray[j]).equals("1")) {
					i1++;
					j++;
					if(j == 9) break;
				} 
				newString.append(Integer.toString(i1));
			} else {
				newString.append(String.valueOf(rowArray[j]));
				j++;
			}
		}
		return newString;
	}
	
	

    private Direction getMoveDirection(String start, String end) {

		int x1 = revertYIndex(start.charAt(1));
		int y1 = letterToIndex(start.charAt(0));

		int x2 = revertYIndex(end.charAt(1));
		int y2 = letterToIndex(end.charAt(0));

		if(getStraightDirections(x1, x2, y1, y2) != null) return getStraightDirections(x1, x2, y1, y2);
		if(getDiagonalDirections(x1, x2, y1, y2) != null) return getDiagonalDirections(x1, x2, y1, y2);
		
		//horse move
		if (isDirectionHorseJump(x1, y1, x2, y2)) return Direction.HORSE_JUMP;

		// return null if no direction was found, e.g. "d3" to "e5"
		// ==> invalid move; only horizontal/vertical/diagonal/horse jump is allowed
		return null;

	}
    
    private Direction getStraightDirections(int x1, int x2, int y1, int y2) {
    	if (y1 < y2 && x1 == x2) return Direction.RIGHT;
		if (y1 > y2 && x1 == x2) return Direction.LEFT;
		if (y1 == y2 && x1 < x2) return Direction.BUTTOM;
		if (y1 == y2 && x1 > x2) return Direction.TOP;
		return null;
    }
    
    private Direction getDiagonalDirections(int x1, int x2, int y1, int y2) {
		if (isDirectionRightTop(x1, y1, x2, y2)) return Direction.RIGHT_TOP;
		if (isDirectionRightButtom(x1, y1, x2, y2)) return Direction.RIGHT_BUTTOM;
		if (isDirectionLeftButtom(x1, y1, x2, y2)) return Direction.LEFT_BUTTOM;
		if (isDirectionLeftTop(x1, y1, x2, y2)) return Direction.LEFT_TOP;
		return null;
    }
    
    private boolean isDirectionRightTop(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < 9; i++) {
			y1++;
			x1--;

			if (y1 == y2 && x1 == x2) {
				return true;
			}
		}

		return false;
	}

	private boolean isDirectionRightButtom(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < 9; i++) {
			y1++;
			x1++;

			if (y1 == y2 && x1 == x2) {
				return true;
			}
		}

		return false;
	}

	private boolean isDirectionLeftButtom(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < 9; i++) {
			y1--;
			x1++;

			if (y1 == y2 && x1 == x2) {
				return true;
			}
		}

		return false;
	}

	private boolean isDirectionLeftTop(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < 9; i++) {
			y1--;
			x1--;

			if (y1 == y2 && x1 == x2) {
				return true;
			}
		}

		return false;
	}
	
	private boolean isDirectionHorseJump(int x1, int y1, int x2, int y2) {
		if (!(((Math.abs(y1-y2) == 2) && (Math.abs(x1-x2) == 1)) || (Math.abs(y1-y2) == 1) && (Math.abs(x1-x2) == 2))) return false;		
		
		if(!checkUpAndDownHorseJump(x1, x2, y1, y2)) return false;
		if(!checkLeftAndRightHorseJump(x1, x2, y1, y2)) return false;
		
		return true;
	}	
	
	/*
	 * Checks if horse is blocked for moves to top and buttom
	 * 
	 */
	private boolean checkUpAndDownHorseJump(int x1, int x2, int y1, int y2) {
		if(y1 == y2 - 2) {
			if (!(this.board[x1][y1+1] == null)) return false; 
		}
		if(y1 == y2 + 2) {
			if (!(this.board[x1][y1-1] == null)) return false; 
		}
		return true;
	}
	
	/*
	 * Checks if horse is blocked for moves to left and right
	 */
	private boolean checkLeftAndRightHorseJump(int x1, int x2, int y1, int y2) {
		if(x1 == x2 + 2) {
			if(!(this.board[x1-1][y1] == null)) return false;
		}
		if(x1 == x2 - 2) {
			if(!(this.board[x1+1][y1] == null)) return false;
		}
		return true;
	}


	/*
	 * Change x-axis from a-i to 0-8 
	 */
	public int letterToIndex(char letter) {
		HashMap<Character, Integer> map = new HashMap<>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put('a', 0);
				put('b', 1);
				put('c', 2);
				put('d', 3);
				put('e', 4);
				put('f', 5);
				put('g', 6);
				put('h', 7);
				put('i', 8);
			}
		};

		return map.get(letter);
	}

	/*
	 * Change x-axis from 0-8 to a-i
	 */
	private char indexToLetter(int letter) {
		HashMap<Integer, Character> map = new HashMap<>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put(0, 'a');
				put(1, 'b');
				put(2, 'c');
				put(3, 'd');
				put(4, 'e');
				put(5, 'f');
				put(6, 'g');
				put(7, 'h');
				put(8, 'i');
			}
		};

		return map.get(letter);
	}

	/*
	 * Reverts y-axis
	 */
	public int revertYIndex(char letter) {
		HashMap<Character, Integer> map = new HashMap<>() {

			private static final long serialVersionUID = 1L;

			{
				put('0', 9);
				put('1', 8);
				put('2', 7);
				put('3', 6);
				put('4', 5);
				put('5', 4);
				put('6', 3);
				put('7', 2);
				put('8', 1);
				put('9', 0);
			}
		};

		return map.get(letter);
	}

	/*
	 * Rollback of reverted y-axis
	 * 
	 * @param i: reverted board row
	 * @return initial board row
	 */
	private int rollbackYIndex(int i) {
		HashMap<Integer, Integer> map = new HashMap<>() {
		
			private static final long serialVersionUID = 1L;

			{
				put(9, 0);
				put(8, 1);
				put(7, 2);
				put(6, 3);
				put(5, 4);
				put(4, 5);
				put(3, 6);
				put(2, 7);
				put(1, 8);
				put(0, 9);
			}
		};

		return map.get(i);
	}
	
}