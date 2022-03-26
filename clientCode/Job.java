

public class Job {
    int createTimeSec ;
    int id ;
    int estimExecTimeSec ;
    int coreReq ;
    int ramReqMb ;
    int DiskReqMb;

    static Job parseJobFromREDY(String jobStr) {
        String[] jobArr = jobStr.split("\\s+");
        int jobCreateTimeSec = Integer.parseInt(jobArr[1]);
        int jobId =  Integer.parseInt(jobArr[2]);
        int jobEstimExecTimeSec =   Integer.parseInt(jobArr[3]);
        int jobCoreReq =   Integer.parseInt(jobArr[4]);
        int jobRAMReqMb =   Integer.parseInt(jobArr[5]);
        int jobDiskReqMb =   Integer.parseInt(jobArr[6]);
        Job job = new Job(jobCreateTimeSec,jobId,jobEstimExecTimeSec,jobCoreReq,jobRAMReqMb,jobDiskReqMb);
        return job;
    }

    static boolean noMoreJobsAvail(String reply){
        if (reply.equals("NONE")){
            return true;
        } else{
            return false;
        }
    }

    static boolean regularJobForScheduling(String reply){
        if (reply.startsWith("JOBN")){
            return true;
        } else{
            return false;
        }
    }


    Job(int pCreateTimeSec, int pId,int pEstimExecTimeSec,int pCoreReq,int pRAMReqMb, int pDiskReqMb){
        createTimeSec = pCreateTimeSec;
        id = pId;
        estimExecTimeSec = pEstimExecTimeSec;
        coreReq = pCoreReq;
        ramReqMb =  pRAMReqMb;
        DiskReqMb = pDiskReqMb;
    }
}
