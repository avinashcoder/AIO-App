package com.rainbow.aiobrowser;

import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements AppsViewFragment.OnFragmentInteractionListener {

    @BindView( R.id.tab_layout )
    TabLayout tabLayout;
    @BindView( R.id.view_pager )
    ViewPager viewPager;
    @BindView( R.id.search )
    FrameLayout search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        ButterKnife.bind( this );

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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
        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position ==0){
                return AppsViewFragment.newInstance( "FAVOURITE","ALL");
            }else if (position == 1)
                return AppsViewFragment.newInstance( "HOST","ALL");
            else if (position == 2) {
                return AppsViewFragment.newInstance( "HOST","SOCIAL");
            } else if (position == 3)
                return AppsViewFragment.newInstance( "HOST","SHOPPING");
            else if (position == 4) {
                return AppsViewFragment.newInstance( "HOST","TRAVEL");
            }
            else
                return AppsViewFragment.newInstance( "HOST","ALL");
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
                title = "FAVOURITE";
            else if (position == 1)
                title = "HOME";
            else if (position == 2)
                title = "SOCIAL";
            else if (position == 3)
                title = "SHOPPING";
            else if(position == 4)
                title = "TRAVEL";

            return title;
        }
    }
}
