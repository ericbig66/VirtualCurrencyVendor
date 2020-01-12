package com.greeting.currencyprojectvendor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import static com.greeting.currencyprojectvendor.MainMenu.Aid;
import static com.greeting.currencyprojectvendor.MainMenu.Aname;

public class Gift extends AppCompatActivity {
    Button pay;
    EditText amount;
    ImageView qrCode;
    Spinner DropDown;
    LinearLayout customInput;
    TextView actName;


    public void onBackPressed(){
        Aid.clear();
        Aname.clear();
        Intent intent = new Intent(Gift.this, MainMenu.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gift);

        pay = findViewById(R.id.pay);
        amount = findViewById(R.id.amount);
        qrCode = findViewById(R.id.qrCode);
        DropDown = findViewById(R.id.DropDown);
        customInput = findViewById(R.id.customInput);
        actName = findViewById(R.id.actName);

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qrCode.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == Aname.size()-1){
                    customInput.setVisibility(View.VISIBLE);
                    qrCode.setVisibility(View.GONE);
                }
                else{
                    customInput.setVisibility(View.GONE);
                    qrCode.setVisibility(View.VISIBLE);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Log.v("test",acc+"fu02l," + Aid.get(position));
                    try{
                        Bitmap bit = encoder.encodeBitmap(acc+"fu02l," +Aid.get(position)
                                , BarcodeFormat.QR_CODE,1000,1000);
                        qrCode.setImageBitmap(bit);
                    }catch (WriterException e){
                        e.printStackTrace();
                    }
                }
                actName.setText(Aname.get(position)+"簽到請掃描我");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    public void getCode(View v) {
        BarcodeEncoder encoder = new BarcodeEncoder();
        Log.v("test", acc+"cj/1l," +amount .getText().toString());
        if(amount.getText().toString().trim().isEmpty() || Integer.parseInt(amount.getText().toString().trim())<1){Toast.makeText(Gift.this,"請輸入紅包金額",Toast.LENGTH_LONG).show();}
        else{

            try{
                Bitmap bit = encoder.encodeBitmap(acc+"cj/1l," +amount .getText().toString()
                        , BarcodeFormat.QR_CODE,1000,1000);
                qrCode.setImageBitmap(bit);
                closekeybord();
                qrCode.setVisibility(View.VISIBLE);
            }catch (WriterException e){
                e.printStackTrace();
            }
        }


    }


    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Gift.this, "請稍後...", Toast.LENGTH_SHORT).show();
        }

        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {

            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result = "";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select activityNumber, activityName from activity where vacc = '"+ acc+"' and actDate = curDate()");
//                Log.v("test","select activityNumber, activityName from activity where vacc = '"+ acc+"' and actDate = curDate()");
                while (rs.next()) {
                    Aid.add(rs.getString("activityNumber"));
                    Aname.add(rs.getString("activityName"));
                }
                int actAmount = Aid.size();
                Aname.add("紅包/人工補簽");
                return Aname.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute (String result){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Gift.this, android.R.layout.simple_spinner_item, Aname);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            DropDown.setAdapter(adapter);
        }

    }

    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

