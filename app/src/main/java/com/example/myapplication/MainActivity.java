package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static final String apiKey = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////////////////////Here starts Chatgpt stuff////////////////////////////////////////////////////////////////////
      /*  Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatGPTService = retrofit.create(ChatGPTService.class);

    */

        //try {
            //String chatGptResponse = extractTextFromChatGPTResponse(callChatGPTAPI("give me a fun fact about yellow"));
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  /*  addNotification(chatGptResponse); */
                    MyTask myTask = new MyTask();
                    myTask.execute();

                    addNotification(myTask.getContentText());
                }
            });

        //} catch (IOException e) {
           // throw new RuntimeException(e);
       // }


    }


    /////////////////////////////////////////// notification stuff /////////////////////////////////////////
    private static final String CHANNEL_ID = "coloris"; //THESE two are important to create a notification channel
    private static final String CHANNEL_NAME = "coloris";
    private static final String CHANNEL_DESC = "coloris";


    public String addNotification(String contentText) {// DISPLAYING NOTIFICATION (??)
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Coloris Fun Facts")
                        .setContentText(contentText)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(contentText)).
                        setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, MainActivity.class);


        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return "not granted";
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// creating a notification channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        notificationManagerCompat.notify(1, builder.build());
        return "granted";//display the notification
    }
    ////////////////////////////////////////// CHATGPT STUFF//////////////////////////////////////////////////////////////


    private static final String CHATGPT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public static String callChatGPTAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(CHATGPT_API_ENDPOINT).newBuilder();
        /*urlBuilder.addQueryParameter("prompt", prompt); */
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "    \"model\": \"gpt-3.5-turbo\",\n" +
                        "    \"messages\": [\n" +
                        "        {\n" +
                        "            \"role\": \"user\",\n" +
                        "            \"content\": \"give me a fun fact about the color red\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}"))
                .build();


        try{
        okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body is null");
            }
            BufferedSource source = responseBody.source();
            String stringResponse = source.readString(Charset.forName("UTF-8"));
            //String stringResponse = responseBody.toString();

            responseBody.close();
            return stringResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;}}

    static String extractTextFromChatGPTResponse(String response) {
        if (response == null) {
            return null;
        }
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() == 0) {
                throw new JSONException("No choices found in response: " + response);
            }
            JSONObject choice = choices.getJSONObject(0);
            JSONObject messageObject = choice.getJSONObject("message");
            String contentValue = messageObject.getString("content");
            return contentValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }






}




