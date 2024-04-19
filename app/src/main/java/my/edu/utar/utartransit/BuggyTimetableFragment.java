package my.edu.utar.utartransit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import my.edu.utar.utartransit.databinding.FragmentBuggyTimetableBinding;
import my.edu.utar.utartransit.databinding.FragmentTimetableBinding;

public class BuggyTimetableFragment extends Fragment {

    private FragmentBuggyTimetableBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentBuggyTimetableBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        checkFavourite();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                boolean check = true;
                if (v.getId() == R.id.checkBox6) {
                    Log.i("Debug", "The check1 clicked");

                    if (!cb.isChecked()) {
                        Log.i("Debug", "The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//                    else{
//                        //check=true;
//                    }

                    recordTimetableAsChecked("Timetable 6", check,R.drawable.e_buggy); // Record timetable as checked
                }
            }
        };
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch full-screen view with the clicked image
                int imageResId = 0;
                if(v.getId()==R.id.card_view6) {

                    imageResId = R.drawable.e_buggy;
                }

                if ( imageResId != 0) {
                    showFullScreenImage(imageResId);
                }
            }
        };
        binding.checkBox6.setOnClickListener(onClickListener);
        binding.cardView6.setOnClickListener(cardClickListener);
        return view;
    }

    private void recordTimetableAsChecked(String timetable, boolean checked, int imageId) {
        // Store the checked timetable in SharedPreferences or SQLite database
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (checked) {
            editor.putInt(timetable, imageId);
        } else {
            editor.putInt(timetable, 0);
        }

        editor.apply();
    }

    private void checkFavourite() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);

        if (sharedPreferences.getInt("Timetable 6", 0)!=0) {
            binding.checkBox6.setChecked(true);
        }

    }
    private void showFullScreenImage(int imageResId) {
        Intent intent = new Intent(requireContext(), FullScreenImageActivity.class);
        intent.putExtra("imageResId", imageResId);
        startActivity(intent);
    }
}