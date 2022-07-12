package com.example.my_budget.analyzeFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AnalyzeViewpagerAdapter extends FragmentStateAdapter {
    public AnalyzeViewpagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new TodayAnalyze();
            case 1: return new WeekAnalyze();
            case 2: return new MonthAnalyze();
            default: return new TodayAnalyze();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
