package com.example.mynewsportal.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mynewsportal.R;
import com.example.mynewsportal.adapter.ListBeritaAdapter;
import com.example.mynewsportal.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBeritaFragment extends Fragment implements RequestDataCallback{

    //View Components
    private ListBeritaAdapter adapter;
    private ProgressBar pbLoading;
    private RequestData request;
    private static final String TAG = "ListBeritaFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_berita, container, false);

//        Show Progress Bar Loading
        pbLoading = v.findViewById(R.id.pb_list_berita);
        pbLoading.setVisibility(View.VISIBLE);

        //Request Data from API
        request = new RequestData();
        request.setRequestDataCallback(this);
        request.setArticle(getArguments().getString("searchKeywords"));
        request.getArticles().observe(getViewLifecycleOwner(), articles -> {
            adapter.setData(articles);
            Log.d(TAG, "onCreateView: "+articles.size());
        });

        //Attach recyclerView
        RecyclerView recyclerView = v.findViewById(R.id.rv_listberita);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Attach Adapter to recyclerView
        adapter = new ListBeritaAdapter();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        return v;
    }
    @Override
    public void onSuccessRetrieve(){
        pbLoading.setVisibility(View.GONE);
        Log.d(TAG, "onSuccessRetrieve: Callback");
    }
    @Override
    public void onFailureRetrieve(String errorMsg){
        pbLoading.setVisibility(View.GONE);
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    };

}
class RequestData extends ViewModel {

    RequestDataCallback callback;
    private static final String TAG = "RequestData";
    //API
    private static final String API_KEY = "38b8efbd1980491babcbc35f8fc096bb";
    private MutableLiveData<ArrayList<Article>> listArticle = new MutableLiveData<>();

    void setArticle(String keywords) {
        // request API
        String keyword = keywords;
        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<Article> listItems = new ArrayList<>();
        String url = "https://newsapi.org/v2/everything?q="+keyword+"&apiKey="+API_KEY;
        client.get(url, new AsyncHttpResponseHandler() {
            //If request API return success
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    //Retrieve JSON Data
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("articles");
                    for (int i = 0; i < list.length(); i++) {
                        //Parsing JSON Data
                        JSONObject articleObject = list.getJSONObject(i);
                        Article article = new Article(
                                articleObject.getJSONObject("source").getString("name"),
                                articleObject.getString("author"),
                                articleObject.getString("title"),
                                articleObject.getString("description"),
                                articleObject.getString("url"),
                                articleObject.getString("urlToImage"),
                                articleObject.getString("publishedAt"),
                                articleObject.getString("content")
                        );
                        listItems.add(article);
                    }
                    listArticle.postValue(listItems);
                    callback.onSuccessRetrieve();
                } catch (JSONException e) {
                    System.out.println("Exception : "+e.getMessage());
                }
            }
            //If request API return failure
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("OnFailure :"+error.getMessage());
                callback.onFailureRetrieve(error.getMessage());
            }
        });
    }
    LiveData<ArrayList<Article>> getArticles() {
        return listArticle;
    }
    void setRequestDataCallback(RequestDataCallback callback){
        this.callback = callback;
    }
}
interface RequestDataCallback {
    void onSuccessRetrieve();
    void onFailureRetrieve(String errorMsg);
}
