import java.util.ArrayList;
import java.util.LinkedList;

import utilities.CommunicationHandler;
import utilities.Job;
import utilities.Server;
import utilities.ServerType;

public class LRR_alg implements SchedulingStrategy{

    CommunicationHandler communicator;

    public void setCommunicator(CommunicationHandler inCommunicator){
        communicator = inCommunicator;
    }

    public void schedule() {
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
            communicator.send("REDY");
            response = communicator.recieve(null);
            
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
                Job job = Job.parseJobFromREDY(response);
                System.out.println("here");
                communicator.scheduleJob(job, largestServerName, nextInLineServerId);
                nextInLineServerId = (nextInLineServerId + 1) % largestServerCount;
            }
        }
    }

    private ServerType getServerWithMostCoresInfo() {
        /**
         * This method goes through the list of servers, finds the first biggest one in respect to CPU and then returns some info about that server
         */
        int maxCores = 0;
        String name = "";
        int serverInstanceCount = 0;

        LinkedList<Server> srvrLst = communicator.getServersFromGETS("GETS All");

        for (Server server : srvrLst) {
            if (server.cpuCores > maxCores) {
                maxCores = server.cpuCores;
                name = server.name;
                serverInstanceCount = 0;
            }
            if (server.name.equals(name)) {
                serverInstanceCount = serverInstanceCount + 1;
            }
        }

        ServerType type = new ServerType(name, serverInstanceCount);

        assert type != null; //post-condition check
        return type; 
    }
}