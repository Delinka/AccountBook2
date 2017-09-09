package com.example.delinka.accountbook2;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.print.PrintHelper;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public DrawerLayout drawerLayout;
    private Button nav_button;
    private BottomNavigationView bottomNavigationView;
    private long exittime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        Toolbar toolbar_account_msg = (Toolbar) findViewById(R.id.toolbar_account_messege);
        setSupportActionBar(toolbar_account_msg);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_button = (Button) findViewById(R.id.nav_button);
        nav_button.setOnClickListener(this);

        replaceFragment(new FragmentAccount());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottomMenu_AccountMessege:
                        replaceFragment(new FragmentAccount());
                        return true;
                    case R.id.bottomMenu_PayPlan:
                        replaceFragment(new FragementPayplan());
                        return true;
                    case R.id.bottomMenu_PayCharts:
                        replaceFragment(new FragmentPaycharts());
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.nav_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }

    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(findViewById(R.id.navigationView))){
            drawerLayout.closeDrawers();
        } else {
            if (System.currentTimeMillis() - exittime < 1000) {
                super.onBackPressed();
            } else {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exittime = System.currentTimeMillis();
            }
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_Layout, fragment);
        transaction.commit();
    }
}
