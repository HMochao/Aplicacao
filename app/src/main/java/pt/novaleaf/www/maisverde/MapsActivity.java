package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    getMarkersTask mMarkersTask = null;
    public static Map<LatLng, String> markers = new HashMap<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //centrar o mapa na ultima localizacao
                centerMapOnLocation(lastKnownLocation);
            }
        }
    }


    public void centerMapOnLocation(Location location) {

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getMarkers();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaOcorrencia();
            }
        });
        Intent i = getIntent();
        boolean toast = i.getBooleanExtra("toast", false);

        if (toast)
            Toast.makeText(this, "Pressionar no local pretendido", Toast.LENGTH_LONG).show();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        } else if(id == R.id.action_logout){
            //TODO: revogar token
            final AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
            alert.setTitle("Terminar sessão");
            alert
                    .setMessage("Deseja terminar sessão?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        } else if(id == R.id.action_acerca){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {

            Intent i = new Intent(MapsActivity.this, FeedActivity.class);
            startActivity(i);
            finish();

        } else if(id == R.id.nav_adicionar_report){

            novaOcorrencia();

        }else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(MapsActivity.this, AreaPessoalActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(MapsActivity.this, GruposMainActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void novaOcorrencia(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setTitle("Criar report");
        alert
                .setMessage("O local do report é a sua localização atual?")
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MapsActivity.this, CriarOcorrenciaActivity.class);
                        intent.putExtra("estaLocal", true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Escolher no mapa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(MapsActivity.this, "Pressionar no local pretendido", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };



        if (Build.VERSION.SDK_INT < 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

            else {
                Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(myIntent);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                centerMapOnLocation(lastKnownLocation);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }


        }



        //lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        // Add a marker in Sydney and move the camera
        LatLng currrPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(currrPos).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currrPos, 15));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                //Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
                alert.setTitle("Criar report");
                    alert
                        .setMessage("Quer fazer um report nesta localização?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MapsActivity.this, CriarOcorrenciaActivity.class);
                                intent.putExtra("lat", latLng.latitude);
                                intent.putExtra("lon", latLng.longitude);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

                /*
                TODO:
                perguntar se quer criar a ocorrencia nesse sitio, se sim:
                ir para o criar ocorrencia ativity, levando com ele as coordenadas
                quando se cria a ocorrencia vai-se para o maps ativity
                levando o titulo e a descricao, com que se vai criar o marker
                (possivelmente atualizando logo na criar ocorrencia)
                mMap.addMarker(new MarkerOptions().position(latLng).title(morada));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                */

            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng botLeft = mMap.getProjection().getVisibleRegion().nearLeft;
                LatLng topRight = mMap.getProjection().getVisibleRegion().farRight;
                updateMarkers(botLeft, topRight);
            }
        });
    }

    //TODO: ver que marcadores estao nesta area
    public void getMarkers(){


        mMarkersTask = new getMarkersTask();
        mMarkersTask.execute((Void) null);

    }

    public void updateMarkers(LatLng botLeft, LatLng topRight){

        List<LatLng> list = new ArrayList<>();
        for (LatLng latLng : markers.keySet()){
            if(latLng.latitude>=botLeft.latitude && latLng.longitude>=botLeft.longitude
                    && latLng.latitude<=topRight.latitude && latLng.longitude <= topRight.longitude) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(markers.get(latLng)));
                list.add(latLng);}
        }
        markers.keySet().removeAll(list);

    }



    public class getMarkersTask extends AsyncTask<Void, Void, String> {


        /**
        private final Double mLatBotLeft;
        private final Double mLonBotLeft;
        private final Double mLatTopRight;
        private final Double mLonTopRight;
         */

        getMarkersTask() {
            /**
            mLonBotLeft = botLeft.longitude;
            mLatBotLeft = botLeft.latitude;
            mLonTopRight = topRight.longitude;
            mLatTopRight = topRight.latitude;*/
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //TODO: create JSON object with credentials and call doPost

                //JSONObject botLeft = new JSONObject();
                //JSONObject topRight = new JSONObject();
                //JSONObject jsonObject = new JSONObject();
                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

                String token = sharedPreferences.getString("tokenID", "erro");
                /*
                botLeft.put("latitude", mLatBotLeft);
                botLeft.put("longitude", mLonBotLeft);
                topRight.put("latitude", mLatTopRight);
                topRight.put("longitude", mLonTopRight);
                jsonObject.put("bottomLeft", botLeft);
                jsonObject.put("topRight", topRight);
                */
                URL url = new URL("https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/mymarkers");
                return RequestsREST.doGET(url, token);
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mMarkersTask = null;

            if (result != null) {
                JSONArray token = null;
                try {
                    // We parse the result
                    Log.i("TOKENMARKERS", result);

                    token = new JSONArray(result);
                    Log.i("TOKENMARKERS", token.toString());
                    // TODO: store the token in the SharedPreferences

                    SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

                    for (int i = 0; i< token.length(); i++){
                        JSONObject marker = token.getJSONObject(i);
                        Double lat = marker.getJSONObject("coordinates").getDouble("latitude");
                        Double lon = marker.getJSONObject("coordinates").getDouble("longitude");
                        String titulo = marker.getString("name");
                        String descricao = marker.getString("description");

                        LatLng position = new LatLng(lat, lon);
                        if (!markers.keySet().contains(position)) {
                            markers.put(position, titulo);
                        }

                    }


                } catch (JSONException e) {
                    // WRONG DATA SENT BY THE SERVER

                    Log.e("Authentication", e.toString());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mMarkersTask = null;

        }
    }





}
