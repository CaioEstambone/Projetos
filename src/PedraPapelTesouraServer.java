import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PedraPapelTesouraServer {
    public static void main(String[] args) {
        final int PORT = 12345;

        try {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Aguardando conexões...");

                // Aceitar a conexão do jogador 1
                Socket player1Socket = serverSocket.accept();
                System.out.println("Jogador 1 conectado.");

                // Aceitar a conexão do jogador 2
                Socket player2Socket = serverSocket.accept();
                System.out.println("Jogador 2 conectado.");

                // Criar as streams de entrada e saída para os jogadores
                ObjectOutputStream player1Out = new ObjectOutputStream(player1Socket.getOutputStream());
                ObjectInputStream player1In = new ObjectInputStream(player1Socket.getInputStream());
                ObjectOutputStream player2Out = new ObjectOutputStream(player2Socket.getOutputStream());
                ObjectInputStream player2In = new ObjectInputStream(player2Socket.getInputStream());

                // Loop principal do jogo
                while (true) {
                    // Receber a jogada do jogador 1
                    String player1Move = null;
                    try {
                        player1Move = (String) player1In.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Jogador 1 jogou: " + player1Move);

                    // Receber a jogada do jogador 2
                    String player2Move = null;
                    try {
                        player2Move = (String) player2In.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Jogador 2 jogou: " + player2Move);

                    // Determinar o vencedor
                    String result = determinarVencedor(player1Move, player2Move);

                    // Enviar o resultado para os jogadores
                    try {
                        player1Out.writeObject(result);
                        player1Out.flush();
                        player2Out.writeObject(result);
                        player2Out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Função para determinar o vencedor do jogo
    private static String determinarVencedor(String move1, String move2) {
        if (move1.equals(move2)) {
            return "Empate!";
        } else if ((move1.equals("Pedra") && move2.equals("Tesoura")) ||
                   (move1.equals("Papel") && move2.equals("Pedra")) ||
                   (move1.equals("Tesoura") && move2.equals("Papel"))) {
            return "Jogador 1 venceu!";
        } else {
            return "Jogador 2 venceu!";
        }
    }
}