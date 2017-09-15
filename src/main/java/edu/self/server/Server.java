package edu.self.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {

    // we always return the same HTTP response
    private static final String RESPONSE_CONTENT = "Hello World!";
    private static final String HTTP_RESPONSE = "HTTP/1.0 200 OK\nContent-Type: text/plain\nContent-Length: " + RESPONSE_CONTENT.length() + "\n\n" + RESPONSE_CONTENT;
    private static final ByteBuffer HTTP_RESPONSE_BUFFER = ByteBuffer.wrap(HTTP_RESPONSE.getBytes());

    public static void main(String args[]) throws Exception {

        if (args.length != 3 && args.length != 4) {
            System.err.println("Arguments must be: [IP version] [IP address] [port], e.g. 6 ::1 8080.");
            System.err.println("Use a fourth argument to use ServerSocketChannel instead of ServerSocket.");
            System.exit(-1);
        }

        // get IP address
        InetAddress ip;
        if (args[0].equals("4")) {
            ip = InetAddress.getByName(args[1]);
        }
        else {
            System.setProperty("java.net.preferIPv4Stack", "false");
            System.setProperty("java.net.preferIPv6Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "true");
            ip = Inet6Address.getByName(args[1]);
        }

        // get port number
        int port = Integer.parseInt(args[2]);
        System.out.println("Listening to " + ip.toString() + " on port " + port);

        if (args.length == 3) {

            try (ServerSocket serverSocket = new ServerSocket(port, -1, ip)) {

                while (true) {

                    try (Socket socket = serverSocket.accept()) {

                        // read request
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;

                        System.out.println("\n---");
                        while ((line = in.readLine()) != null && !line.isEmpty()) {
                            System.out.println(line);
                        }

                        // write response
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeBytes(HTTP_RESPONSE);
                    }
                }
            }
        }
        else {
            try (ServerSocketChannel channel = ServerSocketChannel.open().bind(new InetSocketAddress(ip, port))) {

                while (true) {

                    try (SocketChannel socket = channel.accept()) {

                        // read request
                        ByteBuffer requestBuffer = ByteBuffer.allocate(1000);
                        StringBuilder request = new StringBuilder();

                        do {
                            requestBuffer.clear();
                            socket.read(requestBuffer);
                            request.append(new String(requestBuffer.array()).trim());
                        }
                        while (requestBuffer.remaining() == 0);

                        System.out.println("\n--- length: " + request.length());
                        System.out.println(request.toString());

                        // write response
                        HTTP_RESPONSE_BUFFER.rewind();
                        socket.write(HTTP_RESPONSE_BUFFER);
                    }
                }
            }
        }
    }
}
