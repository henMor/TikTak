package com.hen.tiktak;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.tuanchauict.intentchooser.ImageChooserMaker;
import com.tuanchauict.intentchooser.selectphoto.ImageChooser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyGridView extends AppCompatActivity {


    TextView lblLabelTop;

    int PICK_IMAGE_MULTIPLE = 888;

    TextView lblUploadOk;
    Button btnUpload;
    GridView gridView;



    GridViewAdapter gridViewAdapter;
    String imageEncoded;
    List<String> imagesEncodedList;

    List<Bitmap>lstBitmap;

    ImageView imageView;
    ProgressBar progressBar;

    List<String>listPath;
    String userName = "moshe";
    String currentYear = "2019";
    String currentMonth = "11";

    List<Uri> imageUris;

    int countSAveDB = 0;
    int sendCount = 0;
  //  int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grid_view);

        startImageChooserActivity();

        listPath = new ArrayList<>();

        lblLabelTop = findViewById(R.id.lblNumOfTax);
        lblUploadOk = findViewById(R.id.lblUploadOk);
        progressBar = findViewById(R.id.prograss);
        btnUpload = findViewById(R.id.btnUploadImg);
        gridView = findViewById(R.id.gridView);


        btnUpload.setVisibility(View.INVISIBLE);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                for (Bitmap bitmap : lstBitmap){


                    String myBackendlgessPath ="users/"+userName+"/"+currentYear+"/"+currentMonth;

                    String imageFile = UUID.randomUUID().toString();

 /*
                String  fileName = Environment.DIRECTORY_PICTURES + File.separator
                            + imageFile + ".jpg";


                    File f = new File(fileName);


  */

                    Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 100, imageFile + ".png",
                            myBackendlgessPath, new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(BackendlessFile response) {



                                    countSAveDB++;
                                    Toast.makeText(MyGridView.this, "OK ", Toast.LENGTH_LONG).show();

                                    Log.e("count", "countSaveDB: " + countSAveDB + " lstBitmap " + lstBitmap.size() );

                                    if (countSAveDB == lstBitmap.size()){


                                        progressBar.setVisibility(View.INVISIBLE);
                                        btnUpload.setVisibility(View.INVISIBLE);
                                        lblUploadOk.setVisibility(View.VISIBLE);

                                        lblLabelTop.setText(" ");

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();

                                            }
                                        },3300);

                                    }



                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });



                    //   createPdf(bitmap);
                }

              //  startImageChooserActivity();

                //  UploadImages();
            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {



        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {
            imageUris = ImageChooserMaker.getPickMultipleImageResultUris(this, data);

            Log.e("Uri", "onActivityResult: " + imageUris.size() );

        //    count = imageUris.size();

            new LoadImageDataTask(imageUris).execute();

 /*
            for (Uri uri : imageUris) {

                //      count++;
                new LoadImageDataTask(uri).execute();
            }

*/
        }

        super.onActivityResult(requestCode, resultCode, data);



    }


    private void GridBitmap() {




        /*
        for (int i=0;lstUri.size()>i;i++){


            try {

                InputStream imageStream = getContentResolver().openInputStream(lstUri.get(i));
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                lstBitmap.add(selectedImage);


                //     bitmap = selectedImage;

            //    myImage.setImageBitmap(selectedImage);

           //     createPdf(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

*/


    }


    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);



        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);


    }



    private void startImageChooserActivity() {
        Intent intent = ImageChooserMaker.newChooser(MyGridView.this)
                .add(new ImageChooser(true))
                .create("Select Image");
        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
    }



    public void imageResults(Uri uri) {

        try {

            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(selectedImage);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);


            //   data1 = stream.toByteArray();

            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), selectedImage);
            //      imgPicture.setBackgroundDrawable(bitmapDrawable);


            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bmp = bitmapDrawable.getBitmap();
            Bitmap b = Bitmap.createScaledBitmap(bmp, 200, 200, false);

            imageView.setVisibility(View.INVISIBLE);


            lstBitmap.add(b);

            //       imageView.setImageBitmap(b);

            //       lblCbImage.setText(position + "/6");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    private void createPdf(Bitmap bitmap){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);



        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);



        // write the document content
        //     String targetPdf = "/sdcard/test.pdf";


        //     fileNamePdf = Environment.DIRECTORY_PICTURES + File.separator
        //           + "test1" + ".pdf";

        String imageFile = UUID.randomUUID().toString();


        File newPath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS+"/"+imageFile+".pdf")));


        //    File backendlessPath = new File(backendleesPathRest);


        //   File filePath = new File(fileNamePdf);
        try {
            document.writeTo(new FileOutputStream(newPath));
//            btn_convert.setText("Check PDF");
            //           boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }


        // close the document


        document.close();




        String myBackendlessPath ="users/"+userName+"/"+currentYear+"/"+currentMonth;


        Backendless.Files.upload(newPath, myBackendlessPath, true, new AsyncCallback<BackendlessFile>() {

            @Override
            public void handleResponse(BackendlessFile response) {

                Toast.makeText(MyGridView.this,"OKKK", Toast.LENGTH_LONG).show();

                sendCount++;

                if (sendCount == lstBitmap.size()){
                    progressBar.setVisibility(View.INVISIBLE);
                    btnUpload.setVisibility(View.INVISIBLE);
                    finish();

                }

            }


            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(MyGridView.this,fault.getMessage(),Toast.LENGTH_SHORT).show();
            }



        });



    }



    private class LoadImageDataTask extends AsyncTask<Void, Void, List<Bitmap>> {

        private List<Uri> imagePath;

        LoadImageDataTask(List<Uri>imagePath) {
            this.imagePath = imagePath;
            lstBitmap = new ArrayList<>();

        }

        @Override
        protected List<Bitmap> doInBackground(Void... params) {



            for (Uri image : imagePath) {


                try {

                    InputStream imageStr = getContentResolver().openInputStream(image);

                    lstBitmap.add(BitmapFactory.decodeStream(imageStr));


                //    InputStream imageStream = getContentResolver().openInputStream(image);
                //    return BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    Toast.makeText(MyGridView.this, "The file " + image + " does not exists",
                            Toast.LENGTH_SHORT).show();
                }



            }
                return lstBitmap;


        }

        @Override
        protected void onPostExecute(List<Bitmap> lstBitmap) {
            super.onPostExecute(lstBitmap);


          //  lstBitmap.add(bitmap);

            lblLabelTop.setText( "  "+ lstBitmap.size() + " קבצים נבחרו   לאישור לחץ על שלח"  );
            gridViewAdapter = new GridViewAdapter(lstBitmap, MyGridView.this);
            gridView.setAdapter(gridViewAdapter);
            btnUpload.setVisibility(View.VISIBLE);


    //        Toast.makeText(MyGridView.this, "I got the image data, with size: " +
      //                      Formatter.formatFileSize(MyGridView.this, bitmap.getByteCount()),
        //            Toast.LENGTH_SHORT).show();


/*
            if (lstBitmap.size() == imagePath.size()) {

                gridViewAdapter = new GridViewAdapter(lstBitmap, MyGridView.this);
                gridView.setAdapter(gridViewAdapter);
                btnUpload.setVisibility(View.VISIBLE);
            }
*/
        }
    }





}
