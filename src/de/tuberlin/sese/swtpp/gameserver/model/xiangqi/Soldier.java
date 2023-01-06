package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Soldier extends Piece implements Serializable {


	private static final long serialVersionUID = 8735834218489372652L;

	public Soldier(String pieceLetter) {
		super(pieceLetter);
	}

	private List<Direction> getAllowedDirectionsRed() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.TOP);
		list.add(Direction.RIGHT);
		list.add(Direction.LEFT);
	
		return list;
	}
	
	private List<Direction> getAllowedDirectionsBlack() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.BUTTOM);
		list.add(Direction.RIGHT);
		list.add(Direction.LEFT);
	
		return list;
	}

	private List<Integer> getAllowedStepNumbers() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		return list; 
	}

	private boolean redDirectionRules(String playerLetter, char[] destinationRow, Direction moveDirection) {
		if (playerLetter.equals("r") && Integer.parseInt(String.valueOf(destinationRow[1])) > 4) {
        	if (!getAllowedDirectionsRed().contains(moveDirection)) return false;
        }
        if (playerLetter.equals("r") && Integer.parseInt(String.valueOf(destinationRow[1])) < 5) {
        	if (!moveDirection.equals(Direction.TOP)) return false;
        }
        return true;
	}
	
	private boolean blackDirectionRules(String playerLetter, char[] destinationRow, Direction moveDirection) {
		if (playerLetter.equals("b") && Integer.parseInt(String.valueOf(destinationRow[1])) < 5) {
        	if (!getAllowedDirectionsBlack().contains(moveDirection)) return false;
        }
        if (playerLetter.equals("b") && Integer.parseInt(String.valueOf(destinationRow[1])) > 4) {
        	if (!moveDirection.equals(Direction.BUTTOM)) return false;
        }
        return true;
	}
	
	public boolean checkIfMoveIsValid(String moveString, Piece[][] currentBoard, Direction moveDirection, int numberOfSteps, String playerLetter) {
		String[] moveStringSplit = moveString.split("-");
		String destination = moveStringSplit[1];
		char[] destinationRow = destination.toCharArray();
		
        if (!(getAllowedStepNumbers().contains(numberOfSteps))) return false;    
        
        if(!redDirectionRules(playerLetter, destinationRow, moveDirection)) return false;
        if(!blackDirectionRules(playerLetter, destinationRow, moveDirection)) return false;

		return true;
	}
}