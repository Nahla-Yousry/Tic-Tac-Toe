import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static char[][] board = new char[3][3];
    private static char currentPlayer = 'X';

    public static void main(String[] args) throws IOException {
        initializeBoard();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is running...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected!");
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            clientWriters.add(writer);

            Thread handler = new Thread(new ClientHandler(socket));
            handler.start();
        }
    }

    private static void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            Arrays.fill(board[i], '-');
        }
    }

    private static synchronized boolean makeMove(int row, int col, char symbol) {
        if (board[row][col] == '-') {
            board[row][col] = symbol;
            return true;
        }
        return false;
    }

    private static synchronized String getBoardState() {
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state.append(board[i][j]);
            }
        }
        return state.toString(); // لا نضيف \n هنا
    }

    private static synchronized boolean checkWin(char symbol) {
        // Check rows, columns, and diagonals
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) ||
                (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)) {
                return true;
            }
        }
        if ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
            (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)) {
            return true;
        }
        return false;
    }

    private static synchronized boolean isDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private static synchronized void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);

                    if (makeMove(row, col, currentPlayer)) {
                        String boardState = getBoardState();
                        broadcast("UPDATE:" + boardState);

                        if (checkWin(currentPlayer)) {
                            broadcast("WIN:" + currentPlayer);
                            resetGame();
                        } else if (isDraw()) {
                            broadcast("DRAW");
                            resetGame();
                        } else {
                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                            broadcast("TURN:" + currentPlayer);
                        }
                    } else {
                        broadcast("INVALID");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void resetGame() {
            initializeBoard();
            currentPlayer = 'X';
        }
    }
}






