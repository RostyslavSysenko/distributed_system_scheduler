import java.util.ArrayList;

import utilities.CommunicationHandler;
import utilities.Job;
import utilities.Server;

public class FC implements SchedulingStrategy{

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
                Server server = getFirstCapable(job);
                communicator.scheduleJob(job, server.name, server.idAmongName);
            }
        }
    }

    private Server getFirstCapable(Job job){
        /**
         * gets a valid job and outputs the first server that is capable to do that job
         */
        Server server = null;
        
        String msgToServer = "GETS Capable " + job.coreReq + " " + job.ramReqMb + " " + job.DiskReqMb;
        ArrayList<Server> srvrLst = communicator.getServersFromGETS(msgToServer);

        try{
            server = srvrLst.get(0);
        } catch(Exception e) {
            System.out.print("Some Problem occured");
        }
        
        return server;
    }
}