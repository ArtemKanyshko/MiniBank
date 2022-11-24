import exeption.ExitAppExeption;
import logger.*;
import ui.ConsoleInterface;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleInterface ui = new ConsoleInterface(new Scanner(System.in));
        try {
            ui.loginLoop();
            ui.mainLoop();
        } catch (ExitAppExeption e) {
            System.out.println("Exit");        }
    }
}
