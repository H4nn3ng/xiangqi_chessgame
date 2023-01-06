package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Cannon extends Piece implements Serializable {

	private static final long serialVersionUID = 8735834218489372652L;
	XiangqiGame game = new XiangqiGame();

	public Cannon(String pieceLetter) {
		super(pieceLetter);
	}

	public List<Direction> getAllowedDirections() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.TOP);
		list.add(Direction.BUTTOM);
		list.add(Direction.RIGHT);
		list.add(Direction.LEFT);

		return list;
	}
 	
	public boolean checkBUTTOMorTOP(Piece[][] currentBoard, int x1, int x2, int y2, Direction moveDirection) {
		int flagInt = 0;
		if (moveDirection == Direction.BUTTOM) {
			int i = x1;
			while (i + 1 < x2) {
				if (currentBoard[i + 1][y2] != null) {
					flagInt++;
				}
				i++;
			}
			if (currentBoard[x2][y2] != null) {
				if(flagInt != 1) return false;
			} else {
				if (flagInt > 0) return false;
			}

		} 
		if (moveDirection == Direction.TOP) {
			if(!checkTOP(currentBoard, x1, x2, y2)) return false;
		}
		return true;
	}
	
	public boolean checkTOP(Piece[][] currentBoard, int x1, int x2, int y2) {
		int flagInt = 0;
		int i = x2;
		while (i + 1 < x1) {
			if (currentBoard[i + 1][y2] != null) {
				flagInt++;
			}
			i++;
		}
		if (currentBoard[x2][y2] != null) {
			if(flagInt != 1) return false;
		} else {
			if (flagInt > 0) return false;
		}
		return true;
	}
	
	public boolean checkLEFT(Piece[][] currentBoard, int x1, int x2, int y1, int y2) {
		int flagInt = 0;
		int i = y2;
		while (i + 1 < y1) {
			if (currentBoard[x1][i + 1] != null) {
				flagInt++;
			}
			i++;
		}
		if (currentBoard[x2][y2] != null) {
			if(flagInt != 1) return false;
		} else {
			if (flagInt > 0) return false;
		}
		return true;
	}
	
	public boolean checkRIGHT(Piece[][] currentBoard, int x1, int x2, int y1, int y2) {
		int flagInt = 0;
		int i = y1;
		while (i + 1 < y2) {
			if (currentBoard[x1][i + 1] != null) {
				flagInt++;
			}
			i++;
		}
		if (currentBoard[x2][y2] != null) {
			if(flagInt != 1) return false;
		} else {
			if (flagInt > 0) return false;
		}
		return true;
	}

	public boolean checkIfMoveIsValid(String moveString, Piece[][] currentBoard, Direction moveDirection, int numberOfSteps, String playerLetter) {
		String[] moveStringSplit = moveString.split("-");
		String start = moveStringSplit[0];
		String destination = moveStringSplit[1];
		int x1 = game.revertYIndex(start.charAt(1));
		int y1 = game.letterToIndex(start.charAt(0));

		int x2 = game.revertYIndex(destination.charAt(1));
		int y2 = game.letterToIndex(destination.charAt(0));
		
		// not allowed move direction for the given figure
        if (!getAllowedDirections().contains(moveDirection)) return false;        

		if (moveDirection == Direction.BUTTOM || moveDirection == Direction.TOP) {
			if (!checkBUTTOMorTOP(currentBoard, x1, x2, y2, moveDirection)) return false;			
		} 
		
		if (moveDirection == Direction.LEFT) {
			if (!checkLEFT(currentBoard, x1, x2, y1, y2)) return false;			
		} 
		
		if (moveDirection == Direction.RIGHT) {
			if (!checkRIGHT(currentBoard, x1, x2, y1, y2)) return false;			
		} 
		 
		return true;

	} 
}