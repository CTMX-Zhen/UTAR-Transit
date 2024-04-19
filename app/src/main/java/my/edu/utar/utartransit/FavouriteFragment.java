package my.edu.utar.utartransit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        checkFavourite();
        return view;
    }

    private void checkFavourite(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CheckedTimetables", Context.MODE_PRIVATE);
        SharedPreferences sharedTimetable = getActivity().getSharedPreferences("TimetableName", Context.MODE_PRIVATE);

        // Clear any existing timetable views
        //binding.favLinearLayout1.removeAllViews();
        //binding.favLinearLayout2.removeAllViews();

        int amount = 1;
        for(int i = 1; i<=6; i++){

            String key = "Timetable " + i;
            if (sharedPreferences.contains(key)) {
                int value = sharedPreferences.getInt(key, 0); // Provide a default value if key is not found
                if(value!=0){

                    String timetableName = sharedTimetable.getString(key,null);
                    switch (amount){
                        case 1:{
                            binding.cardView1.setVisibility(View.VISIBLE);
                            binding.imageView1.setImageResource(value);
                            binding.textView1.setText(timetableName);
                        }
                        break;
                        case 2:{
                            binding.cardView2.setVisibility(View.VISIBLE);
                            binding.imageView2.setImageResource(value);
                            binding.textView2.setText(timetableName);
                        }
                        break;
                        case 3:{
                            binding.cardView3.setVisibility(View.VISIBLE);
                            binding.imageView3.setImageResource(value);
                            binding.textView3.setText(timetableName);
                        }
                        break;
                        case 4:{
                            binding.cardView4.setVisibility(View.VISIBLE);
                            binding.imageView4.setImageResource(value);
                            binding.textView4.setText(timetableName);
                        }
                        break;
                        case 5:{
                            binding.cardView5.setVisibility(View.VISIBLE);
                            binding.imageView5.setImageResource(value);
                            binding.textView5.setText(timetableName);
                        }
                        break;
                        case 6:{
                            binding.cardView6.setVisibility(View.VISIBLE);
                            binding.imageView6.setImageResource(value);
                            binding.textView6.setText(timetableName);
                        }
                        break;
                        default:return;
                    }

                    amount++;
//                    // Create a new CardView
//                    CardView cardView = new CardView(requireContext());
//                    cardView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
//                    cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
//                    cardView.setCardElevation(getResources().getDimension(R.dimen.card_elevation));
//                    cardView.setUseCompatPadding(true);
//
//                    // Create a new LinearLayout for the CardView content
//                    LinearLayout linearLayout = new LinearLayout(requireContext());
//                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    linearLayout.setOrientation(LinearLayout.VERTICAL);
//                    linearLayout.setPadding(getResources().getDimensionPixelSize(R.dimen.card_content_padding), getResources().getDimensionPixelSize(R.dimen.card_content_padding), getResources().getDimensionPixelSize(R.dimen.card_content_padding), getResources().getDimensionPixelSize(R.dimen.card_content_padding));
//
//                    ImageView imageView = new ImageView(requireContext());
//                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    imageView.setImageResource(value);
//
//                    // Create and set timetable name TextView
//                    TextView textView = new TextView(requireContext());
//                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    textView.setText(timetableName);
//
//                    linearLayout.addView(imageView);
//                    // Add the TextView to the LinearLayout
//                    linearLayout.addView(textView);
//
//                    // Add the LinearLayout to the CardView
//                    cardView.addView(linearLayout);
//
//                    // Add the CardView to the appropriate LinearLayout
//                    if (i % 2 == 1) {
//                        binding.favLinearLayout1.addView(cardView);
//                    } else {
//                        binding.favLinearLayout2.addView(cardView);
//                    }
                }
            }
        }
    }

}