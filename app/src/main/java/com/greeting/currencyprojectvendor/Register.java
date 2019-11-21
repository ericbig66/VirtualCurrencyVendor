package com.greeting.currencyprojectvendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {

    private static final String url = "jdbc:mysql://140.135.113.196:3360/virtualcurrencyproject";
    private static final String user = "currency";
    private static final String pass = "@SAclass";
    static final int OPEN_PIC = 1021;

    EditText name, em, pwd, chkpwd, phone;
    Button pic, reg, login, clr;

    //裝載轉換出的EditText中的文字
    String NAME="", EM="", PH="", PWD = "", CHKPWD="";
    //清除所有填寫的資料(會被重新填寫按鈕呼叫或註冊成功時會被呼叫)
    public void clear(){
        name.setText("");
        em.setText("");
        pwd.setText("");
        chkpwd.setText("");
        NAME="";
        EM="";
        PWD = "";
    }
    //切換回登入模式(被該按鈕呼叫)
    public void swlogin(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    //檢查填寫資料正確性(按下註冊鈕後呼叫)
    public void verify(){
        boolean haveError = false;
        String err ="";
        err = NAME.trim().isEmpty()?err+="公司名稱,":err;
        err = PWD.trim().isEmpty()?err+="密碼,":err;
        err = CHKPWD.trim().isEmpty()?err+="確認密碼,":err;
        err = EM.trim().isEmpty()?err+="E-mail,":err;
        err = PH.trim().isEmpty()?err+="公司電話號碼":err;
        err = err.isEmpty()?err:err.substring(0, err.length() - 1);
        if(!err.isEmpty()){err+=" 為必填項目\n請確認是否已填寫!";}
        haveError = !err.isEmpty();
        if(haveError){
            Toast.makeText(Register.this, err, Toast.LENGTH_LONG).show();}
        err = "";
        if(!PWD.trim().isEmpty() && !CHKPWD.trim().isEmpty() && !PWD.equals(CHKPWD)){
            err += "您輸入的密碼前後不一致，請重新輸入\n";
            chkpwd.setText("");
            CHKPWD = "";
            haveError = true;
        }

        if ( !EM.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(EM).matches() ) {
            err += "請輸入正確的電子郵件地址";
            em.setText("");
            EM = "";
            haveError = true;
        }
        if(haveError && !err.trim().isEmpty()){Toast.makeText(Register.this, err, Toast.LENGTH_LONG).show();}
        if(!haveError){
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        name = findViewById(R.id.name);
        em = findViewById(R.id.em);
        pwd = findViewById(R.id.pwd);
        chkpwd = findViewById(R.id.chkpwd);
        phone = findViewById(R.id.phone);
        pic = findViewById(R.id.pic);
        reg = findViewById(R.id.reg);
        login = findViewById(R.id.login);
        clr = findViewById(R.id.clr);
        pic.setOnClickListener(v -> picOpen());

        login.setOnClickListener(v -> swlogin());

        reg.setOnClickListener(v -> {
            closekeybord();
            NAME = name.getText().toString();
            EM = em.getText().toString();
            PH = phone.getText().toString();
            PWD = pwd.getText().toString();
            CHKPWD = chkpwd.getText().toString();
            verify();
        });

        clr.setOnClickListener(v -> clear());

    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(Register.this,"註冊中...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{


                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result ="";
                CallableStatement cstmt = con.prepareCall("{call vregister(?,?,?,?,?)}");
                cstmt.setString(1, NAME);
                cstmt.setString(2, PWD);
                cstmt.setString(3, PH);
                cstmt.setString(4, EM);
                cstmt.registerOutParameter(5, Types.VARCHAR);
                cstmt.executeUpdate();
                return cstmt.getString(5);

            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();

            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(Register.this, result, Toast.LENGTH_SHORT).show();
            if(result.equals("註冊成功!")){
                clear();
                swlogin();
                finish();
            }


        }


    }
    //********************************************************************************************
    //開啟頭像
    public void picOpen(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, OPEN_PIC);
    }
    //取得圖片路徑
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri uri = data.getData();
            try{
                String[] projection = {MediaStore.Images.Media.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                String path = cursor.getString(colum_index);
                encode(path);

            }catch (Exception e){
                Toast.makeText(Register.this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
    //將圖片編碼為base64
    private void encode(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, boas);
        byte[] bytes = new  byte[100];//???????????
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        String encodeString = new String(encode);
        Toast.makeText(Register.this, encodeString, Toast.LENGTH_LONG).show();
    }

}

