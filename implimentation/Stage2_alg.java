import java.util.ArrayList;

import utilities.CommunicationHandler;
import utilities.Job;
import utilities.Server;

public class Stage2_alg implements SchedulingStrategy{

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
                
                // TO-DO: here we may choose to migrate things around if the task is being blocked for example
                
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
        
        // here we select the most appropriate server for the job. We do not migrate anything here
        
        return server;
    }
}