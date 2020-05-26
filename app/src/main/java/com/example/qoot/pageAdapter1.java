package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class pageAdapter1 extends FragmentPagerAdapter {
    private int numberoftabs;


    public pageAdapter1( FragmentManager fm, int numberoftabs) {
        super(fm);
        this.numberoftabs= numberoftabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new tab3();
            case 1:
                return new tab4();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberoftabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}