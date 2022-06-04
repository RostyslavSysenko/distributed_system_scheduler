package utilities;
public class Server {
    /**
     * this class does the parsing of a messages from GETS all and putting it into
     * an easy to work with object.
     */

     //many attributed below are not used but they are kept for future optimisation.
    private String name;
    private  int idAmongName;
    private Integer ttlCpuCores;
    private Integer ttlRAM;
    private Integer ttlDiskSpace;
    private String statusOnLstQuery;
    private Integer unusedCpuCoresOnLstQuery;
    private Integer unusedRAMOnLstQuery;
    private Integer unusedDiskOnLstQuery;
    private int jobsWaitingOnLstQuery;
    private int jobsExecutingOnLstQuery;
    private Integer estimatedWaitTimeOfQueuedJobsOnLstQuery = null;

    Server(String inName,
            int inIdAmongName,
            String inStatus,
            Integer inTtlCpuCores,
            Integer inTtlRAM,
            Integer inTtlDisk,
            Integer inUnusedDuringLastQueryCpuCores,
            Integer inUnusedDuringLastQueryRAM,
            Integer inUnusedDuringLastQueryDisk,
            int inJobsWaiting,
            int inJobsExecuting
            ) 
            
    {
        name = inName;
        idAmongName = inIdAmongName;
        statusOnLstQuery = inStatus;

        ttlCpuCores = inTtlCpuCores;
        ttlRAM = inTtlRAM;
        ttlDiskSpace = inTtlDisk;

        unusedCpuCoresOnLstQuery = inUnusedDuringLastQueryCpuCores;
        unusedRAMOnLstQuery = inUnusedDuringLastQueryRAM;
        unusedDiskOnLstQuery = inUnusedDuringLastQueryDisk;

        jobsWaitingOnLstQuery = inJobsWaiting;
        jobsExecutingOnLstQuery = inJobsExecuting;
    }

    public Integer getTotalCpuCoreCount(){
        // can only be called when total cpu core count is not nulltime is not null
        assert this.ttlCpuCores!=null;
        return this.ttlCpuCores;
    }
    
    public Integer getQueueWaitTime() {
        // can only be called when estimated wait time is not null
        assert estimatedWaitTimeOfQueuedJobsOnLstQuery != null;
        return estimatedWaitTimeOfQueuedJobsOnLstQuery;
    }

    public String getName() {
        return name;
    }

    public int getIdAmongName() {
        return idAmongName;
    }

    public Integer getEstimatedWaitTimeOfQueuedJobsOnLstQuery() {
        return estimatedWaitTimeOfQueuedJobsOnLstQuery;
    }

    public void setEstimatedWaitTimeOfQueuedJobsOnLstQuery(Integer estimatedWaitTimeOfQueuedJobsOnLstQuery) {
        this.estimatedWaitTimeOfQueuedJobsOnLstQuery = estimatedWaitTimeOfQueuedJobsOnLstQuery;
    }
    
    public boolean isCapable(Job job){
        // can only be called when ttl attributes are not null
        assert ttlCpuCores!=null && ttlRAM!=null && ttlDiskSpace != null;
        return this.ttlCpuCores >= job.coreReq && this.ttlDiskSpace >= job.DiskReqMb && this.ttlRAM >= job.ramReqMb;
    }

    public static Server parseServerFromGETS(String msg, boolean noSchedulingHasBeenDoneYet) {
        // this method intreprets attributes of cpu, ram and memory as server's availability (because query is made after some scheduling has been done and some resources had been or are being utilised)
        // pre condition: the msg represnts a valid server message from server in response to some gets command which is not empty.
        
        String[] msgArr = msg.split("\\s+");

        assert msgArr.length == 9;

        Integer tempTtlCpuCores = null;
        Integer tempTtlRAM = null;
        Integer tempTtlDisk = null;
       
        Integer tempUnusedCpuCores = Integer.parseInt(msgArr[4]);
        Integer tempUnusedRAM = Integer.parseInt(msgArr[5]);
        Integer tempUnusedDisk = Integer.parseInt(msgArr[6]);

        if(noSchedulingHasBeenDoneYet){
            tempTtlCpuCores = tempUnusedCpuCores;
            tempTtlRAM = tempUnusedRAM;
            tempTtlDisk = tempUnusedDisk;
        }

        return new Server(msgArr[0], Integer.parseInt(msgArr[1]), msgArr[2],tempTtlCpuCores,tempTtlRAM,tempTtlDisk, tempUnusedCpuCores, tempUnusedRAM,
        tempUnusedDisk , Integer.parseInt(msgArr[7]), Integer.parseInt(msgArr[8]));
    }  

}
