package bergmkyl.osu_pokedex;

import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;

public class PokemonAdapter extends ArrayAdapter<PokemonModel> {

    public PokemonAdapter(Context context, ArrayList<PokemonModel> pokemon) {
        super(context, 0, pokemon);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PokemonModel model = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pokemon_list_item, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.list_poke_name);
        TextView tvHeight = (TextView) convertView.findViewById(R.id.list_poke_height);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.list_poke_weight);
        TextView tvIsCaptured = (TextView) convertView.findViewById(R.id.list_poke_is_captured);
        TextView tvType = (TextView) convertView.findViewById(R.id.list_poke_type);
        TextView tvSelf = (TextView) convertView.findViewById(R.id.list_poke_self);

        // Populate the data into the template view using the data object
        tvName.setText(model.name);
        tvHeight.setText(Double.toString(model.height));
        tvWeight.setText(Double.toString(model.weight));
        tvIsCaptured.setText(Boolean.toString(model.is_captured));
        tvType.setText(buildString(model.type));
        tvSelf.setText(model.self.toString());


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
