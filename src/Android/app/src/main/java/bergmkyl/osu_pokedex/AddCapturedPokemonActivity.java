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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddCapturedPokemonActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "KYLEB::";
    Button submitBtn;
    ListView pokeList;
    ListView trainerList;
    String mTrainerSelf;
    String mPokemonSelf;

    TextView mTrainer;
    TextView mPokemon;
    OkHttpClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_captured_pokemon);
        mTrainerSelf = "";
        mPokemonSelf = "";
        mClient = new OkHttpClient();
        mTrainer = (TextView) findViewById(R.id.trainer_capture_tv);
        mPokemon = (TextView) findViewById(R.id.pokemon_capture_tv);


        getPokemon();
        getTrainers();
        submitBtn = (Button) findViewById(R.id.addCapturedPokemonBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makePutRequest("https://cs496-final-160904.appspot.com");
                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }
            }
        });

        pokeList = (ListView) findViewById(R.id.pokemon_list);
        pokeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populatePokemonField(view);
            }
        });
        trainerList = (ListView) findViewById(R.id.trainer_list);
        trainerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populateTrainerField(view);
            }
        });
    }

    private void getPokemon() {
        HttpUrl reqUrl = HttpUrl.parse("https://cs496-final-160904.appspot.com/pokemon?is_captured=false");
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
                populatePokemonListView(r);
            }
        });
    }

    private void getTrainers() {
        HttpUrl reqUrl = HttpUrl.parse("https://cs496-final-160904.appspot.com/trainers");
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
                populateTrainerListView(r);
            }
        });
    }

    private void populatePokemonListView(String json) {
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

    private void populateTrainerListView(String json) {
        try {
            JSONArray arr = new JSONArray(json);
            ArrayList<TrainerModel> newTrainer = TrainerModel.fromJson(arr);
            final TrainerAdapter adapter = new TrainerAdapter(this, newTrainer);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    trainerList.setAdapter(adapter);
                }
            });

        } catch (Exception e) {
            Log.d("KYLEB::", e.toString());
        }

    }

    private void populatePokemonField(View view) {
        mPokemon.setText(((TextView) view.findViewById(R.id.list_poke_name)).getText().toString());
        mPokemonSelf = ((TextView) view.findViewById(R.id.list_poke_self)).getText().toString();
    }

    private void populateTrainerField(View view) {
        mTrainer.setText(((TextView) view.findViewById(R.id.list_trainer_name)).getText().toString());
        mTrainerSelf = ((TextView) view.findViewById(R.id.list_trainer_self)).getText().toString();
    }

    private void makePutRequest(String url) throws IOException {
        if(mPokemon.getText().toString().length() == 0
                || mTrainer.getText().toString().length() == 0
                || mTrainerSelf.length() == 0
                || mPokemonSelf.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast("A trainer and pokemon must be selected!");
                }
            });
        } else {
            HttpUrl reqUrl = HttpUrl.parse(url + mTrainerSelf + mPokemonSelf);
            RequestBody body = RequestBody.create(JSON, "{}");
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
                    getTrainers();
                }
            });
        }
    }


    private void makeToast(CharSequence toShow) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this.getApplicationContext(), toShow, duration);
        toast.show();
    }
}
