package bgu.spl181.net.api.bidi;

import bgu.spl181.net.api.json.Movie;
import bgu.spl181.net.api.json.MoviesList;
import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MovieMessagingProtocol<T> extends UserMessagingProtocol<T>{

    ArrayList<Movie> movies;
    protected ConcurrentHashMap<String,Movie> moviesInfo;

    public MovieMessagingProtocol(UsersList users, MoviesList movies){
        super(users);
        this.movies= (ArrayList) movies.getMovies();
        this.moviesInfo = new ConcurrentHashMap<>();

        for(Movie movie : this.movies){
            moviesInfo.put(movie.getName(),movie);
        }

    }

    @Override
    protected void request(String str) {
        String username = this.loggedIn.get(connectionId);
        boolean error=false;

        if(str.equals("balance info")){
            Integer balance = this.usersInfo.get(username).getBalance();
            connections.send(connectionId,"ACK balance "+balance);

        }
        else if(str.contains("balance add ")){
            str = str.substring(str.lastIndexOf(" ")+1);
            Integer amount = Integer.parseInt(str);
            Integer balance = usersInfo.get(username).getBalance();
            usersInfo.get(username).setBalance(balance+amount);
            connections.send(connectionId,"ACK balance "+(balance+amount)+" added "+amount);

        }
        else if(str.equals("info")){//all movies
            String result = "ACK info";
            for(Movie movie : movies){
                result=result+" \""+movie.getName()+"\"";
            }
            connections.send(connectionId,result);


        }else if(str.contains("info \"")){//specific movie
            int pos1 = str.indexOf("\"");
            int pos2 = str.lastIndexOf("\"");
            String moviename= str.substring(pos1+1, pos2);


            if(!moviesInfo.containsKey(moviename)){
                connections.send(connectionId,"ERROR request info failed");
                return;
            }

            connections.send(connectionId,"ACK info "+moviesInfo.get(moviename).info());

        }
        else if (str.contains("rent ")) {


        }
        else if(str.contains("return ")){

        }
        else {
            //case admin
        }


    }
}
