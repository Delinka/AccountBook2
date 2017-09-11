package com.example.delinka.accountbook2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    private EditText editText_inputCost;
    private EditText editText_inputMsg;
    private String[] Array_outcomeType = {"伙食", "约会", "网购", "其它"};
    private float[] Array_outcomeValue = {1, 1, 1, 1};
    private float temp;

    public ArrayList<CostRecord> costRecordList = new ArrayList<>();
    private LinearLayout layout_messege;
    private CostRecord costRecord;

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
                        removeThismonth();
                        break;
                    case R.id.item_remove_totol:
                        removeTotal();
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

    private void removeTotal() {
        Value_account_thismonth = 0;
        Value_account_total = 0;
        resetAmount();
        removeRecord();
    }

    private void removeThismonth() {
        Value_account_thismonth = 0;
        resetAmount();
        removeRecord();

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
            case R.id.button_addOutcomePlan:
                addPlan();
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
                openDetailActivity();
                break;
        }
    }

    private void openDetailActivity() {
        Intent intent = new Intent(MainActivity.this, CostDetailActivity.class);

        startActivity(intent);

    }

    public void resetAmount() {
        textView_account_thismonth = (TextView) findViewById(R.id.textView_amount_thismonth);
        textView_account_total = (TextView) findViewById(R.id.textView_amount_total);

        textView_account_thismonth.setText("" + Value_account_thismonth);
        textView_account_total.setText("" + Value_account_total);

    }

    private void addIncome() {
        temp = 0;
        costRecord = new CostRecord();
        AlertDialog.Builder builder_income = new AlertDialog.Builder(MainActivity.this);
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_amount_edittext, null);

        builder_income.setView(layout_amount_edittext);
        builder_income.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                costRecord.setType("生活费");

                editText_inputCost = (EditText)  layout_amount_edittext.findViewById(R.id.editText_inputCost);
                editText_inputMsg = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputMsg);

                String textCost = editText_inputCost.getText().toString();
                if(textCost != null && !textCost.equals("")) {
                    temp = Float.parseFloat(textCost);
                }else{
                    Toast.makeText(MainActivity.this, "无金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                Value_account_thismonth = Value_account_thismonth + temp;
                Value_account_total = Value_account_total + temp;
                costRecord.setCost("￥ " + temp);

                String textMsg = editText_inputMsg.getText().toString();
                if(textMsg != null && !textMsg.equals("")){
                    costRecord.setMessege(textMsg);
                } else{
                    costRecord.setMessege("无备注");
                }

                addIncomeRecord(costRecord);
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
        costRecord = new CostRecord();
        costRecord.setType(Array_outcomeType[i]);
        AlertDialog.Builder builder_outcome2 = new AlertDialog.Builder(MainActivity.this);
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_amount_edittext, null);

        builder_outcome2.setView(layout_amount_edittext);
        builder_outcome2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_inputCost = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputCost);
                editText_inputMsg = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputMsg);

                String textCost = editText_inputCost.getText().toString();
                if(textCost != null && !textCost.equals("")){
                    temp = Float.parseFloat(textCost);
                } else{
                    Toast.makeText(MainActivity.this, "无金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                Value_account_thismonth = Value_account_thismonth - temp;
                Value_account_total = Value_account_total - temp;
                costRecord.setCost("￥ " + temp);

                String textMsg = editText_inputMsg.getText().toString();
                if(textMsg != null && !textMsg.equals("")){
                    costRecord.setMessege(textMsg);
                } else{
                    costRecord.setMessege("无备注");
                }
                addOutcomeRecord(costRecord);
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

    private void addPlan() {
        AlertDialog.Builder builder_outcome =new AlertDialog.Builder(MainActivity.this);
        builder_outcome.setTitle("选择消费类型");
        String[] items = {"伙食", "约会", "网购", "其它"};
        builder_outcome.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPlan2(which);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder_outcome.create();
        alertDialog.show();

    }

    private void addPlan2(final int i) {
        temp = 0;
        costRecord = new CostRecord();
        costRecord.setType(Array_outcomeType[i]);
        AlertDialog.Builder builder_outcome2 = new AlertDialog.Builder(MainActivity.this);
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_amount_edittext, null);

        builder_outcome2.setView(layout_amount_edittext);
        builder_outcome2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_inputCost = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputCost);
                editText_inputMsg = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputMsg);

                String textCost = editText_inputCost.getText().toString();
                if(textCost != null && !textCost.equals("")){
                    temp = Float.parseFloat(textCost);
                }else{
                    Toast.makeText(MainActivity.this, "无金额", Toast.LENGTH_SHORT).show();
                    return;
                }

                Value_account_thismonth = Value_account_thismonth - temp;
                Value_account_total = Value_account_total - temp;
                costRecord.setCost("￥ " + temp);

                String textMsg = editText_inputMsg.getText().toString();
                if(textMsg != null && !textMsg.equals("")){
                    costRecord.setMessege(textMsg);
                } else{
                    costRecord.setMessege("无备注");
                }
                addPlanRecord(costRecord);
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

    private void addIncomeRecord(CostRecord costRecord) {
        layout_messege = (LinearLayout) findViewById(R.id.layout_messege_income);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_outcome_messege, layout_messege, false);
        TextView msg_date = (TextView) view.findViewById(R.id.msg_date);
        TextView msg_type = (TextView) view.findViewById(R.id.msg_type);
        TextView msg_msg = (TextView) view.findViewById(R.id.msg_msg);
        TextView msg_cost = (TextView) view.findViewById(R.id.msg_cost);
        msg_date.setText(costRecord.getDate());
        msg_type.setText(costRecord.getType());
        msg_msg.setText(costRecord.getMessege());
        msg_cost.setText(costRecord.getCost());

        layout_messege.addView(view);
    }

    private void addOutcomeRecord(CostRecord costRecord) {
        layout_messege = (LinearLayout) findViewById(R.id.layout_messege_outcome);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_outcome_messege, layout_messege, false);
        TextView msg_date = (TextView) view.findViewById(R.id.msg_date);
        TextView msg_type = (TextView) view.findViewById(R.id.msg_type);
        TextView msg_msg = (TextView) view.findViewById(R.id.msg_msg);
        TextView msg_cost = (TextView) view.findViewById(R.id.msg_cost);
        msg_date.setText(costRecord.getDate());
        msg_type.setText(costRecord.getType());
        msg_msg.setText(costRecord.getMessege());
        msg_cost.setText(costRecord.getCost());

        layout_messege.addView(view);
    }

    private void addPlanRecord(CostRecord costRecord) {
        layout_messege = (LinearLayout) findViewById(R.id.layout_messege_plan);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_outcome_messege, layout_messege, false);
        TextView msg_date = (TextView) view.findViewById(R.id.msg_date);
        TextView msg_type = (TextView) view.findViewById(R.id.msg_type);
        TextView msg_msg = (TextView) view.findViewById(R.id.msg_msg);
        TextView msg_cost = (TextView) view.findViewById(R.id.msg_cost);
        msg_date.setText(costRecord.getDate());
        msg_type.setText(costRecord.getType());
        msg_msg.setText(costRecord.getMessege());
        msg_cost.setText(costRecord.getCost());
        layout_messege.addView(view);
    }

    private void removeRecord(){
        layout_messege = (LinearLayout) findViewById(R.id.layout_messege_income);
        layout_messege.removeAllViews();

        layout_messege = (LinearLayout) findViewById(R.id.layout_messege_outcome);
        layout_messege.removeAllViews();
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
