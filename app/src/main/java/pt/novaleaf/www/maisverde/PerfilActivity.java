package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Hugo Mochao
 * Atividade que serve para modificar os dados de um utilizador
 */

public class PerfilActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    public static ArrayList<PerfilItem> arrayList;
    public static RecyclerView mRecyclerViewPerfil;
    public static MyPerfilRecyclerViewAdapter adapter;
    public static SharedPreferences sharedPreferences;
    public static boolean changed = false;

    TextView textUsername;
    NavigationView navigationView;
    Toolbar toolbar;
    ConstraintLayout constraintLayout;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        constraintLayout = (ConstraintLayout) findViewById(R.id.mConstr);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changed) {
                    changed = false;
                    attemptSendData();
                }
                finish();
            }
        });
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        textUsername = (TextView) findViewById(R.id.textUsername);
        sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

        textUsername.setText(sharedPreferences.getString("username", "User"));

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(4).setChecked(true);


        mRecyclerViewPerfil = (RecyclerView) findViewById(R.id.myList);
        email = sharedPreferences.getString("email", "erro");

        //Ir buscar a informacao do utilizador
        arrayList = new ArrayList<>();
        //arrayList.add(new PerfilItem("Username", sharedPreferences.getString("username", "erro")));
        arrayList.add(new PerfilItem("Email", email));
        arrayList.add(new PerfilItem("Nome", sharedPreferences.getString("name", "")));
        arrayList.add(new PerfilItem("Aprovação dos reports", sharedPreferences.getString("approval_rate", "erro")));
        arrayList.add(new PerfilItem("Reports efetuados", sharedPreferences.getString("numb_reports", "erro")));
        arrayList.add(new PerfilItem("Morada", sharedPreferences.getString("firstaddress", "")));
        arrayList.add(new PerfilItem("Morada complementar", sharedPreferences.getString("complementaryaddress", "")));
        arrayList.add(new PerfilItem("Localidade", sharedPreferences.getString("locality", "")));
        arrayList.add(new PerfilItem("Código Postal", sharedPreferences.getString("postalcode", "")));
        arrayList.add(new PerfilItem("Telemóvel", sharedPreferences.getString("mobile_phone", "")));
        arrayList.add(new PerfilItem("Mudar a password", ""));

        adapter = new MyPerfilRecyclerViewAdapter(this, arrayList);
        mRecyclerViewPerfil.setLayoutManager(new LinearLayoutManager(this));

        float density = this.getResources().getDisplayMetrics().density;

        int actionBarHeight = 56;
        int paddingPixel = (int) ((actionBarHeight + 150) * density + 0.5f);
        Log.e("pixel", "" + paddingPixel + " tool " + actionBar.getHeight() + " " + constraintLayout.getHeight());
        mRecyclerViewPerfil.setPadding(0, paddingPixel, 0, 0);
        mRecyclerViewPerfil.setAdapter(adapter);

        if (getIntent().getBooleanExtra("mudou", false))
            chageData(getIntent());
    }

    private void chageData(Intent intent) {
        String email, nome, morada, morada_complementar, localidade, codigo_postal, telemovel;

        email = intent.getStringExtra("email");
        nome = intent.getStringExtra("nome");
        morada = intent.getStringExtra("morada");
        morada_complementar = intent.getStringExtra("morada_complementar");
        localidade = intent.getStringExtra("localidade");
        codigo_postal = intent.getStringExtra("codigo_postal");
        telemovel = intent.getStringExtra("telemovel");
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (!email.isEmpty()){
            arrayList.remove(0);
            arrayList.add(0, new PerfilItem("Email", email));
            editor.putString("email", email);
        }

        if (!nome.isEmpty()){
            arrayList.remove(1);
            arrayList.add(1, new PerfilItem("Nome", nome));
            editor.putString("nome", nome);
        }

        if (!morada.isEmpty()){
            arrayList.remove(4);
            arrayList.add(4, new PerfilItem("Morada", morada));
            editor.putString("firstaddress", morada);
        }

        if (!morada_complementar.isEmpty()){
            arrayList.remove(5);
            arrayList.add(5, new PerfilItem("Morada complementar", morada_complementar));
            editor.putString("complementaryaddress", morada_complementar);
        }

        if (!localidade.isEmpty()){
            arrayList.remove(6);
            arrayList.add(6, new PerfilItem("Localidade", localidade));
            editor.putString("locality", localidade);
        }

        if (!codigo_postal.isEmpty()){
            arrayList.remove(7);
            arrayList.add(7, new PerfilItem("Código postal", codigo_postal));
            editor.putString("postalcode", codigo_postal);
        }

        if (!telemovel.isEmpty()){
            arrayList.remove(8);
            arrayList.add(8, new PerfilItem("Telemóvel", telemovel));
            editor.putString("mobile_phone", telemovel);
        }

    }
    //


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.area_pessoal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.editPerfil) {
            Intent intent = new Intent(PerfilActivity.this, AlterarDadosActivity.class);
            intent.putExtra("email", arrayList.get(0).getDescricao());
            intent.putExtra("nome", arrayList.get(1).getDescricao());
            intent.putExtra("morada", arrayList.get(4).getDescricao());
            intent.putExtra("morada_complementar", arrayList.get(5).getDescricao());
            intent.putExtra("localidade", arrayList.get(6).getDescricao());
            intent.putExtra("codigo_postal", arrayList.get(7).getDescricao());
            intent.putExtra("telemovel", arrayList.get(8).getDescricao());
            startActivity(intent);

        } else if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(PerfilActivity.this);
            alert.setTitle("Terminar sessão");
            alert
                    .setMessage("Deseja terminar sessão?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                            if (changed)
                                attemptSendData();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
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


        } else if (id == R.id.action_acerca) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (changed) {
            changed = false;
            attemptSendData();
        }
        //navigationView.getMenu().getItem(0).setChecked(true);
        finish();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSendData() {

        String email = sharedPreferences.getString("email", "");
        String firstaddress = sharedPreferences.getString("firstaddress", "");
        String complementaryaddress = sharedPreferences.getString("complementaryaddress", "");
        String locality = sharedPreferences.getString("locality", "");
        String mobile_phone = sharedPreferences.getString("mobile_phone", "");
        String postalcode = sharedPreferences.getString("postalcode", "");


        //alterarDadosVolley(email, firstaddress, complementaryaddress, locality, mobile_phone, postalcode);

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eventos) {
            Intent i = new Intent(PerfilActivity.this, FeedEventosActivity.class);
            startActivityForResult(i, 0);
        } else if (id == R.id.nav_feed) {
            Intent i = new Intent(PerfilActivity.this, FeedActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_mapa) {

            if (changed)
                attemptSendData();
            Intent i = new Intent(PerfilActivity.this, MapsActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_area_pessoal) {

        } else if (id == R.id.nav_grupos) {

            if (changed)
                attemptSendData();
            Intent i = new Intent(PerfilActivity.this, GruposListActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}




