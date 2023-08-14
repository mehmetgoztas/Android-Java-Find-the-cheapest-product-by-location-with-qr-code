package com.gzt.proje;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FinalActivity extends AppCompatActivity {
    RecyclerView finalR;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        finalR = findViewById(R.id.finalR);
        Bundle bundle = getIntent().getExtras();

        String product = null;
        try{
            GetProductNameAPI api = (GetProductNameAPI) new GetProductNameAPI(bundle.getString("barcode")).execute();
            product = api.get();
            product = product.contains(" ") ? product : String.join("+", product.split(" "));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        JSONObject object = null;
        try {
            products = new ArrayList<>();
            object = new JSONObject(new HTTPAsyncTask().execute("https://serpapi.com/search.json?q=" + product + "&tbm=shop&location=" + bundle.getString("city") + "&hl=en&gl="+ bundle.getString("country") + "&api_key=740620565609583058d1d74f1aaf2c0dd1a6873850bace9d848ae1c10d640adf").get());
            JSONArray arr = object.getJSONArray("shopping_results");
            for(int i = 0; i < arr.length(); i++) {
                Product product1 = new Product(
                        arr.getJSONObject(i).getString("thumbnail"),
                        arr.getJSONObject(i).getString("title"),
                        arr.getJSONObject(i).getString("price"),
                        arr.getJSONObject(i).getString("source"),
                        arr.getJSONObject(i).getString("link")
                );
                products.add(product1);
            }
            RecAdapter adapter = new RecAdapter(products);
            finalR.setAdapter(adapter);
            finalR.setLayoutManager(new LinearLayoutManager(this));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try{
                String result = "";
                InputStream stream;
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                stream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    result += line;
                }
                stream.close();

                return result;
            } catch(Exception e) {
                return "Unable to connect";
            }
        }
    }
    private class GetProductNameAPI extends AsyncTask<Void, Void, String> {
        private String barcodeNumber;

        public GetProductNameAPI(String barcodeNumber) {
            this.barcodeNumber = barcodeNumber;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL("https://www.barkodoku.com/ws/BarkodServis.asmx").openConnection();
                String req = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
                                "<soap12:Envelope xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                                  "<soap12:Body>\n" +
                                    "<BarkodGetir xmlns=\"http://barkodoku.com/\">\n" +
                                      "<apiKey>e96a0c9f25a76602098980f706f7f92748f51a1ed0c74540f1f5b02e97babf16</apiKey>\n" +
                                      "<barkod>" + this.barcodeNumber + "</barkod>\n" +
                                    "</BarkodGetir>\n"+
                                  "</soap12:Body>\n" +
                                "</soap12:Envelope>\n";
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/soap+xml; charset=utf-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                OutputStream stream = conn.getOutputStream();
                stream.write(req.getBytes());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line = bufferedReader.readLine())!=null) {
                    result += line;
                }
                return parse(result);
            }catch(Exception e) {}
            return null;
        }
        private String parse(String xml) throws XmlPullParserException, IOException {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            String result = "";
            String tag = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = xpp.getName();
                } else if (eventType == XmlPullParser.TEXT) {
                    if (tag.equals("UrunAd")) {
                        result = xpp.getText();
                        break;
                    }
                }
                eventType = xpp.next();
            }
            return result;
        }
    }
}