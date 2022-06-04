import utilities.CommunicationHandler;
import utilities.ServerFinder;
import utilities.Job;
import utilities.Server;

public class FC_alg implements SchedulingStrategy {
    // first capable algorithm

    CommunicationHandler communicator;

    public void setCommunicator(CommunicationHandler inCommunicator) {
        communicator = inCommunicator;
    }

    public void schedule() {
        // the implimentation of firstCapable algorithm which schules all jobs to the
        // first server that is capable of doing the job
        String response;

        while (true) {
            communicator.send("REDY");
            response = communicator.recieve(null);

            // Do the scheduling
            if (Job.noMoreJobsAvail(response)) {
                break;
            } else if (Job.regularJobForScheduling(response)) {
                // if the response is a Job issue response, then we look at different servers
                // that are capable of accomodating that job and we sen that job to the first
                // server
                Job job = Job.parseJobFromREDY(response);
                Server server = ServerFinder.getFirstCapableWithQueries(job, communicator);
                communicator.scheduleJob(job, server.getName(), server.getIdAmongName());
            }
        }
    }

}