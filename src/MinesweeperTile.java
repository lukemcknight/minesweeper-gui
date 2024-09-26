import javax.swing.*;

public class MinesweeperTile extends JButton {
    private int row;
    private int col;
    private int color;
    private boolean isFlagged;
    private boolean isRevealed;
    private boolean hasBomb;

    public MinesweeperTile(int row, int col){
        super();
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public boolean hasBomb(){
        return hasBomb;
    }

    public void setBomb(boolean isBomb){
        hasBomb = isBomb;
    }

    public boolean isRevealed(){
        return isRevealed;
    }

    public void setRevealed(boolean revealed){
        isRevealed = revealed;
    }

    public boolean isFlagged(){
        return isFlagged;
    }

    public void setFlagged(boolean flagged){
        isFlagged = flagged;
    }

    public int getColor(){
        return color;
    }

    public void setColor(int numColor){
        if((row + col) % 2 == 1){
            color = 1;
        } else {
            color = 0;
        }
    }
}
