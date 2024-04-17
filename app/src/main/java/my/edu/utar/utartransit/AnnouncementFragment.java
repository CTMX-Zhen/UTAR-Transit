package my.edu.utar.utartransit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AnnouncementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }

    private class MyThread extends Thread {
        private String name;
        private Handler mHandler;

        public MyThread(String name, Handler handler) {
            this.name = name;
            mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://slyrebgznitqrqnzoquz.supabase.co/rest/v1/bus_route?route_id=eq." + name);

                Log.i("Net", url.toString());

                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                //For Q3 & Q4
                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY));

                //Q4 To set request method and some additional properties
//                hc.setRequestMethod("POST");
//                hc.setRequestProperty("Content-Type", "application/json");
//                hc.setRequestProperty("Prefer", "return=minimal");

                //For HTTP POST
//                hc.setDoOutput(true);
//                OutputStream output = hc.getOutputStream();
//                output.write(jsonObject.toString().getBytes());
//                output.flush();

                InputStream input = hc.getInputStream();


                if (hc.getResponseCode() == 200) {
                    //OK


                    //Q4
                } else if (hc.getResponseCode() == 201) {
                    //New resource is created
                    Log.i("MainActivity2", "Name successfully inserted");
                } else {
                    Log.i("MainActivity2", "Response code = " + hc.getResponseCode());
                }

                input.close();
                //For HTTP POST
//                output.close();
            } catch (Exception e) {
                Log.e("Net", "Error" + e.getMessage(), e);
            }
        }
    }

}