import java.net.*;
import java.io.*;

public class ClientSideCommunication {
    public static void main(String[] args) {
        String username = System.getProperty("user.name");
        Client client = new Client();
        client.startLocalConnection(51000);
        client.sendMsgAndGetResponse("HELO", "OK");
        client.sendMsgAndGetResponse("AUTH " + username,null);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Some problem occured when thread was sleeping");;
        }

        client.sendMsgAndGetResponse("QUIT","QUIT");
        client.stopConnection();
    }
}

class Client {
    private Socket clientSocket;
    private DataOutputStream outCommunication;
    private BufferedReader inCommunication;
    
    public void startLocalConnection(int port) {
        System.out.println("Starting to initiate connection...");
        try {
            clientSocket = new Socket("localhost",port);
            outCommunication = new DataOutputStream(clientSocket.getOutputStream());
            inCommunication = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connection Sucesfully Initiated");
        } catch (Exception e) {
            System.out.print("Some problem occured when connecting to server");
        }
    }

    public void sendMsgAndGetResponse(String msgStr, String expectedRespStr) {
        send(msgStr);       
        recieve(expectedRespStr);
    }

    private void recieve(String expectedRespStr) {
        try {
            String ActualResp = inCommunication.readLine().toString();
            String modifiedExpectedStr = expectedRespStr + " \n";

            if (expectedRespStr != null && (ActualResp.isEmpty() || ActualResp.equals(modifiedExpectedStr))){throw new Exception();} 
            System.out.println("Recievesd From server: " + ActualResp);              
        }
        catch (Exception e) {
            System.out.println("Something went wrong when sending or recieving message");
        }}

    private void send(String rowMsgStr) {
        String msgWthEndTag = rowMsgStr +"\n";
        byte [] stringAsByteArray = msgWthEndTag.getBytes();
        try{
            outCommunication.writeInt(stringAsByteArray.length);
            outCommunication.write(stringAsByteArray);
            outCommunication.flush();
            System.out.println("Sent from client in text: " + rowMsgStr);
            printBytes(stringAsByteArray);
        } catch(Exception e) {
            System.out.println("Communication with server did not succeed");
        }
    }

    private void printBytes(byte [] bytes){
        System.out.println("byte string format in Asci: ");
        for(byte b:bytes)
            System.out.print(b + ", ");
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
