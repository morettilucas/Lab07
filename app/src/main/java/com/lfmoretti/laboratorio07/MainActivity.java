package com.lfmoretti.laboratorio07;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lfmoretti.laboratorio07.Modelo.Reclamo;

import java.util.ArrayList;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    protected static final int ALTA_RECLAMO_OK = 2;
    protected static final int ALTA_RECLAMO_CANCELADO = 3;

    private GoogleMap mMap;
    private ArrayList<Reclamo> reclamos;
    private ArrayList<Polyline> polylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reclamos = new ArrayList<>();
        polylines = new ArrayList<>();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable("reclamos",reclamos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        reclamos = (ArrayList<Reclamo>) savedInstanceState.getSerializable("reclamos");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        for (Reclamo r : reclamos) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(r.getLatitud(),  r.getLongitud()))
                    .title("Reclamo de "+r.getEmail())
                    .snippet(r.getTitulo())
                    .draggable(true));
        }
        visualizarDistancias();
        setLocationWithPermission();
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        //mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

    }

    public void setLocationWithPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Ubicación actual");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Necesitamos obtener tu posición actual. Nos das permiso?");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(new String[] {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                        }
                });
                    builder.show();
                } else {
                    requestPermissions(new String[] {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
            else{
                // ya tenia el permiso no lo tuve que pedir
                mMap.setMyLocationEnabled(true);            }
        }
        else{
            // la versión alcanza con tenerlo declarado
            mMap.setMyLocationEnabled(true);        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
        switch (requestCode) {
            case MainActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // si el request es cancelado el arreglo es vacio.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No Permiso", Toast.LENGTH_SHORT).show();
                    // no tenemos permisos
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //llamar a la actividad para crear un reclamo
        Intent i = new Intent(this,AltaReclamoActivity.class);
        i.putExtra("latitud",latLng.latitude);
        i.putExtra("longitud",latLng.longitude);
        startActivityForResult(i,ALTA_RECLAMO_OK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==ALTA_RECLAMO_OK){
            if(resultCode==Activity.RESULT_OK && data!=null){
                Reclamo reclamoAux = (Reclamo) data.getExtras().getSerializable("result");
                if(reclamoAux!=null) {
                    reclamos.add(reclamoAux);

                    //creamos el marcador y lo asociamos a un reclamo
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(reclamoAux.getLatitud(), reclamoAux.getLongitud()))
                            .title("Reclamo de "+reclamoAux.getEmail())
                            .snippet(reclamoAux.getTitulo())
                            .draggable(true)); //TODO implementar
                    Toast.makeText(getApplicationContext(), "Reclamo marcado", Toast.LENGTH_SHORT).show();
                }
            }
            else if (resultCode==ALTA_RECLAMO_CANCELADO){}
            else{
                Log.v("info","error data null o codigo");
                Toast.makeText(getApplicationContext(),"Ha ocurrido un error inesperado. Por favor, intente nuevamente",Toast.LENGTH_LONG).show();

            }
        }
        else{
            Log.v("info","error request code null");
            Toast.makeText(getApplicationContext(),"Ha ocurrido un error inesperado. Por favor, intente nuevamente",Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInfoWindowClick(final Marker marker) {
        final EditText distancia = new EditText(getApplicationContext());
        distancia.setInputType(InputType.TYPE_CLASS_NUMBER);
        distancia.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Black));
        distancia.setHint("Distancia máxima en km");


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Mostrar reclamos cercanos")
                .setPositiveButton(android.R.string.search_go, null)
                .setCancelable(true)
                .setView(distancia)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!Objects.equals(distancia.getText().toString(),"")) {
                            float distanciaMaxima = 1000 * Float.parseFloat(distancia.getText().toString()); //distancia en metros
                            ArrayList<Reclamo> reclamosCercanos = buscarReclamosCercanos(marker.getPosition(), distanciaMaxima);
                            visualizarReclamosCercanos(marker.getPosition(), reclamosCercanos);
                        }
                    }
        });
        builder.show();
    }

    private ArrayList<Reclamo> buscarReclamosCercanos(LatLng posOrigen, float distanciaMaxima) {
        ArrayList<Reclamo> reclamosCercanos = new ArrayList<>();
        for(Reclamo r: reclamos){
            float[] results = new float[2];
            Location.distanceBetween(posOrigen.latitude,posOrigen.longitude,r.getLatitud(),r.getLongitud(),results);
            if(results[0]<distanciaMaxima){
                reclamosCercanos.add(r);
            }
        }
        return reclamosCercanos;
    }

    private void visualizarReclamosCercanos(LatLng posOrigen, ArrayList<Reclamo> reclamosCercanos) {
        for(Reclamo r: reclamosCercanos){
            PolylineOptions distancia = new PolylineOptions();
            distancia.add(posOrigen,new LatLng(r.getLatitud(),r.getLongitud()));
            polylines.add(mMap.addPolyline(distancia));
        }
    }

    private void visualizarDistancias(){
        for(Polyline distancia: polylines){
            mMap.addPolyline(new PolylineOptions().addAll(distancia.getPoints()));
        }
    }

    private void removerDistancias(){
        for(Polyline distancia: polylines){
            distancia.remove();
        }
        polylines.clear();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        removerDistancias();
    }

    /*
    private Reclamo findReclamoByLatLng(LatLng latlng){
        for(Reclamo r: reclamos){
            if(r.getLatitud()==latlng.latitude && r.getLongitud()==latlng.longitude){
                return r;
            }
        }
        return null;
    }


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_marker, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            Reclamo reclamo = findReclamoByLatLng(marker.getPosition());

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());
            ImageView imvImagen = (ImageView) myContentsView.findViewById(R.id.imagen);
            imvImagen.setImageBitmap(BitmapFactory.decodeFile(reclamo.getImagenPath()));

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }
*/
}