package utilities;
import java.util.LinkedList;

public class ServerFinder {
    /**This class represents a collections of reusable functions that are helpful for implimentation of algorithms and in particular this class makes use of low level communication functions in order to get us some higher level information about servers */

    public static Server getFirstAvailable(Job job, CommunicationHandler communicator) throws IndexOutOfBoundsException {

        /**
         * Accepts a valid job and returns a server that is available for execution of given job immidiately.
         */
        String msgToServer = "GETS Avail " + job.getJobRequirementsStringForGETS();
        LinkedList<Server> srvrLst = communicator.getServersFromGETS(msgToServer);
        Server server = null;
        
        server = srvrLst.get(0);
        return server;
    }

    public static Server getShortestQueueServer(Job job,CommunicationHandler communicator) {
        /**
         * Accepts a valid job and returns a server that is capable of executing that job eventually and that has the shortest queue of jobs
         */

        Server shortestQueueServer = null;
        Integer shortestCurrentQueueTime = null;
        
        String msgToServer = "GETS Capable " + job.getJobRequirementsStringForGETS();
        LinkedList<Server> srvrLst;
        
        srvrLst = communicator.getServersFromGETS(msgToServer);
        assert !srvrLst.isEmpty();

        for (Server server : srvrLst){
            server.estimatedWaitTimeOfQueuedJobs = communicator.getEstimatedTimeLengthOfQuedJobs(server);
            try{
                if (server.estimatedWaitTimeOfQueuedJobs<shortestCurrentQueueTime){
                    shortestQueueServer = server;
                }
            } catch(Exception e){
                shortestQueueServer = server;
            }
        }

        assert shortestQueueServer != null; //post-condition
        return shortestQueueServer;
    }

    public static Server getFirstCapableWithQueries(Job job, CommunicationHandler communicator){
        /**
         * gets a valid job and outputs the first server that is capable to do that job
         */
        assert job != null; //pre-condition test

        Server server = null;
        
        String msgToServer = "GETS Capable " + job.coreReq + " " + job.ramReqMb + " " + job.DiskReqMb;
        LinkedList<Server> srvrLst = communicator.getServersFromGETS(msgToServer);

        assert srvrLst.size() >1; //post-condition test
        server = srvrLst.get(0);
        
        return server;
    }

    public static Server getFirstCapableInMemory(Job job, LinkedList<Server> srvrListAll){
        /**
         * gets a valid job and outputs the first server that is capable to do that job. Will always return a server because ds-sim is designed in such way that at least 1 server will be capable of executing all jobs
         */
        assert job != null; //pre-condition test
        assert !srvrListAll.isEmpty(); //pre-condition test
        
        for (Server server :srvrListAll){
            if(server.isCapable(job)){
                return server;
            }
        }

        return null;
    }

    public static ServerType getServerWithMostCoresInfo(CommunicationHandler communicator) {
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
