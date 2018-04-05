package com.example.joha.lab01_camara_voicespeach;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {

    List<Persona> model=new ArrayList<>();
    personasAdapter adapter=null;
    Button save,tomarFoto;
    Bitmap fotoTomadda;
    ListView list;
    EditText nombreMain,perfilMain;
    Spinner tipos;
    private final static String[] names = { "Femenino", "Masculino"};
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save= (Button) findViewById(R.id.onSave);

        tomarFoto= (Button) findViewById(R.id.tomarFoto);
        nombreMain= (EditText)findViewById(R.id.nombre);
        perfilMain= (EditText)findViewById(R.id.perfil);
        perfilMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,10);
                }else{
                    Toast.makeText(getApplicationContext(),"Error.. su celular no soporta reconocimiento de voz :(", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        save.setOnClickListener(onSave);
        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        adapter= new personasAdapter();
        list= (ListView)findViewById(R.id.lista);
        list.setAdapter(adapter);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, names);

        tipos = (Spinner)findViewById(R.id.sexoType);
        tipos.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            fotoTomadda= imageBitmap;
        }
        else{
            switch (requestCode){
                case 10:
                    if(data != null){
                        ArrayList<String>result=  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        perfilMain.setText(result.get(0));
                    }
                    break;
            }
        }
    }

    private View.OnClickListener onSave= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Persona res= new Persona();
            res.setNombre(nombreMain.getText().toString());
            res.setPerfilProfesional(perfilMain.getText().toString());
            res.setSexo(tipos.getSelectedItem().toString());
            res.setFoto(fotoTomadda);
            adapter.add(res);
        }
    };

    class personasAdapter extends ArrayAdapter<Persona> {
        personasAdapter() {
            super(MainActivity.this, R.layout.row, model);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View row=convertView;
            personasHolder holder=null;
            if(row==null){
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.row, parent,false);
                holder=new personasHolder(row);
                row.setTag(holder);
            }
            else{
                holder=(personasHolder)row.getTag();
            }
            holder.populateFrom(model.get(position));
            return (row);
        }
    }

    class personasHolder {
        private TextView nombre=null;
        private TextView perfil=null;
        private TextView sexo=null;
        private ImageView view=null;
        personasHolder(View row){
            nombre=(TextView)row.findViewById(R.id.nombreRow);
            perfil=(TextView)row.findViewById(R.id.perfilRow);
            sexo=(TextView)row.findViewById(R.id.sexoRow);
            view=(ImageView)row.findViewById(R.id.fotoImageView);
        }
        void populateFrom(Persona r){
            nombre.setText(r.getNombre());
            perfil.setText(r.getPerfilProfesional());
            sexo.setText(r.getSexo());
            view.setImageBitmap(r.getFoto());
        }
    }
}
