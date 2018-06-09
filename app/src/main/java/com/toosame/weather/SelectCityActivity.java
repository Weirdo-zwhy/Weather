package com.toosame.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.toosame.weather.model.City;
import com.toosame.weather.model.DisCity;
import com.toosame.weather.model.Districts;
import com.toosame.weather.model.DistrictsRoot;
import com.toosame.weather.utils.CreateSQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    private List<Integer> cities;
    private List<String> cityKeyVal;
    private List<String> cityVal;
    private Gson jsonConverter = new Gson();

    private String selectName;
    private String selectCode;
    private SharedPreferences userSettings;
    private CreateSQL dbHelper;
    private List<City> diaryList=new ArrayList<>();
    private City city;

    @Override
    protected void onStart() {
        super.onStart();
        dbHelper =new CreateSQL(this,"City.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        diaryList.clear();
        Cursor cursor=db.query("weather",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                int code = cursor.getInt(cursor.getColumnIndex("selectCode"));
                String name=cursor.getString(cursor.getColumnIndex("selectName"));
                city=new City(name,code);
                diaryList.add(city);
            }while (cursor.moveToNext());
        }
        cityAdapter adapter=new cityAdapter(SelectCityActivity.this,R.layout.attention_city,diaryList);
        ListView listView=(ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_select_city);
        ListView listView=(ListView) findViewById(R.id.list_view);
        userSettings = getSharedPreferences("setting", MODE_PRIVATE);

        cities = new ArrayList<>();
        cities.add(R.raw.anhui);
        cities.add(R.raw.aomeng);
        cities.add(R.raw.beijin);
        cities.add(R.raw.chongqing);
        cities.add(R.raw.fujiang);
        cities.add(R.raw.gangsu);
        cities.add(R.raw.guangdong);
        cities.add(R.raw.guangxi);
        cities.add(R.raw.guizhou);
        cities.add(R.raw.hainang);
        cities.add(R.raw.hebei);
        cities.add(R.raw.heilongjiang);
        cities.add(R.raw.henang);
        cities.add(R.raw.hongkong);
        cities.add(R.raw.hubei);
        cities.add(R.raw.hunang);
        cities.add(R.raw.jiangsu);
        cities.add(R.raw.jiangxi);
        cities.add(R.raw.jiling);
        cities.add(R.raw.liaoning);
        cities.add(R.raw.neimenggu);
        cities.add(R.raw.ningxia);
        cities.add(R.raw.qinghai);
        cities.add(R.raw.shangdong);
        cities.add(R.raw.shanghai);
        cities.add(R.raw.shangxi);
        cities.add(R.raw.shanxi);
        cities.add(R.raw.sichuang);
        cities.add(R.raw.tianjin);
        cities.add(R.raw.xinjiang);
        cities.add(R.raw.xizan);
        cities.add(R.raw.yunnang);
        cities.add(R.raw.zhejiang);

        Button doneBtn = findViewById(R.id.done_btn);
        autoCompleteTextView = findViewById(R.id.city_textview);

        cityKeyVal = new ArrayList<>();
        cityVal = new ArrayList<>();

        findCity();
        ArrayAdapter<String> autoTextString = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cityKeyVal);
        autoCompleteTextView.setAdapter(autoTextString);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                int index = cityKeyVal.indexOf(obj);
                selectCode = cityVal.get(index);
                selectName = obj.toString();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(selectCode) && !TextUtils.isEmpty(selectName)){
//                    SharedPreferences.Editor editor = userSettings.edit();
//                    editor.putString(SplashActivity.ADCODE, selectCode);
//                    editor.putString(SplashActivity.CITYNAME, selectName);
//                    editor.apply();

                    Intent intent = new Intent(SelectCityActivity.this,MainActivity.class);
                    intent.putExtra(SplashActivity.ADCODE,selectCode);
                    intent.putExtra(SplashActivity.CITYNAME,selectName);

                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(SelectCityActivity.this, "请输入你城市的名字，然后在下拉框选择支持的城市", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city=diaryList.get(position);
                Intent intent=new Intent(SelectCityActivity.this,MainActivity.class);//跳转到日记内容页面
                intent.putExtra("code",city.getId());
                intent.putExtra(SplashActivity.CITYNAME,city.getName());
                Log.d("usss",city.getId()+city.getName());
                startActivity(intent);
            }
        });

    }
    //对json数据读取解析
    private void findCity(){
        for (int i : cities){
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = getResources().openRawResource(i);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line = "";
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                DistrictsRoot dis = jsonConverter.fromJson(stringBuilder.toString(),DistrictsRoot.class);
                if (dis.getDistricts().size() > 0){
                    List<Districts> _dis = dis.getDistricts();
                    if (_dis.size() > 0){
                        Districts currentDis = _dis.get(0);
                        DisCity disCity = new DisCity();
                        disCity.setAdcode(currentDis.getAdcode());
                        disCity.setName(currentDis.getName());
                        disCity.setDistricts(currentDis.getDistricts());
                        whileCity(currentDis.getDistricts(),disCity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //传入城市名以及城市adcode数组中
    private void whileCity(List<DisCity> districtses, DisCity parenCity){
        for (DisCity c : districtses){
            if (c.getDistricts().size() > 0){
                whileCity(c.getDistricts(),c);
            }else {
                cityKeyVal.add(parenCity.getName() + " " + c.getName());
                cityVal.add(c.getAdcode());
            }
        }
    }
}
//城市适配器，对应显示
class cityAdapter extends ArrayAdapter<City> {
    private int resourceId;
    public cityAdapter(@NonNull Context context, int resource, List<City> objects) {
        super(context, resource,objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        City City=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView item_id=(TextView) view.findViewById(R.id.item_id);
        TextView item_name=(TextView) view.findViewById(R.id.item_name);
        item_id.setText(String.valueOf(City.getId()));
        item_name.setText(City.getName());
        return view;
    }
}