package my.edu.utar.utartransit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.GridLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import my.edu.utar.utartransit.databinding.FragmentTimetableBinding;


public class TimetableFragment extends Fragment {
    private FragmentTimetableBinding binding;
    Resources res;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimetableBinding.inflate(inflater,container,false);

        //res.getIdentifier("standford_ck_mahsuri","drawable",getContext().getPackageName());
        View view = binding.getRoot();
        checkFavourite();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                boolean check = true;
                if(v.getId() == R.id.checkBox1){
                    Log.i("Debug","The check1 clicked");

                    if(!cb.isChecked()){
                        Log.i("Debug","The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//                    else{
//                        //check=true;
//                    }

                    recordTimetableAsChecked("Timetable 1",check); // Record timetable as checked

                } else if (v.getId() == R.id.checkBox2) {
                    Log.i("Debug","The check2 clicked");
                    if(!cb.isChecked()){
                        Log.i("Debug","The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//                    else{
//                        check=true;
//                    }

                    recordTimetableAsChecked("Timetable 2",check);
                } else if (v.getId() == R.id.checkBox3) {
                    Log.i("Debug","The check3 clicked");

                    if(!cb.isChecked()){
                        Log.i("Debug","The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//                    else{
//                        check=true;
//                    }

                    recordTimetableAsChecked("Timetable 3",check);
                }else if (v.getId() == R.id.checkBox4) {
                    Log.i("Debug","The check4 clicked");

                    if(!cb.isChecked()){
                        Log.i("Debug","The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//                    else{
//                        check=true;
//                    }

                    recordTimetableAsChecked("Timetable 4",check);
                }else if (v.getId() == R.id.checkBox5) {
                    Log.i("Debug","The check5 clicked");

                    if(!cb.isChecked()){
                        Log.i("Debug","The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//                    else{
//                        check=true;
//                    }

                    recordTimetableAsChecked("Timetable 5",check);
                }
            }
        };
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch full-screen view with the clicked image
                int imageResId = 0;
                if(v.getId()==R.id.card_view1) {

                    imageResId = R.drawable.standford_ck_mahsuri;
                }
                if(v.getId()==R.id.card_view2){
                    imageResId = R.drawable.mahsuri;
                }
                if(v.getId()==R.id.card_view3) {

                    imageResId = R.drawable.havard_cambridge;
                }
                if(v.getId()==R.id.card_view4){
                    imageResId = R.drawable.havard_cambridge_westlake;
                }
                if(v.getId()==R.id.card_view5) {

                    imageResId = R.drawable.westlake_all;
                }

                if ( imageResId != 0) {
                    showFullScreenImage(imageResId);
                }
            }
        };

        binding.cardView1.setOnClickListener(cardClickListener);
        binding.cardView2.setOnClickListener(cardClickListener);
        binding.cardView3.setOnClickListener(cardClickListener);
        binding.cardView4.setOnClickListener(cardClickListener);
        binding.cardView5.setOnClickListener(cardClickListener);
        binding.checkBox1.setOnClickListener(onClickListener);
        binding.checkBox2.setOnClickListener(onClickListener);
        binding.checkBox3.setOnClickListener(onClickListener);
        binding.checkBox4.setOnClickListener(onClickListener);
        binding.checkBox5.setOnClickListener(onClickListener);


        //binding.announcementGridView.setOnClickListener(onClickListener);

        // Inflate the layout for this fragment
        return view;
    }
    //function to check if the item is in the favourite

    private void recordTimetableAsChecked(String timetable, boolean checked) {
        // Store the checked timetable in SharedPreferences or SQLite database
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(checked){
            editor.putBoolean(timetable, true);
        }else{
            editor.putBoolean(timetable,false);
        }

        editor.apply();
    }

    private void checkFavourite(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("Timetable 1",false)){
            binding.checkBox1.setChecked(true);
        }
        if(sharedPreferences.getBoolean("Timetable 2",false)) {
            binding.checkBox2.setChecked(true);
        }
        if(sharedPreferences.getBoolean("Timetable 3",false)) {
            binding.checkBox3.setChecked(true);
        }
        if(sharedPreferences.getBoolean("Timetable 4",false)) {
            binding.checkBox4.setChecked(true);
        }
        if(sharedPreferences.getBoolean("Timetable 5",false)) {
            binding.checkBox5.setChecked(true);
        }

    }
    private void showFullScreenImage(int imageResId) {
        Intent intent = new Intent(requireContext(), FullScreenImageActivity.class);
        intent.putExtra("imageResId", imageResId);
        startActivity(intent);
    }
}