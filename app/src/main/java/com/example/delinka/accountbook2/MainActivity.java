package com.example.delinka.accountbook2;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public DrawerLayout drawerLayout;
    private Button nav_button;
    private BottomNavigationView bottomNavigationView;
    private long exittime = 0;

    private FragmentAccount fragmentAccount;
    private FragementPayplan fragmentPayplan;
    private FragmentPaycharts fragmentPaycharts;


    private TextView textView_account_thismonth;
    private float Value_account_thismonth = 0;
    private TextView textView_account_total;
    private float Value_account_total = 0;
    private EditText editText_input;
    private String[] Array_outcomeType = {"伙食", "约会", "网购", "其它"};
    private float[] Array_outcomeValue = {1, 1, 1, 1};
    private float temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setContentView(R.layout.activity_main);
        setTitle("");
        ClothStatusBar();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_button = (Button) findViewById(R.id.nav_button);
        nav_button.setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.item_remove_thismonth:
                        Value_account_thismonth = 0;
                        resetAmount();
                        break;
                    case R.id.item_remove_totol:
                        Value_account_thismonth = 0;
                        Value_account_total = 0;
                        resetAmount();
                        break;
                }
                drawerLayout.closeDrawers();
                Toast.makeText(MainActivity.this, "重置完成", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        showAccount();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        setBottomNavigationViewOnClick();

    }

    private void setBottomNavigationViewOnClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottomMenu_AccountMessege:
                        showAccount();
                        return true;
                    case R.id.bottomMenu_PayPlan:
                        showPayplan();
                        return true;
                    case R.id.bottomMenu_PayCharts:
                        showPaycharts();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        resetAmount();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        saveData();
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putFloat("月余额", Value_account_thismonth);
        editor.putFloat("总余额", Value_account_total);
        editor.putFloat("伙食", Array_outcomeValue[0]);
        editor.putFloat("约会", Array_outcomeValue[1]);
        editor.putFloat("网购", Array_outcomeValue[2]);
        editor.putFloat("其它", Array_outcomeValue[3]);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        Value_account_thismonth = pref.getFloat("月余额", 0);
        Value_account_total = pref.getFloat("总余额", 0);
        Array_outcomeValue[0] = pref.getFloat("伙食", 0);
        Array_outcomeValue[1] = pref.getFloat("约会", 0);
        Array_outcomeValue[2] = pref.getFloat("网购", 0);
        Array_outcomeValue[3] = pref.getFloat("其它", 0);
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

    public void MyOnClick(View v){
        switch (v.getId()){
            case R.id.button_addOutcome:
                addOutcome();
                break;
            case R.id.button_addIncome:
                addIncome();
                break;
            case R.id.layout_thismonth:
                Toast.makeText(MainActivity.this, "「查看本月支出记录列表」还未实现", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_total:
                Toast.makeText(MainActivity.this, "「查看所有支出记录列表」还未实现", Toast.LENGTH_SHORT).show();
                break;
            case R.id.title_month:
                Toast.makeText(MainActivity.this, "「显示月历式选择菜单」还未实现", Toast.LENGTH_SHORT).show();
                break;
            case R.id.outcome_msg:
                Toast.makeText(MainActivity.this, "「查看记录详情」还未实现", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void resetAmount() {
        textView_account_thismonth = (TextView) findViewById(R.id.textView_amount_thismonth);
        textView_account_total = (TextView) findViewById(R.id.textView_amount_total);

        textView_account_thismonth.setText("" + Value_account_thismonth);
        textView_account_total.setText("" + Value_account_total);

    }

    private void addIncome() {
        temp = 0;
        AlertDialog.Builder builder_income = new AlertDialog.Builder(MainActivity.this);
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_amount_edittext, null);

        builder_income.setView(layout_amount_edittext);
        builder_income.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_input = (EditText)  layout_amount_edittext.findViewById(R.id.editText_input);
                String text = editText_input.getText().toString();
                if(text != null && !text.equals("")) {
                    temp = Float.parseFloat(text);
                }
                Value_account_thismonth = Value_account_thismonth + temp;
                Value_account_total = Value_account_total + temp;
                resetAmount();
                Toast.makeText(MainActivity.this, "添加收入项目完成", Toast.LENGTH_SHORT).show();

            }
        });
        builder_income.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder_income.setCancelable(true);
        AlertDialog alertDialog = builder_income.create();
        alertDialog.show();
    }

    private void addOutcome() {
        AlertDialog.Builder builder_outcome =new AlertDialog.Builder(MainActivity.this);
        builder_outcome.setTitle("选择消费类型");
        String[] items = {"伙食", "约会", "网购", "其它"};
        builder_outcome.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addOutcome2(which);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder_outcome.create();
        alertDialog.show();

    }

    private void addOutcome2(final int i) {
        temp = 0;
        AlertDialog.Builder builder_outcome2 = new AlertDialog.Builder(MainActivity.this);
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_amount_edittext, null);

        builder_outcome2.setView(layout_amount_edittext);
        builder_outcome2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_input = (EditText) layout_amount_edittext.findViewById(R.id.editText_input);
                String text = editText_input.getText().toString();
                if(text != null && !text.equals("")){
                    temp = Float.parseFloat(text);
                }
                Value_account_thismonth = Value_account_thismonth - temp;
                Value_account_total = Value_account_total - temp;
                resetAmount();
                Array_outcomeValue[i] = Array_outcomeValue[i] + temp;
                Toast.makeText(MainActivity.this, "添加支出项目完成", Toast.LENGTH_SHORT).show();

            }
        });

        builder_outcome2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder_outcome2.setCancelable(true);
        AlertDialog alertDialog = builder_outcome2.create();
        alertDialog.show();
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

    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(fragmentAccount != null){
            fragmentTransaction.hide(fragmentAccount);
        }
        if(fragmentPayplan != null){
            fragmentTransaction.hide(fragmentPayplan);
        }
        if(fragmentPaycharts != null){
            fragmentTransaction.hide(fragmentPaycharts);
        }
    }

    private void showAccount(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragmentAccount == null){
            fragmentAccount = new FragmentAccount();
            fragmentTransaction.add(R.id.frame_Layout, fragmentAccount);
        }

        hideFragment(fragmentTransaction);
        fragmentTransaction.show(fragmentAccount);

        fragmentTransaction.commit();
    }

    private void showPayplan(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragmentPayplan == null){
            fragmentPayplan = new FragementPayplan();
            fragmentTransaction.add(R.id.frame_Layout, fragmentPayplan);
        }

        hideFragment(fragmentTransaction);
        fragmentTransaction.show(fragmentPayplan);

        fragmentTransaction.commit();
    }

    private void showPaycharts(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragmentPaycharts == null){
            fragmentPaycharts = new FragmentPaycharts();
            fragmentTransaction.add(R.id.frame_Layout, fragmentPaycharts);
        }

        hideFragment(fragmentTransaction);
        fragmentTransaction.show(fragmentPaycharts);

        fragmentTransaction.commit();
    }
}
