package mg.studio.weatherappdesign;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;




public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getWeather() throws Exception {

    Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //星期
        int mWay = calendar.get(Calendar.DAY_OF_WEEK);
        TextView date = (TextView) findViewById(R.id.tv_date);
        date.setText(month + "/" + day + "/" + year);
        TextView week = (TextView) findViewById(R.id.text_week);
        String str = new String();
        switch (mWay) {
            case 1:
                str = "Sunday";
                break;
            case 2:
                str = "Monday";
                break;
            case 3:
                str = "Tuesday";
                break;
            case 4:
                str = "Wednesday";
                break;
            case 5:
                str = "Thursday";
                break;
            case 6:
                str = "Friday";
                break;
            case 7:
                str = "Saturday";
                break;

        }
        week.setText(str);
        TextView[] tv_nd=new TextView[4];
        tv_nd[0]=(TextView) findViewById(R.id.tv_day0);
        tv_nd[1]=(TextView) findViewById(R.id.tv_day1);
        tv_nd[2]=(TextView) findViewById(R.id.tv_day2);
        tv_nd[3]=(TextView) findViewById(R.id.tv_day3);
        String[] str_week={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

        for(int i=0;i<4;i++){
            if(mWay+i<=7){
                tv_nd[i].setText(str_week[mWay+i]);
            }
            else {
                tv_nd[i].setText(str_week[mWay+i-7]);
            }
        }

        Weather weather= new Weather();
        Runnable r = new PullWeatherService(weather);
        Thread thread=new Thread(r);
        thread.start();
        sleep(2000);
        Log.i("weather2", weather.temprature+weather.nextday[3]);
        /*
        try {
            PullWeatherService.getWeather(weather);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //Random random = new Random();
        //int temperature = random.nextInt(20) + 10;

        TextView temp = (TextView) findViewById(R.id.temperature_of_the_day);
        temp.setText(weather.temprature);
        //绑定imageView控件
        ImageView[] imageViews=new ImageView[5];
        imageViews[0]=(ImageView) findViewById(R.id.img_weather_condition);
        imageViews[1]=(ImageView) findViewById(R.id.img_day0);
        imageViews[2]=(ImageView) findViewById(R.id.img_day1);
        imageViews[3]=(ImageView) findViewById(R.id.img_day2);
        imageViews[4]=(ImageView) findViewById(R.id.img_day3);
        Log.i("weather3", weather.temprature+weather.nextday[3]);


        for(int i=0;i<5;i++){
            if(weather.nextday[i].equals("晴")){
              imageViews[i].setImageResource(R.drawable.sunny_small);
            }
            else if(weather.nextday[i].equals("阴")){
                imageViews[i].setImageResource(R.drawable.windy_small);
            }
            else if(weather.nextday[i].equals("多云")){
                imageViews[i].setImageResource(R.drawable.partly_sunny_small);
            }
            else if(weather.nextday[i].contains("雨")){
                imageViews[i].setImageResource(R.drawable.rainy_small);
            }
        }

        
        
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }

    public void refresh(View view) {
        try {
            getWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        try {
            getWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        //final ProgressBar progressBar = new ProgressBar(MainActivity.this);
        //Button butt_refresh = (Button) findViewById(R.id.button_refresh);
        //progressBar.setVisibility(View.VISIBLE);
        /*final RotateAnimation animation = new RotateAnimation(0.0f, 180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration( 50 );
        butt_refresh.startAnimation( animation );
*/

        /*try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //progressDialog.dismiss();   //关掉进度条

        // progressBar.setVisibility(View.GONE);
        //butt_refresh.clearAnimation();
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://mpianatra.com/Courses/info.txt";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
        }
    }
}
