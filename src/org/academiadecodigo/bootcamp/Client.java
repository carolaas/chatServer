package org.academiadecodigo.bootcamp;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 29/06/2019.
 */
public class Client {

    private String host = "";
    private int portNum = 8382;
    private String message = "";
    private BufferedReader bReader;
    private Socket clientSocket;
    private ExecutorService singleExecutor;

    public Client() {

        bReader = new BufferedReader(new InputStreamReader(System.in));
        singleExecutor = Executors.newSingleThreadExecutor();
    }

    public static void main(String[] args) {

        Client client = new Client();
        client.connect();

    }

    public void connect() {

        try {
            System.out.println("Wich host?");
            host = bReader.readLine();
            InetAddress ia = InetAddress.getByName(host);
            clientSocket = new Socket(InetAddress.getLocalHost(), portNum);
            singleExecutor.submit(new ReadWrite());
            System.out.println("Write a message");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private class ReadWrite implements Runnable {

        BufferedReader in;
        PrintWriter out;

        private void readMessage () {

            try {

                System.out.println("here");

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                while (!message.equals("null")) {

                    message = in.readLine();
                    System.out.println(message);

                }
                in.close();

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    private void sendMessage() {

            try {


            while (true) {


                message = bReader.readLine();
                System.out.println("send");
                System.out.println(message);
                out.print(message);
                out.flush();
            }


            } catch (IOException e) {

                e.printStackTrace();
            }

    }

        @Override
        public void run() {

            sendMessage();
            readMessage();

        }
    }
}
