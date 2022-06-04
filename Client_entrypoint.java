public class Client_entrypoint {
    /**
     * this is the class which servers as an entry-point into our Scheduler. This class
     * handles the user provided parameters, creates a Scheduler instance,
     * then asks that Scheduler to start a connection with the ds-server using scheduler's
     * dedicated communication class instance, then it executes the scheduling using the specificed scheduling strategy, then the
     * algorithm safely quit. This purpose of this class is to decouple what we want our program to do from particular components that impliment particular functionalities
     * 
     * @param args
     */
    public static void main(String[] args) {

        // generic port and ip
        String ip = "127.0.0.1";
        String port = "50000";
        Scheduler scheduler = new Scheduler(ip, port);

        //handling command line arguments in a simple way
        if(args[0] == "FATFC"){
            scheduler.setScheduler(new FATFC_alg());
        } else if (args[0] == "FC"){
            scheduler.setScheduler(new FC_alg());
        } else if (args[0] == "LRR"){
            scheduler.setScheduler(new LRR_alg());
        } else {
            System.err.print("invalid alg name");
            System.exit(1);
        }
        
        scheduler.executeScheduler();
    }
}
