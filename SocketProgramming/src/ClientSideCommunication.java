import java.net.*;
import java.io.*;

public class ClientSideCommunication {
    public static void main(String[] args) {
        Client client = new Client();
        client.startLocalConnection(6000);
        client.sendMsgAndGetResponse("HELO");
        client.sendMsgAndGetResponse("BYE");
        client.stopConnection();
    }
}

class Client {
    private Socket clientSocket;
    private PrintWriter outCommunication;
    private BufferedReader inCommunication;
    
    public void startLocalConnection(int port) {
        try {
            clientSocket = new Socket("localhost",port);
            outCommunication = new PrintWriter(clientSocket.getOutputStream(), true);
            inCommunication = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connection Sucesfully Initiated");
        } catch (Exception e) {
            System.out.print("Some problem occured when connecting to server");
        }
    }

    public void sendMsgAndGetResponse(String msg) {
        outCommunication.println(msg);
        System.out.println("Sent from client: " + msg);
        try {
            String resp;
            resp = inCommunication.readLine();
            if (resp.isEmpty()){throw new NullPointerException();} 
            else {System.out.println("Recievesd From server: " + resp);}            
        } catch (NullPointerException e){
            System.out.print("Response was empty");
        } catch (Exception e) {
            System.out.println("Something went wrong when sending or recieving message");
        }
    }

    public void stopConnection() {
        try {
            inCommunication.close();
            outCommunication.close();
            clientSocket.close();
            System.out.println("Client ShutDown");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
