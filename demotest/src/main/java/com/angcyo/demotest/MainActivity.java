package com.angcyo.demotest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView view = new ScrollView(this);
        LinearLayout child = (LinearLayout) View.inflate(this, R.layout.layout_1, null);

        view.addView(child);

//        File file = new File("ang.txt");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("ang.txt")));
            String str;
            while ((str = reader.readLine()) != null) {
                Log.e("robi", str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("robi", " 文件没有找到" + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("robi", "文件读取错误" + e.toString());
        }

        setContentView(view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
