package omdvet.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import omdvet.com.WebServices.Requests.getProductsRequest;
import omdvet.com.WebServices.Responses.getProductsResponse;
import omdvet.com.WebServices.RetrofitWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ArrayList productNameList;
    private ArrayList productQntList;
    private ArrayList productPriceList;
    private ArrayList productImgList;
    private ArrayList productIdList;
    ProgressBar progressBar ;

    ImageView error_image;
    TextView error_text;

    RecyclerView recyclerView;
    static RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = findViewById(R.id.progress_home);
        progressBar.setVisibility(View.VISIBLE);
        error_image = findViewById(R.id.error_image);
        error_text  = findViewById(R.id.error_text);
        init();
    }

    public void init(){
        recyclerView = (RecyclerView)findViewById(R.id.ProductsRecycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        MenuInflater inflater = getMenuInflater();
        SharedPreferences prefs = getSharedPreferences(
                "USER", Context.MODE_PRIVATE);
        String s = prefs.getString("apiToken","");
        RetrofitWebService.getService().GET_PRODUCT_CALL(new getProductsRequest(s))
                .enqueue(new Callback<getProductsResponse>() {
                    @Override
                    public void onResponse(Call<getProductsResponse> call, Response<getProductsResponse> response) {
                        int status=response.body().getStatus();
                        if(status==200) {
                            progressBar.setVisibility(View.INVISIBLE);
                            productNameList = new ArrayList<>();
                            productQntList = new ArrayList<>();
                            productPriceList = new ArrayList<>();
                            productImgList = new ArrayList<>();
                            productIdList = new ArrayList();

                            for (int i = 0; i < response.body().orders.size(); i++) {
                                productNameList.add(response.body().orders.get(i).getName());
                                productQntList.add(response.body().orders.get(i).getQuantity());
                                productPriceList.add(response.body().orders.get(i).getPrice());
                                productIdList.add(response.body().orders.get(i).getId());
                                productImgList.add(response.body().orders.get(i).getPhoto());
                            }
                        }

                         adapter = new ProductAdapter(HomeActivity.this,productNameList,productQntList,productPriceList,productImgList,productIdList);
                        recyclerView.setAdapter(adapter);

                        if(productNameList.size() == 0 ){
                            error_image.setVisibility(View.VISIBLE);
                            error_text.setVisibility(View.VISIBLE);
                        }

                        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                                return false;
                            }

                            @Override
                            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

//                    int position = rv.getChildAdapterPosition(child);
//                    View child = rv.getChildAt(position);


                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                            }
                        });

                    }
                    @Override
                    public void onFailure(Call<getProductsResponse> call, Throwable t) {

                    }
                });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        SharedPreferences prefs = getSharedPreferences(
                "USER", Context.MODE_PRIVATE);
        String s = prefs.getString("IS_ADMIN","");
        if(s.equals("1"))
            inflater.inflate(R.menu.admin_menu, menu);
        else if(s.equals("0"))
            inflater.inflate(R.menu.menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logOut:
                SharedPreferences prefs = getSharedPreferences(
                        "USER", Context.MODE_PRIVATE);
                prefs.edit().putString("login","logged_out").apply();

                Intent intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                break;
            case R.id.addProduct:
                Intent i = new Intent(HomeActivity.this,AddProductActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.customers:
                Intent i2 = new Intent(HomeActivity.this,CustomersActivity.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i2);
                finish();
                break;
            case R.id.delegates:
                Intent i3 = new Intent(HomeActivity.this,DelegatesActivity.class);
                i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i3);
                finish();
                break;
            case R.id.orders:
                Intent i4 = new Intent(HomeActivity.this,getOrdersActivity.class);
                i4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i4);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
