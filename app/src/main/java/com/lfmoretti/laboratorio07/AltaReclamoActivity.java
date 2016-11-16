package com.lfmoretti.laboratorio07;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lfmoretti.laboratorio07.Modelo.Reclamo;

public class AltaReclamoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText descripcion;
    private EditText telefono;
    private EditText email;
    private Double latitud;
    private Double longitud;

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
                .setLongitud(longitud);

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

                break;
        }
    }

    private void salir() {
        setResult(MainActivity.ALTA_RECLAMO_CANCELADO);
        finish();
    }
}
