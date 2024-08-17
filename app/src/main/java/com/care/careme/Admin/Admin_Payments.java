package com.care.careme.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.care.careme.Helpers.Admin_Payments_Current;
import com.care.careme.Helpers.Admin_Payments_Previous;
import com.care.careme.R;

import java.util.ArrayList;
import java.util.List;

public class Admin_Payments extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_payment);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        Toast.makeText(Admin_Payments.this, "Swipe to mark the payment!", Toast.LENGTH_LONG).show();
        getTabs();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getTabs()
    {
        final MyPagerAdapter fragmentPagerAdapter=new MyPagerAdapter(getSupportFragmentManager());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                fragmentPagerAdapter.addFragment(Admin_Payments_Previous.getInstance(),"Completed");
                fragmentPagerAdapter.addFragment(Admin_Payments_Current.getInstance(),"Upcoming");

                viewPager.setAdapter(fragmentPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    // Custom FragmentPagerAdapter subclass
    private class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
