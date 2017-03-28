package bergmkyl.osu_pokedex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PutPokemonActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "KYLEB::";
    Button editBtn;
    ListView pokeList;
    String mSelf;
    EditText mName;
    EditText mWeight;
    EditText mHeight;
    EditText mType;
    OkHttpClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_pokemon);
        mSelf = "";
        mClient = new OkHttpClient();

        mName = (EditText) findViewById(R.id.pokemon_name);
        mWeight = (EditText) findViewById(R.id.pokemon_weight);
        mHeight = (EditText) findViewById(R.id.pokemon_height);
        mType = (EditText) findViewById(R.id.pokemon_types);

        getPokemon();
        editBtn = (Button) findViewById(R.id.putPokemonBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makePutRequest("https://cs496-final-160904.appspot.com/");
                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }
            }
        });

        pokeList = (ListView) findViewById(R.id.pokemon_put_list);
        pokeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populateFields(view);
            }
        });
    }

    private void getPokemon() {
        HttpUrl reqUrl = HttpUrl.parse("https://cs496-final-160904.appspot.com/pokemon");
        Request req = new Request.Builder()
                .url(reqUrl)
                .build();
        mClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();
                populateListView(r);
            }
        });
    }

    private void populateListView(String json) {
        try {
            JSONArray arr = new JSONArray(json);
            ArrayList<PokemonModel> newPokemon = PokemonModel.fromJson(arr);
            Log.d(TAG, newPokemon.toString());
            final PokemonAdapter adapter = new PokemonAdapter(this, newPokemon);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pokeList.setAdapter(adapter);
                }
            });

        } catch (Exception e) {
            Log.d("KYLEB::", e.toString());
        }

    }

    private void populateFields(View view) {
        mName.setText(((TextView) view.findViewById(R.id.list_poke_name)).getText().toString());
        mWeight.setText(((TextView) view.findViewById(R.id.list_poke_weight)).getText().toString() + " kgs");
        mHeight.setText(((TextView) view.findViewById(R.id.list_poke_height)).getText().toString() + " m");
        mType.setText(((TextView) view.findViewById(R.id.list_poke_type)).getText().toString());
        mSelf = ((TextView) view.findViewById(R.id.list_poke_self)).getText().toString();
    }

    private void makePutRequest(String url) throws IOException {
        if(mName.getText().toString().length() == 0 || mSelf.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast("A name must be entered and Pokemon must be selected!");
                }
            });
        } else {
            HttpUrl reqUrl = HttpUrl.parse(url + mSelf);
            String json = makeJson();
            if(json.length() > 0) {
                RequestBody body = RequestBody.create(JSON, json);
                Request req = new Request.Builder()
                        .url(reqUrl)
                        .put(body)
                        .build();
                mClient.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String r = response.body().string();
                        getPokemon();
                    }
                });
            }
        }
    }

    private String makeJson() {
        JSONObject obj = new JSONObject();
        try {
            String name = mName.getText().toString();
            String height = mHeight.getText().toString();
            String weight = mWeight.getText().toString();
            String types = mType.getText().toString();
            String typesTrimmed = types.replaceAll("\\s+]","");

            String[] typesArr = typesTrimmed.split(",");

            obj.put("name", name);
            obj.put("weight", Integer.valueOf(weight));
            obj.put("height", Integer.valueOf(height));
            obj.put("type", new JSONArray(typesArr));

        } catch (Exception e) {
            Log.d("KYLEB:::", e.toString());
        }
        return obj.toString();
    }

    private void makeToast(CharSequence toShow) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this.getApplicationContext(), toShow, duration);
        toast.show();
    }
}


