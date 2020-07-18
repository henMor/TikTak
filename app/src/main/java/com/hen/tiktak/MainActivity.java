package com.hen.tiktak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    final private int MY_PERMISSION_OK = 2;

    final private int CAMERA_REQUEST = 1;

    int GALLERY_PICTURE = 123;

    private String currentMonth;
    private String currentYear;

    private String backendlessObjectId = "";
    TextView lblLoginUser;
    TextView lblLOgOut;


    File file2;
String fileNamePdf = "";


    private String userName = "";
    private String fileName = "";
    private Context context;
    private ImageView myImage;

    Button btnFromGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermissions();

        Backendless.initApp(this,"159BF6E9-530E-BB43-FF6D-F2C8D9357F00","081951EE-7922-4E56-FFBA-10FF417DA400");



        setPointer();



        // UserTokenStorageFactory is available in the com.backendless.persistence.local package

  //      String userToken = UserTokenStorageFactory.instance().getStorage().get();


        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if (response) {
                    Log.e("logged", "handleResponse: " + response);
                    backendlessObjectId = UserIdStorageFactory.instance().getStorage().get();



                    Backendless.Data.of( BackendlessUser.class ).findById(
                            backendlessObjectId, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser response) {
                                    lblLoginUser.setText("שלום " +response.getProperty("name").toString());
                                    userName = response.getProperty("userName").toString();



                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });

                    Log.e("logged", "handleResponse: object id " + backendlessObjectId);
                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.e("logged", "handleResponse: " + fault.getMessage() );

            }
        });




        Date date1 = new Date();
        //    SimpleDateFormat formatNowDay = new SimpleDateFormat("dd");
        SimpleDateFormat formatNowMonth = new SimpleDateFormat("MM");
        SimpleDateFormat formatNowYear = new SimpleDateFormat("YYYY");

        //     String currentDay = formatNowDay.format(date1);
         currentMonth = formatNowMonth.format(date1);
         currentYear = formatNowYear.format(date1);

    }

    private void setPointer() {
        this.context = this;

        btnFromGallery = findViewById(R.id.btnGallery);

        btnFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lblLoginUser.getText().toString().equalsIgnoreCase("התחברות")) {

                    Toast.makeText(context, "אינך מחובר למערכת", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(MainActivity.this,MyGridView.class);
                    startActivity(intent);

                    /*
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_PICTURE);

                */

                }

            }
        });

        this.myImage = findViewById(R.id.imgPic);
        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lblLoginUser.getText().toString().equalsIgnoreCase("התחברות")) {

                    Toast.makeText(context, "אינך מחובר למערכת", Toast.LENGTH_SHORT).show();
                } else {
                    //call camera intent
                    dispatchCamera();
                }
            }

        });


        lblLOgOut = findViewById(R.id.lbllogOut);

        lblLOgOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Log.e("logOut", "handleResponse: " + response );

                        lblLoginUser.setText("התחברות");


                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Log.e("logOut", "handleFault: " + fault.getMessage() );
                    }
                });
            }
        });


        lblLoginUser = findViewById(R.id.lblLOgin);
/*

        String userToken = UserTokenStorageFactory.instance().getStorage().get();

        if( userToken != null && !userToken.equals("") )
        {
            lblLoginUser.setText(" שלום" +userToken );
        }
*/

//       boolean check = Backendless.UserService.isValidLogin();

     //   Log.e("check", "setPointer: " + check );





        lblLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(lblLoginUser.getText().toString().equalsIgnoreCase("התחברות")){

                    alertDialogLogin();

                }
            }
        });
    }



    private boolean checkForPermissions () {
        Log.e("CHK permission", "checkForPermissions: checking permission");
        //we create a list of permission to ask, so we can add more later on.
        List<String> listPermissionsNeeded = new ArrayList<>();
        //check if we have a permission for camera
        int camPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //we don't have the permission
        if (writePerm == PackageManager.PERMISSION_GRANTED && camPerm == PackageManager.PERMISSION_GRANTED && readPerm == PackageManager.PERMISSION_GRANTED) {
            //we have a permission we can move next
            return true;
        }
               listPermissionsNeeded.add(Manifest.permission.CAMERA);
        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSION_OK);
        }
        return false;
    }



    //we have a feedback from the user for permission
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        //checking if we got a permission result
        Log.e("camera", "onRequestPermissionsResult: request");
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission", "onRequestPermissionsResult: camera true ");
            //       dispatchTakeImageIntent();
        } else {
            //tell the user why we can not take pictures.
            //     Toast.makeText(context, "We can not take picture without permission", Toast.LENGTH_SHORT).show();
        }
    }



    private void dispatchCamera() {
        String imageFile = UUID.randomUUID().toString();

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        fileName = Environment.DIRECTORY_PICTURES + File.separator
                + imageFile + ".jpg";
        file2 = new File(fileName);

        Log.e("image file", "dispatchCamera: " + fileName);
        //.getExternalStorageDirectory() -> root storage of external storage (for moti by arial request)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, file2);

        try {
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            Log.e("camera", "dispatchCamera: error in dispatch camera intent");
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

/*
        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {


            Uri imageUri = data.getData();

            try {


                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

           //     bitmap = selectedImage;

                myImage.setImageBitmap(selectedImage);

                createPdf(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
*/
          if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            //create bounds
          //  String imageFile = UUID.randomUUID().toString();

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            createPdf(thumbnail);
            
            /*
            
            
            String backendlessPath = "users/"+userName+"/"+currentYear+"/"+currentMonth;

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            Backendless.Files.Android.upload(thumbnail, Bitmap.CompressFormat.PNG, 100, imageFile+".pdf",
                    backendlessPath, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {

                            Toast.makeText( MainActivity.this,
                                    "Upload Ok",
                                    Toast.LENGTH_SHORT ).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText( MainActivity.this,
                                    fault.toString(),
                                    Toast.LENGTH_SHORT ).show();
                        }
                    });
            
            */
            
            myImage.setImageBitmap(thumbnail);

        }

    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);


        return imageEncoded;
    }


    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);


        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }





    public void alertDialogLogin() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View adView = inflater.inflate(R.layout.alertdialog_login,null,false);

        Button btnLoginAlert = adView.findViewById(R.id.btnNewPlayerAlertAdd);
        Button btnCancelAlert = adView.findViewById(R.id.btnNewPlayerAlertCancel);
        final EditText txtMailLogin = adView.findViewById(R.id.txtMailLogin);
        final  EditText txtPassLogin = adView.findViewById(R.id.txtPassLogin);


        builder.setView(adView);

        final AlertDialog dialog = builder.create();


        txtMailLogin.requestFocus();

        txtMailLogin.postDelayed(new Runnable() {

            @Override
            public void run() {


                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(txtMailLogin, 0);

            }
        },100); //use 300 to make it run when coming back from lock screen








        btnCancelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        btnLoginAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Backendless.UserService.login(txtMailLogin.getText().toString(), txtPassLogin.getText().toString(), new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {

                        userName = response.getProperty("userName").toString();

                        lblLoginUser.setText("שלום " + response.getProperty("name").toString());

                        dialog.dismiss();

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(context, fault.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, true);

            }
        });



        dialog.show();



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

        fileNamePdf = Environment.DIRECTORY_PICTURES + File.separator
                + "test1" + ".pdf";

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

                Toast.makeText(MainActivity.this,"OKKK", Toast.LENGTH_LONG).show();



            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(MainActivity.this,fault.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }

    }