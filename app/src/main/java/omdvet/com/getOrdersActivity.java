package omdvet.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import omdvet.com.WebServices.Models.AllBilles;
import omdvet.com.WebServices.Models.billes;
import omdvet.com.WebServices.Requests.clientsRequest;
import omdvet.com.WebServices.Requests.getProductsClints;
import omdvet.com.WebServices.Responses.GetAllBillesResponse;
import omdvet.com.WebServices.Responses.clientsResponse;
import omdvet.com.WebServices.Responses.getProductClints;
import omdvet.com.WebServices.RetrofitWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class getOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    ImageView error_image;
    TextView error_text;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MyTheme);
        setContentView(R.layout.activity_get_orders);
        progressBar = findViewById(R.id.progress_getOrder);
        progressBar.setVisibility(View.VISIBLE);

        error_image = findViewById(R.id.error_image_order);
        error_text = findViewById(R.id.error_text_order);


        Intent intent = getIntent();

        recyclerView = findViewById(R.id.oredrsRecylcer);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        if(intent.hasExtra("from")){
            flag = intent.getBooleanExtra("from",false);
        }

        if(intent.hasExtra("client_id")){
            initClient(intent.getIntExtra("client_id",-1));
            flag = true;
        }else{
            init();
        }

    }

    public void init(){

        RetrofitWebService.getService().GetAllBilles().enqueue(new Callback<GetAllBillesResponse>() {
            @Override
            public void onResponse(Call<GetAllBillesResponse> call, Response<GetAllBillesResponse> response) {

                if(response.body().getStatus() == 200) {
                    progressBar.setVisibility(View.INVISIBLE);
                    GetAllBillesResponse mRespose = response.body();

                    getOrdersAdapter adapter = new getOrdersAdapter(
                            getOrdersActivity.this,
                            mRespose.getAllBilles(),flag);
                    if(mRespose.getAllBilles().size() == 0 ){
                        error_image.setVisibility(View.VISIBLE);
                        error_text.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<GetAllBillesResponse> call, Throwable t) {
            }
        });
    }

    public void initClient(int id){
        RetrofitWebService.getService().GetPClint(new getProductsClints(String.valueOf(id)))
                .enqueue( new Callback<getProductClints>() {
                    @Override
                    public void onResponse(Call<getProductClints> call, Response<getProductClints> response) {
                        ArrayList<AllBilles> myArray = new ArrayList<>();
                        ArrayList<billes> responseList = response.body().getBilles();
                        progressBar.setVisibility(View.INVISIBLE);
                        for(int i = 0 ; i < responseList.size() ; i ++){
                            myArray.add(new AllBilles(responseList.get(i).getId(),responseList.get(i).getName(),
                                    responseList.get(i).getPhone(),responseList.get(i).getAddress(),responseList.get(i).getCost()
                            ,responseList.get(i).getAfterDiscount(),responseList.get(i).getDate(),responseList.get(i).getCreated_at(),
                                    responseList.get(i).getType(),responseList.get(i).getIs_cach(),responseList.get(i).getMony_agel(),
                                    responseList.get(i).getEmp_id(),responseList.get(i).getClient_id()));
                        }

                        getOrdersAdapter adapter = new getOrdersAdapter(
                                getOrdersActivity.this,
                                myArray,flag);
                        if(myArray.size() == 0 ){
                            error_image.setVisibility(View.VISIBLE);
                            error_text.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<getProductClints> call, Throwable t) {

                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i ;
        if(flag){
            i = new Intent(this,CustomersActivity.class);
        }else {
            i = new Intent(this,HomeActivity.class);
        }

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
