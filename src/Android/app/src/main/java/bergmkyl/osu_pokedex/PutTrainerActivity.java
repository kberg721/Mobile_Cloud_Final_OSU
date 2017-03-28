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

public class PutTrainerActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "KYLEB::";
    Button editBtn;
    ListView trainerList;
    String mSelf;
    EditText mName;
    EditText mNumBadges;
    EditText mHometown;
    OkHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_trainer);
        mSelf = "";
        mClient = new OkHttpClient();

        mName = (EditText) findViewById(R.id.trainer_name);
        mNumBadges = (EditText) findViewById(R.id.trainer_num_badges);
        mHometown = (EditText) findViewById(R.id.trainer_hometown);

        getTrainers();
        editBtn = (Button) findViewById(R.id.putTrainerBtn);
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

        trainerList = (ListView) findViewById(R.id.trainer_put_list);
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
        mName.setText(((TextView) view.findViewById(R.id.list_trainer_name)).getText().toString());
        mNumBadges.setText(((TextView) view.findViewById(R.id.list_trainer_num_badges)).getText().toString());
        mHometown.setText(((TextView) view.findViewById(R.id.list_trainer_hometown)).getText().toString());
        mSelf = ((TextView) view.findViewById(R.id.list_trainer_self)).getText().toString();
    }

    private void makePutRequest(String url) throws IOException {
        if(mName.getText().toString().length() == 0 || mSelf.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast("A name must be entered and Trainer must be selected!");
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
                        getTrainers();
                    }
                });
            }
        }
    }

    private String makeJson() {
        JSONObject obj = new JSONObject();
        try {
            String name = mName.getText().toString();
            String num_badges = mNumBadges.getText().toString();
            String hometown = mHometown.getText().toString();

            obj.put("name", name);
            obj.put("num_badges", Integer.valueOf(num_badges));
            obj.put("hometown", hometown);

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
