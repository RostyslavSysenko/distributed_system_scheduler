import java.net.*;
import java.io.*;

class Client_scheduler {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedOutputStream out;

    private InetAddress ip;
    private int port;

    public void start(String inip, String inport) {
        // parse ip and port
        try {
            ip = InetAddress.getByName(inip);
        } catch (UnknownHostException e) {
            quit("Invalid parameter ip address exist");
        }

        port = Integer.parseInt(inport);

        // create socket & set up in and out communications
        try {
            clientSocket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
        } catch (Exception e) {
            quit("There was a problem when creating a socket");
        }

        // handshake
        send("HELO");
        recieve("OK");

        send("AUTH " + System.getProperty("user.name"));
        recieve(null);

        send("REDY");
        recieve(null);
    }

    public void quit(String quitMsg) {

        if (quitMsg != null){
            System.out.println("Stopping the connection with message: " + quitMsg);
        }
        try {
            send("QUIT");
            recieve("QUIT");

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

    public void atl_scheduling() {
        String jobStr;
        boolean noMoreJobsAvail = false;
        boolean regularJobForScheduling = false;
        String largestServerType = getServerNameWithMostCores();

        while (true) {
            send("REDY");
            jobStr = recieve(null);
            noMoreJobsAvail = Job.noMoreJobsAvail(jobStr);
            regularJobForScheduling = Job.regularJobForScheduling(jobStr);

            if (noMoreJobsAvail) {
                break;
            } else if (regularJobForScheduling) {
                // schedule job when it is available
                Job job = Job.parseJobFromREDY(jobStr);
                send("SCHD " + job.id + " " + largestServerType + " " + 0);
                recieve(null);
            } else {
                // some job yet to be comepleted and response was not a regular JOBN.
            }
        }
    }

    private String getServerNameWithMostCores() {
        String msg;
        int maxCores = 0;
        String name = "";

        send("GETS All");
        String dataResponse = recieve(null);
        String numServersStr = dataResponse.split("\\s+")[1];
        int numberOfServers = Integer.parseInt(numServersStr);

        send("OK");
        for (int i = 0; i < numberOfServers; i++) {
            msg = recieve(null);
            Server serverModel = Server.parseServerInfoFromGETSALL(msg);

            if (serverModel.cpuCores > maxCores) {
                maxCores = serverModel.cpuCores;
                name = serverModel.name;
            }
        }
        send("OK");
        recieve(null);
        return name;
    }

    private String recieve(String expectedRespStr) {
        String resp = "";

        // if we are expecting a resposne

        try {
            resp = in.readLine();
            if (expectedRespStr != null && !resp.equals(expectedRespStr)) {
                throw new Exception("When recieving message we expected '" + expectedRespStr
                + "' but got '" + resp + "'");
            }
        } catch (Exception e) {
            quit(e.getMessage());
        }
        System.out.println("Recieved From server: " + resp);
        return resp;
    }

    private void send(String rowMsgStr) {
        String msgWthEndTag = rowMsgStr + '\n';
        byte[] strByteArr = msgWthEndTag.getBytes();
        try {
            out.write(strByteArr);
            out.flush();
            System.out.println("Sent from client in text: " + rowMsgStr);
        } catch (Exception e) {
            quit("Something went wrong when sending message");
        }
    }
}