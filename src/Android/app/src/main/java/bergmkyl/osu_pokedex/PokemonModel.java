package bergmkyl.osu_pokedex;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class PokemonModel {
    public String name;
    public String self;
    public ArrayList<String> type;
    public boolean is_captured;
    public double weight;
    public double height;

    // Constructor to convert JSON object into a Java class instance
    public PokemonModel(JSONObject object){
        try {
            this.name = object.getString("name");
            this.self = object.getString("self");
            JSONArray jsonArr = object.getJSONArray("type");
            ArrayList<String> arrType =new ArrayList<String>();
            for(int i = 0; i < jsonArr.length(); i++)
                arrType.add(jsonArr.getString(i));
            this.type = arrType;

            this.is_captured = object.getBoolean("is_captured");
            this.height = object.getDouble("height");
            this.weight = object.getDouble("weight");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<PokemonModel> fromJson(JSONArray jsonObjects) {
        ArrayList<PokemonModel> pokemon = new ArrayList<PokemonModel>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                pokemon.add(new PokemonModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pokemon;
    }

}
