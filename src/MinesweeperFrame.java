import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class MinesweeperFrame extends JFrame {
    Random random = new Random();
    private static final int ROW = 8;
    private static final int COL = 10;
    private static final int NUM_OF_BOMBS = 10;
    int tilesRevealed = 0;
    private boolean gameInProgress = false;
    MinesweeperTile[][] tiles = new MinesweeperTile[ROW][COL];
    JPanel mainPnl;
    JPanel gamePnl;
    JPanel headerPnl;
    JLabel flagLbl;
    Color lightgreen = new Color(66,245,135);
    Color darkgreen = new Color(49,181,100);
    Color darkBrown = new Color(209, 172, 71);
    Color lightBrown = new Color(201, 176, 107);
    Color blue = new Color(53, 144, 219);
    Color green = new Color(37, 143, 42);
    Color red = new Color(255, 50, 43);
    Color purple = new Color(167, 0, 176);
    Color orange = new Color(255, 184, 51);
    int numFlags;

    ImageIcon flag;
    ImageIcon clock;
    int numColor = 0;
    int seconds = 0;
    JLabel timeLbl;
    Timer timer;

    public MinesweeperFrame(){
        setSize(800,700);
        setLocation(0,0);

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        createGamePanel();
        mainPnl.add(gamePnl, BorderLayout.CENTER);
        createHeaderPanel();
        mainPnl.add(headerPnl, BorderLayout.NORTH);
        add(mainPnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void createHeaderPanel(){
        headerPnl = new JPanel();
        headerPnl.setLayout(new GridLayout(1,4));
        flag = new ImageIcon("src/flag.png");
        clock = new ImageIcon("src/clock.png");
        Image img = flag.getImage();
        Image resizedImg = img.getScaledInstance(50,50, Image.SCALE_SMOOTH);
        ImageIcon resizedFlag = new ImageIcon(resizedImg);

        Image clockImg = clock.getImage();
        Image clockImage = clockImg.getScaledInstance(50,50, Image.SCALE_SMOOTH);
        ImageIcon resizedClock = new ImageIcon(clockImage);

        flagLbl = new JLabel(String.valueOf(numFlags), resizedFlag, JLabel.CENTER);
        timeLbl = new JLabel(String.valueOf(seconds), resizedClock, JLabel.CENTER);
        headerPnl.add(flagLbl);
        headerPnl.add(timeLbl);
    }
    public void createGamePanel(){
        gamePnl = new JPanel();
        gamePnl.setLayout(new GridLayout(ROW, COL));
        startGame();
    }
    private void startGame() {
        numFlags = 10;
        seconds = 0;
        tilesRevealed = 0;
        startTimer();
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                if (tiles[row][col] == null) {
                    tiles[row][col] = new MinesweeperTile(row, col);
                    tiles[row][col].setFont(new Font("Sans", Font.BOLD, 20));
                    tiles[row][col].setComponentPopupMenu(null);
                    tiles[row][col].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if(gameInProgress) {
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    tileRevealed((MinesweeperTile) e.getSource());
                                }
                                if (e.getButton() == MouseEvent.BUTTON3) {
                                    checkTile((MinesweeperTile) e.getSource());
                                }
                            }
                        }
                    });
                    tiles[row][col].setColor(numColor);
                    numColor++;;
                    gamePnl.add(tiles[row][col]);
                }
                tiles[row][col].setIcon(null);
                tiles[row][col].setBorder(null);
                tiles[row][col].setText("");
                tiles[row][col].setRevealed(false);
                tiles[row][col].setFlagged(false);
                tiles[row][col].setBomb(false);
                setBackgroundColor(row, col);
            }
        }
        gameInProgress = true;
    }

    private void startTimer(){
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                timeLbl.setText(String.valueOf(seconds));
            }
        });
        timer.start();
    }
    private void tileRevealed(MinesweeperTile tile) {
        if(gameInProgress) {
            //Get coords of tile clicked
            int col = tile.getCol();
            int row = tile.getRow();
            //On first click, place bombs
            if(!tiles[row][col].isRevealed()) {
                if (!tiles[row][col].isFlagged()) {
                    if (tilesRevealed < 1) {
                        placeBombs(row, col);
                    }
                    tilesRevealed++;
                    //Set revealed tiles
                    tiles[row][col].setRevealed(true);
                    //Check for empty tiles around
                    checkEmpty(row, col);
                    //Check for bomb
                    bombClick(row, col);
                    //How many bombs in area
                    display(areaBombs(row,col), row, col);
                    //Check for win
                    isWin();
                }
            }
        }
    }
    private void endGame(boolean won) {
        gameInProgress = false;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        int playAgain;
        if(won){
            playAgain = JOptionPane.showConfirmDialog(null, "You Won!\nDo you want to play again?");
        } else {
            playAgain = JOptionPane.showConfirmDialog(null, "You were blown up!\nDo you want to play again?");
        }

        if(playAgain == JOptionPane.YES_OPTION){
            new Thread(this::startGame).start();
        } else {
            System.exit(0);
        }
    }
    private void checkTile(MinesweeperTile tile){
        if(!gameInProgress){
            return;
        }
        int row = tile.getRow();
        int col = tile.getCol();
        if(tiles[row][col].isFlagged()){
            tileUnFlagged(tile);
        } else {
            tileFlagged(tile);
        }
    }
    private void tileFlagged(MinesweeperTile tile){
        flag = new ImageIcon("src/flag.png");
        Image img = flag.getImage();
        Image resizedImg = img.getScaledInstance(50,50, Image.SCALE_SMOOTH);
        ImageIcon resizedFlag = new ImageIcon(resizedImg);
        int col = tile.getCol();
        int row = tile.getRow();
        if(!tiles[row][col].isRevealed()){
            if(!tiles[row][col].isFlagged()){
                if(numFlags > 0){
                    tiles[row][col].setFlagged(true);
                    tiles[row][col].setIcon(resizedFlag);
                    numFlags--;
                    flagLbl.setText(String.valueOf(numFlags));
                }
            }
        }
    }
    private void tileUnFlagged(MinesweeperTile tile){
        int row = tile.getRow();
        int col = tile.getCol();
        tiles[row][col].setFlagged(false);
        tiles[row][col].setIcon(null);
        numFlags++;
        flagLbl.setText(String.valueOf(numFlags));
    }
    private void placeBombs(int r, int c){
        for(int i = 0; i < NUM_OF_BOMBS; i++){
            int randomRow;
            int randomCol;

            do {
                do{
                    randomRow = random.nextInt(ROW);
                    randomCol = random.nextInt(COL);
                } while (checkStartBombs(randomRow, randomCol, r, c));
            } while (tiles[randomRow][randomCol].hasBomb());

            tiles[randomRow][randomCol].setBomb(true);
        }
    }

    private void isWin(){
        int tilesRevealedCounter = 0;
        for(int row = 0; row < ROW; row++){
            for(int col = 0; col < COL; col++){
                if(tiles[row][col].isRevealed()){
                    tilesRevealedCounter++;
                }
            }
        }
        if(tilesRevealedCounter == ((ROW * COL) - NUM_OF_BOMBS)){
            endGame(true);
        }
    }

    private void bombClick(int row, int col){
        if(tiles[row][col].hasBomb()){
            tiles[row][col].setText("B");
            endGame(false);
        }
    }

    private int areaBombs(int row, int col){
        int areaBombs = 0;
        int[] dx = { -1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = { -1, -1, -1, 0, 0, 1, 1, 1};

        for(int i = 0; i < dx.length; i++){
            int newRow = row + dx[i];
            int newCol = col + dy[i];

            if(newRow >= 0 && newRow < ROW && newCol >= 0 && newCol < COL) {
                if(tiles[newRow][newCol].hasBomb()){
                    areaBombs++;
                }
            }
        }
        return areaBombs;
    }

    private boolean checkStartBombs(int randomRow, int randomCol, int row, int col){
        if(tiles[row][col] == tiles[randomRow][randomCol]){
            return true;
        }

        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        for(int i = 0; i < dx.length; i++){
            int newRow = row + dx[i];
            int newCol = col + dy[i];
            if(isValidPosition(newRow, newCol) && tiles[newRow][newCol] == tiles[randomRow][randomCol]){
                return true;
            }
        }

        return false;
    }
    private void checkEmpty(int row, int col){
        if(areaBombs(row, col) == 0){
            int[] dx = { -1, 0, 1, -1, 1, -1, 0, 1};
            int[] dy = { -1, -1, -1, 0, 0, 1, 1, 1};

            for(int i = 0; i < dx.length; i++){
                int newRow = row + dx[i];
                int newCol = col + dy[i];

                if(isValidPosition(newRow, newCol) && !tiles[newRow][newCol].isRevealed()){
                    tiles[newRow][newCol].setRevealed(true);
                    if(areaBombs(newRow, newCol) == 0){
                        display(areaBombs(newRow, newCol), newRow, newCol);
                        checkEmpty(newRow, newCol);
                    } else {
                        display(areaBombs(newRow, newCol), newRow, newCol);
                    }
                }
            }
        }
    }

    private void display(int areaBombs, int row, int col){
        if(areaBombs == 0){
            tiles[row][col].setText("");
        } else {
            tiles[row][col].setText(String.valueOf(areaBombs));
            if(areaBombs == 1){
                tiles[row][col].setForeground(blue);
            } else if(areaBombs == 2) {
                tiles[row][col].setForeground(green);
            } else if(areaBombs == 3) {
                tiles[row][col].setForeground(red);
            } else if(areaBombs == 4) {
                tiles[row][col].setForeground(purple);
            } else if(areaBombs == 5) {
                tiles[row][col].setForeground(orange);
            }
        }
        setBackgroundColor(row, col);
        tiles[row][col].setIcon(null);
    }

    private void setBackgroundColor(int row, int col){
        if(tiles[row][col].isRevealed()){
            if(tiles[row][col].getColor() == 1) {
                tiles[row][col].setBackground(lightBrown);
            } else {
                tiles[row][col].setBackground(darkBrown);
            }
        }
        else{
            if(tiles[row][col].getColor() == 1) {
                tiles[row][col].setBackground(lightgreen);
            } else {
                tiles[row][col].setBackground(darkgreen);
            }
        }
    }

    private boolean isValidPosition(int row, int col){
        return row >= 0 && row < ROW && col >= 0 && col < COL;
    }
}
