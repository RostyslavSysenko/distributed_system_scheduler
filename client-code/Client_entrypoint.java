
public class Client_entrypoint {
    /**
     * this is the class which servers as an entry-point into our client. This class
     * handles the user provided parameters, creates a Client scheduler instance,
     * then asks that instance to start a connection with the ds-server, run the
     * algorithm and safely quit. This purpose of this class is to separate the
     * functionality associated with Client scheduler and the flow of the actual
     * program to allow for more maintainable and easy to work with code for the
     * Part 2 of the task.
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

        Client_scheduler client = new Client_scheduler();
        client.start(ip, port);
        client.lrr_scheduling();
        client.quit(null, false);
    }
}
