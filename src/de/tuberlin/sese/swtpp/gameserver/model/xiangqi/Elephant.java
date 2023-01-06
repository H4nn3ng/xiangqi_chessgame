package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Elephant extends Piece implements Serializable {

	private static final long serialVersionUID = 8735834218489372652L;
	XiangqiGame game = new XiangqiGame();

	public Elephant(String pieceLetter) {
		super(pieceLetter);
	}	

	public List<Direction> getAllowedDirections() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.LEFT_TOP);
		list.add(Direction.RIGHT_TOP);
		list.add(Direction.RIGHT_BUTTOM);
		list.add(Direction.LEFT_BUTTOM);

		return list;
	}
	
	public List<Integer> getAllowedStepNumbers() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(2);
		return list;
	} 
	
	public boolean checkIfMoveIsValid(String moveString, Piece[][] currentBoard, Direction moveDirection, int numberOfSteps, String playerLetter) {
		String[] moveStringSplit = moveString.split("-");
		String start = moveStringSplit[0];
		String destination = moveStringSplit[1];
		char[] destinationRow = destination.toCharArray();
		
		if (isBlocked(start, moveDirection, currentBoard)) return false;
		
		// not allowed move direction for the given figure 
		if (!getAllowedDirections().contains(moveDirection)) return false;
        if (!(getAllowedStepNumbers().contains(numberOfSteps))) return false;
        
        //do not cross the river
        if (playerLetter.equals("r") && Integer.parseInt(String.valueOf(destinationRow[1])) > 4) return false;
        if (playerLetter.equals("b") && Integer.parseInt(String.valueOf(destinationRow[1])) < 5) return false;
        
		return true;
	}

	public boolean isBlocked(String start, Direction moveDirection, Piece[][] currentBoard) {
		int x1 = game.revertYIndex(start.charAt(1));
		int y1 = game.letterToIndex(start.charAt(0));

		if (moveDirection.equals(Direction.LEFT_TOP)) {
			if(currentBoard[x1-1][y1-1] != null) return true;
		}
		
		if (moveDirection.equals(Direction.LEFT_BUTTOM)) {
			if(currentBoard[x1+1][y1-1] != null) return true;
		}
		
		if (moveDirection.equals(Direction.RIGHT_TOP)) {
			if(currentBoard[x1-1][y1+1] != null) return true;
		}
		
		if (moveDirection.equals(Direction.RIGHT_BUTTOM)) {
			if(currentBoard[x1+1][y1+1] != null) return true;
		}
		return false;
	}
}