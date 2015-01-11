package com.deal.aje.deal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;


public class SellItem extends ActionBarActivity {
    EditText et_hashatg, et_desc;
    Button btn_pic, btn_next;
    ImageView iv_img;
    byte[] img_byteArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item);
        et_hashatg = (EditText) findViewById(R.id.editText_hashtag_sell);
        et_desc = (EditText) findViewById(R.id.editText_desc_sell);
        btn_pic = (Button) findViewById(R.id.btn_pict);
        btn_next = (Button) findViewById(R.id.btn_sell);
        iv_img = (ImageView) findViewById(R.id.imageView_item_image);

        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
                /*
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);

                Intent i = new Intent(ntent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                */
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO bring to next intent
                String hashtag = et_hashatg.getText().toString();
                String desc = et_desc.getText().toString();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_img.setImageBitmap(imageBitmap);
            //compressing to byte array
            ByteArrayOutputStream img_stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, img_stream);
            img_byteArray = img_stream.toByteArray();
            Toast.makeText(this, "byte array Image size: "+ img_byteArray.length ,
                    Toast.LENGTH_LONG).show();
        }
        /*
        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            String photo_uri = (targetUri.toString());
            Toast.makeText(this, "Target URI: "+photo_uri ,
                    Toast.LENGTH_LONG).show();
            //itm_img.setImageURI(targetUri);

            Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(targetUri.toString(),options);
            itm_img.setImageBitmap(bitmap);
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                itm_img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                Toast.makeText(this, "catch setImage " ,
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sell_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
