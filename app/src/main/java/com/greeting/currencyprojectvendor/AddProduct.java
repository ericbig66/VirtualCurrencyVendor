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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;
import static com.greeting.currencyprojectvendor.MainMenu.vname;

public class AddProduct extends AppCompatActivity {

    EditText pid, pname, Pprice, stock;
    ImageView propic;
    Button loadpic, rotate, submit;

    final int OPEN_PIC = 1021;
    String PID = "", PNAME = "", b64 = "";
    int PPRICE = 0, STOCK = 0;

//    public static int DP(float dp){
//        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//        return (int)dp;
//    }
//
//    LinearLayout.LayoutParams landscape = new LinearLayout.LayoutParams(DP(120),DP(90));
//    LinearLayout.LayoutParams portrait = new LinearLayout.LayoutParams(DP(90),DP(120));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_product);

        Log.v("test", "vname = " + vname);

        pid = findViewById(R.id.pid);
        pname = findViewById(R.id.pname);
        Pprice = findViewById(R.id.Pprice);
        stock = findViewById(R.id.stock);

        propic = findViewById(R.id.propic);

        loadpic = findViewById(R.id.loadpic);
//        rotate = findViewById(R.id.rotate);
        submit = findViewById(R.id.submit);

        loadpic.setOnClickListener(v -> picOpen());
//        rotate.setOnClickListener(v -> rotate());
        submit.setOnClickListener(v -> verify());
    }

    //開啟頭像
    public void picOpen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "請選擇商品照片"), OPEN_PIC);
    }

    Bitmap dataToConvert;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        if (requestCode == OPEN_PIC && RESULT_OK == resultCode) {
            Uri imgdata = data.getData();
            propic.setImageURI(imgdata);
            propic.setVisibility(View.VISIBLE);
//            rotate.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable) propic.getDrawable()).getBitmap();
//            rotate.setVisibility(View.VISIBLE);
            propic.setVisibility(View.VISIBLE);
            AddProduct.ConvertToBase64 convertToBase64 = new AddProduct.ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(AddProduct.this, "請稍後...", Toast.LENGTH_SHORT).show();
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
            if (s.contains("成功")) {
                onBackPressed();
            }
        }
    }

    public void verify() {
        PID = pid.getText().toString().trim();
        PNAME = pname.getText().toString().trim();
        PPRICE = Integer.parseInt(Pprice.getText().toString().trim());
        STOCK = Integer.parseInt(stock.getText().toString().trim());
        String error = "";
        error = PID.isEmpty() ? error + "商品編號, " : error;
        error = PNAME.isEmpty() ? error + "商品名稱, " : error;
        error = PPRICE < 1 ? error + "商品價格, " : error;
        error = STOCK < 1 ? error + "庫存量, " : error;
        error = b64.isEmpty() ? error + "商品照片, " : error;
        error = error.isEmpty() ? error : "請確認以下資料是否正確填寫:" + error.substring(0, error.length() - 3);
        if (error.isEmpty()) {
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        } else {
            Toast.makeText(AddProduct.this, error, Toast.LENGTH_LONG);
        }
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(AddProduct.this, "註冊中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {


                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result = "";
                CallableStatement cstmt = con.prepareCall("{call productChange(?,?,?,?,?,?,?)}");
                cstmt.setString(1, vname);
                cstmt.setString(2, PID);
                cstmt.setString(3, PNAME);
                cstmt.setInt(4, PPRICE);
                cstmt.setInt(5, STOCK);
                cstmt.registerOutParameter(6, Types.VARCHAR);
                cstmt.setString(7, b64);
                cstmt.executeUpdate();
                return cstmt.getString(6);

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();

            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AddProduct.this, result, Toast.LENGTH_LONG).show();
        }


    }

    public void onBackPressed() {
        Intent intent = new Intent(AddProduct.this, MainMenu.class);
        startActivity(intent);
        finish();
    }
//    Float degree = 0f;
//    public void rotate(){
//        degree=(degree+90f)>=(360f)?0f:degree+90f;
//        propic.setRotation(degree);
//        if(degree == 90 || degree == 270){
//            propic.setLayoutParams(portrait);
//        }else{
//            propic.setLayoutParams(landscape);
//        }
//        Log.v("test","propic size = "+propic.getWidth()+"*"+propic.getHeight());
//        propic.setScaleType(ImageView.ScaleType.CENTER_CROP);
//    }

}
