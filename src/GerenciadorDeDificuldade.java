import javax.swing.*;
import java.awt.*;

public class GerenciadorDeDificuldade {
    public void executarDificuldade() {
        String[] dificuldades = {"Fácil", "Médio", "Difícil", "IMPOSSÍVEL"};

        int dificuldadeEscolhida = JOptionPane.showOptionDialog(null,
                "Selecione a dificuldade:",
                "Escolha a Dificuldade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                dificuldades,
                dificuldades[0]);

        if (dificuldadeEscolhida != JOptionPane.CLOSED_OPTION) {
            // Fecha a janela atual antes de iniciar uma nova dificuldade
            if (Minesweeper.currentFrame != null) {
                Minesweeper.currentFrame.dispose();
            }

            int tileSize = 70;
            int numRows = 8;
            int numCols = numRows;
            int mineCount = 10;

            switch (dificuldadeEscolhida) {
                case 0: // Fácil
                    tileSize = 70;
                    numRows = 8;
                    numCols = numRows;
                    mineCount = 10;
                    break;
                case 1: // Médio
                    tileSize = 50;
                    numRows = 12;
                    numCols = numRows;
                    mineCount = 20;
                    break;
                case 2: // Difícil
                    tileSize = 38;
                    numRows = 16;
                    numCols = numRows;
                    mineCount = 40;
                    break;
                case 3: // Impossível
                    tileSize = 30;
                    numRows = 19;
                    numCols = 26;
                    mineCount = 99;
                    break;
                default:
                    System.out.println("Nenhuma dificuldade selecionada. Saindo...");
            }

            // Agora instanciando a classe Minesweeper com os parâmetros necessários
            new Minesweeper(tileSize, numRows, numCols, mineCount);
        }
    }
}
