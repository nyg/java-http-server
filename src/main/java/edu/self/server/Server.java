package edu.self.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String args[]) throws Exception {

	if (args.length != 3) {
            System.err.println("Three arguments needed, e.g. [IP version] [IP address] [port]");
            System.exit(-1);
        }

        InetAddress ip;
        
        if (args[0].equals("4")) {
            System.setProperty("java.net.preferIPv4Stack", "true"); // optional
            ip = InetAddress.getByName(args[1]);
        }
        else {
            System.setProperty("java.net.preferIPv4Stack", "false");
            ip = Inet6Address.getByName(args[1]);
        }

	System.out.println("IP: " + ip.toString());

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[2]), -1, ip)) {

            String line;
            String content = "Hello World!";
            String response = "HTTP/1.0 200 OK\nContent-Type: text/plain\nContent-Length: " + content.length() + "\n\n" + content;

            while (true) {

                Socket socket = serverSocket.accept();

                // read request - optional
                System.out.println("--");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                }

                // always return HTTP response
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeBytes(response);
            }
        }
    }
}
