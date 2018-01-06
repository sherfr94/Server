package bgu.spl181.net.api.bidi;

import bgu.spl181.net.api.json.Movie;
import bgu.spl181.net.api.json.MoviesList;
import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MovieMessagingProtocol<T> extends UserMessagingProtocol<T>{

    protected ArrayList<Movie> movies;
    protected ConcurrentHashMap<String,Movie> moviesInfo;

    protected Integer maxMovieId = 0;



    public MovieMessagingProtocol(UsersList users, MoviesList movies){
        super(users);
        this.movies= (ArrayList) movies.getMovies();
        this.moviesInfo = new ConcurrentHashMap<>();


        for(Movie movie : this.movies){
            moviesInfo.put(movie.getName(),movie);
            if(movie.getId()>maxMovieId) maxMovieId=movie.getId();
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

                connections.send(connectionId,"ACK rent \""+moviename+"\" success");
                //connections.broadcast("BROADCAST movie \""+moviename+"\" "+(copies-1)+" "+moviePrice);//TODO: another thread
            }


        }
        else if(str.contains("return ")){

            int pos1 = str.indexOf("\"");
            int pos2 = str.lastIndexOf("\"");
            String moviename= str.substring(pos1+1, pos2);
            Movie toRemove = new Movie();

            boolean error=false;

            //3 - movie doesn't exist
            if(!moviesInfo.containsKey(moviename)) error=true;
            //2 - user not renting
            else {
                boolean found=false;
                for(Movie movie: usersInfo.get(username).getMovies()){
                    if(movie.getName().equals(moviename)){
                        found=true;
                        toRemove=movie;
                        break;
                    }
                }
                if(!found) error=true;
            }

            if(error){
                connections.send(connectionId, "ERROR request return failed");
                return;
            }
            else{
                Integer moviePrice = moviesInfo.get(moviename).getPrice().get();
                Integer copies = moviesInfo.get(moviename).getAvailableAmount();

                usersInfo.get(username).getMovies().remove(toRemove);
                moviesInfo.get(moviename).setAvailableAmount(copies+1);

                System.out.println(usersInfo.get(username));
                System.out.println(moviesInfo.get(moviename));

                connections.send(connectionId,"ACK return \""+moviename+"\" success");
                //connections.broadcast("BROADCAST movie \""+moviename+"\" "+(copies+1)+" "+moviePrice);//TODO: another thread
            }

        }
        else {
            int pos1 = str.indexOf(" ");
            String requestType = str.substring(0,pos1);
            str = str.substring(pos1+2);
            //case admin
            if(!usersInfo.get(username).getType().equals("admin")){
                connections.send(connectionId,"ERROR request "+requestType+" failed");
            }
            else{

                int pos3 = str.indexOf("\"");

                String moviename=str.substring(0,pos3);
                System.out.println("im in else");



                //addmovie
                if(requestType.equals("addmovie")){
                    //2 - movie already exists
                    if(moviesInfo.containsKey(moviename)){
                        connections.send(connectionId,"ERROR request "+requestType+" failed");
                        return;
                    }





                    else{

                        str=str.substring(pos3+2);
                        System.out.println("i read addmovie");

                        int pos4=str.indexOf(" ");
                        String amount = str.substring(0,pos4);
                        str=str.substring(pos4+1);

                        pos4 = str.indexOf(" ");
                        System.out.println(str+"######"+pos4);
                        String price;
                        ArrayList<String> bannedcountries = new ArrayList<>();

                        if(pos4==-1){
                            price = str;
                        }
                        else{//yes banned country
                            price=str.substring(0,pos4);
                            str=str.substring(pos4+1);
                            int pos = str.indexOf(" ");
                            while(pos!=-1){
                                int start = str.indexOf("\"");
                                int end = str.indexOf("\"", start +1);
                                bannedcountries.add(str.substring(start+1,end));
                                str=str.substring(end+1);

                                pos = str.indexOf(" ");
                            }
                        }


                        if(Integer.parseInt(amount)<=0 || Integer.parseInt(price)<=0){
                            connections.send(connectionId,"ERROR request "+requestType+" failed");
                            return;
                        }

                        System.out.println("PRICE: "+price+" AMOUNT: "+amount);
                        Movie toAdd = new Movie();
                        toAdd.setId(++maxMovieId);
                        toAdd.setName(moviename);
                        toAdd.setPrice(Integer.parseInt(price));
                        toAdd.setTotalAmount(Integer.parseInt(amount));
                        toAdd.setAvailableAmount(Integer.parseInt(amount));
                        toAdd.setBannedCountries(bannedcountries);

                        moviesInfo.put(moviename,toAdd);
                        movies.add(toAdd);

                        connections.send(connectionId,"ACK addmovie \""+moviename+"\" success");

                        System.out.println(moviesInfo.get(moviename).getName());


//                        connections.broadcast("BROADCAST movie \""+moviename+"\" "
//                                +Integer.parseInt(amount)+" "+Integer.parseInt(price));//TODO: broadcastas


                    }

                }
                //remove movie
                else if(requestType.equals("remmovie")) {

                    if(!moviesInfo.containsKey(moviename)){
                        connections.send(connectionId,"ERROR request remmovie failed");
                        return;
                    }
                    else{
                        if(moviesInfo.get(moviename).getAvailableAmount()!=moviesInfo.get(moviename).getTotalAmount()){
                            connections.send(connectionId,"ERROR request remmovie failed");
                            return;
                        }
                        else{
                            Movie toRemove = moviesInfo.get(moviename);
                            moviesInfo.remove(toRemove);
                            movies.remove(toRemove);
                            connections.send(connectionId,"ACK remmovie \""+moviename+"\" success");
                            //connections.broadcast("BROADCAST movie"+moviename+" removed");

                        }
                    }

                }
                // change price
                else if(requestType.equals("changeprice")) {

                    if(!moviesInfo.containsKey(moviename)){
                        connections.send(connectionId,"ERROR request changeprice failed");
                        return;
                    }
                    else{
                        int pos2 = str.lastIndexOf(" ");
                        Integer price = Integer.parseInt(str.substring(pos2+1));

                        if(price<=0){
                            connections.send(connectionId,"ERROR request changeprice failed");
                            return;
                        }
                        else{
                            Integer copies = moviesInfo.get(moviename).getAvailableAmount();

                            moviesInfo.get(moviename).setPrice(price);
                            connections.send(connectionId,"ACK changeprice \""+moviename+"\" success");
                            //connections.broadcast("BROADCAST movie \""+moviename+"\" "+copies+" "+price);//TODO: broadcast
                        }

                    }

                }



            }



        }


    }
}
