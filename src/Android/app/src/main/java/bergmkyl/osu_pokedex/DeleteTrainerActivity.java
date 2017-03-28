package bergmkyl.osu_pokedex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteTrainerActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "KYLEB::";
    TextView mTrainerName;
    Button deleteBtn;
    ListView trainerList;
    String mSelf;

    OkHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_trainer);
        mSelf = "";
        mClient = new OkHttpClient();

        mTrainerName = (TextView) findViewById(R.id.trainer_name);

        getTrainers();
        deleteBtn = (Button) findViewById(R.id.delete_trainer_btn);
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

        trainerList = (ListView) findViewById(R.id.trainer_delete_list);
        trainerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populateFields(view);
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
                populateListView(r);
            }
        });
    }

    private void populateListView(String json) {
        try {
            JSONArray arr = new JSONArray(json);
            ArrayList<TrainerModel> newTrainer = TrainerModel.fromJson(arr);
            Log.d(TAG, newTrainer.toString());
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

    private void populateFields(View view) {
        mTrainerName.setText(((TextView) view.findViewById(R.id.list_trainer_name)).getText().toString());
        mSelf = ((TextView) view.findViewById(R.id.list_trainer_self)).getText().toString();
    }

    private void makeDeleteRequest(String url) throws IOException {
        if(mTrainerName.getText().toString().length() == 0 || mSelf.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast("A Trainer must be selected!");
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
                    getTrainers();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeToast("Trainer Successfully Deleted");
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
