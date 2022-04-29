package utilities;
import javax.management.InvalidAttributeValueException;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class CommunicationHandler {
    /**
     * This class facilitates the communication between client and server. It doesnt impliment any algorithms but instead it takes care of low level functions that are related to communication between client and server
     */

    private Socket clientSocket;
    private BufferedReader in;
    private BufferedOutputStream out;
    private InetAddress ip;
    private int port;

    public void start(String inIP, String inPort) {
        /**
         * This method opens up a socket, then sets up input and output channels and
         * does the handshake
         */

        // parse ip and port
        try {
            ip = InetAddress.getByName(inIP);
        } catch (UnknownHostException e) {
            quit("Invalid parameter ip address", true);
        }

        port = Integer.parseInt(inPort);

        // create socket & set up in and out communications
        try {
            clientSocket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
        } catch (Exception e) {
            quit("There was a problem when creating a socket", true);
        }

        // handshake
        send("HELO");
        recieve("OK");

        send("AUTH " + System.getProperty("user.name"));
        recieve(null);

    }

    public void quit(String quitMsg, boolean connectionProblemOccured) {
        /**
         * this method exchanges quit messages with server, closes the socket to
         * stop program communication and closes client’s communication channels.
         */

        if (quitMsg != null) {
            System.out.println("Stopping the connection with message: " + quitMsg);
        }
        try {
            if(!connectionProblemOccured){
                //if connection problem exists, there is no point trying to send those messages.
                send("QUIT");
                recieve("QUIT");
            }

            in.close();
            out.close();
            clientSocket.close();

            System.out.println("Client ShutDown");
        } catch (IOException e) {
            System.out.print("Problem occured when stopping connection");
        } finally {
            System.exit(1);
        }
    }

    public void scheduleJob(Job job, String serverType, int serverId) {
        /**
         * this method gets a job string and sends a message to the server to
         * schedule the job’s completion as according to parameters provided.
         */
        send("SCHD " + job.id + " " + serverType + " " + serverId);
        recieve(null);
    }

    public ArrayList<Server> getServersFromGETS(String command) {
        /**
         * This method is designed to accomodate methods like GETS All and GETS Capable where the first response is the number of things that will later be sent and the second thing are those servers. The purpose of this method is to allow for the code to be more decoupled and cleaner. This method first asks for the number of records that will be sent back as a result of the input command and then it goes through those lines of response and parses them. Due to how our assignmed is structured we are confident that there will never be a job which can not be done by at least 1 server
         */

        String msg;
        ArrayList<Server>list = new ArrayList<Server>();

        send(command); // command that we send to get a response back
        String dataResponse = recieve(null); //reciveving some info about how big the response list will be
        String numServersStr = dataResponse.split("\\s+")[1]; 
        int numServersInt = Integer.parseInt(numServersStr);

        send("OK"); //acknowledging that we go the data due to requirement of protocol
        for (int i = 0; i < numServersInt; i++) {
            msg = recieve(null);
            Server server = Server.parseServerInfoFromGETSALL(msg);
            list.add(server);
        }
        send("OK");
        recieve(null);

        return list;
    }

    public String recieve(String expectedRespStr) {
        /**
         * takes in a single line of in-flowing communication to the client and checks
         * whether we are expecting any input. If we are not expecting anything
         * particular, then we just return the in-flowing line of communication. On the
         * other hand, if there is some expectation of what we should receive, then we
         * check the actual received message against that expectation and if they don’t
         * match then we print exception to the screen and quit() the process while if
         * the expectation is satisfied, then the received string gets returned by this
         * method
         */
        String resp = "";

        try {
            resp = in.readLine();
            if (expectedRespStr != null && !resp.equals(expectedRespStr)) {
                throw new InvalidAttributeValueException("When recieving message we expected '" + expectedRespStr
                        + "' but got '" + resp + "'");
            }
        } catch (InvalidAttributeValueException e) {
            quit(e.getMessage(), false);
        } catch (Exception e){
            quit(e.getMessage(), true);
        }
        System.out.println("Recieved From server: " + resp);
        return resp;
    }

    public void send(String rowMsgStr) {
        /**
         * This method appends the end of line symbol to the string we are intending to
         * send, then we break the string down into an array of bytes because that is
         * what ds-server is expecting. Then we write to the outgoing communication
         * buffer which is stored locally followed by flashing of that the buffer to the
         * ds-server.
         */
        String msgWthEndTag = rowMsgStr + '\n';
        byte[] strByteArr = msgWthEndTag.getBytes();
        try {
            out.write(strByteArr);
            out.flush();
            System.out.println("Sent from client in text: " + rowMsgStr);
        } catch (Exception e) {
            quit("Something went wrong when sending message", true);
        }
    }
}
