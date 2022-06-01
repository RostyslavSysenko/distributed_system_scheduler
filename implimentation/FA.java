import java.util.LinkedList;

import utilities.CommunicationHandler;
import utilities.Job;
import utilities.Server;
import utilities.ServerFinder;

public class FA implements SchedulingStrategy{
    /** Schedules to the first available server (server that is immideately able to take on the job)*/

    CommunicationHandler communicator;

    public void setCommunicator(CommunicationHandler inCommunicator){
        communicator = inCommunicator;
    }

    public void schedule(){
        // the implimentation of firstCapable algorithm which schules all jobs to the first server that is capable of doing the job
        String response;
        LinkedList<Server> srvrLst = null;
        //here we can sort srvrLst in some way so that firstCapable will return us worst or best fit.

        while (true) {
            communicator.send("REDY");
            response = communicator.recieve(null);

            if (srvrLst == null){
                //only Gets all at the very start of execution.
                srvrLst = communicator.getServersFromGETS("GETS All");
            }
            // Do the scheduling
            if (Job.noMoreJobsAvail(response)) {
                break;
            } else if (Job.regularJobForScheduling(response)) {
                // if the response is a Job issue response, then we look at different servers that are capable of
                // accomodating that job and we sen that job to the first server
                Job job = Job.parseJobFromREDY(response);
                Server server = null;

                try{
                    server = ServerFinder.getFirstAvailable(job, communicator);
                } catch(IndexOutOfBoundsException e){
                    //in the future this can be further improved by allowing for migration of jobs when some server has enough resources to execute the job immideately, but also has waiting queue. We could potentially work with migration here before proceeding to finding server with shortest queue
                    server = ServerFinder.getFirstCapableInMemory(job,srvrLst);
                }
                assert server != null;
                communicator.scheduleJob(job, server.name, server.idAmongName);
            }
        }
    }


}