package utilities;
public class ServerType {
    /**
     * this class is used for easy to work with representation of data about
     * largest server and in particular its number of instances and its name.
     * This object is then used during scheduling stage.
     */
    public String name;
    public int availableInstances;

    public ServerType(String pName, int pAvailableInstances) {
        name = pName;
        availableInstances = pAvailableInstances;
    }
}
