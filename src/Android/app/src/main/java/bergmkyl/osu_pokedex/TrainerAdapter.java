package bergmkyl.osu_pokedex;

import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;

public class TrainerAdapter extends ArrayAdapter<TrainerModel> {

    public TrainerAdapter(Context context, ArrayList<TrainerModel> trainers) {
        super(context, 0, trainers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TrainerModel model = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trainer_list_item, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.list_trainer_name);
        TextView tvHometown = (TextView) convertView.findViewById(R.id.list_trainer_hometown);
        TextView tvNumBadges = (TextView) convertView.findViewById(R.id.list_trainer_num_badges);
        TextView tvSelf = (TextView) convertView.findViewById(R.id.list_trainer_self);
        TextView tvPokemonCaught = (TextView) convertView.findViewById(R.id.list_captured_pokemon);


        // Populate the data into the template view using the data object
        tvName.setText(model.name);
        tvHometown.setText(model.hometown);
        tvNumBadges.setText(Integer.toString(model.num_badges));
        tvSelf.setText(model.self);
        tvPokemonCaught.setText(buildString(model.pokemon_caught));


        // Return the completed view to render on screen
        return convertView;
    }

    private String buildString(ArrayList<String> list) {
        StringBuilder result = new StringBuilder();
        for(String item : list) {
            result.append(item + ", ");
        }
        String toReturn = result.toString();
        if(toReturn.length() > 2) {
            return toReturn.substring(0, result.length()-2);
        } else {
            return toReturn;
        }
    }
}
