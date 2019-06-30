package org.academiadecodigo.bootcamp;

import java.io.*;
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
    //private String message = "";
    private BufferedReader bReader;
    private Socket clientSocket;
    BufferedReader in;

    public Client() {

        bReader = new BufferedReader(new InputStreamReader(System.in));

    }

    public static void main(String[] args) {

        Client client = new Client();
        client.connect();

    }

    public void connect() {


        try {
            System.out.println("Which host?");
            host = bReader.readLine();
            InetAddress ia = InetAddress.getByName(host);
            clientSocket = new Socket(InetAddress.getLocalHost(), portNum);
            Thread thread = new Thread(new SendManager());
            thread.start();
            readMessage();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void readMessage () {


        try {


            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String messageReceived = "";

            while (!messageReceived.equals("null")) {

                messageReceived = in.readLine();
                System.out.println(messageReceived);

            }
            in.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    private class SendManager implements Runnable {

        @Override
        public void run() {

        try {


            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Write a message");

            String messageToSend = "";

            while (!clientSocket.isClosed()) {


                try {

                    messageToSend = in.readLine();


                } catch (IOException e) {

                    e.printStackTrace();
                }

               /* if (messageToSend == null || messageToSend.equals("quit")) {

                    break;
                }

                */

                out.write(messageToSend);
                out.newLine();
                out.flush();

            }

            if(messageToSend.equals("quit")) {


                try {

                    in.close();
                    out.close();
                    clientSocket.close();

            } catch (IOException e) {

                    System.out.println("Error closing socket");
                }
}
        } catch (IOException e) {

            e.printStackTrace();
        }

        }

    }
}

