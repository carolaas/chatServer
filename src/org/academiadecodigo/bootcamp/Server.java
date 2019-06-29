package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 29/06/2019.
 */
public class Server {

    private ServerSocket bindSocket;
    private ExecutorService cachedPool;
    private ArrayList<Socket> arrayList;

    public Server() {

        cachedPool = Executors.newCachedThreadPool();
        arrayList = new ArrayList<>();

    }

    public static void main(String[] args) {



            Server server = new Server();
            server.listen(8382);

    }

    private void replyAll(String message) {

        PrintWriter out;

        for (int i = 0; i < arrayList.size() ; i++) {

            try {

                out = new PrintWriter(arrayList.get(i).getOutputStream());
                out.print("Client " + i + " " + message + "\n");
                out.flush();

            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    private void listen(int portNum) {

        try {

            bindSocket = new ServerSocket(portNum);
            serve(bindSocket);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    private void serve(ServerSocket bindSocket) {

        while(true) {

            try {

                Socket clientSocket = bindSocket.accept(); // accept method returns a Socket

                cachedPool.submit(new ClientHandler(clientSocket));

                arrayList.add(clientSocket);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;
        private String messageReceived = "";
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {

            this.clientSocket = clientSocket;
        }

        public void receiveMessage() {


            try {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (!messageReceived.equals("quit")) {

                messageReceived = in.readLine();
                System.out.println(messageReceived);
                broadCast(messageReceived);

            }
            clientSocket.close();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        public void broadCast(String messageReceived)  {


            replyAll(messageReceived);
        }

        @Override
        public void run() {
            //receive message and broadcast message are methods to implement here

            receiveMessage();
        }
    }

}
