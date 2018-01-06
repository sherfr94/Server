package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl181.net.api.bidi.MovieMessagingProtocol;
import bgu.spl181.net.api.json.MoviesList;
import bgu.spl181.net.api.json.UsersList;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ReactorMain {

    public static void main(String[] args) throws FileNotFoundException {

        Gson gson = new Gson();
        JsonReader reader1 = new JsonReader(new FileReader("Database/Users.json"));
        JsonReader reader2 = new JsonReader(new FileReader("Database/Movies.json"));

        UsersList users = gson.fromJson(reader1, UsersList.class);
        MoviesList movies = gson.fromJson(reader2, MoviesList.class);


        int port = Integer.parseInt("8888");

        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port, //port
                new MovieMessagingProtocol<>(users,movies), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }


}
