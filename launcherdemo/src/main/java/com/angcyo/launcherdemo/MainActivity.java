package com.angcyo.launcherdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    GridView mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadApps();
        mGrid = (GridView) findViewById(R.id.apps_list);
        mGrid.setAdapter(new AppsAdapter());
        mGrid.setOnItemClickListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    ListView listView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            listView = new ListView(this);
            listView.setAdapter(new ListAdapter());
            setContentView(listView);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<Map<String, Object>> getData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (ResolveInfo info : mApps) {
            Map<String, Object> map = new HashMap<>();
            map.put("ico", drawableToBitmap(info.activityInfo.loadIcon(getPackageManager())));
            map.put("name", info.activityInfo.packageName);
            data.add(map);
        }

        return data;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    List<ResolveInfo> mApps;

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;
            if (convertView == null) {
                i = new ImageView(MainActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_XY);
                i.setLayoutParams(new GridView.LayoutParams(80, 80));
            } else {
                i = (ImageView) convertView;
            }

            ResolveInfo info = mApps.get(position);
            i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

            Log.e("Robi", "包名2:" + info.activityInfo.packageName);

            return i;
        }

        public final int getCount() {
            return mApps.size();
        }

        public final Object getItem(int position) {
            return mApps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //事件内容....
            ResolveInfo info = mApps.get(position);

            //该应用的包名
            String pkg = info.activityInfo.packageName;
            //应用的主activity类
            String cls = info.activityInfo.name;

            ComponentName componet = new ComponentName(pkg, cls);

            Intent i = new Intent();
            i.setComponent(componet);
            startActivity(i);
        }
    };

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mApps.size();
        }

        @Override
        public Object getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.list_item, null);
            ((ImageView) view.findViewById(R.id.ico)).setImageDrawable(mApps.get(position).activityInfo.loadIcon(getPackageManager()));
            ((TextView) view.findViewById(R.id.name)).setText(mApps.get(position).activityInfo.loadLabel(getPackageManager()) + "|" + mApps.get(position).activityInfo.packageName);


            return view;
        }
    }
}
