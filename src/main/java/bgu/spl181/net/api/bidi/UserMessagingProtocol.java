package bgu.spl181.net.api.bidi;

import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class UserMessagingProtocol<T> implements BidiMessagingProtocol<T>, Supplier<BidiMessagingProtocol<T>> {

    protected ArrayList<User> users;
    protected ConcurrentHashMap<Integer,String> loggedIn;
    protected ConcurrentHashMap<String,String> passwords;
    protected ConcurrentHashMap<String,User> usersInfo;


    protected ConnectionsImpl connections;
    protected Integer connectionId;


    public UserMessagingProtocol() {
    }

    public UserMessagingProtocol(UsersList users) {
        this.users= (ArrayList) users.getUsers();
        this.passwords=new ConcurrentHashMap<>();
        this.usersInfo=new ConcurrentHashMap<>();


        for(User user : this.users) {
            passwords.put(user.getUsername(),user.getPassword());
            usersInfo.put(user.getUsername(),user);
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
        this.loggedIn = this.connections.getLoggedIn();

    }

    private void login(String str){
        boolean error = false;
        int pos2 = str.indexOf(" ");
        String username = str.substring(0, pos2);
        String password = str.substring(pos2+1);
        System.out.println("username: "+username);
        System.out.println("password: "+password);

        if((passwords.get(username)==null)) {//not in user list
            System.out.println("error4");
            error = true;
        }
        if(loggedIn.get(connectionId)!=null && !error) { // case client id is already logged in
            System.out.println("error1");
            error=true;
        }
        if(loggedIn.containsValue(username) && !error) { // case other username is already logged in
            System.out.println("error2");

            error=true;
        }
        if((passwords.get(username)!=null) && !error) {
            if (!(passwords.get(username).equals(password))) { // wrong password
                System.out.println("error3");
                error = true;
            }
        }


        if(!error) {
            loggedIn.put(connectionId, username);
            connections.send(connectionId,"ACK login succeeded");
        }
        else {
            connections.send(connectionId,"ERROR login failed");
        }


    }

    protected void register(String str){
        boolean error = false;
        int pos2 = str.indexOf(" ");
        //3 missing username / password
        if(pos2==-1) {
            connections.send(connectionId,"ERROR register failed");
            return;
        }

        String username = str.substring(0, pos2);
        str=str.substring(pos2+1);
        String password;
        User newUser;

        //1
        if(!error && loggedIn.containsKey(connectionId)){
            error=true;
        }
        //2
        if(!error && passwords.containsKey(username)){
            error=true;
        }

        if(error){
            connections.send(connectionId,"ERROR register failed");
            return;
        }

        int pos3 = str.indexOf(" ");
        if(pos3==-1){ //no country
            password = str;
            newUser = new User(username,password);
            //TODO: json update

        }
        else{//yes country

            password = str.substring(0,pos3);
            str=str.substring(pos3+1);

            //4 //TODO: check more country problems
            if(!str.contains("country=\"")){
                connections.send(connectionId,"ERROR register failed");
                return;
            }

            int pos4 = str.indexOf("\"");
            String country=str.substring(pos4+1,str.length()-1);

            newUser = new User(username,password);
            newUser.setCountry(country);


        }

        if(!error){
            users.add(newUser);
            passwords.put(newUser.getUsername(),newUser.getPassword());
            usersInfo.put(newUser.getUsername(),newUser);
            connections.send(connectionId,"ACK registration succeeded");

            System.out.println(newUser);
        }

    }

    private void requestUser(String str){
        int pos1 = str.indexOf(" ");
        String result;
        if (pos1==-1){
            result = str;
        }
        else{
            result = str.substring(0,pos1);
        }

        if(!loggedIn.containsKey(connectionId)){
            connections.send(connectionId,"ERROR request "+result+" failed");
        }
        else{
            request(str);

        }
    }

    protected abstract void request(String str);

    private void signout() {
        boolean error = false;
        if(!(loggedIn.containsKey(connectionId))){
            System.out.println("#1");
            connections.send(connectionId,"ERROR signout failed");
        }
        else{
            System.out.println("#2");
            loggedIn.remove(connectionId);

            connections.send(connectionId,"ACK signout succeeded");
        }
    }

    @Override
    public void process(Object message) {

        String str = (String)message;
        System.out.println("str: "+str);

        if(str.equals("SIGNOUT")){
            System.out.println("#");
            signout();
        }
        else{
            int pos1 = str.indexOf(" ");
            String first = str.substring(0,pos1);
            System.out.println("first: "+first);
            str = str.substring(pos1+1);

            if(first.equals("LOGIN")){
                login(str);
            }
            else if(first.equals("REGISTER")){
                register(str);
            }
            else if(first.equals("REQUEST")){
                requestUser(str);
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
