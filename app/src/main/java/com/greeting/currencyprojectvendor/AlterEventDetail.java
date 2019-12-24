package com.greeting.currencyprojectvendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import static com.greeting.currencyprojectvendor.MainMenu.AactDate;
import static com.greeting.currencyprojectvendor.MainMenu.AactEnd;
import static com.greeting.currencyprojectvendor.MainMenu.AactStart;
import static com.greeting.currencyprojectvendor.MainMenu.Aamount;
import static com.greeting.currencyprojectvendor.MainMenu.AamountLeft;
import static com.greeting.currencyprojectvendor.MainMenu.Actpic;
import static com.greeting.currencyprojectvendor.MainMenu.Adesc;
import static com.greeting.currencyprojectvendor.MainMenu.Aendapp;
import static com.greeting.currencyprojectvendor.MainMenu.Aid;
import static com.greeting.currencyprojectvendor.MainMenu.Aname;
import static com.greeting.currencyprojectvendor.MainMenu.Areward;
import static com.greeting.currencyprojectvendor.MainMenu.Avendor;
import static com.greeting.currencyprojectvendor.MainMenu.PID;
import static com.greeting.currencyprojectvendor.MainMenu.PIMG;
import static com.greeting.currencyprojectvendor.MainMenu.Pamount;
import static com.greeting.currencyprojectvendor.MainMenu.Pname;
import static com.greeting.currencyprojectvendor.MainMenu.Pprice;
import static com.greeting.currencyprojectvendor.MainMenu.ReleseQuantity;
import static com.greeting.currencyprojectvendor.MainMenu.EventId;
import static com.greeting.currencyprojectvendor.MainMenu.attended;
import static com.greeting.currencyprojectvendor.MainMenu.vname;

public class AlterEventDetail extends AppCompatActivity {



    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(Actpic.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            int w = proimg.getWidth();
            int h = proimg.getHeight();
            Log.v("test","pic"+ID+" original = "+w+"*"+h);
            int scale = 1;
            if(w>h && (w/360)>1 || h==w && (w/360)>1){
                scale = w/360;
                w = w/scale;
                h = h/scale;
            }else if(h>w && (h/360)>1){
                scale = h/360;
                w = w/scale;
                h = h/scale;
            }
            Log.v("test","pic"+ID+" resized = "+w+"*"+h);
            proimg = Bitmap.createScaledBitmap(proimg, w, h, false);
            return proimg;
        }catch (Exception e){
            Log.v("test","error = "+e.toString());
            return null;
        }


    }

    EditText edtEventDetail, EdtEventName, edtAmount, edtReward;
    DatePicker endApp, actDate;
    TimePicker actStart, actEnd;
    Button alterPic;
    ImageView merPic;
    String EA, AD, AS, AE, ACTN="", DESC="";
    int RW=0, PP=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_event_detail);

        merPic = findViewById(R.id.merPic);
        EdtEventName = findViewById(R.id.EdtEventName);
        edtReward = findViewById(R.id.edtReward);
        edtAmount = findViewById(R.id.edtAmount);
        alterPic = findViewById(R.id.alterPic);
        edtEventDetail = findViewById(R.id.edtEventDetail);
        endApp = findViewById(R.id.endApp);
        actDate = findViewById(R.id.actDate);
        actStart = findViewById(R.id.actStart);
        actEnd = findViewById(R.id.actEnd);
        Button btnChangeConfirm = findViewById(R.id.btnChangeConfirm);

        merPic.setImageBitmap(ConvertToBitmap(EventId));
        EdtEventName.setText(Aname.get(EventId));
        edtAmount.setText(Aamount.get(EventId)+"");
        edtReward.setText(Areward.get(EventId)+"");
        edtEventDetail.setText(Adesc.get(EventId));

        EA = Aendapp.get(EventId).toString();
        AD = AactDate.get(EventId).toString();
        AS = AactStart.get(EventId).toString();
        AE = AactEnd.get(EventId).toString();


        String tmp[];

        tmp = EA.split("-");
        endApp.init(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1])-1,Integer.parseInt(tmp[2]),null);

        tmp = AD.split("-");
        actDate.init(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1])-1,Integer.parseInt(tmp[2]),null);

        tmp = AS.split(":");
        actStart.setCurrentHour(Integer.parseInt(tmp[0]));
        actStart.setCurrentMinute(Integer.parseInt(tmp[1]));


        tmp = AE.split(":");
        actEnd.setCurrentHour(Integer.parseInt(tmp[0]));
        actEnd.setCurrentMinute(Integer.parseInt(tmp[1]));



        alterPic.setOnClickListener(v ->picOpen());


        btnChangeConfirm.setOnClickListener(v -> verifier());
    }

    public void clear(){
        Aid.clear();
        Aname.clear();
        Areward.clear();
        Aamount.clear();
        AamountLeft.clear();
        Adesc.clear();
        Avendor.clear();
        Aendapp.clear();
        AactDate.clear();
        AactStart.clear();
        AactEnd.clear();
        Actpic.clear();
        attended.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AlterEventDetail.this,AlterEvent.class);
        startActivity(intent);
        clear();
        finish();
    }

    int OPEN_PIC = 1021;
    String b64 = Actpic.get(EventId);
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
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri imgdata = data.getData();
            merPic.setImageURI(imgdata);
            merPic.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable)merPic.getDrawable()).getBitmap();
            int w = dataToConvert.getWidth();
            int h = dataToConvert.getHeight();
            int scale = 1;
            if(w>h && (w/360)>1 || h==w && (w/360)>1){
                scale = w/360;
                w = w/scale;
                h = h/scale;
            }else if(h>w && (h/360)>1){
                scale = h/360;
                w = w/scale;
                h = h/scale;
            }
            merPic.setImageBitmap(Bitmap.createScaledBitmap(dataToConvert, w, h, false));
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(AlterEventDetail.this, "請稍後...", Toast.LENGTH_SHORT).show();
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

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");//格式化日期顯示方式
    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");//格式化時間顯示方式

    public void verifier(){
//        error = EdtEventName.getText().toString().trim().isEmpty()?error+"活動名稱, ":error;
//        error = edtAmount.getText().toString().trim().isEmpty()?error+"人數限制, ":error;
//        error = edtEventDetail.getText().toString().trim().isEmpty()?error+"活動說明, ":error;
//
//        if(!error.trim().isEmpty()){
//            error = "請確實填寫"+error.substring(0,error.length()-2);
//            Toast.makeText(AlterEventDetail.this,error,Toast.LENGTH_SHORT).show();///////////////////
//        }else{
//            DESC = edtEventDetail.getText().toString();
//            RW= Integer.parseInt(edtReward.getText().toString().trim());
//            PP = Integer.parseInt(edtAmount.getText().toString().trim());
//            ACTN = EdtEventName.getText().toString();
//            ConnectMySql connectMySql = new ConnectMySql();
//            connectMySql.execute("");
        String error = "";
        ACTN = EdtEventName.getText().toString();
        Log.v("test","ACTN = "+ACTN.trim().isEmpty());
        error = ACTN.trim().isEmpty()?error+"活動名稱, ":error;
        DESC = edtEventDetail.getText().toString();
        error = DESC.trim().isEmpty()?error+"活動說明, ":error;
        RW = Integer.parseInt(edtReward.getText().toString().trim().isEmpty()?"-1":edtReward.getText().toString().trim());
        error = RW==-1?error+"每人獎金, ":error;
        PP = Integer.parseInt(edtAmount.getText().toString().trim().isEmpty()?"-1":edtAmount.getText().toString().trim());
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
            Toast.makeText(AlterEventDetail.this,error,Toast.LENGTH_LONG).show();
        }

    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(AlterEventDetail.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }

        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            ////////////////////////////////////////////
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result = "";
                CallableStatement cstmt = con.prepareCall("{call createActivity(?,?,?,?,?,?,?,?,?,?,?,?)}");
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
                cstmt.setString(12, Aid.get(EventId));
                cstmt.executeUpdate();
                return cstmt.getString(11);

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            try {
                Toast.makeText(AlterEventDetail.this, result, Toast.LENGTH_SHORT).show();
                if (result.contains("成功")) {
                    onBackPressed();
                }
            } catch (Exception e) {
                Log.v("test", "錯誤: " + e.toString());
            }
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
