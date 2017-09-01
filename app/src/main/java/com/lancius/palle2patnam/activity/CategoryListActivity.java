package com.lancius.palle2patnam.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.JsonParser;
import com.lancius.palle2patnam.utils.WebServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lancius on 7/18/2017.
 */

public class CategoryListActivity extends AppCompatActivity {

    JsonParser jsonParser = new JsonParser();
    ProgressDialog pDialog;
    JSONArray result = null;

    static final String TAG_SUCCESS = "success";
    static final String TAG_CATEGORY_PRODUCT_NAME = "product_name";
    static final String TAG_CATEGORY_PRODUCT_PRICE = "price";
    static final String TAG_CATEGORY_PRODUCT_PRICE_ID = "price_id";
    static final String TAG_CATEGORY_PRODUCT_WEIGHT_ID = "weight_type_id";
    static final String TAG_CATEGORY_PRODUCT_WEIGHT = "weight_type";
    static final String TAG_CATEGORY_PRODUCT_IMAGE = "image";
    static final String TAG_RESULTS = "lists";

    String productName, productPrice, productPriceId, productWeight, productWeightId, productImage, selected_weight, selected_price, categoryId, message;
    Toolbar toolbar;
    ArrayList<HashMap<String, String>> productsList;
    ListView listview;
    CategoryDetailAdapter adapter;
    ArrayList priceList, weightList;
    Dialog pwindow;
    ListView product_price_listview;
    private TextView category_product_name, category_product_price, category_product_weight;
    private ImageView category_product_image;
    WeightListAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        productsList = new ArrayList<>();
        priceList = new ArrayList();
        weightList = new ArrayList();
        Bundle b = getIntent().getExtras();
        categoryId = b.getString("category_id");

        new categoryDetails().execute();

    }

    class categoryDetails extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog'5 9+\789+\789+
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CategoryListActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        /**
         * Creating Application
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("category_id", categoryId));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = null;
            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.MAIN_CATEGORY_DETAIL, "POST", params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }

                // check log cat fro response
                Log.d("Create Order Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        result = json.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < result.length(); i++) {
                            JSONObject c = result.getJSONObject(i);
                            // successfully created product
                            productName = c.getString(TAG_CATEGORY_PRODUCT_NAME);
                            productPrice = c.getString(TAG_CATEGORY_PRODUCT_PRICE);
                            productPriceId = c.getString(TAG_CATEGORY_PRODUCT_PRICE_ID);
                            productWeight = c.getString(TAG_CATEGORY_PRODUCT_WEIGHT);
                            productWeightId = c.getString(TAG_CATEGORY_PRODUCT_WEIGHT_ID);
                            productImage = c.getString(TAG_CATEGORY_PRODUCT_IMAGE);

                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_CATEGORY_PRODUCT_NAME, productName);
                            map.put(TAG_CATEGORY_PRODUCT_PRICE, productPrice);
                            map.put(TAG_CATEGORY_PRODUCT_PRICE_ID, productPriceId);
                            map.put(TAG_CATEGORY_PRODUCT_WEIGHT, productWeight);
                            map.put(TAG_CATEGORY_PRODUCT_WEIGHT_ID, productWeightId);
                            map.put(TAG_CATEGORY_PRODUCT_IMAGE, productImage);

                            productsList.add(map);

                        }
                    } else {
                        // failed to create product
                        message = json.getString("message");
                        showToast();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (StringIndexOutOfBoundsException e) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        message = "Server Error..Please Try Again Later..";
                        showToast();
                    }
                });
            }

            return null;
        }

        private void showToast() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CategoryListActivity.this, message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();

            if (!productsList.isEmpty()) {
                listview = (ListView) findViewById(R.id.category_listview);
                adapter = new CategoryDetailAdapter(
                        CategoryListActivity.this, productsList);
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
                listview.invalidateViews();
            }

        }

    }

    public class CategoryDetailAdapter extends BaseAdapter {

        Context activity;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> data;
        HashMap<String, String> resultp = new HashMap<String, String>();

        public CategoryDetailAdapter(Activity activity2,
                                     ArrayList<HashMap<String, String>> productsList) {
            // TODO Auto-generated constructor stub
            activity = activity2;
            data = productsList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            Button add_to_cart;

            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View itemView = inflater.inflate(R.layout.activity_category_detail_list_item,
                    parent, false);

            resultp = data.get(position);

            priceList.clear();
            weightList.clear();

            add_to_cart = (Button) itemView.findViewById(R.id.category_button_add);

            category_product_name = (TextView) itemView
                    .findViewById(R.id.category_product_name);
            category_product_price = (TextView) itemView
                    .findViewById(R.id.category_product_quantity_price);
            category_product_weight = (TextView) itemView
                    .findViewById(R.id.category_product_weight);
            category_product_image = (ImageView) itemView
                    .findViewById(R.id.category_product_image);

            String price_type = resultp.get(CategoryListActivity.TAG_CATEGORY_PRODUCT_PRICE);
            ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
            for (int i = 0; i < aListType.size(); i++) {
                System.out.println("-->" + aListType.get(i));
                priceList.add(aListType.get(i).toString());
            }

            String weight_type = resultp.get(CategoryListActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
            ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
            for (int i = 0; i < aList.size(); i++) {
                System.out.println("-->" + aList.get(i));
                weightList.add(aList.get(i).toString());
            }

            category_product_price.setText("Rs. " + priceList.get(0));
            category_product_weight.setText("" + weightList.get(0));

            category_product_weight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    priceList.clear();
                    weightList.clear();

                    resultp = data.get(position);

                    String price_type = resultp.get(CategoryListActivity.TAG_CATEGORY_PRODUCT_PRICE);
                    ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
                    for (int i = 0; i < aListType.size(); i++) {
                        System.out.println("-->" + aListType.get(i));
                        priceList.add(aListType.get(i).toString());
                    }

                    String weight_type = resultp.get(CategoryListActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
                    ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
                    for (int i = 0; i < aList.size(); i++) {
                        System.out.println("-->" + aList.get(i));
                        weightList.add(aList.get(i).toString());
                    }

                    selected_price = "" + priceList.get(0);
                    selected_weight = "" + weightList.get(0);

                    pwindow = new Dialog(activity);
                    pwindow.setCanceledOnTouchOutside(true);
                    pwindow.setCancelable(true);
                    pwindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    pwindow.setContentView(R.layout.activity_category_product_price);
                    pwindow.show();

                    product_price_listview = (ListView) pwindow.findViewById(R.id.category_product_price_listview);

                    listAdapter = new WeightListAdapter(activity, priceList, weightList);
                    listAdapter.notifyDataSetChanged();

                    product_price_listview.setAdapter(listAdapter);

                    product_price_listview.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    // When clicked, show a toast with the TextView text

                                    selected_price = "" + priceList.get(position);
                                    selected_weight = "" + weightList.get(position);

                                    ((TextView) itemView.findViewById(R.id.category_product_weight)).setText(selected_weight);
                                    ((TextView) itemView.findViewById(R.id.category_product_quantity_price)).setText(selected_price);

                                    pwindow.dismiss();
                                }
                            });

                }
            });

            category_product_name.setText(resultp
                    .get(CategoryListActivity.TAG_CATEGORY_PRODUCT_NAME));

            Glide.with(activity).load(resultp.get(MainActivity.TAG_CATEGORY_IMAGE))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(category_product_image);

            return itemView;

        }

    }

    public class WeightListAdapter extends BaseAdapter {

        Context activity;
        LayoutInflater inflater;
        ArrayList<String> weightList, priceList;

        public WeightListAdapter(Context activity,
                                 ArrayList<String> priceList, ArrayList<String> weightList) {
            // TODO Auto-generated constructor stub
            this.activity = activity;
            this.weightList = weightList;
            this.priceList = priceList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return priceList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            TextView product_weight, product_price;

            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View itemView = inflater.inflate(R.layout.activity_product_pwindow_list_item,
                    parent, false);

            product_weight = (TextView) itemView.findViewById(R.id.list_item_weightTv);
            product_price = (TextView) itemView.findViewById(R.id.list_item_priceTv);

            product_weight.setText(weightList.get(position));
            product_price.setText("-" + priceList.get(position));

            return itemView;

        }
    }
}
