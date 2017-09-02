package edu.csulb.mediease;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private PlacesAdapter adapter;
    private RecyclerView recyclerView;
    private List<Place> placeList;

    public HomeFragment() {
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPlaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", placeList.get(position).getLat());
                bundle.putDouble("lng", placeList.get(position).getLng());
                bundle.putString("hname", placeList.get(position).getHname());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
    }

    public void loadList(List<Place> places) {
        recyclerView.setVisibility(View.VISIBLE);
        placeList = new ArrayList<>();
        placeList = places;
        System.out.println("places list count = " + placeList.size());
        adapter = new PlacesAdapter(placeList);
        recyclerView.setAdapter(adapter);
    }/*public void loadList(List<Place> places) {
        recyclerView.setVisibility(View.VISIBLE);
        placeList = places;
        adapter = new PlacesAdapter(placeList);
    }*/

}
