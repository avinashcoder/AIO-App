package com.rainbow.aiobrowser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements AppsViewFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView( R.id.tab_layout )
    TabLayout tabLayout;
    @BindView( R.id.view_pager )
    ViewPager viewPager;
    @BindView( R.id.search )
    FrameLayout search;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.headerMenuBtn)
    FrameLayout headerMenu;

    View drawerView;
    LinearLayout navFavourite,navHome,navSocial,navShopping,navEntertainment,navFood,navUtility,navSports,
                navRechange,navNews,navTravel,navHealth,navOthers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        ButterKnife.bind( this );

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem( 1,false );

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
                drawer.getChildAt(0).setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        drawerView = navigationView.getHeaderView( 0 );
        initDrawerView();

        Intent intentData = getIntent();
        if(intentData.hasExtra("URL")){
            Intent i = new Intent(this,WebViewActivity.class);
            i.putExtra("URL",intentData.getExtras().getString("URL"));
            startActivity(i);
        }

    }

    private void initDrawerView() {
        navFavourite = drawerView.findViewById( R.id.nav_favourite );
        navHome = drawerView.findViewById( R.id.nav_home );
        navSocial = drawerView.findViewById( R.id.nav_social );
        navShopping = drawerView.findViewById( R.id.nav_shopping );
        navEntertainment = drawerView.findViewById( R.id.nav_entertainment );
        navFood = drawerView.findViewById( R.id.nav_food );
        navUtility = drawerView.findViewById( R.id.nav_utility );
        navSports = drawerView.findViewById( R.id.nav_sports );
        navRechange = drawerView.findViewById( R.id.nav_recharge );
        navNews = drawerView.findViewById( R.id.nav_news );
        navTravel = drawerView.findViewById( R.id.nav_travel );
        navHealth = drawerView.findViewById( R.id.nav_health );
        navOthers = drawerView.findViewById( R.id.nav_others );

        navFavourite.setOnClickListener( view -> {
            viewPager.setCurrentItem( 0 ,false);
            headerMenuClicked();
        } );

        navHome.setOnClickListener( view -> {
            viewPager.setCurrentItem( 1,false );
            headerMenuClicked();
        } );

        navSocial.setOnClickListener( view -> {
            viewPager.setCurrentItem( 2 ,false);
            headerMenuClicked();
        } );

        navShopping.setOnClickListener( view -> {
            viewPager.setCurrentItem( 3 ,false);
            headerMenuClicked();
        } );

        navEntertainment.setOnClickListener( view -> {
            viewPager.setCurrentItem( 4 ,false);
            headerMenuClicked();
        } );

        navFood.setOnClickListener( view -> {
            viewPager.setCurrentItem( 5 ,false);
            headerMenuClicked();
        } );

        navUtility.setOnClickListener( view -> {
            viewPager.setCurrentItem( 6 ,false);
            headerMenuClicked();
        } );

        navSports.setOnClickListener( view -> {
            viewPager.setCurrentItem( 7 ,false);
            headerMenuClicked();
        } );

        navRechange.setOnClickListener( view -> {
            viewPager.setCurrentItem( 8 ,false);
            headerMenuClicked();
        } );

        navNews.setOnClickListener( view -> {
            viewPager.setCurrentItem( 9 ,false);
            headerMenuClicked();
        } );

        navTravel.setOnClickListener( view -> {
            viewPager.setCurrentItem( 10 ,false);
            headerMenuClicked();
        } );

        navHealth.setOnClickListener( view -> {
            viewPager.setCurrentItem( 11 ,false);
            headerMenuClicked();
        } );

        navOthers.setOnClickListener( view -> {
            viewPager.setCurrentItem( 12 ,false);
            headerMenuClicked();
        } );
    }

    @OnClick(R.id.headerMenuBtn)
    void headerMenuClicked(){
        if (drawer.isDrawerOpen( GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            drawer.openDrawer( GravityCompat.START );
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
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
                return AppsViewFragment.newInstance( "HOST","ENTERTAINMENT");
            }else if (position == 5) {
                return AppsViewFragment.newInstance( "HOST","FOOD");
            }else if (position == 6) {
                return AppsViewFragment.newInstance( "HOST","UTILITY");
            }else if (position == 7) {
                return AppsViewFragment.newInstance( "HOST","SPORTS");
            }else if (position == 8) {
                return AppsViewFragment.newInstance( "HOST","RECHARGE");
            }else if (position == 9) {
                return AppsViewFragment.newInstance( "HOST","NEWS");
            }else if (position == 10) {
                return AppsViewFragment.newInstance( "HOST","TRAVEL");
            }else if (position == 11) {
                return AppsViewFragment.newInstance( "HOST","HEALTH");
            }else if (position == 12) {
                return AppsViewFragment.newInstance( "HOST","OTHERS");
            }
            else
                return AppsViewFragment.newInstance( "HOST","ALL");
        }

        @Override
        public int getCount() {
            return 13;
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
                title = "ENTERTAINMENT";
            else if(position == 5)
                title = "FOOD";
            else if(position == 6)
                title = "UTILITY";
            else if(position == 7)
                title = "SPORTS";
            else if(position == 8)
                title = "RECHARGE";
            else if(position == 9)
                title = "NEWS";
            else if(position == 10)
                title = "TRAVEL";
            else if(position == 11)
                title = "HEALTH";
            else if(position == 12)
                title = "OTHERS";

            return title;
        }
    }
    @OnClick(R.id.search)
    void searchKeyword(){
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    /*
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

     */

    @Override
    protected void onPause() {
        if(drawer.isDrawerOpen( GravityCompat.START ))
            drawer.closeDrawer( GravityCompat.START );
        super.onPause();
    }
}
