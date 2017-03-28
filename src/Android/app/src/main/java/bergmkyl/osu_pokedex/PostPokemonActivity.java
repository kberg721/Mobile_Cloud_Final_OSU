package bergmkyl.osu_pokedex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostPokemonActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    EditText pokemonName;
    EditText pokemonHeight;
    EditText pokemonWeight;
    EditText pokemonTypes;

    Button submitBtn;
    OkHttpClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_pokemon);

        mClient = new OkHttpClient();
        pokemonName = (EditText) findViewById(R.id.pokemon_name);
        pokemonHeight = (EditText) findViewById(R.id.pokemon_height);
        pokemonWeight = (EditText) findViewById(R.id.pokemon_weight);
        pokemonTypes = (EditText) findViewById(R.id.pokemon_types);

        submitBtn = (Button) findViewById(R.id.postPokemonBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postPokemon("https://cs496-final-160904.appspot.com/pokemon");
                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }
            }
        });
    }

    private void postPokemon(String url) throws IOException {
        if(pokemonName.getText().toString().length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast("A name must be entered!");
                }
            });
        } else {
            HttpUrl reqUrl = HttpUrl.parse(url);
            String json = makeJson();
            if(json.length() > 0) {
                RequestBody body = RequestBody.create(JSON, json);
                Request req = new Request.Builder()
                        .url(reqUrl)
                        .post(body)
                        .build();
                mClient.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String r = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeToast("Pokemon Successfully Added");
                            }
                        });
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeToast("Error Adding Pokemon");
                    }
                });
            }
        }
    }

    private String makeJson() {
        JSONObject obj = new JSONObject();
        try {
            String name = pokemonName.getText().toString();
            String height = pokemonHeight.getText().toString();
            String weight = pokemonWeight.getText().toString();
            String types = pokemonTypes.getText().toString();
            String typesTrimmed = types.replaceAll("\\s+","");

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
