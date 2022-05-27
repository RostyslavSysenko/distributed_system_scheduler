import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import utilities.CommunicationHandler;
import utilities.Job;
import utilities.Server;

public class SEQ implements SchedulingStrategy{
    /**Gets us the Server with shortest estimated queue among n best fittest servers where fitness is defined in terms of computational resources required (not just cpu) */

    CommunicationHandler communicator;

    public void setCommunicator(CommunicationHandler inCommunicator){
        communicator = inCommunicator;
    }

    public void schedule(){
        // the implimentation of firstCapable algorithm which schules all jobs to the first server that is capable of doing the job
        String response;

        while (true) {
            communicator.send("REDY");
            response = communicator.recieve(null);

            // Do the scheduling
            if (Job.noMoreJobsAvail(response)) {
                break;
            } else if (Job.regularJobForScheduling(response)) {
                // if the response is a Job issue response, then we look at different servers that are capable of
                // accomodating that job and we sen that job to the first server
                Job job = Job.parseJobFromREDY(response);
                Server server = getFittest(job);
                
                communicator.scheduleJob(job, server.name, server.idAmongName);
            }
        }
    }

    private Server getFittest(Job job){
        /**
         * gets the fittest server for the job according to XXXX fitness function
         */
        assert job != null; //pre-condition test
        Server server = null;
            
        String msgToServer = "GETS Capable " + job.coreReq + " " + job.ramReqMb + " " + job.DiskReqMb;
        LinkedList<Server> srvrLst = communicator.getServersFromGETS(msgToServer);
    
            
        assert srvrLst.size() >1; //post-condition test
        
        server = selectCustomFittest(srvrLst, job);
            
        return server;
    }

    private Server selectCustomFittest(LinkedList<Server> srvrLst, Job job) {
        /**
         * Takes in a valid list of capable servers and returns the best one of them for the job. Also takes in job object
         */

        //setting up fittness scores and getting estimated queue jobs lenghts
        PriorityQueue<Server> serverFittnesPQ = new PriorityQueue<Server>(Comparator.comparing(Server::getFitnessScore));
        
        for (Server server : srvrLst){
            int cpuImportance = 3;
            server.fitnessScore = (float) (cpuImportance*(server.cpuCores/job.coreReq) + server.diskSpace/job.DiskReqMb +server.memory/job.ramReqMb);
            server.estimatedWaitTimeOfQueuedJobs = communicator.getEstimatedTimeLengthOfQuedJobs(server);
            serverFittnesPQ.add(server);
        }

        // move from best -> worst fit and return the first one that is immediately available.
        Integer minQueueTimeLength = null;
        Server winningServer = null;
        while (!serverFittnesPQ.isEmpty()){
            Server server = serverFittnesPQ.poll();
            if(server.immidiatelyAvailable(job))return server;
            if (minQueueTimeLength == null || server.estimatedWaitTimeOfQueuedJobs < minQueueTimeLength){
                winningServer = server;
                minQueueTimeLength = server.estimatedWaitTimeOfQueuedJobs;
            }
        } 
        assert winningServer != null;
        return winningServer;
    }
}