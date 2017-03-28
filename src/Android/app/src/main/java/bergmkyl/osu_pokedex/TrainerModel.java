package bergmkyl.osu_pokedex;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class TrainerModel {
    public String name;
    public String self;
    public String hometown;
    public int num_badges;
    public ArrayList<String> pokemon_caught;


    // Constructor to convert JSON object into a Java class instance
    public TrainerModel(JSONObject object){
        try {
            this.name = object.getString("name");
            this.self = object.getString("self");
            this.hometown = object.getString("hometown");
            this.num_badges = object.getInt("num_badges");
            JSONArray jsonArr = object.getJSONArray("pokemon_caught");
            ArrayList<String> arrPokemon =new ArrayList<String>();
            for(int i = 0; i < jsonArr.length(); i++)
                arrPokemon.add(jsonArr.getString(i));
            this.pokemon_caught = arrPokemon;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<TrainerModel> fromJson(JSONArray jsonObjects) {
        ArrayList<TrainerModel> trainer = new ArrayList<TrainerModel>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                trainer.add(new TrainerModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return trainer;
    }

}
