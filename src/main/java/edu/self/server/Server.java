import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String args[]) throws Exception {

	InetAddress ip = Inet6Address.getByName(args[0]);
	System.out.println("Wanted IPv6:" + ip.toString());

        try (ServerSocket serverSocket = new ServerSocket(8080, -1, ip)) {

            String line;
            String content = "Hello World!";
            String response = "HTTP/1.0 200 OK\nContent-Type: text/plain\nContent-Length: " + content.length() + "\n\n" + content;

            while (true) {

                Socket socket = serverSocket.accept();

                // read request - optional
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