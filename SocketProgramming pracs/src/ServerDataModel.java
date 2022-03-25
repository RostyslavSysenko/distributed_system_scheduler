public class ServerDataModel {
    String name;
    String idAmongName;
    String status;
    int cpuCores;
    int memory;
    int diskSpace;
    int jobsWaiting;
    int jobsExecuting;

    ServerDataModel(String inName,
            String inIdAmongName,
            String inStatus,
            int inCpuCores,
            int inMemory,
            int inDiskSpace,
            int inJobsWaiting,
            int inJobsExecuting) {
        name = inName;
        idAmongName = inIdAmongName;
        status = inStatus;
        cpuCores = inCpuCores;
        memory = inMemory;
        diskSpace = inDiskSpace;
        jobsWaiting = inJobsWaiting;
        jobsExecuting = inJobsExecuting;
    }

    static ServerDataModel parseServerInfoFromGETSALL(String msg){
        String[] msgArr = msg.split("\\s+");
        return new ServerDataModel(msgArr[0], msgArr[1], msgArr[2], Integer.parseInt(msgArr[4]), Integer.parseInt(msgArr[5]), Integer.parseInt(msgArr[6]), Integer.parseInt(msgArr[7]), Integer.parseInt(msgArr[8]));

    }

}
