package com.example.delinka.accountbook2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delinka on 2017/9/8.
 */

public class FragmentAccount extends Fragment{

    private View layout_income, layout_outcome;
    private ViewPager viewPager;
    private List<View> viewList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_account_messege, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        LayoutInflater Vinflater = getActivity().getLayoutInflater();
        layout_income = Vinflater.inflate(R.layout.layout_income, null);
        layout_outcome = Vinflater.inflate(R.layout.layout_outcome, null);

        viewList = new ArrayList<>();
        viewList.add(layout_outcome);
        viewList.add(layout_income);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object){
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position){
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };

        viewPager.setAdapter(pagerAdapter);

        return view;
    }

}
