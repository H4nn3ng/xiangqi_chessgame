package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;


public class Rook extends Piece implements Serializable {

	private static final long serialVersionUID = 8735834218489372652L;
	XiangqiGame game = new XiangqiGame();


	public Rook(String pieceLetter) {
		super(pieceLetter);
		// TODO Auto-generated constructor stub
	}

	public List<Direction> getAllowedDirections() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.TOP);
		list.add(Direction.BUTTOM);
		list.add(Direction.RIGHT);
		list.add(Direction.LEFT);

		return list;
	}
	
	public boolean checkBUTTOMorTOP(int x1, int x2, int y2, Piece[][] currentBoard, Direction moveDirection) {
		boolean flag = false;
		if (moveDirection == Direction.BUTTOM) {
			int i = x1;
			while (i + 1 < x2) {
				if (currentBoard[i + 1][y2] != null) flag = true;
				i++;
			}
			if (flag == true) return false;
						
		} 
		if (moveDirection == Direction.TOP) {
			int i = x2;
			while (i + 1 < x1) {
				if (currentBoard[i + 1][y2] != null) flag = true;
				i++;
			}
			if (flag == true) return false;
		}
		return true;
	}
	
	public boolean checkLEFTorRIGHT(int x1, int y1, int y2, Piece[][] currentBoard, Direction moveDirection) {
		boolean flag = false;
		if (moveDirection == Direction.LEFT) {
			int i = y2;
			while (i + 1 < y1) {
				if (currentBoard[x1][i + 1] != null) flag = true;
				i++;
			}
			if (flag == true) return false;
			
		} 
		if (moveDirection == Direction.RIGHT) {
			int i = y1;
			while (i + 1 < y2) {
				if (currentBoard[x1][i + 1] != null) flag = true;
				i++;
			}
			if (flag == true) return false;
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
		
		if (moveDirection == Direction.BUTTOM || moveDirection == Direction.TOP) {
			if (!checkBUTTOMorTOP(x1, x2, y2, currentBoard, moveDirection)) return false;			
		} 
		
		if (moveDirection == Direction.LEFT  || moveDirection == Direction.RIGHT) {
			if (!checkLEFTorRIGHT(x1, y1, y2, currentBoard, moveDirection)) return false;			
		} 
		
        if (!getAllowedDirections().contains(moveDirection)) return false;
		
		return true;
	}

}