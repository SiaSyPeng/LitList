package com.wabalub.cs65.litlist.MyLibs;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Library for completing all of the HTTP get and posting
 */

public class InternetMgmtLib {
    public interface InternetListener {
        void onResponse(final int requestCode, final String res);
        void onErrorResponse(final int requestCode, final String res);
    }

    /**
     * Method to do a post request, sending JSON
     * @param internetListener the listener
     * @param queue the Volley queue
     * @param url the base URL
     * @param requestCode the request code
     * @param req the String request in valid JSON format
     */
    public static void postJSON(final InternetListener internetListener, RequestQueue queue, String url, final int requestCode, String req) {
        // Request a JSON response from the provided URL by adding to the request queue
        queue.add(createJSONRequest(internetListener, url, requestCode, toJSON(req), Request.Method.POST));
    }

    /**
     * Method to do a get request, sending JSON
     * @param internetListener the listener
     * @param queue the Volley queue
     * @param url the base URL
     * @param requestCode the request code
     * @param req the String request in valid JSON format
     */
    public static void getJSON(final InternetListener internetListener, RequestQueue queue, String url,  final int requestCode, String req) {
        // Request a JSON response from the provided URL.
        JsonObjectRequest joRequest = createJSONRequest(internetListener, url, requestCode, toJSON(req), Request.Method.GET);
        queue.add(joRequest);
    }

    /**
     * Method to do a post request, sending a String
     * @param internetListener the listener
     * @param queue the Volley queue
     * @param url the base URL
     * @param requestCode the request code
     */
    public static void postString(final InternetListener internetListener, RequestQueue queue, String url,  final int requestCode, Map<String, String> args) {
        // Add the request to the RequestQueue
        queue.add(createStringRequest(internetListener, buildUrlFromArgs(url, args), requestCode,Request.Method.POST));
    }

    /**
     * Method to do a get request, sending a String
     * @param internetListener the listener
     * @param queue the Volley queue
     * @param url the base URL
     * @param requestCode the request code
     */
    public static void getString(final InternetListener internetListener, RequestQueue queue, String url,  final int requestCode, Map<String, String> args) {
        // Add the request to the RequestQueue
        queue.add(createStringRequest(internetListener, buildUrlFromArgs(url, args), requestCode,Request.Method.GET));
    }

    /**
     * Method to upload a file
     * @param internetListener listener for the response tot he uplaod
     * @param progressListener listener for the progress
     * @param queue queue to add the request to
     * @param url base url for the server
     * @param requestCode request code
     * @param file file to upload
     */
    public static void uploadFile(final InternetListener internetListener, final Response.ProgressListener progressListener, RequestQueue queue, String url, final int requestCode, File file){
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, "http://posttestserver.com/post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        Log.d("UPLOAD:", res);
                        internetListener.onResponse(requestCode, res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                internetListener.onErrorResponse(requestCode, error.toString());
            }
        });

        smr.addFile("image", file.getPath());
        smr.setFixedStreamingMode(true);
        smr.setOnProgressListener(progressListener);

        queue.add(smr);
    }

    /**
     * Method to create a JSON request
     * @param internetListener listener to handle the response
     * @param url base URL
     * @param requestCode GET or POST
     * @param req the request
     */
    private static JsonObjectRequest createJSONRequest(final InternetListener internetListener, String url, final int requestCode, JSONObject req, int method) {
        return new JsonObjectRequest(method, url,  // POST is presumed
                req,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        internetListener.onResponse(requestCode, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                internetListener.onErrorResponse(requestCode, "Error" + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                {
                    Map<String, String> params = new HashMap<String, String>();
                    // params.put("Accept", "application/json");
                    params.put("Accept-Encoding", "identity");
                    // params.put("Content-Type", "application/json");

                    return params;
                }
            }
        };
    }

    /**
     * Creates a String request
     * @param internetListener the listener to handle the response
     * @param url The base URL
     * @param requestCode The request code
     * @param method GET or POST
     * @return string request
     */
    private static StringRequest createStringRequest(final InternetListener internetListener, String url, final int requestCode, int method){
        StringRequest request = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        internetListener.onResponse(requestCode, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                internetListener.onErrorResponse(requestCode, "Error" + error.toString());
            }
        });
        request.setShouldCache(false);
        return request;
    }

    /**
     * Converts a string to JSON
     * @param str string
     * @return JSON object
     */
    private static JSONObject toJSON(String str){
        JSONObject jsonReq;

        try {
            jsonReq = new JSONObject(str);
        } catch (JSONException e) {
            // Warn the user that something is wrong; do not connect
            Log.d("JSON", "Invalid JSON: " + e.toString());

            return null;
        }

        return jsonReq;
    }

    /**
     * Method to build a URL from the arguments of the string
     * @param url the base url
     * @param args the arguments you want to pass to the server
     * @return String url
     */
    private static String buildUrlFromArgs(String url, Map<String, String> args){
        StringBuilder full_url = new StringBuilder(url + "?");
        for(String key : args.keySet()) full_url.append(key).append("=").append(args.get(key)).append("&");
        full_url.setLength(full_url.length() - 1); // removes the extra "&"
        Log.d("NET", "built url: " + full_url.toString());
        return full_url.toString();
    }
}