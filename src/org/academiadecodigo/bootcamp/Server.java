package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 29/06/2019.
 */
public class Server {

    private ServerSocket bindSocket;
    private List<ServerWorker> workers = Collections.synchronizedList(new ArrayList<ServerWorker>());
    private int workerNum = 0;


    public static void main(String[] args) {



            Server server = new Server();
            server.listen(8382);

    }

    private void replyAll(String message) {

        PrintWriter out;

        synchronized (workers) {

            Iterator<ServerWorker> it = workers.iterator();

            while (it.hasNext()) {

                try {

                    out = new PrintWriter(it.next().getClientSocket().getOutputStream());
                    out.print(message + "\n");
                    out.flush();

                } catch (IOException e) {

                    e.printStackTrace();
                }
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

                String name = "Client " + workerNum;
                ServerWorker serverWorker = new ServerWorker(clientSocket, name);

                workerNum++;


                workers.add(serverWorker);

                Thread thread = new Thread(serverWorker, name);
                thread.setName(name);
                thread.start();


            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private class ServerWorker implements Runnable {

        private Socket clientSocket;
        private String messageReceived = "";
        private BufferedReader in;
        private String name;


        public ServerWorker(Socket clientSocket, String name) {

            this.clientSocket = clientSocket;
            this.name = name;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public void setName(String name) {

            Thread.currentThread().setName(name);
        }

        public void receiveMessage() {


            try {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (!clientSocket.isClosed()) {

                messageReceived = in.readLine();
                System.out.println(Thread.currentThread().getName() + ": " + messageReceived);

                if(messageReceived == null || messageReceived.equals("/quit")) {

                    in.close();
                    clientSocket.close();
                    continue;

                } else if (messageReceived.equals("/alias")) {

                    name = in.readLine();

                    System.out.println(name);

                    setName(name);
                    continue;


                } else {

                    broadCast(Thread.currentThread().getName() + ": " + messageReceived);
                }

            }
            workers.remove(this);

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
