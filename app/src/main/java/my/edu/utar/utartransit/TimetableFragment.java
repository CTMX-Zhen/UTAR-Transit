package my.edu.utar.utartransit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        binding.imageView1.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.stanford_ck_mahsuri_n, 100, 100));

        binding.imageView2.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.mahsuri1, 100, 100));
        binding.imageView3.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.havard_cambridge_n, 100, 100));
        binding.imageView4.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.havard_cambridge1, 100, 100));
        binding.imageView5.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.westlake_all1, 100, 100));
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
                    recordTimetableAsChecked("Timetable 1",check, R.drawable.stanford_ck_mahsuri_n,"standford_ck_mahsuri"); // Record timetable as checked

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

                    recordTimetableAsChecked("Timetable 2",check,R.drawable.mahsuri1,"mahsuri");
                } else if (v.getId() == R.id.checkBox3) {
                    Log.i("Debug","The check3 clicked");

                    if(!cb.isChecked()){
                        Log.i("Debug","The favourite has been deleted");
                        //recordTimetableAsChecked("Timetable 1",false); // Record timetable as checked
                        //update database
                        check = false;
                    }
//
                    recordTimetableAsChecked("Timetable 3",check,R.drawable.havard_cambridge,"havard_cambridge");
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

                    recordTimetableAsChecked("Timetable 4",check,R.drawable.havard_cambridge_westlake,"havard_cambridge_westlake");
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

                    recordTimetableAsChecked("Timetable 5",check,R.drawable.westlake_all1,"westlake_all");
                }
            }
        };
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch full-screen view with the clicked image
                int imageResId = 0;
                if(v.getId()==R.id.card_view1) {

                    imageResId = R.drawable.stanford_ck_mahsuri_n;
                }
                if(v.getId()==R.id.card_view2){
                    imageResId = R.drawable.mahsuri1;
                }
                if(v.getId()==R.id.card_view3) {

                    imageResId = R.drawable.havard_cambridge_n;
                }
                if(v.getId()==R.id.card_view4){
                    imageResId = R.drawable.havard_cambridge1;
                }
                if(v.getId()==R.id.card_view5) {

                    imageResId = R.drawable.westlake_all1;
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

    private void recordTimetableAsChecked(String timetable, boolean checked, int imageId,String imageName) {
        // Store the checked timetable in SharedPreferences or SQLite database
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        SharedPreferences sharedTimetable = getActivity().getSharedPreferences("TimetableName", Context.MODE_PRIVATE);

        SharedPreferences.Editor editName = sharedTimetable.edit();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(checked){
            //editor.putBoolean(timetable, true);
            editName.putString(timetable,imageName);
            editor.putInt(timetable,imageId);
        }else{
            //editor.putBoolean(timetable,false);
            editName.putString(timetable,imageName);
            editor.putInt(timetable,0);
        }

        editor.apply();
        editName.apply();
    }

    private void checkFavourite(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        if(sharedPreferences.getInt("Timetable 1",0)!=0){
            binding.checkBox1.setChecked(true);
        }
        if(sharedPreferences.getInt("Timetable 2",0)!=0) {
            binding.checkBox2.setChecked(true);
        }
        if(sharedPreferences.getInt("Timetable 3",0)!=0) {
            binding.checkBox3.setChecked(true);
        }
        if(sharedPreferences.getInt("Timetable 4",0)!=0) {
            binding.checkBox4.setChecked(true);
        }
        if(sharedPreferences.getInt("Timetable 5",0)!=0) {
            binding.checkBox5.setChecked(true);
        }

    }
    private void showFullScreenImage(int imageResId) {
        Intent intent = new Intent(requireContext(), FullScreenImageActivity.class);
        intent.putExtra("imageResId", imageResId);
        startActivity(intent);
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
