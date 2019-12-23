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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import static com.greeting.currencyprojectvendor.Login.acc;
import static com.greeting.currencyprojectvendor.Login.pass;
import static com.greeting.currencyprojectvendor.Login.url;
import static com.greeting.currencyprojectvendor.Login.user;
import static com.greeting.currencyprojectvendor.MainMenu.PID;
import static com.greeting.currencyprojectvendor.MainMenu.PIMG;
import static com.greeting.currencyprojectvendor.MainMenu.Pamount;
import static com.greeting.currencyprojectvendor.MainMenu.Pname;
import static com.greeting.currencyprojectvendor.MainMenu.Pprice;
import static com.greeting.currencyprojectvendor.MainMenu.ReleseQuantity;
import static com.greeting.currencyprojectvendor.MainMenu.SellId;
import static com.greeting.currencyprojectvendor.MainMenu.vname;
import static com.greeting.currencyprojectvendor.Register.OPEN_PIC;

public class AlterProductDetail extends AppCompatActivity {
    
    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(PIMG.get(ID), Base64.DEFAULT);
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

    EditText Qt, EdtProductName, edtProductPrice, edtProductID;
    RadioButton RadioShelves, RadioTakeOff;
    Button alterPic;
    ImageView merPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_product_detail);
        merPic = findViewById(R.id.merPic);
        merPic.setImageBitmap(ConvertToBitmap(SellId));

        EdtProductName = findViewById(R.id.EdtProductName);
        EditText edtVdrName = findViewById(R.id.edtVdrName);
        edtProductID = findViewById(R.id.edtProductID);
        EditText edtProductAmount = findViewById(R.id.edtProductAmount);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        EdtProductName.setText(Pname.get(SellId));
        edtVdrName.setText(vname);
        edtProductID.setText(PID.get(SellId));
        edtProductAmount.setText(Pamount.get(SellId)+"");
        edtProductPrice.setText(Pprice.get(SellId)+"");

        RadioShelves=findViewById(R.id.RadioShelves);
        RadioTakeOff=findViewById(R.id.RadioTakeOff);
        alterPic = findViewById(R.id.alterPic);
        alterPic.setOnClickListener(v ->picOpen());
        Button btnChangeConfirm=findViewById(R.id.btnChangeConfirm);

        Qt = findViewById(R.id.Qt);
        Qt.setText(ReleseQuantity+"");

//      Button btnChangeConfirm = findViewById(R.id.btnChangeConfirm);
        btnChangeConfirm.setOnClickListener(v -> verifier());
    }

//    public void onRadioButtonClicked(View view){
//        boolean checked=((RadioButton) view).isChecked();
//        switch (view.getId()){
//            case R.id.RadioShelves:
//                if(checked)
//                    break;
//            case R.id.RadioTakeOff:
//                if (checked);
//                break;
//        }
//    }

    public void clear(){
        PID.clear();
        Pname.clear();
        Pprice.clear();
        Pamount.clear();
        PIMG.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AlterProductDetail.this,AlterProduct.class);
        startActivity(intent);
        clear();
        finish();
    }

    int OPEN_PIC = 1021;
    String b64 = PIMG.get(SellId);
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
            merPic.setVisibility(View.VISIBLE);
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(AlterProductDetail.this,"請稍後...",Toast.LENGTH_SHORT).show();
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
    

    String error = "", pid, pname;
    int quantity = 0, price;
    public void verifier(){
        error = EdtProductName.getText().toString().trim().isEmpty()?error+"商品名稱, ":error;
        error = edtProductPrice.getText().toString().trim().isEmpty()?error+"商品價格, ":error;
        if(Qt.getText().toString().trim().isEmpty()){Qt.setText("0");}
        else if(RadioShelves.isChecked()){quantity = Integer.parseInt(Qt.getText().toString());}
        else if(RadioTakeOff.isChecked()){quantity = Integer.parseInt(Qt.getText().toString())*-1;}

        if(!error.trim().isEmpty()){
            error = "請確實填寫"+error.substring(0,error.length()-2);
            Toast.makeText(AlterProductDetail.this,error,Toast.LENGTH_SHORT).show();
        }else{
            pid= edtProductID.getText().toString();
            price = Integer.parseInt(edtProductPrice.getText().toString().trim());
            pname = EdtProductName.getText().toString();
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }
        


    }


    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(AlterProductDetail.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            ////////////////////////////////////////////
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                CallableStatement cstmt = con.prepareCall("{call productChange(?,?,?,?,?,?,?)}");
                cstmt.setString(1, vname);
                cstmt.setString(2, pid);
                cstmt.setString(3, pname);
                cstmt.setInt(4, price);
                cstmt.setInt(5,quantity);
                cstmt.registerOutParameter(6, Types.VARCHAR);
                cstmt.setString(7,b64);
                cstmt.executeUpdate();
                Log.v("test","info updated:\nvname ="+vname+"\npid ="+pid+"\npname ="+pname+"\nprice ="+price+"\nquantity ="+quantity);
                return cstmt.getString(6);

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            try{
                Toast.makeText(AlterProductDetail.this, result, Toast.LENGTH_SHORT).show();
                if(result.contains("成功")){
                    onBackPressed();
                }
            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }

        }
    }

    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
