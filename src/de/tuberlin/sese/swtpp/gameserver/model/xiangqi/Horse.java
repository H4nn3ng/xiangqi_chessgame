package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Horse extends Piece implements Serializable {

	private static final long serialVersionUID = 8735834218489372652L;

	public Horse(String pieceLetter) {
		super(pieceLetter);
	}

	public List<Direction> getAllowedDirections() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.HORSE_JUMP);

		return list;
	}
	
	public List<Integer> getAllowedStepNumbers() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(2);
		return list;
	}
	
	public boolean checkIfMoveIsValid(String moveString, Piece[][] currentBoard, Direction moveDirection, int numberOfSteps, String playerLetter) {
        if (!(getAllowedStepNumbers().contains(numberOfSteps))) return false;
		if (!getAllowedDirections().contains(moveDirection)) return false;
        
		return true;
	}

}