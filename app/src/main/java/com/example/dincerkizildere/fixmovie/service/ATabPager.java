package com.example.dincerkizildere.fixmovie.service;

import com.example.dincerkizildere.fixmovie.function.FavoriteFragment;
import com.example.dincerkizildere.fixmovie.function.NowPlayingFragment;
import com.example.dincerkizildere.fixmovie.function.UpcomingFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ATabPager extends FragmentPagerAdapter {

    private static final int NUM_ITEMS=3;

    public ATabPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NowPlayingFragment();

            case 1:
                return new UpcomingFragment();

            case 2:
                return new FavoriteFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
