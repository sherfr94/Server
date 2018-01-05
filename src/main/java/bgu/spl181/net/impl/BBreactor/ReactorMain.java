package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl181.net.api.bidi.MovieMessagingProtocol;
import bgu.spl181.net.api.json.UsersList;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ReactorMain {

    public static void main(String[] args) throws FileNotFoundException {

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("Database/Users.json"));
        UsersList users = gson.fromJson(reader, UsersList.class);


        int port = Integer.parseInt(args[0]);

        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port, //port
                MovieMessagingProtocol::new, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }


}
