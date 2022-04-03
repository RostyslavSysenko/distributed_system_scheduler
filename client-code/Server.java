public class Server {
    /**
     * this class does the parsing of a messages from GETS all and putting it into
     * an easy to work with object.
     */
    String name;
    String idAmongName;
    String status;
    int cpuCores;
    int memory;
    int diskSpace;
    int jobsWaiting;
    int jobsExecuting;

    Server(String inName,
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

    static Server parseServerInfoFromGETSALL(String msg) {
        String[] msgArr = msg.split("\\s+");
        return new Server(msgArr[0], msgArr[1], msgArr[2], Integer.parseInt(msgArr[4]), Integer.parseInt(msgArr[5]),
                Integer.parseInt(msgArr[6]), Integer.parseInt(msgArr[7]), Integer.parseInt(msgArr[8]));

    }

}
