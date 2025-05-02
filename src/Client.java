import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private char playerSymbol;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    // زر لإعادة اللعب
    private JButton replayButton;

    public Client(char symbol) {
        this.playerSymbol = symbol;
        setTitle("Tic-Tac-Toe - Player " + playerSymbol);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // إعداد لوحة اللعب
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setBackground(new Color(255, 204, 153)); // لون خلفية جميل

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 48));
                buttons[i][j].setForeground(Color.BLUE); // لون النص
                buttons[i][j].setBackground(new Color(255, 255, 204)); // لون الزر
                buttons[i][j].setBorderPainted(true);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                boardPanel.add(buttons[i][j]);
            }
        }

        // إعداد زر إعادة اللعب
        replayButton = new JButton("Play Again");
        replayButton.setFont(new Font("Arial", Font.BOLD, 24));
        replayButton.setForeground(Color.WHITE);
        replayButton.setBackground(new Color(0, 102, 204));
        replayButton.addActionListener(e -> resetGame());

        // ترتيب العناصر
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(replayButton, BorderLayout.SOUTH);
        replayButton.setVisible(false); // يظهر فقط عند انتهاء اللعبة

        setVisible(true);

        connectToServer();
        listenForMessages();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("UPDATE:")) {
                        updateBoard(message.substring(7)); // ازالة "UPDATE:"
                    } else if (message.startsWith("WIN:")) {
                        JOptionPane.showMessageDialog(this, "Player " + message.charAt(4) + " wins!");
                        showReplayOption();
                    } else if (message.equals("DRAW")) {
                        JOptionPane.showMessageDialog(this, "It's a draw!");
                        showReplayOption();
                    } else if (message.startsWith("TURN:")) {
                        enableButtons(message.charAt(5) == playerSymbol);
                    } else if (message.equals("INVALID")) {
                        JOptionPane.showMessageDialog(this, "Invalid move!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateBoard(String boardState) {
        if (boardState.length() != 9) { // التحقق من أن الرسالة تحتوي على 9 أحرف فقط
            JOptionPane.showMessageDialog(this, "Invalid board state received!");
            return;
        }

        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char symbol = boardState.charAt(index++);
                buttons[i][j].setText(symbol == '-' ? "" : String.valueOf(symbol));
            }
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
    }

    private void enableButtons(boolean enabled) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(enabled);
            }
        }
    }

    private void showReplayOption() {
        resetBoard();
        enableButtons(false); // تعطيل الأزرار
        replayButton.setVisible(true); // إظهار زر إعادة اللعب
    }

    private void resetGame() {
        replayButton.setVisible(false); // إخفاء زر إعادة اللعب
        connectToServer(); // إعادة الاتصال بالخادم
        listenForMessages(); // إعادة الاستماع للرسائل
        enableButtons(playerSymbol == 'X'); // بدء اللعبة مع اللاعب X
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            writer.println(row + "," + col);
        }
    }

    public static void main(String[] args) {
        String symbol = JOptionPane.showInputDialog("Enter your symbol (X or O):").toUpperCase();
        if (symbol.equals("X") || symbol.equals("O")) {
            new Client(symbol.charAt(0));
        } else {
            JOptionPane.showMessageDialog(null, "Invalid symbol!");
        }
    }
}



























