package com.example.my_budget.spendFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TimeViewpagerAdapter extends FragmentStateAdapter {

    public TimeViewpagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Today();
            case 1: return new Week();
            case 2: return new Month();
            default:return new Today();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
