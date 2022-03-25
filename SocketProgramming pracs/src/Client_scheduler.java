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
            System.out.println("No such Ip address exist");
            ;
            stopConnectionAndQuit();
        }

        port = Integer.parseInt(inport);

        // create socket (adress with which we are communicating) & set up in and out
        // communications
        try {
            clientSocket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
        } catch (Exception e) {
            System.out.print("There was a problem when creating a socket");
            stopConnectionAndQuit();
        }

        // handshake
        sendAndRecieve("HELO", "OK");
        sendAndRecieve("AUTH " + System.getProperty("user.name"), null);
        sendAndRecieve("REDY", null);
    }

    public void runAllToSingleLargestSchedulingAlgorithm() {
        String largestServerType = "";
        int serverID = 0;

        largestServerType = getServerNameWithMostCores();
        allocateJobsToSingleLargestServer(largestServerType, serverID);
    }

    private String getServerNameWithMostCores() {
        String msg;
        int maxCores = 0;
        String name = "";

        String dataResponse = sendAndRecieve("GETS All", null);
        String numServersStr = dataResponse.split("\\s+")[1];
        int numberOfServers = Integer.parseInt(numServersStr);

        send("OK");
        for (int i = 0; i < numberOfServers; i++) {
            msg = recieve(null);
            ServerDataModel serverModel = ServerDataModel.parseServerInfoFromGETSALL(msg);

            if (serverModel.cpuCores > maxCores) {
                maxCores = serverModel.cpuCores;
                name = serverModel.name;
            }
            System.out.print("Point 1.1");
        }
        System.out.print("Point 1.2");
        sendAndRecieve("OK", null);
        return name;
    }

    private void allocateJobsToSingleLargestServer(String largestServerType, int serverID) {
        String jobStr;
        boolean noMoreJobsAvail = false;
        boolean regularJobForScheduling = false;

        while (true) {
            jobStr = sendAndRecieve("REDY", null);
            noMoreJobsAvail = Job.noMoreJobsAvail(jobStr);
            regularJobForScheduling = Job.regularJobForScheduling(jobStr);

            if (noMoreJobsAvail) {
                break;
            } else if (regularJobForScheduling) {
                // schedule job when it is available
                Job job = Job.parseJobFromREDY(jobStr);
                sendAndRecieve("SCHD " + job.id + " " + largestServerType + " " + serverID, null);
            } else {
                // some job yet to be comepleted and response was not a regular JOBN. for now we
                // ignore it.
            }
        }
    }

    public void quit() {
        this.sendAndRecieve("QUIT", "QUIT");
        this.stopConnectionAndQuit();
    }

    private String sendAndRecieve(String msgStr, String expectedRespStr) {
        // parameter 1: what we are sending
        // parameter 2: what we are expecting to recieve. Inapropriate response will
        // cause the program to shut down. If this is set to null, then any response is
        // accepted
        send(msgStr);
        return recieve(expectedRespStr);
    }

    public String recieve(String expectedRespStr) {
        String resp = "";

        // if we are expecting a resposne

        try {
            resp = in.readLine();
            if (expectedRespStr != null && !resp.equals(expectedRespStr)) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong when recieving message | expected: '" + expectedRespStr
                    + "' | actual: '" + resp + "'");
            stopConnectionAndQuit();
        }
        System.out.println("Recieved From server: " + resp);
        return resp;
    }

    public void send(String rowMsgStr) {
        String msgWthEndTag = rowMsgStr + '\n';
        byte[] strByteArr = msgWthEndTag.getBytes();
        try {
            out.write(strByteArr);
            out.flush();
            System.out.println("Sent from client in text: " + rowMsgStr);
        } catch (Exception e) {
            System.out.println("Something went wrong when sending message");
            stopConnectionAndQuit();
        }
    }

    private void stopConnectionAndQuit() {
        try {
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
}