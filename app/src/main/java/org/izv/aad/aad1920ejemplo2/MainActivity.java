package org.izv.aad.aad1920ejemplo2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /*
    /data/user/0/org.izv.aad.aad1920ejemplo2/files/Hola.txt
    /storage/emulated/0/Hola.txt
    /storage/emulated/0/Android/data/org.izv.aad.aad1920ejemplo2/files/Hola.txt

    */

    private static final int NONE = -1;
    private static final int INTERN = 0; // radio id: interno
    private static final int PUBLIC = 1;
    private static final int PRIVATE = 2;
    private static final int ID_PERMISO_LEER_ESCRIBIR = 4;
    private static final String TAG = MainActivity.class.getName();


    private Button btLeer, btEscribir; //Botones leer y escribir
    private EditText etNombre, etValor;
    private RadioGroup rgTipo;
    private TextView tvResultado;

    private String name, value; //Valores del EditText etName;
    private int type;

    private static int getCheckedType(int item) { //Le pasamos el radioButton pulsado
        int tipo = NONE;
        switch (item) {
            case R.id.rbInterno:
                tipo = INTERN;
                break;
            case R.id.rbPublico:
                tipo = PUBLIC;
                break;
            case R.id.rbPrivado:
                tipo = PRIVATE;
                break;
        }
        return tipo;
    }

    private static File getFile(Context context, int type) {
        File file = null;
        switch (type) {
            case INTERN:
                file = context.getFilesDir();
                break;
            case PUBLIC:
                file = Environment.getExternalStorageDirectory();
                break;
            case PRIVATE:
                file = context.getExternalFilesDir(null);
                break;
        }
        return file;
    }

    private void assignEvents() {
        btLeer.setOnClickListener(new View.OnClickListener() { //Cuando se pulsa el botón btLeer
            @Override
            public void onClick(View v) {
                readFile();
            }
        });

        btEscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFile();
            }
        });

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                explain(R.string.tituloExplicacion, R.string.mensajeExplicacion, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ID_PERMISO_LEER_ESCRIBIR);
            }
        } else {
            writeNotes();
        }
    }

    private void explain(int title, int message, final String permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissions}, ID_PERMISO_LEER_ESCRIBIR);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private File getFile(int type) {
        /*File file = null;
        switch(type) {
            case INTERN:
                file = this.getFilesDir();
                break;
            case PUBLIC:
                file = Environment.getExternalStorageDirectory();
                break;
            case PRIVATE:
                file = this.getExternalFilesDir(null);
                break;
        }
        return file;*/
        return MainActivity.getFile(this, type);
    }

    private void initComponents() {
        btLeer = findViewById(R.id.btLeer);
        btEscribir = findViewById(R.id.btEscribir); // Asigno el botón btEscribit al objeto btEsccribir
        etNombre = findViewById(R.id.etNombre);
        etValor = findViewById(R.id.etValor);
        rgTipo = findViewById(R.id.rgTipo);
        tvResultado = findViewById(R.id.tvResultado);

    }

    private boolean isValues() {
        name = etNombre.getText().toString().trim(); // Devuelve el contenido más informaciñon adicional, trim quita espacios iniciales
        type = MainActivity.getCheckedType(rgTipo.getCheckedRadioButtonId()); // Obtienes el radio button pulsado

        //MainActivity. (opcional)

        return !(name.isEmpty() || type == NONE); // Devuelve false si está vacío o si es -1
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        assignEvents();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ID_PERMISO_LEER_ESCRIBIR) {
            //PackageManager.PERMISSION_DENIED;
            //PackageManager.PERMISSION_GRANTED;
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeNotes();
            }
        }
    }

    private void readFile() {
        if (isValues()) {
            if (type == PUBLIC) {

            } else {

            }
        }
    }

    private void readNotes() {
    }

    private void writeFile() {

        value = etValor.getText().toString().trim();

        if (isValues() && !value.isEmpty()) {
            if (type == PUBLIC) {
                checkPermissions();
            } else {
                writeNotes();
            }
        }
    }

    private void writeNotes() {
        File f = new File(getFile(type), name);
        Log.v(TAG, f.getAbsolutePath());
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(value);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            tvResultado.setText(e.getMessage());
        }
    }
}
