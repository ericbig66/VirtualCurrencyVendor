package com.greeting.currencyprojectvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.pf;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;

public class MainMenu extends AppCompatActivity {
    TextView wmsg;
    Intent intent;
    ImageView profile;
    public static String vname="";
    final String acc = Login.acc;
    int obp = 0; //times of on back pressed

    //寄放區

    //Alter product
    public static ArrayList<String> PID = new ArrayList<>();
    public static ArrayList<String> Pname = new ArrayList<>();
    public static ArrayList<Integer> Pprice = new ArrayList<>();
    public static ArrayList<Integer> Pamount = new ArrayList<>();
    public static ArrayList<String> PIMG = new ArrayList<>();

    public static int SellId=-1, ReleseQuantity=0;

    //Alter event
    public static ArrayList<String> Aid = new ArrayList<>();
    public static ArrayList<String> Aname = new ArrayList<>();
    public static ArrayList<Integer> Areward = new ArrayList<>();
    public static ArrayList<Integer> Aamount = new ArrayList<>();
    public static ArrayList<Integer> AamountLeft = new ArrayList<>();
    public static ArrayList<String> Adesc = new ArrayList<>();
    public static ArrayList<String> Avendor = new ArrayList<>();
    public static ArrayList<Date> Aendapp = new ArrayList<>();
    public static ArrayList<Date> AactDate = new ArrayList<>();
    public static ArrayList<Date> AactStart = new ArrayList<>();
    public static ArrayList<Date> AactEnd = new ArrayList<>();
    public static ArrayList<String> Actpic = new ArrayList<>();
    public static ArrayList<String> attended = new ArrayList<>();

    public static int EventId = -1;


    //測試用變數
    public static ArrayList<String> TMP = new ArrayList<>();
    //

    public void execute(View v){
        switch (v.getId()){
            case R.id.getcoin:
                intent = new Intent(MainMenu.this, Purchase.class);
                break;
            case R.id.paycoin:
                intent = new Intent(MainMenu.this, Gift.class);
                break;
            case R.id.diary:
//                intent = new Intent(MainMenu.this, Diary.class);
                intent = new Intent(MainMenu.this,NewDiary.class);
                break;
            case R.id.AddProd:
                intent = new Intent(MainMenu.this, AddProduct.class);
                break;
            case R.id.AlterProd:
                intent = new Intent(MainMenu.this,AlterProduct.class);
                break;
            case R.id.addAct:
                intent = new Intent(MainMenu.this,AddActivity.class);
                break;
            case R.id.AlterEvent:
                intent = new Intent(MainMenu.this,AlterEvent.class);
                break;
            case R.id.alter_vendor:
                intent = new Intent(MainMenu.this,AlterVendor.class);
                break;
        }
        startActivity(intent);
        finish();
    }
    public void onBackPressed(){
        obp++;
        Timer timer = new Timer(true);

        if(obp>=2){
            Login.wcm ="";
            Login.acc ="";
            Intent intent = new Intent(MainMenu.this, Login.class);
            startActivity(intent);
            pf = null;
            finish();
        }
        else{
            Toast.makeText(MainMenu.this,"再按一次返回以登出",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        String msg = intent.getStringExtra("msg");
//        Log.v("test", "profile size = " + pf.getWidth()+"*"+pf.getHeight());
        String msg = Login.wcm;
        setContentView(R.layout.layout_main_menu);
        wmsg = findViewById(R.id.msg);
//        wmsg.setText(msg);
//        wmsg.setText(Login.wcm);
        profile = findViewById(R.id.profile);
        try {
            profile.setImageBitmap(pf);
            profile.setRotation(Login.pfr);
            Log.v("test", "profile size = " + pf.getWidth()+"*"+pf.getHeight());
        }catch (Exception e){
            Log.v("test","profile error = "+e.toString());
        }

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(MainMenu.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select name, money from vendor where mail = '"+acc+"'");

                while(rs.next()){
                    vname = rs.getString(1);
                    result += rs.getString(1)+"您好!\n目前您尚有$"+rs.getInt(2);
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
                return res;
            }

        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            wmsg.setText(result);
//            Log.v("test","vname M = "+vname);
        }
    }
}
