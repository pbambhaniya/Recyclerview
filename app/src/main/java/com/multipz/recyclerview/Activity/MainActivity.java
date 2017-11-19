package com.multipz.recyclerview.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.multipz.recyclerview.Adapter.VehicleTransportAdapter;
import com.multipz.recyclerview.Model.Model;
import com.multipz.recyclerview.Model.VehicleTransportModel;
import com.multipz.recyclerview.R;
import com.multipz.recyclerview.Util.Config;
import com.multipz.recyclerview.Util.Constant_method;
import com.multipz.recyclerview.Util.ItemClickListener;
import com.multipz.recyclerview.Util.MyAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAsyncTask.AsyncInterface, ItemClickListener {
    RecyclerView recyclerView;
    Context context;
    ArrayList<VehicleTransportModel> userList;
    private VehicleTransportAdapter adapter;
    public int firstVisibleItem, visibleItemCount, totalItemCount;
    int pagecount = 1;
    private boolean userScrolled = true;
    private int lastVisibleItem, firstVisible;
    LinearLayoutManager layoutManager;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        userList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                totalItemCount = layoutManager.getItemCount();
                firstVisible = layoutManager.findFirstVisibleItemPosition();
                if (userScrolled) {
                    if ((lastVisibleItem + firstVisible) >= totalItemCount) {
                        userScrolled = false;
                        pagecount++;
                        getApicall();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        getApicall();


    }

    private void getApicall() {
        if (Constant_method.checkConn(context)) {
            String param = "{\"page\":\"" + pagecount + "\",\"action\":\"getVehicleTranspData\"}";
//            String param = "{\"page\":\"1\",\"action\":\"getVehicleHourlyPayData\"}";
            MyAsyncTask myAsyncTask = new MyAsyncTask(Config.Main_Api, this, param, Config.APi_Get_vehicle_Transport);
            myAsyncTask.execute();
        }
    }

    @Override
    public void onResponseService(String response, int flag) {
        if (flag == Config.APi_Get_vehicle_Transport) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.e("Responce", jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i1 = 0; i1 < jsonArray.length(); i1++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i1);
                    VehicleTransportModel transportModel = new VehicleTransportModel();
                    transportModel.setVehicle_transport_id(jsonObject1.getString("vehicle_transport_id"));
                    transportModel.setVehicle_detail_id(jsonObject1.getString("vehicle_detail_id"));
                    transportModel.setUser_id(jsonObject1.getString("user_id"));
                    transportModel.setMaterial_id(jsonObject1.getString("material_id"));
                    transportModel.setLoad_to(jsonObject1.getString("load_to"));
                    transportModel.setLoad_compid(jsonObject1.getString("load_compid"));
                    transportModel.setLoad_amount(jsonObject1.getString("load_amount"));
                    transportModel.setEmpty_to(jsonObject1.getString("empty_to"));
                    transportModel.setEmpty_compid(jsonObject1.getString("empty_compid"));
                    transportModel.setEmpty_amount(jsonObject1.getString("empty_amount"));
                    transportModel.setLoadCompanyName(jsonObject1.getString("loadCompanyName"));
                    transportModel.setEmptyCompanyName(jsonObject1.getString("EmptyCompanyName"));
                    transportModel.setUserName(jsonObject1.getString("userName"));
                    transportModel.setVehicle_no(jsonObject1.getString("vehicle_no"));
                    transportModel.setMaterial_type(jsonObject1.getString("material_type"));
                    userList.add(transportModel);
                }
                adapter = new VehicleTransportAdapter(context, userList);
                layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void itemClicked(View View, int position) {
        Intent intent = new Intent(context, NewActivity.class);
        VehicleTransportModel model = userList.get(position);
        intent.putExtra("amount", model.getEmpty_amount());
        startActivity(intent);
    }
}
