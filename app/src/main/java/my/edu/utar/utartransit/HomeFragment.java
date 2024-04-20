package my.edu.utar.utartransit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.utar.utartransit.Adapters.BuildingsAdapter;
import my.edu.utar.utartransit.Domains.itemBuildings;

public class HomeFragment extends Fragment {

    private RecyclerView.Adapter adapterBuilding;
    private RecyclerView recyclerViewBuilding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewBuilding=(RecyclerView) view.findViewById (R.id.RV_build1);

        initRecycleView();

        // Find the CardView
        CardView busCardView = view.findViewById(R.id.bus_cv);
        // ... rest of your code
        CardView ScooterCardView = view.findViewById(R.id.escooter_cv);


        //When
        ScooterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to launch BeamPage activity
                Intent intent = new Intent(getActivity(), BeamPage.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;//inflater.inflate(R.layout.fragment_home, container, false);





    }

    private void initRecycleView(){
        ArrayList<itemBuildings> items = new ArrayList<>();
        items.add(new itemBuildings("Block M", "", "blockm"));
        items.add(new itemBuildings("Block N", "", "blockn"));
        items.add(new itemBuildings("Block L", "", "blockl"));
        items.add(new itemBuildings("Block A", "", "blocka"));
        items.add(new itemBuildings("Block B", "", "blockb"));
        items.add(new itemBuildings("Block C", "", "blockc"));
        items.add(new itemBuildings("Block E", "", "blocke"));
        items.add(new itemBuildings("Block F", "", "blockf"));
        items.add(new itemBuildings("Block G", "", "blockg"));
        items.add(new itemBuildings("Block H", "", "blockh"));
        items.add(new itemBuildings("Block I", "", "blocki"));
        items.add(new itemBuildings("Block J", "", "blockj"));
        items.add(new itemBuildings("Block K", "", "blockk"));


        recyclerViewBuilding.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adapterBuilding=new BuildingsAdapter(items);
        recyclerViewBuilding.setAdapter(adapterBuilding);


    }
}