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

    public void lrr_scheduling() {
        String response;
        ServerType largerServerInfo = null;
        String largestServerName = null;
        int largestServerCount = 0;
        int nextInLineServerId = 0;

        while (true) {
            send("REDY");
            response = recieve(null);

            //get largest server info if we dont know it yet
            if(largerServerInfo == null ){
                largerServerInfo = getServerWithMostCoresInfo();
                largestServerName = largerServerInfo.name;
                largestServerCount = largerServerInfo.availableInstances;
            }

            //Do the scheduling
            if (Job.noMoreJobsAvail(response)) {
                break;
            } else if (Job.regularJobForScheduling(response)) {
                //schedule to the next available largest server in a line
                scheduleJob(response, largestServerName, nextInLineServerId);
                nextInLineServerId = (nextInLineServerId+1) % largestServerCount;
            }
        }
    }

    private void scheduleJob(String jobStr, String largestServerType, int largestServerId) {
        Job job = Job.parseJobFromREDY(jobStr);
        send("SCHD " + job.id + " " + largestServerType + " " + largestServerId);
        recieve(null);
    }

    private ServerType getServerWithMostCoresInfo() {
        String msg;
        int maxCores = 0;
        String name = "";
        int serverInstanceCount = 0;

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
                serverInstanceCount = 0;
            }
            if (serverModel.name.equals(name)){
                serverInstanceCount =serverInstanceCount+ 1;
            }
        }
        send("OK");
        recieve(null);

        ServerType type = new ServerType(name, serverInstanceCount);
        return type;
    }

    private String recieve(String expectedRespStr) {
        String resp = "";

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