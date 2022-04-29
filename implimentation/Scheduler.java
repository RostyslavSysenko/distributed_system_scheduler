import utilities.CommunicationHandler;

interface SchedulingStrategy{
    void schedule();
    void setCommunicator(CommunicationHandler communicator);
}

public class Scheduler {
    /**
     * this class that servers as context for all possible scheduling strategies. We can assign any scheduling strategy to this scheduler
     * class and it will happily execute that strategy. Each Scheduler is assigned its own communicator class which helps to facilitate the lower
     * level communication
     */

    private SchedulingStrategy scheduler = null;
    CommunicationHandler communicator = null;

    Scheduler(){
        communicator = new CommunicationHandler();
     }

    public void setScheduler(SchedulingStrategy inScheduler){
        assert communicator != null; //pre-condition test

        scheduler = inScheduler;
        scheduler.setCommunicator(communicator);
     }

    public void executeScheduler(){
        assert scheduler != null; //pre-condition test
        scheduler.schedule();
     }
}
