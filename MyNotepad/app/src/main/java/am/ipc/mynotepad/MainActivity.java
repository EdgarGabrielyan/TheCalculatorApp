package am.ipc.mynotepad;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity{

    public EditText editText;
    public String filename =null;
    private String path = Environment.getExternalStorageDirectory().toString()+ "/files/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)findViewById(R.id.editText);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        float fSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size),"20"));
        editText.setTextSize(fSize);

        String regular =sharedPreferences.getString(getString(R.string.pref_style), "");
        int typeface = Typeface.NORMAL;
        if(regular.contains("Полужирный"))
            typeface+=Typeface.BOLD;
        if(regular.contains("Курсив"))
            typeface+=Typeface.ITALIC;

        editText.setTypeface(null,typeface);

        int color = Color.BLACK;
        if(sharedPreferences.getBoolean(getString(R.string.pref_color_red),false))
            color+=Color.RED;

        if(sharedPreferences.getBoolean(getString(R.string.pref_color_green),false))
            color+=Color.GREEN;

        if(sharedPreferences.getBoolean(getString(R.string.pref_color_blue),false))
            color+=Color.BLUE;

        editText.setTextColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_clear:
                editText.setText("");
                Toast.makeText(this, "Очищено", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.action_open:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Имя файла");
                builder.setMessage("введите Имя файла для Открытия");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("Открыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editText.setText("");
                        String  value = input.getText().toString();
                        filename=value;
                        File file = new File(path+filename);
                        if(file.exists()&&file.isFile()){
                            editText.setText(openFile(filename));
                        }else{
                            Toast.makeText(MainActivity.this, "Файла не существует", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.show();
                return true;

            case R.id.action_save:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Имя файла");
                alert.setMessage("введите имя файла для сохранения");
                final EditText input2 = new EditText(this);
                alert.setView(input2);
                alert.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value=input2.getText().toString();
                        filename=value;
                        saveFile(filename,editText.getText().toString());

                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "Вы нажали Отмена", Toast.LENGTH_SHORT).show();

                    }
                });
                     alert.show();
                        return true;

            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);



        }
        return super.onOptionsItemSelected(item);
    }

    private void saveFile(String filename,String body) {
        try {
            File root = new File(this.path);
            if(!root.exists()){
                root.mkdirs();
            }
            File file = new File(root,filename);
            FileWriter writer = new FileWriter(file);
            writer.append(body);
            writer.flush();
            writer.close();
            Toast.makeText(MainActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String openFile(String filename){
        StringBuilder text = new StringBuilder();
        try {
            File file = new File(this.path,filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line=br.readLine())!=null){
                text.append(line+"\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return text.toString();
    }

}
