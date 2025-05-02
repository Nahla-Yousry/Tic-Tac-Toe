package tic.tac.toe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe extends JFrame {
    private JButton[] buttons = new JButton[9];
    private boolean playerTurn = true; // true for X, false for O
    private boolean isSinglePlayer = true; // Single player mode by default

    public TicTacToe() {
        setTitle("Tic Tac Toe Game");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.PINK);

        // Create panel for game buttons
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));
        gamePanel.setBackground(Color.PINK);

        // Initialize buttons
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(new ButtonClickListener(i));
            gamePanel.add(buttons[i]);
        }

        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.PINK);

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.setBackground(Color.LIGHT_GRAY);
        singlePlayerButton.addActionListener(e -> {
            isSinglePlayer = true;
            resetGame();
        });

        JButton multiPlayerButton = new JButton("Multi Player");
        multiPlayerButton.setBackground(Color.LIGHT_GRAY);
        multiPlayerButton.addActionListener(e -> {
            isSinglePlayer = false;
            resetGame();
        });

        controlPanel.add(singlePlayerButton);
        controlPanel.add(multiPlayerButton);

        // Add panels to frame
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        resetGame();
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].setEnabled(true);
            buttons[i].setBackground(Color.WHITE);
        }
        playerTurn = true;
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[index].getText().equals("")) {
                if (playerTurn) {
                    buttons[index].setText("X");
                    buttons[index].setForeground(Color.BLUE);
                } else {
                    buttons[index].setText("O");
                    buttons[index].setForeground(Color.RED);
                }
                buttons[index].setEnabled(false);
                checkForWin();
                playerTurn = !playerTurn;

                if (isSinglePlayer && !playerTurn && !isGameOver()) {
                    computerMove();
                }
            }
        }
    }

    private void computerMove() {
        // Simple AI: Find the first available empty button
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                buttons[i].setForeground(Color.RED);
                buttons[i].setEnabled(false);
                checkForWin();
                playerTurn = !playerTurn;
                break;
            }
        }
    }

    private void checkForWin() {
        String[][] board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = buttons[i * 3 + j].getText();
            }
        }

        // Check rows, columns, and diagonals
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].equals("") && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                announceWinner(board[i][0], new int[]{i * 3, i * 3 + 1, i * 3 + 2});
                return;
            }
            if (!board[0][i].equals("") && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                announceWinner(board[0][i], new int[]{i, i + 3, i + 6});
                return;
            }
        }

        if (!board[0][0].equals("") && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            announceWinner(board[0][0], new int[]{0, 4, 8});
            return;
        }

        if (!board[0][2].equals("") && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            announceWinner(board[0][2], new int[]{2, 4, 6});
            return;
        }

        if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "It's a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    private boolean isGameOver() {
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    private void announceWinner(String winner, int[] winningPositions) {
        // Highlight winning positions
        for (int pos : winningPositions) {
            buttons[pos].setBackground(Color.GREEN); // Green background for winning positions
        }

        // Display winner message
        String message = winner.equals("X") ? "Player X wins!" : "Player O wins!";
        int choice = JOptionPane.showConfirmDialog(this, message + "\nDo you want to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0); // Exit the game
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToe game = new TicTacToe();
            game.setVisible(true);
        });
    }
}
