package com.telecom.project4t.testactivity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.telecom.project4t.testactivity.R;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

    FragmentManager fragmentManager;
    List<Fragment> fragmentList = new ArrayList<>();

    public TabsAdapter(FragmentManager manager) {
        super(manager);

        fragmentManager = manager;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackstack) {
        /*if (addToBackstack) {
            fragmentManager.beginTransaction().addToBackStack(null).commit();
        }*/
        fragmentManager.beginTransaction().replace(R.id.viewPager, fragment).commit();
        //fragmentManager.beginTransaction().commit();
    }

}
