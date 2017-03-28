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
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeletePokemonActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "KYLEB::";
    TextView mPokemonName;
    Button deleteBtn;
    ListView pokeList;
    String mSelf;

    OkHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pokemon);
        mSelf = "";
        mClient = new OkHttpClient();

        mPokemonName = (TextView) findViewById(R.id.pokemon_name);

        getPokemon();
        deleteBtn = (Button) findViewById(R.id.delete_pokemon_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeDeleteRequest("https://cs496-final-160904.appspot.com/");
                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }
            }
        });

        pokeList = (ListView) findViewById(R.id.pokemon_delete_list);
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
        mPokemonName.setText(((TextView) view.findViewById(R.id.list_poke_name)).getText().toString());
        mSelf = ((TextView) view.findViewById(R.id.list_poke_self)).getText().toString();
    }

    private void makeDeleteRequest(String url) throws IOException {
        if(mPokemonName.getText().toString().length() == 0 || mSelf.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast("A Pokemon must be selected!");
                }
            });
        } else {
            HttpUrl reqUrl = HttpUrl.parse(url + mSelf);
            Request req = new Request.Builder()
                    .url(reqUrl)
                    .delete()
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeToast("Pokemon Successfully Deleted");
                        }
                    });

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
