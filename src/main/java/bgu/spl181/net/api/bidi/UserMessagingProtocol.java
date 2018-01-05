package bgu.spl181.net.api.bidi;

public class UserMessagingProtocol implements BidiMessagingProtocol {

    private ConnectionsImpl connections;
    private Integer connectionId;

   // private SharedData sharedData;

   /* public BidiMessagingProtocolImpl(SharedData sharedData) {
        this.sharedData = sharedData;
    }*/

    //TODO: implement methods
    @Override
    public void start(int connectionId, Connections connections) {
        //TODO which connection handler
        this.connections=(ConnectionsImpl)connections;
        this.connectionId=connectionId;

    }

    @Override
    public void process(Object message) {

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
