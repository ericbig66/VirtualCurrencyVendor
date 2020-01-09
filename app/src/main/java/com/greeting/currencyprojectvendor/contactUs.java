package com.greeting.currencyprojectvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;

public class contactUs extends AppCompatActivity {

    EditText comment;
    Button submint;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact_us);


        comment = findViewById(R.id.comment);
        submint = findViewById(R.id.submit);

        submint.setOnClickListener(v -> {
            if(!comment.getText().toString().trim().isEmpty()){
                Log.v("test","comment is going to send");
                message = comment.getText().toString().trim();
                ConnectMySql connectMySql = new ConnectMySql();
                connectMySql.execute("");
            }
        });

    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作

        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="success";
                Statement st = con.createStatement();
                Log.v("test","insert into comment(message, endUser, submitDate) values('\"+message+\"', 'client', now())");
                st.execute("insert into comment(message, endUser, submitDate) values('"+message+"', 'vendor', now())");

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
            Toast.makeText(contactUs.this,"謝謝您，我們以收到您寶貴的意見!",Toast.LENGTH_SHORT).show();
            comment.setText("");
        }
    }

    public void onBackPressed(){
        Intent intent = new Intent(contactUs.this, MainMenu.class);
        startActivity(intent);
    }

}
