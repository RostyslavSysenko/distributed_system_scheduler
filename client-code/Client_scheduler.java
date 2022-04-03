import java.net.*;

import javax.management.InvalidAttributeValueException;

import java.io.*;

class Client_scheduler {
    /**
     * this class handles the communication between itself and the ds-server which
     * are implemented through start(), quit(), send(), receive() methods. In
     * addition, this class executes the LRR algorithm by calling lrr_scheduling()
     * which is supported by methods scheduleJob() and getServerWithMostCoresInfo().
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

    public void lrr_scheduling() {
        /**
         * This method keeps iterating through a loop which sends the ds-server a ”REDY”
         * message and receives a response string. Then, if the information about
         * largest server such as name and number of its instances has not yet been
         * learnt, the a call is made to getServerWithMostCoresInfo() to to get that
         * information. Then, when method has access to both response from ”REDY” and
         * information about largest server, the response string is checked to
         * understand whether response represents a new job, end of all jobs or
         * something else. Based on the finding, if end of all jobs has not been
         * reached, the job either gets scheduled by scheduleJob() or the response is
         * ignored, but either way the loop continues. On the other hand, if no more
         * jobs are available then loop breaks and the scheduling algorithm stops. After
         * each job is scheduled, the id of next server gets incremented by 1 and the
         * modulo operation ensures that when the counter tips to a value which is too
         * big that the counter will cycle back to 0 and start again.
         */
        String response;
        ServerType largerServerInfo = null;
        String largestServerName = null;
        int largestServerCount = 0;
        int nextInLineServerId = 0;

        while (true) {
            send("REDY");
            response = recieve(null);

            // get largest server info if we dont know it yet
            if (largerServerInfo == null) {
                largerServerInfo = getServerWithMostCoresInfo();
                largestServerName = largerServerInfo.name;
                largestServerCount = largerServerInfo.availableInstances;
            }

            // Do the scheduling
            if (Job.noMoreJobsAvail(response)) {
                break;
            } else if (Job.regularJobForScheduling(response)) {
                // schedule to the next available largest server in a line
                scheduleJob(response, largestServerName, nextInLineServerId);
                nextInLineServerId = (nextInLineServerId + 1) % largestServerCount;
            }
        }
    }

    private void scheduleJob(String jobStr, String largestServerType, int largestServerId) {
        /**
         * this method parses the job string and sends a message to the server to
         * schedule the job’s completion as according to parameters provided.
         */
        Job job = Job.parseJobFromREDY(jobStr);
        send("SCHD " + job.id + " " + largestServerType + " " + largestServerId);
        recieve(null);
    }

    private ServerType getServerWithMostCoresInfo() {
        /**
         * This method attempts to do 2 things in a single run to reduce the amount of
         * back and forward messaging between applications and thus reduce latency. This
         * methods sends ”GETS All” and the goes through the response it receives from
         * the server line by line and looks for the first server with the highest
         * number of cores. The first instance of such server gets recorded and the only
         * time this would be overwritten is if there is a more powerful server
         * available. Then another if statement counts the number of instances of this
         * server as we go on with our iteration by verifying the name first. To ensure
         * accurate data, the number of instances of server gets updated to 0 every time
         * the method encounters a new largest server. Through this loop and 2 if
         * statements mechanism, the methodis guaranteed to get an accurate name of
         * largest server and its corresponding number of instances. Then, an ”OK”
         * acknowledgment is sent to the ds-server, a new Object is created to capture
         * the number of instance and name of largest server and then that object gets
         * returned.
         */
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
            if (serverModel.name.equals(name)) {
                serverInstanceCount = serverInstanceCount + 1;
            }
        }
        send("OK");
        recieve(null);

        ServerType type = new ServerType(name, serverInstanceCount);
        return type;
    }

    private String recieve(String expectedRespStr) {
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

    private void send(String rowMsgStr) {
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