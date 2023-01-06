package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Advisor extends Piece implements Serializable {
	
public Advisor(String pieceLetter) {
		super(pieceLetter);
		// TODO Auto-generated constructor stub
	}

private static final long serialVersionUID = 8735834218489372652L;
	
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
		list.add(1);
		return list;
	}
	
	public boolean checkIfMoveIsValid(String moveString, Piece[][] currentBoard, Direction moveDirection, int numberOfSteps, String playerLetter) {
		String[] moveStringSplit = moveString.split("-");
		String destination = moveStringSplit[1];
		String[] palace;
		
        if (!(getAllowedStepNumbers().contains(numberOfSteps))) return false;

		// not allowed move direction for the given figure
        if (!getAllowedDirections().contains(moveDirection)) return false;
        if (playerLetter.equals("r")) {
        	palace = getRedPalace();
        } else {
        	palace = getBlackPalace();
        }
        if (!staysInPalace(palace, destination)) return false; 
        
		return true;
	}
	
	public String[] getRedPalace() {
        String[] redPalace = {"d0", "d2", "e1", "f0", "f2"};
        return redPalace;
	}
	
	public String[] getBlackPalace() {
        String[] blackPalace = {"d7", "d9", "e8", "f7", "f9"};
        return blackPalace;
	}
	
	public boolean staysInPalace(String[] palace, String destination) {
		for (int i = 0; i < palace.length; i++) {
			if (palace[i].equals(destination)) return true;
		}
		return false;
}
}
