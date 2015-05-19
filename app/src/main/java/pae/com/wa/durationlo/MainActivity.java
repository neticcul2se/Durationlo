package pae.com.wa.durationlo;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {
    private double fromLat;
    private double fromLon;
    private double toLat;
    private double toLon;
    private int timeToDestination;
    private int distanceToDestination;
    private  int mint;
    private int sec;
    private int kilo;
    private int mater;
    private int hr;
    JSONObject json_data_invitation = new JSONObject();

    JSONArray jArray_invitation;
Button btn ;
    TextView txt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        btn = (Button)findViewById(R.id.bnt);
       txt1=(TextView)findViewById(R.id.txt1);

        final String urlall = getUrl("13.846435,100.85833","13.842559,100.856334");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlall;
                String resultServer = null;

                resultServer = getHttpGet(url);





                try {
                    JSONObject rootObj = new JSONObject(resultServer); //rootObj
                    JSONArray routes = (JSONArray) rootObj.get("routes");
                    if(routes.length()<1)
                        txt1.setText("ERROR no route there");
                    JSONObject firstRoute = routes.getJSONObject(0);
                    JSONArray legs = (JSONArray) firstRoute.get("legs");
                    if(legs.length()<1)
                        txt1.setText("ERROR no legs there");

                    JSONObject firstLeg = legs.getJSONObject(0);
                    JSONObject durationObject = (JSONObject) firstLeg.get("duration");
                    JSONObject distanceObject = (JSONObject) firstLeg.get("distance");
                                    // finally we will get the values distance in meters and time in seconds!!
                    timeToDestination = (Integer) durationObject.get("value");
                    distanceToDestination = (Integer) distanceObject.get("value");
                    hr=timeToDestination/3600;
                    mint=timeToDestination%3600/60;
                    sec=timeToDestination%3600%60;
                    kilo=distanceToDestination/1000;
                    mater=distanceToDestination%1000;

                    txt1.setText("Time: "+hr+" hr "+mint+" mint "+sec+" sec "+"kilometers "+ kilo+" Meters: "+distanceToDestination);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        });


    }
    public static String getUrl(String fromAdress, String toAdress)
    {// connect to map web service
        StringBuffer urlString = new StringBuffer();
        urlString.append("http://maps.google.com/maps/api/directions/json?origin=");
        urlString.append(fromAdress.toString());
        urlString.append("&destination=");
        urlString.append(toAdress.toString());
        urlString.append("&sensor=false");
        return urlString.toString();
    }


    public String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("strA","pae"));
            params.add(new BasicNameValuePair("strB", "paee"));

            httpGet.setEntity(new UrlEncodedFormEntity(params));*/
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


}
