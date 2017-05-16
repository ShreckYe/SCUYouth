/*
 * Copyright 2017 Yongshun Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shreckye.scu.youth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    BottomNavigationView bottomNavigationView;
    ViewPager contentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (!toggleViewWithNavResId(id))
                    switch (id) {
                        case R.id.nav_about:
                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            break;
                    }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                toggleViewWithNavResId(item.getItemId());
                return true;
            }
        });

        contentPager = (ViewPager) findViewById(R.id.content_pager);
        contentPager.setAdapter(new ContentPagerAdapter(getSupportFragmentManager()));
        contentPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int navResId = positionToNavResId(position);
                navigationView.setCheckedItem(navResId);

                /*Menu menu = bottomNavigationView.getMenu();
                int size = menu.size();
                for (int i = 0; i < size; i++)
                    menu.getItem(i).setChecked(false);
                bottomNavigationView.getMenu().findItem(navResId).setChecked(true);*/
                // TODO: This is a temporary solution since the API is not complete
                bottomNavigationView.findViewById(navResId).callOnClick();

                setTitle(bottomNavigationView.getMenu().findItem(navResId).getTitle());
            }
        });

        toggleView(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void toggleView(int position) {
        contentPager.setCurrentItem(position);
    }

    private boolean toggleViewWithNavResId(int navResId) {
        int position = navIdToPosition(navResId);
        if (position != -1) {
            toggleView(position);
            return true;
        } else
            return false;
    }

    static int navIdToPosition(int navResId) {
        switch (navResId) {
            case R.id.nav_homepage:
                return 0;
            case R.id.nav_news:
                return 1;
            case R.id.nav_notices:
                return 2;
            case R.id.nav_activities:
                return 3;
            case R.id.nav_youth:
                return 4;
            default:
                return -1;
        }
    }

    static int positionToNavResId(int position) {
        switch (position) {
            case 0:
                return R.id.nav_homepage;
            case 1:
                return R.id.nav_news;
            case 2:
                return R.id.nav_notices;
            case 3:
                return R.id.nav_activities;
            case 4:
                return R.id.nav_youth;
            default:
                return -1;
        }
    }
}
