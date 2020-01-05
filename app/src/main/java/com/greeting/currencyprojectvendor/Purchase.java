package com.greeting.currencyprojectvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.greeting.currencyprojectvendor.Login.acc;
import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;
import static com.greeting.currencyprojectvendor.MainMenu.PID;
import static com.greeting.currencyprojectvendor.MainMenu.Pname;
import static com.greeting.currencyprojectvendor.MainMenu.vname;

public class Purchase extends AppCompatActivity {

    ImageView qrCode;
    Spinner DropDown;
    TextView actName;

    public void onBackPressed(){
        Intent intent = new Intent(Purchase.this, MainMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_purchase);

        qrCode = findViewById(R.id.qrCode);
        DropDown = findViewById(R.id.DropDown);
        actName = findViewById(R.id.actName);

        DropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BarcodeEncoder encoder = new BarcodeEncoder();
//                Log.v("test",acc+"2jo4cj04," + Aid.get(position));
                try{
                    Bitmap bit = encoder.encodeBitmap(PID.get(position)+"2jo4cj04,"+acc+"2jo4cj04,"+1
                            , BarcodeFormat.QR_CODE,1000,1000);
                    qrCode.setImageBitmap(bit);
                }catch (WriterException e) {
                    e.printStackTrace();
                    actName.setText("掃描我兌換"+Pname.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Purchase.this, "請稍後...", Toast.LENGTH_SHORT).show();
        }

        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {

            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select productID, productName from product where vendor = '"+vname+"'");

                while (rs.next()) {
                    PID.add(rs.getString(1));
                    Pname.add(rs.getString(2));
                }
                return Pname.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute (String result){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Purchase.this, android.R.layout.simple_spinner_item, Pname);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            DropDown.setAdapter(adapter);
        }

    }
}
