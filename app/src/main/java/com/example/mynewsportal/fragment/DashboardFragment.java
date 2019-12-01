package com.example.mynewsportal.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mynewsportal.R;
import com.example.mynewsportal.adapter.HorizontalBeritaAdapter;
import com.example.mynewsportal.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class DashboardFragment extends Fragment implements Dashboard{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View v;
    private DashboardViewModel request;
    private HorizontalBeritaAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v == null){
            v = inflater.inflate(R.layout.fragment_dashboard, container, false);

            fetchData();

            RecyclerView recyclerView = v.findViewById(R.id.rv_dashboard_listberita);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            //Attach Adapter to recyclerView
            adapter = new HorizontalBeritaAdapter();
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickCallback(article ->
                    Navigation.findNavController(v).navigate(DashboardFragmentDirections.actionDashboardFragmentToDetailBeritaFragment(article)));
        }
        return v;
    }
    private void fetchData(){
        request = new DashboardViewModel();
        request.setDashboardCallback(this);
        request.setArticle("");
        request.getArticles().observe(getViewLifecycleOwner(), articles -> {
            adapter.setData(articles);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onSuccessRetrieve(int results) {

    }

    @Override
    public void onFailureRetrieve(String errorMsg) {
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }
}

class DashboardViewModel extends ViewModel {

    private Dashboard callback;
    private static final String TAG = "ListBeritaViewModel";
    //API
    private static final String API_KEY = "38b8efbd1980491babcbc35f8fc096bb";
    private MutableLiveData<ArrayList<Article>> listArticle = new MutableLiveData<>();

    void setArticle(String arguments) {
        String category = "entertainment";
        String language = "en";
        String country = "us";
        String url;
        String endpoint = "top-headlines";
        // request API
        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<Article> listItems = new ArrayList<>();
        if (endpoint.equals("everything")){
            url = "http://newsapi.org/v2/"+endpoint+"?q=" + arguments +"&language="+ language + "&apiKey=" + API_KEY;
        }else{
            url = "http://newsapi.org/v2/"+endpoint+"?q=" + arguments +"&language="+ language + "&country="+ country +"&apiKey=" + API_KEY;
        }

        Log.d(TAG, "setArticle: "+url);

        //Establish connection and request
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
                    //number of query results
                    callback.onSuccessRetrieve(listItems.size());
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
    void setDashboardCallback(Dashboard callback){
        this.callback = callback;
    }
}
interface Dashboard {
    void onSuccessRetrieve(int results);
    void onFailureRetrieve(String errorMsg);
}