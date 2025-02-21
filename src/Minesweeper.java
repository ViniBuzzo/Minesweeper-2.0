import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper {
    private static class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    static JFrame currentFrame = null;

    // Parâmetros que variam conforme a dificuldade
    int tileSize;
    int numRows;
    int numCols;
    int boardWidth;
    int boardHeight;
    int mineCount;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton restartButton = new JButton("Reiniciar");
    JButton selectDificulty = new JButton("Dificuldade");
    private Timer timer;
    private int elapsedTime = 0;
    private final JLabel timerLabel = new JLabel("Tempo: 0s");

    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;
    boolean timerStarted = false;

    // Construtor sem o parâmetro Font; os valores serão calculados dinamicamente
    public Minesweeper(int tileSize, int numRows, int numCols, int mineCount) {
        this.tileSize = tileSize;
        this.numRows = numRows;
        this.numCols = numCols;
        this.mineCount = mineCount;
        this.boardWidth = numCols * tileSize;
        this.boardHeight = numRows * tileSize + 50;

        if (currentFrame != null) {
            currentFrame.dispose();
        }

        setupFrame();
        setupBoard();
        setMines();
        setTimer();
    }

    private Font getTileFont() {
        int computedSize = (int) (tileSize * 0.66);
        return new Font("Arial Unicode MS", Font.PLAIN, computedSize);
    }

    void setTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Tempo: " + elapsedTime + "s");
        });
    }

    void stopTimer() {
        timer.stop();
    }

    void setupFrame() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);

        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);

        restartButton.addActionListener(e -> resetGame());
        selectDificulty.addActionListener(e -> showDifficultDialog());

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(selectDificulty);
        buttonPanel.add(timerLabel);
        buttonPanel.add(restartButton);
        textPanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);

        currentFrame = frame;
    }

    void setupBoard() {
        board = new MineTile[numRows][numCols];
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel, BorderLayout.CENTER);

        Font tileFont = getTileFont();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(tileFont);

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        MineTile tile = (MineTile) e.getSource();

                        if (!timerStarted) {
                            timerStarted = true;
                            timer.start();
                        }

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().isEmpty()) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText().isEmpty() && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText().equals("🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);
    }

    void setMines() {
        mineList = new ArrayList<>();
        int minesPlaced = 0;
        while (minesPlaced < mineCount) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];

            if (!mineList.contains(tile)) {
                mineList.add(tile);
                minesPlaced++;
            }
        }
    }

    void revealMines() {
        for (MineTile mine : mineList) {
            mine.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Explodiu!");
        stopTimer();
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) return;

        tile.setEnabled(false);
        tilesClicked++;

        int minesFound = 0;
        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == (numRows * numCols) - mineList.size()) {
            gameOver = true;
            textLabel.setText("Vitória!");
            timer.stop();
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return 0;
        return mineList.contains(board[r][c]) ? 1 : 0;
    }

    void resetGame() {
        // Se o jogo acabou, reinicie completamente o jogo
        if (gameOver) {
            gameOver = false;
            tilesClicked = 0;
            elapsedTime = 0;
            textLabel.setText("Minesweeper");
            timerLabel.setText("Tempo: 0s");
            timerStarted = false;
            setupBoard(); // Recria o tabuleiro
            setMines(); // Coloca as minas novamente
        } else {
            // Caso contrário, apenas resetamos o tabuleiro e o tempo, mantendo o estado de gameOver
            boardPanel.removeAll();
            setupBoard(); // Recria o tabuleiro
            setMines(); // Coloca as minas novamente

            // Reinicia o tempo
            timer.stop();
            elapsedTime = 0;
            timerLabel.setText("Tempo: 0s");
            timerStarted = false;
        }
    }

    void startNewGame() {
        gameOver = false;
        timerStarted = false;
        elapsedTime = 0;
        tilesClicked = 0;
        mineList.clear();
        setMines();
        timer.start();
    }

    void showDifficultDialog() {
        String[] options = {"Fácil", "Médio", "Difícil", "Impossível"};
        int choice = JOptionPane.showOptionDialog(frame, "Escolha a dificuldade", "Dificuldade",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0:
                new Minesweeper(70, 8, 8, 10); // Fácil
                break;
            case 1:
                new Minesweeper(50, 12, 12, 20); // Médio
                break;
            case 2:
                new Minesweeper(38, 16, 16, 40); // Difícil
                break;
            case 3:
                new Minesweeper(30, 19, 26, 99); // Impossível
                break;
            default:
                break;
        }
    }
}
