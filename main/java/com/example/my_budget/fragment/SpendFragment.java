package com.example.my_budget.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.my_budget.R;
import com.example.my_budget.timeFragment.TimeViewpagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class SpendFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private View mView;

    public SpendFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_spend, container, false);

        tabLayout = mView.findViewById(R.id.time_tab_layout);
        viewPager = mView.findViewById(R.id.spend_viewpager);

        TimeViewpagerAdapter timeViewpagerAdapter = new TimeViewpagerAdapter(getActivity());
        viewPager.setAdapter(timeViewpagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Today");
                    break;
                case 1:
                    tab.setText("Week");
                    break;
                case 2:
                    tab.setText("Month");
                    break;
            }
        }).attach();

        return mView;
    }


}