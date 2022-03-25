import java.net.*;
import java.util.Optional;
import java.io.*;

public class ExampleServerEntrypoint {
    public static void main(String[] args) {
        Server_example server= new Server_example();
        server.launch(6000);
    }
}

class Server_example {
    private ServerSocket serverSocket;   // the communication chanel which our own server opened up for clients to contact us.
    private Socket clientSocket;  // one end of communication channel connecting server to client which initiated communication with us
    private PrintWriter outCommunication; // An object which formats everything we want to send into simple non formatted text and sends it.
    private BufferedReader inCommunication; // An object which allocates some memory dynamically and captures incoming communications in an effecient manner.

    public void launch(int port) {
        setUpCommunication(port);
        ExchangeMessages();
        shutdown();
    }

    private void ExchangeMessages() {
        while (true) {
            // actual messaging here
            Optional<String> msg = Optional.empty();
            String response;
            Boolean toBreakCurrIteration = false;
            
            try {
                msg = Optional.of(inCommunication.readLine());
                System.out.println("Recieved from client: "+ msg.get());
            } catch (IOException e) {
                System.out.print("Problem occured when recieving the message");
            }

            //decide on communication
            if (msg.get().equals("HELO")) {
                response = "G'DAY";
            } else if (msg.get().equals("BYE")) {
                response = "G'BYE";
                toBreakCurrIteration = true;
            } else {
                response = "Unrecognised Command";
            }

            //conduct communication
            outCommunication.println(response);
            System.out.println("Sent to client: "+ response);
            if(toBreakCurrIteration){
                break;
            }
            
        }
    }

    private void setUpCommunication(int port) {
        try {
            // setting up the system
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            clientSocket = serverSocket.accept(); // wait until there is an incoming connection and once there is accept it and keep going
            System.out.println("Connection with client created");

            outCommunication = new PrintWriter(clientSocket.getOutputStream(), true);
            inCommunication = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void shutdown() {
        try{
            inCommunication.close(); // shutting down objects used for communication
            outCommunication.close(); // shutting down objects used for communication
            clientSocket.close(); //shutting down the connection with the client
            serverSocket.close(); // disallowing further connections to our servers from outside
            System.out.println("Server Shut Down");
        } catch (Exception e){
            System.out.print(e);
        }
        
    }
}