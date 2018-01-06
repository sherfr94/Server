package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.bidi.*;
import bgu.spl181.net.api.json.Movie;
import bgu.spl181.net.api.json.MoviesList;
import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TPCMain {

    public static void main(String[] args) throws FileNotFoundException, UnknownHostException {
        Gson gson = new Gson();
        JsonReader reader1 = new JsonReader(new FileReader("Database/Users.json"));
        JsonReader reader2 = new JsonReader(new FileReader("Database/Movies.json"));

        UsersList users = gson.fromJson(reader1, UsersList.class);
        MoviesList movies = gson.fromJson(reader2, MoviesList.class);

//        System.out.println(users.getUsers().get(1).getMovies().get(0).getName());
//        System.out.println(movies.getMovies().get(0).getAvailableAmount());

//        for(User user: users.getUsers()){
//            System.out.println(user.getUsername());
//        }
//
//        for(Movie movie: movies.getMovies()){
//            System.out.println(movie.getId());
//            System.out.println(movie.getName());
//        }



        //int port = Integer.parseInt(args[0]);
        int port = Integer.parseInt("8888");

        System.out.println(InetAddress.getLocalHost());

        Server.threadPerClient(

                port, //port
                new MovieMessagingProtocol<>(users,movies), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }


}
