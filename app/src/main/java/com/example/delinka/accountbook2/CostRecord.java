package com.example.delinka.accountbook2;

/**
 * Created by Delinka on 2017/9/11.
 */

public class CostRecord {
    String date;
    String type;
    String messege;
    String cost;

    CostRecord(){
        date = "9月11日";
        type = "伙食";
        messege = "测试一下";
        cost = "3.0";
    }

    void setDate(String d){date = d;}
    void setType(String t){type = t;}
    void setMessege(String m){messege = m;}
    void setCost(String c){cost = c;}

    String getDate(){return date;}
    String getType(){return type;}
    String getMessege(){return messege;}
    String getCost(){return cost;}
}
