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
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public DrawerLayout drawerLayout;
    private Button nav_button;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
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
    private int[] colors = {
            Color.parseColor("#7eed8b"),
            Color.parseColor("#da70d6"),
            Color.parseColor("#7ed6ed"),
            Color.parseColor("#e7ed7e")}; //饼状图颜色
    private float[] Array_outcomeValue = {1, 1, 1, 1};
    private float temp;

    private LinearLayout layout_messege;
    private CostRecord costRecord;

    private PieChartView pieChartView;
    private PieChartData pieChartData;
    private boolean hasLabels = true; //是否在饼状图上显示标签
    private boolean hasLabelsOutside = false; //是否在饼状图外显示标签
    private boolean hasCenterCircle = true; //是否有中心空洞
    private boolean hasCenterText1 = true; //是否显示中心标签1
    private boolean hasCenterText2 = false; //是否显示中心标签2
    private boolean isExploded = false; //是否呈爆炸式
    private boolean hasLabelForSelected = false; //是否只在选择时显示标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPayplan();
        showAccount();
        loadData();
        setContentView(R.layout.activity_main);
        setTitle("");
        ClothStatusBar();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_button = (Button) findViewById(R.id.nav_button);
        nav_button.setOnClickListener(this);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        setNavigationViewClick();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        setBottomNavigationViewOnClick();

    }

    private void setNavigationViewClick() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.item_remove_thismonth:
                        removeThismonth();
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this, "重置完成", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_remove_totol:
                        removeTotal();
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this, "重置完成", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_exit:
                        ActivityCollector.finishAllActivity();
                        break;
                }
                return true;
            }
        });
    }

    private void initPieCharts(){
        pieChartView = (PieChartView) findViewById(R.id.pieCharts);
        ArrayList index = new ArrayList(); //存放支出类型中的非0值的下标

        for(int i = 0; i < Array_outcomeValue.length; i++){
            if(Array_outcomeValue[i] != 0)
                index.add(i);
        }

        /**存放扇形数据的集合*/
        List<SliceValue> values = new ArrayList<SliceValue>();
        pieChartData = new PieChartData(values);
        if(index.size() == 0){
            SliceValue sliceValue = new SliceValue(1);
            pieChartData.setCenterText1("没有花钱");
            pieChartData.setCenterText1FontSize(18) ;
            pieChartData.setCenterText1Color(Color.WHITE);
            hasLabels = false;
            values.add(sliceValue);
        }
        else{
            for(int i = 0; i < index.size(); i++) {
                int k = (int) index.get(i);
                SliceValue sliceValue = new SliceValue(Array_outcomeValue[k], colors[k]);
                sliceValue.setLabel(Array_outcomeType[k]);
                values.add(sliceValue);
                pieChartData.setCenterText1("已消费");
                pieChartData.setCenterText1FontSize(18) ;
                pieChartData.setCenterText1Color(Color.WHITE);
                hasLabels = true;
            }
        }

        pieChartData.setHasLabels(hasLabels);
        pieChartData.setHasLabelsOutside(hasLabelsOutside);
        pieChartData.setHasLabelsOnlyForSelected(hasLabelForSelected);
        pieChartData.setHasCenterCircle(hasCenterCircle);

        pieChartView.setPieChartData(pieChartData);
        pieChartView.setAlpha(0.65f); //设置不透明度
    }

    private void removeTotal() {
        Value_account_thismonth = 0;
        Value_account_total = 0;
        resetAmount();
        removeRecord();
        for(int i = 0; i < Array_outcomeValue.length; i++){
            Array_outcomeValue[i] = 0;
        }
        initPieCharts();
    }

    private void removeThismonth() {
        Value_account_thismonth = 0;
        resetAmount();
        removeRecord();
        for(int i = 0; i < Array_outcomeValue.length; i++){
            Array_outcomeValue[i] = 0;
        }
        initPieCharts();

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
        initPieCharts();
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
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_income_edittext, null);

        builder_income.setView(layout_amount_edittext);
        builder_income.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                costRecord.setType("生活费");

                editText_inputCost = (EditText)  layout_amount_edittext.findViewById(R.id.editText_inputCost_income);
                editText_inputMsg = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputMsg_income);

                String textCost = editText_inputCost.getText().toString();
                if(textCost != null && !textCost.equals("")){
                    try {
                        temp = Float.parseFloat(textCost);
                        if (temp == 0) {
                            Toast.makeText(MainActivity.this, "无金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "请输入正确金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
        final AlertDialog.Builder builder_outcome2 = new AlertDialog.Builder(MainActivity.this);
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_outcome_edittext, null);

        TextView textView_SelectedType_outcome = (TextView) layout_amount_edittext.findViewById(R.id.textView_SelectedType_outcome);
        textView_SelectedType_outcome.setText(Array_outcomeType[i]);

        builder_outcome2.setView(layout_amount_edittext);
        builder_outcome2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_inputCost = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputCost_income);
                editText_inputMsg = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputMsg_income);

                String textCost = editText_inputCost.getText().toString();
                if(textCost != null && !textCost.equals("")){
                    try {
                        temp = Float.parseFloat(textCost);
                        if (temp == 0) {
                            Toast.makeText(MainActivity.this, "无金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "请输入正确金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                addOutcomeRecord(costRecord);
                resetAmount();
                Array_outcomeValue[i] = Array_outcomeValue[i] + temp;
                Toast.makeText(MainActivity.this, "添加支出项目完成", Toast.LENGTH_SHORT).show();

                initPieCharts();
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
        final LinearLayout layout_amount_edittext = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_income_edittext, null);

        builder_outcome2.setView(layout_amount_edittext);
        builder_outcome2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_inputCost = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputCost_income);
                editText_inputMsg = (EditText) layout_amount_edittext.findViewById(R.id.editText_inputMsg_income);

                String textCost = editText_inputCost.getText().toString();
                if(textCost != null && !textCost.equals("")){
                    try {
                        temp = Float.parseFloat(textCost);
                        if (temp == 0) {
                            Toast.makeText(MainActivity.this, "无金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "请输入正确金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
