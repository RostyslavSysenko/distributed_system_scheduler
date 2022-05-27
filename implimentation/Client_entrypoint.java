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
        String ip;
        String port;
        try {
            ip = args[0];
            port = args[1];
        } catch (IndexOutOfBoundsException e) {
            // set default if no parameters provided
            ip = "127.0.0.1";
            port = "50000";
        }

        Scheduler scheduler = new Scheduler();
        
        scheduler.communicator.start(ip, port);
        scheduler.setScheduler(new SEQ());
        scheduler.executeScheduler();
        scheduler.communicator.quit(null, false);
    }
}
