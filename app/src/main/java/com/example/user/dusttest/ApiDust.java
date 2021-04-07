package com.example.user.dusttest;

import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;

public class ApiDust {
    private static String DUSTAPI = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/";
    private static String DUSTSERVICEKEY = "4hkg1Uq%2F9ExOvPRrUZ6XYnYlirLdNzV9leuqM0mNxyOoYBV%2B2CbPKR4LXRrUNNMg%2BJwMtgMZGZDNGxrU70yiGw%3D%3D";
    private static AsyncHttpClient asyncHttpClient;
    private static ApiDust api = null;

    static {
        setAsyncHttpClient();
    }

    private synchronized static void setAsyncHttpClient() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(15000);
            asyncHttpClient.setMaxRetriesAndTimeout(5, 3000);
        }
    }

    public synchronized static ApiDust getInstance() {
        setAsyncHttpClient();
        if (api == null) {
            api = new ApiDust();
        }
        return api;
    }

    /**
     * 버전 체크
     *
     * @param activity
     * @param result
     */
    public synchronized void getDustInfo(final Activity activity, final GetDustJson result) {
        final StringBuffer buffer=new StringBuffer();

        asyncHttpClient.post("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?" +
                "sidoName=서울&" +
                "searchCondition=HOUR&" +
                "pageNo=1&" +
                "numOfRows=20&" +
                "ServiceKey=4hkg1Uq%2F9ExOvPRrUZ6XYnYlirLdNzV9leuqM0mNxyOoYBV%2B2CbPKR4LXRrUNNMg%2BJwMtgMZGZDNGxrU70yiGw%3D%3D", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int eventType;

                try {
                    //String To InputStream
                    InputStream is = new ByteArrayInputStream(responseBody);
                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = parserCreator.newPullParser();
                    xpp.setInput(is, null);
                    xpp.next();
                    eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                buffer.append("파싱 시작...\n\n");
                                break;
                            case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행

                                if (xpp.getName().equals("dataTime")) { //title 만나면 내용을 받을수 있게 하자
                                    buffer.append("측정 시간 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("cityName")) { //address 만나면 내용을 받을수 있게 하자
                                    buffer.append("측정 지역 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("so2Value")) { //address 만나면 내용을 받을수 있게 하자
                                    buffer.append("아황산가스 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("coValue")) { //mapx 만나면 내용을 받을수 있게 하자
                                    buffer.append("이산화탄소 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("o3Value")) { //mapy 만나면 내용을 받을수 있게 하자
                                    buffer.append("오존 평균 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("no2Value")) { //mapy 만나면 내용을 받을수 있게 하자
                                    buffer.append("이산화질소 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("pm10Value")) { //mapy 만나면 내용을 받을수 있게 하자
                                    buffer.append("미세먼지 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }else if (xpp.getName().equals("pm25Value")) { //mapy 만나면 내용을 받을수 있게 하자
                                    buffer.append("초미세먼지 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }
                                break;

                            case XmlPullParser.TEXT:
                                break;

                            case XmlPullParser.END_TAG:
                                if(xpp.getName().equals("item")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈

                                break;
                        }
                        eventType = xpp.next();
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("=========statusCode", String.valueOf(statusCode));
                Log.d("=========headers", String.valueOf(headers));
                Log.d("=========responseBody", String.valueOf(responseBody));
                Log.d("=========error", String.valueOf(error));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null)
                                result.getDust(buffer.toString());
                        }
                    });
            }
        });
    }
}
