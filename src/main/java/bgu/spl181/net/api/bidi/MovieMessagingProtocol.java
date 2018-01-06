package bgu.spl181.net.api.bidi;

import bgu.spl181.net.api.json.Movie;
import bgu.spl181.net.api.json.MoviesList;
import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    //Register to movie service must have country!
    @Override
    protected void register(String str) {

        if(!str.contains("country= \"")){
            connections.send(connectionId,"ERROR register failed");
            return;
        }
        else{
            super.register(str);
        }
    }


    @Override
    protected void request(String str) {

        String username = this.loggedIn.get(connectionId);
        String country = usersInfo.get(username).getCountry();



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
            boolean error=false;

            int pos1 = str.indexOf("\"");
            int pos2 = str.lastIndexOf("\"");
            String moviename= str.substring(pos1+1, pos2);

            //2 - movie doesn't exist
            if(!moviesInfo.containsKey(moviename)){
                error=true;
            }
            //4 - user is in banned country of movie
            else if(moviesInfo.get(moviename).getBannedCountries().contains(country)){
                error=true;
            }
            //5 - user already rents the movie
            else if(usersInfo.get(username).getMovies().contains(moviename)){
                error=true;
            }
            //3
            else if(moviesInfo.get(moviename).getAvailableAmount()==0){
                error=true;
            }
            //1 - you don't have enough money
            else{//TODO: what if admin changes price while renting movie
                Integer moviePrice = moviesInfo.get(moviename).getPrice().get();
                Integer userBalance = usersInfo.get(username).getBalance();
                if(moviePrice>userBalance){
                    error = true;
                }
            }

            if(error){
                connections.send(connectionId,"ERROR rent failed");
            }
            else{
                //reduce availabe amount
                Integer copies = moviesInfo.get(moviename).getAvailableAmount();
                moviesInfo.get(moviename).setAvailableAmount(copies-1);

                //remove balnce by cost
                Integer moviePrice = moviesInfo.get(moviename).getPrice().get();
                Integer userBalance = usersInfo.get(username).getBalance();

                usersInfo.get(username).setBalance(userBalance-moviePrice);

                //add to user movie list
                usersInfo.get(username).getMovies().add(moviesInfo.get(moviename));
                System.out.println(usersInfo.get(username));
                System.out.println(moviesInfo.get(moviename));
                connections.send(connectionId,"ACK rent \""+moviename+"\" success");
            }


        }
        else if(str.contains("return ")){

        }
        else {
            //case admin
        }


    }
}
