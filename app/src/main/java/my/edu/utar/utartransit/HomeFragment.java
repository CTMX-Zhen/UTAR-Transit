package my.edu.utar.utartransit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import my.edu.utar.utartransit.Adapters.BuildingsAdapter;
import my.edu.utar.utartransit.Domains.itemBuildings;

public class HomeFragment extends Fragment {

    private RecyclerView.Adapter adapterBuilding;
    private RecyclerView recyclerViewBuilding;
    private FragmentManager fragmentManager;
    private ImageView myImageView;

    LinearLayout linearLayout;
    private ImageView busImageView;
    private ImageView buggyImageView;
    private boolean isBusVisible = false;
    private boolean isBuggyVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewBuilding=(RecyclerView) view.findViewById (R.id.RV_build1);

        linearLayout = view.findViewById(R.id.c02);
        busImageView = view.findViewById(R.id.image01);
        buggyImageView = view.findViewById(R.id.image02);

        initRecycleView();

        // Find the CardView
        CardView buggyCardView = view.findViewById(R.id.buggy_cv);
        CardView busCardView = view.findViewById(R.id.bus_cv);


        CardView scooterCardView = view.findViewById(R.id.escooter_cv);

        myImageView = view.findViewById(R.id.announce_icon);

        fragmentManager = getActivity().getSupportFragmentManager(); // Get FragmentManager

        myImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layout, new AnnouncementFragment()); // Replace with your container and target fragment
                transaction.commit();
            }
        });
        //When


        buggyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int randomNumber = random.nextInt(10);
                String message;
                if (randomNumber %3 == 0) {
                    message = "Embrace The Wind!";
                } else if (randomNumber %3 == 1) {
                    message = "Free of Charge!";
                } else {
                    message = "Limited Seat";
                }

                // Create and display Toast message
                Toast toast = Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT);
                toast.show();

                if (isBusVisible) {
                    // Bus is visible, hide it
                    toggleBusImageVisibility();
                }
                // Toggle buggy image visibility
                toggleBuggyImageVisibility();
            }
        });

        busCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int randomNumber = random.nextInt(10);
                String message;
                if (randomNumber % 3 == 0) {
                    message = "Monthly Pass Only RM32 (Original RM35)";
                } else if (randomNumber % 3 == 1) { // Adjust range for 30% chance of "RM1/Trip"
                    message = "RM1/Trip";
                } else {
                    message = "Find All The Available Route Here!";
                }

                // Create and display Toast message
                Toast toast = Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT);
                toast.show();

                if (isBuggyVisible) {
                    // Buggy is visible, hide it
                    toggleBuggyImageVisibility();
                }
                // Toggle bus image visibility
                toggleBusImageVisibility();
            }
        });

        scooterCardView.setOnClickListener(new View.OnClickListener() {
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
        items.add(new itemBuildings("Block M", "", "blockm"));


        recyclerViewBuilding.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adapterBuilding=new BuildingsAdapter(items);
        recyclerViewBuilding.setAdapter(adapterBuilding);
    }

    private void toggleBusImageVisibility() {
        isBusVisible = !isBusVisible;
        if (isBusVisible) {
            busImageView.setVisibility(View.VISIBLE);
            buggyImageView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            busImageView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
    }

    private void toggleBuggyImageVisibility() {
        isBuggyVisible = !isBuggyVisible;
        if (isBuggyVisible) {
            buggyImageView.setVisibility(View.VISIBLE);
            busImageView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            buggyImageView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
    }
}