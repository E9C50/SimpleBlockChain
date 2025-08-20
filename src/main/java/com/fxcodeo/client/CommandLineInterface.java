package com.fxcodeo.client;

import java.util.Scanner;

public class CommandLineInterface {
    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            switch (command) {
                case "/help":
                    printHelp();
                    break;
                case "/balance":
                    break;
                case "/tx":
                    break;
            }
        }
    }

    public void printHelp() {
        System.out.println("Commands:");
        System.out.println("/help 帮助");
        System.out.println("/balance 查看余额");
        System.out.println("/tx 转账");
    }
}
