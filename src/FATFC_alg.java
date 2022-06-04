
import java.util.Comparator;
import java.util.LinkedList;
import utilities.CommunicationHandler;
import utilities.Job;
import utilities.Server;
import utilities.ServerFinder;

public class FATFC_alg implements SchedulingStrategy {
    /**
     * FATCBF - first avialble then first capable
     * Schedules to the first available server (server that is immideately able to
     * take on the job). If no server is immideately available then schedules to the
     * first capable where the first capable is selected from a list is in order 
     * from lowest Cpu core count to highest so first capable selects the server 
     * with lowest num of cores which is capable
     */

    CommunicationHandler communicator;

    public void setCommunicator(CommunicationHandler inCommunicator) {
        communicator = inCommunicator;
    }

    public void schedule() {
        String response;
        LinkedList<Server> srvrLst = null;
        
        // sets the ordering condition for servers in our server list
        Comparator<Server> serverListComparator = Comparator.comparingInt(Server::getTotalCpuCoreCount); 
        
        while (true) {
            //tell server we are ready to start schedling
            communicator.send("REDY");
            response = communicator.recieve(null);

            if (srvrLst == null) {
                // only enters this if condition at the very start of execution. gets a list of all servers
                // (sorted) just in case ds-sim doesnt do the sorting
                srvrLst = ServerFinder.getSortedServerListPriorToScheduling(serverListComparator, communicator);
            }
            // Do the scheduling
            if (Job.noMoreJobsAvail(response)) {
                break;
            } else if (Job.regularJobForScheduling(response)) {
                // code below is executed if more jobs left to be scheudled. 
                Job job = Job.parseJobFromREDY(response);
                Server server = null;
                try {
                    // we schedule to first available when such exists
                    server = ServerFinder.getFirstAvailable(job, communicator);
                } catch (IndexOutOfBoundsException e) {
                    // else if no avaialble server exist, this is handled here by schedule to the first capable instead
                    // as we know that first capable will always exist
                    server = ServerFinder.getFirstCapableInMemory(job, srvrLst);
                }
                
                assert server != null; // test condition to ensure program behaves properly
                communicator.scheduleJob(job, server.getName(), server.getIdAmongName());
            }
        }
    }

}