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

public class PostTrainerActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    EditText trainerName;
    EditText trainerNumBadges;
    EditText trainerHometown;

    Button submitBtn;
    OkHttpClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trainer);

        mClient = new OkHttpClient();
        trainerName = (EditText) findViewById(R.id.trainer_name);
        trainerNumBadges = (EditText) findViewById(R.id.trainer_num_badges);
        trainerHometown = (EditText) findViewById(R.id.trainer_hometown);

        submitBtn = (Button) findViewById(R.id.post_trainer_button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postTrainer("https://cs496-final-160904.appspot.com/trainers");
                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }
            }
        });
    }

    private void postTrainer(String url) throws IOException {
        if(trainerName.getText().toString().length() == 0) {
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
                                makeToast("Trainer Successfully Added");
                            }
                        });
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeToast("Error Adding Trainer");
                    }
                });
            }
        }
    }

    private String makeJson() {
        JSONObject obj = new JSONObject();
        try {
            String name = trainerName.getText().toString();
            String num_badges = trainerNumBadges.getText().toString();
            String hometown = trainerHometown.getText().toString();

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