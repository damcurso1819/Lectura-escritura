package org.izv.aad.aad1920ejemplo2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.izv.aad.aad1920ejemplo2.operaciones.AfterPermissionsCheck;
import org.izv.aad.aad1920ejemplo2.settings.SettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    private static final String TAG = "xyzyx" + MainActivity.class.getName();
    private static final String KEY_ARCHIVO = "archivo";

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

    private void checkPermissions(String permiso, int titulo, int mensaje, AfterPermissionsCheck apc) {
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
            apc.doTheJob();
            //writeNotes();
            //readNotes();
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
        etNombre.setText(readPreferences());
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
        readSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnSettings:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "on stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "on destroy");
    }

    private void readFile() {
        if (isValues()) {
            if (type == PUBLIC) {
                AfterPermissionsCheck apc = new AfterPermissionsCheck() {
                    @Override
                    public void doTheJob() {
                       readNotes();
                    }
                };
                checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        R.string.tituloExplicacion2,
                        R.string.mensajeExplicacion2,
                        apc);
            } else {
                readNotes();
            }
        }
    }

    private void readNotes() {
        File f = new File(getFile(type), name);
        Log.v(TAG, f.getAbsolutePath());
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuffer lineas = new StringBuffer("");
            while ((linea = br.readLine()) != null) {
                lineas.append(linea + "\n");
            }
            br.close();
            tvResultado.setText(lineas);
            savePreferrences();
        } catch(IOException e) {
            tvResultado.setText(e.getMessage());
        }

    }

    private String readPreferences() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_ARCHIVO, "");
    }

    private void readSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("apelo", "siempre");
        Log.v(TAG, name);
    }

    private void savePreferrences() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ARCHIVO, name);
        editor.commit();
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void writeFile() {
        value = etValor.getText().toString().trim();
        if (isValues() && !value.isEmpty()) {
            if (type == PUBLIC) {
                checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        R.string.tituloExplicacion, R.string.mensajeExplicacion,
                        new AfterPermissionsCheck() {
                            @Override
                            public void doTheJob() {
                                writeNotes();
                            }
                        });
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
            tvResultado.setText(R.string.escribir);
            savePreferrences();
        } catch (IOException e) {
            tvResultado.setText(e.getMessage());
        }
    }
}
