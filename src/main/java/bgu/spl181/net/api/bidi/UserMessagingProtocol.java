package bgu.spl181.net.api.bidi;

import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class UserMessagingProtocol<T> implements BidiMessagingProtocol<T>, Supplier<BidiMessagingProtocol<T>> {

    private ArrayList<User> users;
    private ConcurrentHashMap<Integer,String> loggedIn;
    private ConcurrentHashMap<String,String> passwords;

    private ConnectionsImpl connections;
    private Integer connectionId;


    public UserMessagingProtocol() {
    }

    public UserMessagingProtocol(UsersList users) {
        this.users= (ArrayList<User>) users.getUsers();
        this.passwords=new ConcurrentHashMap<>();
        this.loggedIn = new ConcurrentHashMap<>();

        for(User user : this.users) {
            passwords.put(user.getUsername(),user.getPassword());
        }
    }


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

        boolean error = false;

        String str = (String)message;
        if(str.equals("SIGNOUT")){

        }
        else{
            int pos1 = str.indexOf(" ");
            String first = str.substring(0,pos1);
            str = str.substring(pos1+1);

            if(first.equals("LOGIN")){
                int pos2 = str.indexOf(" ");
                String username = str.substring(0, pos2);
                String password = str.substring(pos2+1);

                if(loggedIn.get(connectionId)!=null) { // case client id is already logged in
                    error=true;
                }
                if(loggedIn.containsValue(username)) { // case other username is already logged in
                    error=true;
                }
                if(!(passwords.get(username)!=null)) {
                    if (!passwords.get(username).equals(password)) { // wrong password
                        error = true;
                    }
                }
                if(!users.contains(username)){
                    error=true;
                }

                if(!error) {
                    loggedIn.put(connectionId, username);
                    connections.send(connectionId,"ACK login succeeded\n");
                }
                else {
                    connections.send(connectionId,"ERROR login failed\n");
                }


            }
        }

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    @Override
    public BidiMessagingProtocol<T> get() {
        return this;
    }
}
