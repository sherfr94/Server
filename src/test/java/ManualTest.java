import bgu.spl181.net.api.json.User;
import bgu.spl181.net.api.json.UsersList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ManualTest {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        //JsonWriter writer = new JsonWriter(new FileWriter("Database/Test.json"));
        FileWriter writer = new FileWriter("Database/Test1.json");
////        writer.beginObject();
////        writer.name("username");
////        writer.beginArray();
////        writer.beginObject();
////        writer.name("id").value("1");
////        writer.name("name").value("john");
////        writer.endObject();
////        writer.endArray();
////        writer.endObject();
//        writer.name("id").value("2");
//
//

        UsersList usersList = new UsersList();
        usersList.getUsers().add(new User("john","potato"));
        usersList.getUsers().add(new User("lisa","potato"));

        String json = gson.toJson(usersList);


        System.out.println(json);

        JsonObject obj = new JsonObject();
        obj.getAsJsonObject(json);

        writer.write(json);
        writer.close();

    }
}

