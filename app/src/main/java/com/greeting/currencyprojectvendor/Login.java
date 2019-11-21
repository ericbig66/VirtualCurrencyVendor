package com.greeting.currencyprojectvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

public class Login extends AppCompatActivity {

    //連接資料庫的IP、帳號(不可用root)、密碼
    private static final String url = "jdbc:mysql://140.135.113.196:3360/virtualcurrencyproject";
    private static final String user = "currency";
    private static final String pass = "@SAclass";
    public static String wcm;
    public static String acc;

    Button btnFetch, btnClear, reg;
    TextView txtData;
    EditText myacc, pwd;
    String account, password, data;

    public void swreg(){  //切換註冊頁面
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void swmenu(){   //切換到主選單
//        Intent intent = new Intent(this, MainMenu.class);
//        intent.putExtra("msg",data);
//        wcm = data;
//        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        btnFetch = findViewById(R.id.btnFetch);//登入
        btnClear = findViewById(R.id.btnClear);//清除
        reg = findViewById(R.id.reg);//切換到註冊頁面
        txtData =  findViewById(R.id.txtData);//登入結果
        myacc = findViewById(R.id.acc);//帳號(Email)
        pwd = findViewById(R.id.pwd);//密碼

        //註冊紐動作
        reg.setOnClickListener(v -> swreg());
//登入紐動作
        btnFetch.setOnClickListener(v -> {
            account = myacc.getText().toString();
            password = pwd.getText().toString();
//                dtv.setText("call login(@fname, "+account+", "+password+"); select @fname;");
            if(account.trim().isEmpty()||password.trim().isEmpty()){
                Toast.makeText(Login.this,"請輸入帳號密碼以登入!",Toast.LENGTH_SHORT).show();
            }else{
                ConnectMySql connectMySql = new ConnectMySql();
                connectMySql.execute("");
            }

        });

        //清除紐動作
        btnClear.setOnClickListener(v -> {
            txtData.setText("");
            myacc.setText(null);
            pwd.setText(null);
        });
    }


    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(Login.this,"請稍後...",Toast.LENGTH_SHORT).show();
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
                //Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery("call login(@fname, '"+account+"', '"+password+"'); select @fname;");
                //experiment part start
                //此處呼叫Stored procedure(call 函數名稱(?)==>問號數量代表輸出、輸入的變數數量)
                CallableStatement cstmt = con.prepareCall("{call vlogin(?,?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);//設定輸出變數(參數位置,參數型別)
                cstmt.setString(2, account);
                cstmt.setString(3, password);
                cstmt.registerOutParameter(4, Types.INTEGER);
                cstmt.executeUpdate();
                return cstmt.getString(1)+"您好!\n目前您尚有$"+cstmt.getString(4);//回傳結果給onPostExecute==>取得輸出變數(位置)
                //experiment part end
//
//                while(rs.next()){
//                    result += rs.getString(1).toString()+"您好!";
//                }
            }catch (Exception e){
//                res = result;
                e.printStackTrace();
                res = e.toString();
                Log.v("test",res);
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {

            if (result.equals("null遊客您好!\n目前您尚有$null")){
                result= "遊客您好!\n如需購物請先註冊帳號\n謝謝您的合作!";
                txtData.setText(result);//設定結果顯示
            }
            else{
                data = result;
                acc=account;
                Toast.makeText(Login.this,"請稍後...", Toast.LENGTH_SHORT).show();
                swmenu();
            }


        }
    }
}
