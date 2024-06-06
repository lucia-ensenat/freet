package com.lucia.Freet.views;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.lucia.Freet.ui.login.fragment_login_tab;
import com.lucia.Freet.ui.login.fragment_signup_tab;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new fragment_signup_tab();
        }
        return new fragment_login_tab();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}