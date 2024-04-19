package my.edu.utar.utartransit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utartransit.databinding.FragmentFavouriteBinding;

public class FavouriteFragment extends Fragment {
    FragmentFavouriteBinding binding;
    List<Timetable> timetables= new ArrayList<Timetable>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment

        return view;
    }

    private void checkFavourite(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        SharedPreferences sharedTimetable = getActivity().getSharedPreferences("TimetableName", Context.MODE_PRIVATE);

        for(int i = 1; i<=5; i++){
            String key = "Timetable " + i;
            if (sharedPreferences.contains(key)) {
                int value = sharedPreferences.getInt(key, 0); // Provide a default value if key is not found
                if(value!=0){
                    String timetableName = sharedTimetable.getString(key,null);

                    timetables.add(new Timetable(timetableName,value));
                }
            }
        }

        if(!timetables.isEmpty()){

        }

    }
}