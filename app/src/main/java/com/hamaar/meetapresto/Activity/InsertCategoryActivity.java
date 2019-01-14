package com.hamaar.meetapresto.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.hamaar.meetapresto.Model.Category;
import com.hamaar.meetapresto.R;
import com.hamaar.meetapresto.Utils.GlobalVars;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import org.json.JSONException;
import org.json.JSONObject;

public class InsertCategoryActivity extends AppCompatActivity {

    Button btnInsertCategory;
    AutoCompleteTextView edtInsertCategory;

    // create a database, using the default files dir as path, database name and a password
    String path = getFilesDir().getPath();
    String databaseName = "myDb";
    String password = "passw0rd";
    WaspHash cat;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_category);

        btnInsertCategory = findViewById(R.id.btnSaveCategory);
        edtInsertCategory = findViewById(R.id.edtTxtCategory);

        WaspDb db = WaspFactory.openOrCreateDatabase(path, databaseName, password);

        // now create an WaspHash, it's like a sql table
        cat = db.openOrCreateHash("cat");

        gson = new Gson();

        btnInsertCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCategory();
            }
        });
    }

    private void saveCategory() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cat", edtInsertCategory.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(GlobalVars.BASE_IP + "category/create.php")
                .addJSONObjectBody(jsonObject) // posting json
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Toast.makeText(InsertCategoryActivity.this, "success add data", Toast.LENGTH_SHORT).show();
                        Category category = gson.fromJson(response.toString(), Category.class);
                        cat.put(category.getId(), category);
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(InsertCategoryActivity.this, "error add data", Toast.LENGTH_SHORT).show();
                        Log.e("ErrorActivity", error.toString());
                        // handle error
                    }
                });
    }


}
