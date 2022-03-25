
public class Client_entrypoint {
    public static void main(String[] args) {
        String ip;
        String port;
        try{
            ip = args[0];
            port = args[1];
        }
        catch(IndexOutOfBoundsException e ){
            //set default if no parameters provided
            ip = "127.0.0.1";
            port = "50000";
        }

        Client_scheduler client = new Client_scheduler();
        client.start(ip,port);
        client.runAllToSingleLargestSchedulingAlgorithm();

        //TO-DO: Maybe verify that all jobs are completed?

        client.quit();
    }
}
