import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PedraPapelTesouraClientGUI extends Application {

    // Constantes para o endereço IP e porta do servidor  
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    // Variáveis para a conexão com o servidor
    private Socket socket;
    private ObjectOutputStream serverOut;
    private ObjectInputStream serverIn;

    // Componentes da interface gráfica 
    private Label resultLabel;
    private Button pedraButton;
    private Button papelButton;
    private Button tesouraButton;

    public static void main(String[] args) {
        // Método de entrada da aplicação JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Estabelece a conexão com o servidor 
            socket = new Socket(SERVER_IP, SERVER_PORT);
            serverOut = new ObjectOutputStream(socket.getOutputStream());
            serverIn = new ObjectInputStream(socket.getInputStream());

            // Configurações da janela principal
            primaryStage.setTitle("Pedra, Papel e Tesoura");

           // Cria um painel de grade para organizar os componentes
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));

            // Adiciona um rótulo para indicar a escolha do jogador
            Label titleLabel = new Label("Escolha sua jogada:");
            grid.add(titleLabel, 0, 0, 2, 1);

            // Cria os botões para as opções de jogada
            pedraButton = new Button("Pedra");
            pedraButton.setOnAction(e -> enviarJogada("Pedra"));
            grid.add(pedraButton, 0, 1);

            papelButton = new Button("Papel");
            papelButton.setOnAction(e -> enviarJogada("Papel"));
            grid.add(papelButton, 1, 1);

            tesouraButton = new Button("Tesoura");
            tesouraButton.setOnAction(e -> enviarJogada("Tesoura"));
            grid.add(tesouraButton, 2, 1);

            // Botão para jogar novamente
            Button jogarNovamenteButton = new Button("Jogar Novamente");
            jogarNovamenteButton.setOnAction(e -> resetarJogo());
            grid.add(jogarNovamenteButton, 0, 2, 3, 1);

            // Rótulo para exibir o resultado
            resultLabel = new Label();
            grid.add(resultLabel, 0, 3, 3, 1);

            // Botão para sair do jogo
            Button sairButton = new Button("Sair");
            sairButton.setOnAction(e -> sair());
            grid.add(sairButton, 0, 4, 3, 1);

           // Cria uma cena com o painel de grade e define seu tamanho
            Scene scene = new Scene(grid, 300, 250);
            primaryStage.setScene(scene);

            // Configura o evento de fechar a janela para sair do jogo
            primaryStage.setOnCloseRequest(e -> sair());

            // Exibe a janela principal
            primaryStage.show();

            // Thread para receber o resultado do servidor
            Thread receiverThread = new Thread(this::receberResultado);
            receiverThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Envia a jogada escolhida pelo jogador para o servidor
    private void enviarJogada(String jogada) {
        pedraButton.setDisable(true);
        papelButton.setDisable(true);
        tesouraButton.setDisable(true);

        try {
            serverOut.writeObject(jogada);
            serverOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Recebe o resultado do servidor e atualiza a interface gráfica
    private void receberResultado() {
        try {
            while (true) {
                String resultado = (String) serverIn.readObject();
                Platform.runLater(() -> {
                    resultLabel.setText("Resultado: " + resultado);
                    habilitarBotoes();
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    // Reseta o jogo, removendo o resultado e habilitando os botões de jogada
    private void resetarJogo() {
        resultLabel.setText("");
        habilitarBotoes();
    }
    // Habilita os botões de jogada
    private void habilitarBotoes() {
        pedraButton.setDisable(false);
        papelButton.setDisable(false);
        tesouraButton.setDisable(false);
    }
    // Encerra a conexão com o servidor e finaliza a aplicação
    private void sair() {
        try {
            serverOut.close();
            serverIn.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.exit();
        System.exit(0);
    }
}