package com.greeting.currencyprojectvendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.greeting.currencyprojectvendor.Login.acc;
import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;

public class AddActivity extends AppCompatActivity {

    EditText actName, reward, people, ActDesc;
    DatePicker endApp, actDate;
    TimePicker actStart, actEnd;
    ImageView actPic;
    Button uploadPic, AddAct;

    final int OPEN_PIC = 1021;
    String b64 = "", ACTN="", DESC="", EA="", AD="", AS="", AE="";
    int RW=0, PP=0;

    //系統時間及格式設定
    Date curDate = new Date(System.currentTimeMillis()) ;//取得系統時間
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");//格式化日期顯示方式
    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");//格式化時間顯示方式
    //格式化出可直接使用的年月日變數
    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("mm");
    SimpleDateFormat day = new SimpleDateFormat("dd");
    String yyyy = year.format(curDate);
    String mm = month.format(curDate);
    String dd = day.format(curDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_activity);

        actName = findViewById(R.id.actName);
        reward = findViewById(R.id.reward);
        people = findViewById(R.id.people);
        ActDesc = findViewById(R.id.ActDesc);
        endApp = findViewById(R.id.endApp);
        actDate = findViewById(R.id.actDate);
        actStart = findViewById(R.id.actStart);
        actEnd = findViewById(R.id.actEnd);
        actPic = findViewById(R.id.actPic);
        uploadPic = findViewById(R.id.uploadPic);
        AddAct = findViewById(R.id.AddAct);

        uploadPic.setOnClickListener(v -> picOpen());
        AddAct.setOnClickListener(v -> verify());
        endApp.setMinDate(System.currentTimeMillis());
        actDate.setMinDate(System.currentTimeMillis()+24*60*60*1000);
        actStart.setIs24HourView(true);
        actEnd.setIs24HourView(true);

    }

    public void verify(){
       String error = "";
       ACTN = actName.getText().toString();
       Log.v("test","ACTN = "+ACTN.trim().isEmpty());
       error = ACTN.trim().isEmpty()?error+"活動名稱, ":error;
       DESC = ActDesc.getText().toString();
       error = DESC.trim().isEmpty()?error+"活動說明, ":error;
       RW = Integer.parseInt(reward.getText().toString().trim().isEmpty()?"-1":reward.getText().toString().trim());
       error = RW==-1?error+"每人獎金, ":error;
       PP = Integer.parseInt(people.getText().toString().trim().isEmpty()?"-1":people.getText().toString().trim());
       error = PP==-1?error+"人數限制, ":error;
       error = b64.trim().isEmpty()?error+"活動封面照片, ":error;
       error = error.isEmpty()?error:"請確實填寫以下資料:\n"+error.substring(0,error.length()-3);
        try {
            EA = (endApp.getYear()+"/"+(endApp.getMonth()+1)+"/"+(endApp.getDayOfMonth()));
            AD = (actDate.getYear()+"/"+(actDate.getMonth()+1)+"/"+(actDate.getDayOfMonth()));
            //Log.v("test", "\nEA="+EA+"\nAD="+AD);
            Date ea = formatter.parse(EA);
            Date ad = formatter.parse(AD);
            error = ea.before(ad)?error:error+"\n\n注意:活動日期必須大於報名截止日期";
            AS = (actStart.getCurrentHour()+":"+actStart.getCurrentMinute());
            AE = (actEnd.getCurrentHour()+":"+actEnd.getCurrentMinute());
            Date as = timeFormatter.parse(AS);
            Date ae = timeFormatter.parse(AE);
            error = as.before(ae)?error:error+"\n\n注意:簽到截止時間必須大於簽到開始時間";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(error.isEmpty()){
//            Toast.makeText(AddActivity.this,"可以上傳",Toast.LENGTH_LONG).show();
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }else{
            Toast.makeText(AddActivity.this,error,Toast.LENGTH_LONG).show();
        }

    }

    //開啟頭像
    public void picOpen(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"請選擇商品照片"), OPEN_PIC);
    }

    Bitmap dataToConvert;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri imgdata = data.getData();
            actPic.setImageURI(imgdata);
            actPic.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable)actPic.getDrawable()).getBitmap();
            int w,h,scale;
            w = dataToConvert.getWidth();
            h = dataToConvert.getHeight();
            if(w>h||w==h&&w/120>1){scale = w/120;}
            else{scale = h/120;}
            w/=scale;
            h/=scale;
            actPic.setImageBitmap(Bitmap.createScaledBitmap(dataToConvert,w,h,false));

            actPic.setVisibility(View.VISIBLE);
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(AddActivity.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... strings) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dataToConvert.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return imageString;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            b64 = s;

        }
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(AddActivity.this,"新增中...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{


                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result ="";
                CallableStatement cstmt = con.prepareCall("{call createActivity(?,?,?,?,?,?,?,?,?,?,?)}");
                cstmt.setString(1, acc);
                cstmt.setString(2, ACTN);
                cstmt.setInt(3, RW);
                cstmt.setInt(4, PP);
                cstmt.setString(5, DESC);
                cstmt.setString(6, EA);
                cstmt.setString(7, AD);
                cstmt.setString(8, AS);
                cstmt.setString(9, AE);
                cstmt.setString(10, b64);
                cstmt.registerOutParameter(11, Types.VARCHAR);
                cstmt.executeUpdate();
                return cstmt.getString(11);

            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AddActivity.this,result,Toast.LENGTH_LONG).show();
        }


    }


}
