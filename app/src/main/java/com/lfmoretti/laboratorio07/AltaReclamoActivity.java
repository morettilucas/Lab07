package com.lfmoretti.laboratorio07;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lfmoretti.laboratorio07.Modelo.Reclamo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AltaReclamoActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 3;

    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/reclamos/";

    private EditText descripcion;
    private EditText telefono;
    private EditText email;
    private Double latitud;
    private Double longitud;
    private String imagePath;
    private ImageView imagenReclamo;

    private Button btnReclamar;
    private Button btnCancelar;
    private ImageButton btnAgregarFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_reclamo);
        setParametros();
        btnReclamar.setOnClickListener(this);
        btnAgregarFoto.setOnClickListener(this);
    }

    private void setParametros() {
        descripcion = (EditText) findViewById(R.id.et_descripcion);
        telefono = (EditText) findViewById(R.id.et_telefono);
        email = (EditText) findViewById(R.id.et_email);
        imagenReclamo = (ImageView) findViewById(R.id.imv_imagen_reclamo);
        btnReclamar = (Button) findViewById(R.id.btn_reclamar);
        btnCancelar = (Button) findViewById(R.id.btn_cancelar);
        btnAgregarFoto = (ImageButton) findViewById(R.id.btn_agregar_foto);
        Intent i = getIntent();
        latitud = i.getDoubleExtra("latitud",0);
        longitud = i.getDoubleExtra("longitud",0);
    }

    //crear reclamo y retornarlo a la vista principal
    private void altaReclamo(){
        Reclamo reclamo = new Reclamo()
                .setTitulo(descripcion.getText().toString())
                .setEmail(email.getText().toString())
                .setTelefono(telefono.getText().toString())
                .setLatitud(latitud)
                .setLongitud(longitud)
                .setImagenPath(imagePath);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        returnIntent.putExtra("result",reclamo);

        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.btn_reclamar:
                altaReclamo();
                break;
            case R.id.btn_cancelar:
                salir();
                break;
            case R.id.btn_agregar_foto:
                dispatchTakePictureIntent();
                break;
        }
    }

    private void salir() {
        setResult(MainActivity.ALTA_RECLAMO_CANCELADO);
        finish();
    }

    private void dispatchTakePictureIntent() {

        //crea la ruta donde se va a guardar la foto
        imagePath = ruta_fotos + getCode() + ".jpg";
        File mi_foto = new File( imagePath );
        try {
            mi_foto.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //si se creó correctamente
        Uri uri = Uri.fromFile( mi_foto );
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @SuppressLint("SimpleDateFormat")
    private String getCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        return "pic_" + date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imagenReclamo.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }
}
