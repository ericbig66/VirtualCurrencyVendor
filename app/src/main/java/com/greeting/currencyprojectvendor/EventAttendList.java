package com.greeting.currencyprojectvendor;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.zxing.common.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static com.greeting.currencyprojectvendor.Login.acc;
import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;
import static com.greeting.currencyprojectvendor.MainMenu.Aid;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventAttendList extends Fragment {

    private ArrayList<String> Aname = new ArrayList<>();
    private ArrayList<String> Mail = new ArrayList<>();
    private ArrayList<String> Name = new ArrayList<>();
    private ArrayList<String> Sign = new ArrayList<>();

    public EventAttendList() {
        // Required empty public constructor
    }

    public static EventAttendList newInstance() {
        return new EventAttendList();
    }

    TableLayout tradeData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());

        View view = inflater.inflate(R.layout.fragment_sell_diary,container, false);
        clear();
        tradeData = view.findViewById(R.id.tradeData);
        Aname.add("活動名稱　");
        Mail.add("客戶帳號　");
        Name.add("客戶姓名　");
        Sign.add("簽到時間");

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
        return view;

    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select activityName, LEFT(attendlist.account,5) EML, CHAR_LENGTH(attendlist.account)-5 EMR, L_name, CHAR_LENGTH(f_name) F_name, signTime from attendlist left join activity on activityNumber = activity left join `client` on attendlist.account = client.account where vacc = '"+acc+"'");
                //將查詢結果裝入陣列
                while(rs.next()){
                    String star = "";
                    for(int i = 0 ; i<rs.getInt("EMR") ; i++){star+="*";}
                    Aname.add(rs.getString("activityName")+"　");
                    Mail.add(rs.getString("EML")+star+"　");
                    star="";
                    for(int i = 0 ; i<rs.getInt("F_name") ; i++){star+="*";}
                    Name.add(rs.getString("L_name")+star+"　");
                    Sign.add(rs.getString("signTime"));
                }
                return "0";
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            Log.v("test","YOUR RESULT ="+result);
            renderTable();
        }
        private void renderTable(){
            for(int row = 0 ; row < Aname.size() ; row++ ){
//                Toast.makeText(Diary.this,"第"+row+"列建構中",Toast.LENGTH_SHORT).show();
                //新增一列
                TableRow tr = new TableRow(getActivity());
                //新增一個TextView
                TextView t1 = new TextView(getActivity());
                TextView t2 = new TextView(getActivity());
                TextView t3 = new TextView(getActivity());
                TextView t4 = new TextView(getActivity());
                //設定TextView的文字
                t1.setText(Aname.get(row));
                t2.setText(Mail.get(row));
//                Log.v("test",trade.get(row));
                t3.setText(Name.get(row));
                t4.setText(Sign.get(row));
                //將TextView放入列
                tr.addView(t1);
                tr.addView(t2);
                tr.addView(t3);
                tr.addView(t4);
                //將整列加入預先建立的TableLayout中
                tradeData.addView(tr,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            }

        }
    }

    public void clear(){
        Aname.clear();
        Mail.clear();
        Name.clear();
        Sign.clear();
    }

}
