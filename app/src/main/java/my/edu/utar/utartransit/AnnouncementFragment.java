package my.edu.utar.utartransit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utartransit.databinding.FragmentAnnouncementBinding;
import my.edu.utar.utartransit.databinding.FragmentTimetableBinding;


public class AnnouncementFragment extends Fragment {


    private FragmentAnnouncementBinding binding;
    private JSONArray jsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAnnouncementBinding.inflate(inflater, container, false);

        TextView tv = new TextView(requireContext());

        MyThread connectThread = new MyThread();
        connectThread.start();



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }

    private class MyThread extends Thread {
//        private String name;
//        private Handler mHandler;
//
//        public MyThread(String name, Handler handler) {
//            this.name = name;
//            mHandler = handler;
//        }

        public void run() {
            try {
                URL url = new URL("https://slyrebgznitqrqnzoquz.supabase.co/rest/v1/bus_announcement?select=*");

                Log.i("Net", url.toString());

                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                //For Q3 & Q4
                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY));


                InputStream input = hc.getInputStream();

                String result = readStream(input);

                if (hc.getResponseCode() == 200) {
                    //OK
                    //JSONArray
                    jsonArray = new JSONArray(result);
                    Log.i("MainActivity2", "JSON Array Contents:");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });

                    //Q4
                } else if (hc.getResponseCode() == 201) {
                    //New resource is created
                    Log.i("MainActivity2", "Name successfully inserted");
                } else {
                    Log.i("MainActivity2", "Response code = " + hc.getResponseCode());
                }

                input.close();

            } catch (Exception e) {
                Log.e("Net", "Error" + e.getMessage(), e);
            }
        }
    }

    private void updateUI() {
        RecyclerView recyclerView = getView().findViewById(R.id.recycle_view);


        if (jsonArray != null) {
            List<Item> items = new ArrayList<Item>();


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    String announcementTitle = jsonObject.getString("a_title");
                    String date = jsonObject.getString("created_at");
                    String imageURL = jsonObject.getString("a_image_link");
                    items.add(new Item(date,announcementTitle,imageURL));
                    TextView textView = new TextView(requireContext());
                    textView.setText(announcementTitle);
                    //binding.recycleView.addView(textView);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.i("MainActivity2", "Element " + i + ": " + jsonObject.toString());
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(new RVAdapter(requireContext().getApplicationContext(),items));
        }
    }
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new
                    ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


}