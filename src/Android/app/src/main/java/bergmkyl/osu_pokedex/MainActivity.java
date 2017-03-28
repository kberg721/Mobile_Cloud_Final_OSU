package bergmkyl.osu_pokedex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    Button postPokemon;
    Button putPokemon;
    Button deletePokemon;
    Button postTrainer;
    Button putTrainer;
    Button deleteTrainer;
    Button releasePokemon;
    Button capturePokemon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
    }

    private void initButtons() {
        postPokemon = (Button) findViewById(R.id.add_pokemon_btn);
        postPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostPokemonActivity.class);
                startActivity(intent);
            }
        });

        putPokemon = (Button) findViewById(R.id.edit_pokemon_btn);
        putPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PutPokemonActivity.class);
                startActivity(intent);
            }
        });
        postTrainer = (Button) findViewById(R.id.add_trainer_btn);
        postTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostTrainerActivity.class);
                startActivity(intent);
            }
        });

        putTrainer = (Button) findViewById(R.id.edit_trainer_btn);
        putTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PutTrainerActivity.class);
                startActivity(intent);
            }
        });
        deletePokemon = (Button) findViewById(R.id.delete_pokemon_btn);
        deletePokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeletePokemonActivity.class);
                startActivity(intent);
            }
        });

        deleteTrainer = (Button) findViewById(R.id.delete_trainer_btn);
        deleteTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeleteTrainerActivity.class);
                startActivity(intent);
            }
        });
        capturePokemon = (Button) findViewById(R.id.capture_pokemon_btn);
        capturePokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCapturedPokemonActivity.class);
                startActivity(intent);
            }
        });

        releasePokemon = (Button) findViewById(R.id.release_pokemon_btn);
        releasePokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReleasePokemonActivity.class);
                startActivity(intent);
            }
        });
    }
}

