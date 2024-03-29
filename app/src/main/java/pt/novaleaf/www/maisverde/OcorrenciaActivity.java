package pt.novaleaf.www.maisverde;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OcorrenciaActivity extends AppCompatActivity implements Serializable {

    private TextView mTitulo;
    private TextView mRisco;
    private TextView mStatus;
    private TextView mDataDia;
    private TextView mDataMes;
    private TextView mTexto;
    private ImageView mMapa;
    private ImageView mImage;
    private ImageView mImageStatus;
    private ImageView mImageTipo;
    private TextView mTipo;
    private TextView mCriador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocorrencia);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        final Ocorrencia ocorrencia = (Ocorrencia) getIntent().getSerializableExtra("Ocorrencia");


        mTitulo = (TextView) findViewById(R.id.ocorrenciaTitulo);
        mDataDia = (TextView) findViewById(R.id.textViewDiaOcorrencia);
        mDataMes = (TextView) findViewById(R.id.textViewMesOcorrencia);
        mTexto = (TextView) findViewById(R.id.ocorrenciaTexto);
        mMapa = (ImageView) findViewById(R.id.imageView8);
        mImage = (ImageView) findViewById(R.id.ocorrenciaImagem);
        mImageTipo = (ImageView) findViewById(R.id.typeimageview);
        mImageStatus = (ImageView) findViewById(R.id.imageStatus);
        mRisco = (TextView) findViewById(R.id.risktext);
        mTipo = (TextView) findViewById(R.id.textView5);
        mStatus = (TextView) findViewById(R.id.statusOcorrencia);
        mCriador = (TextView) findViewById(R.id.textCriador);

        long status = ocorrencia.getStatus();
        if (status == 3) {
            mStatus.setText("Em aberto");
            mImageStatus.setImageResource(R.drawable.statuscirclered);
        }
        else if (status == 2) {
            mStatus.setText("Em tratamento");
            mImageStatus.setImageResource(R.drawable.statuscircleredyellow);
        }
        else if (status == 1) {
            mStatus.setText("Tratada");
            mImageStatus.setImageResource(R.drawable.statuscircle);
        }

        String tipo = ocorrencia.getType();
        if (tipo.equals("bonfire")) {
            mTipo.setText("Queimada");
            mImageTipo.setImageResource(R.mipmap.ic_bonfire_foreground);
        } else if (tipo.equals("fire")) {
            mTipo.setText("Incêndio");
            mImageTipo.setImageResource(R.mipmap.ic_fire_foreground);
        } else if (tipo.equals("trash")) {
            mTipo.setText("Lixo");
            mImageTipo.setImageResource(R.mipmap.ic_garbage_foreground);
        } else {
            mTipo.setText("Mata por limpar");
            mImageTipo.setImageResource(R.mipmap.ic_grass_foreground);
        }

        mRisco.setText(String.format("%d", (int) ocorrencia.getRisk()));

        if (ocorrencia.getRisk() < 33)
            mRisco.setTextColor(ContextCompat.getColor(this, R.color.loginColor));
        else if (ocorrencia.getRisk() < 66)
            mRisco.setTextColor(ContextCompat.getColor(this, R.color.coloryellow));
        else
            mRisco.setTextColor(ContextCompat.getColor(this, R.color.colorred));

        mCriador.setText(String.format("Criado por: %s", ocorrencia.getOwner()));

        setTitle(ocorrencia.getName());

        mMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OcorrenciaActivity.this, MapsActivity.class);
                intent.putExtra("longitude", ocorrencia.getLongitude());
                intent.putExtra("latitude", ocorrencia.getLatitude());
                startActivity(intent);
            }
        });

        mTitulo.setText(ocorrencia.getName());
        long time = ocorrencia.getCreationDate();

        if (time != 0) {
            SimpleDateFormat simpleDateFormatMes = new SimpleDateFormat("MMM", Locale.UK);
            SimpleDateFormat simpleDateFormatDia = new SimpleDateFormat("dd", Locale.UK);
            String dataMes = simpleDateFormatMes.format(new Date(time));
            String dataDia = simpleDateFormatDia.format(new Date(time));
            mDataMes.setText(dataMes.toUpperCase());
            mDataDia.setText(dataDia.toUpperCase());
        }

        mTexto.setText(ocorrencia.getDescription());


        if (ocorrencia.getBitmap() == null)
            mImage.setImageResource(ocorrencia.getImageID());
        else
            mImage.setImageBitmap(BitmapFactory.decodeByteArray(ocorrencia.getBitmap(), 0, ocorrencia.getBitmap().length));

    }
}
