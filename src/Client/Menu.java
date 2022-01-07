package Client;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private Scanner scan;
    private List<String> opcoes;
    private int op;

    public Menu(String[] opcoes) {
        this.scan = new Scanner(System.in);
        this.opcoes = Arrays.asList(opcoes);
        this.op = -1;
    }

    public void execute() {
        showMenu();
        this.op = lerOpcao();
    }

    private void showMenu() {
        System.out.println("\n <<<||| TAP - Portugal |||>>>\n");
        for (int i = 0; i < this.opcoes.size(); i++)
            System.out.println(i + 1 + " - " + this.opcoes.get(i));
        System.out.println("0 - Sair");
    }

    private int lerOpcao() {
        int op;

        System.out.print("\nOpção: ");
        try {
            op = Integer.parseInt(this.scan.nextLine());
            if (op < 0 || op > this.opcoes.size()) {
                System.out.println("Opção Inválida!");
                op = -1;
            }
        } catch (NumberFormatException e) {
            op = -1;
            System.out.println("Opção Inválida!");
        }
        return op;
    }

    public int getOpcao() {
        return this.op;
    }
}
