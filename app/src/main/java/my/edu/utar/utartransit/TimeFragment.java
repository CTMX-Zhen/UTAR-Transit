package my.edu.utar.utartransit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


public class TimeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_time,container,false);
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new TimetableFragment(),"Bus Timetable");
        vpAdapter.addFragment(new BuggyTimetableFragment(), "Buggy Timetable");
        //vpAdapter.addFragment(new AnnouncementFragment(), "Announce");
        viewPager.setAdapter(vpAdapter);

        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_time, container, false);
        return view;
    }
}