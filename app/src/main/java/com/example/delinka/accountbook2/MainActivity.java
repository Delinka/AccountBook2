package com.example.delinka.accountbook2;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.print.PrintHelper;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public DrawerLayout drawerLayout;
    private Button nav_button;
    private BottomNavigationView bottomNavigationView;
    private long exittime = 0;

    TextView textView_account_thismonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");
        ClothStatusBar();

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

    private void ClothStatusBar() {
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.nav_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    public void ClickAddButton(View v){
        switch (v.getId()){
            case R.id.button_addOutcome:
                textView_account_thismonth = (TextView) findViewById(R.id.textView_amount_thismonth);
                textView_account_thismonth.setText("99.9");
                break;
            case R.id.button_addIncome:
                Toast.makeText(MainActivity.this, "222", Toast.LENGTH_SHORT).show();
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
