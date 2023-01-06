package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


public class General extends Piece implements Serializable {
	
	private static final long serialVersionUID = 8735834218489372652L;

	
	public General(String pieceLetter) {
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

	public List<Integer> getAllowedStepNumbers() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		return list;
	}
	
	public boolean checkIfMoveIsValid(String moveString, Piece[][] currentBoard, Direction moveDirection, int numberOfSteps, String playerLetter) {
		String[] moveStringSplit = moveString.split("-");
		String destination = moveStringSplit[1];
		String[] palace;
		
		// don't go diagonal
        if (!getAllowedDirections().contains(moveDirection)) return false;
        // don't go more than one step
        if (!(getAllowedStepNumbers().contains(numberOfSteps))) return false;
        if (playerLetter.equals("r")) {
        	palace = getRedPalace();
        } else {
        	palace = getBlackPalace();
        }
        if (!staysInPalace(palace, destination)) return false; 

        return true;
	
	} 
	
	public String[] getRedPalace() {
        String[] redPalace = {"d0", "d1", "d2", "e0", "e1", "e2", "f0", "f1", "f2"};
        return redPalace;
	}
	
	public String[] getBlackPalace() {
        String[] blackPalace = {"d7", "d8", "d9", "e7", "e8", "e9", "f7", "f8", "f9"};
        return blackPalace;
	}
	
	public boolean staysInPalace(String[] palace, String destination) {
			for (int i = 0; i < palace.length; i++) {
				if (palace[i].equals(destination)) return true;
			}
			return false;
	}
}
