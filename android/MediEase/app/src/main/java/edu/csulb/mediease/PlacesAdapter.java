package edu.csulb.mediease;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>{

    private List<Place> placeList;

    PlacesAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
        return new PlacesAdapter.PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {
        holder.txtName.setText(placeList.get(position).getHname());
        holder.txtAddr.setText(placeList.get(position).getAddr());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName,txtAddr;

        PlacesViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.textViewPlace);
            txtAddr = (TextView) itemView.findViewById(R.id.textViewAddr);
        }
    }

}
