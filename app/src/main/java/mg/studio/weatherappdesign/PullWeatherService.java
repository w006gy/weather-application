package mg.studio.weatherappdesign;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PullWeatherService implements Runnable{
    public Weather weather;
    public PullWeatherService(Weather w){
        weather=w;
    }
    @Override
    public void run(){
        try {
            getWeather(weather);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ------------------------使用PULL解析XML-----------------------
     *
     * @return
     * @throws Exception
     * @paraminStream
     */
    public static void getWeather(Weather weather)
            throws Exception {
        Log.i("getweather", "0");

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = factory.newPullParser();

        //参数url化
        String city = java.net.URLEncoder.encode("重庆", "utf-8");
        //拼地址
        String apiUrl = String.format("https://www.sojson.com/open/api/weather/xml.shtml?city=%s", city);
        //开始请求
        URL url = new URL(apiUrl);
        Log.i("getweather", url.toString());
        URLConnection open = url.openConnection();
        InputStream input = open.getInputStream();
        /*HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        onnection.setConnectTimeout(5000);
        int code=connection.getResponseCode();
        XmlPullParser xmlPullParser= Xml.newPullParser();
        Log.i("getweather", "connection");
        if(code==200){
            InputStream is=connection.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            Log.i("getweather", "code200");

            xmlPullParser.setInput(is,"UTF-8");
        }*/
        Log.i("getweather", "open connection succeed");
        //InputStream input = open.getInputStream();
        Log.i("getweather", "inputStream succeed");
        //InputStream input= getAssets().open(url);
        //xmlPullParser.setInput(new StringReader(weather));

        //xmlPullParser.setInput(new StringReader(xmlData));
        Log.i("getweather", "1");
        xmlPullParser.setInput(input, "utf-8");
        int eventType = xmlPullParser.getEventType();
        boolean b_finish = false;
        String temprature = "";
        String[] nextday = new String[5];
        int i = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {

            String nodeName = xmlPullParser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG: {
                    if ("wendu".equals(nodeName)) {
                        temprature = xmlPullParser.nextText();
                        Log.i("getweather", "2");
                    } else if ("forecast".equals(nodeName)) {

                        //进入第二个循环
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            String nodeName2 = xmlPullParser.getName();
                            switch (eventType) {
                                case XmlPullParser.START_TAG: {
                                    if ("day".equals(nodeName2)) {
                                        while (eventType != XmlPullParser.END_DOCUMENT){
                                            //xmlPullParser.nextText();

                                            String nodeName3 = xmlPullParser.getName();
                                            if ("type".equals(nodeName3)) {
                                                if (i >= 5) {
                                                    break;
                                                }
                                                else{
                                                nextday[i] = xmlPullParser.nextText();
                                                i++;
                                                }
                                                break;
                                            }
                                            eventType = xmlPullParser.next();
                                        }
                                    }
                                }
                                default:
                                    break;
                            }
                            eventType = xmlPullParser.next();
                            if (i >= 5) {
                                b_finish = true;
                                Log.i("getweather", "3");
                                break;
                            }

                        }break;
                    }
                    break;
                }
            }
            eventType=xmlPullParser.next();
            if (b_finish) {
                break;
            }


        }
        //遍历完毕
        //TextView temp= (TextView)findViewById(R.id.temperature_of_the_day) ;
        Log.i("getweather", "4");
        weather.temprature = temprature;
        weather.nextday = nextday;
        Log.i("weather",i + "" );
        Log.i("weather", weather.temprature+weather.nextday[3]);
    }
}
/*
    public void testPullGetPersons() throws Throwable {
        InputStream inStream = this.getClass().getClassLoader()
                .getResourceAsStream("person.xml");
        List<Weather> weatherList= PullWeatherService.getWeather(inStream);
        for (Weather weather : weatherList) {
            Log.i(TAG, weather.toString());
        }
    }*/