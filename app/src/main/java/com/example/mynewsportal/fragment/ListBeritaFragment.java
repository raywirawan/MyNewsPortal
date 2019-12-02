package com.example.mynewsportal.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynewsportal.R;
import com.example.mynewsportal.adapter.VerticalBeritaAdapter;
import com.example.mynewsportal.models.Article;
import com.example.mynewsportal.utils.MyUtils;
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
public class ListBeritaFragment extends Fragment implements ListBerita {

    //View Components
    private VerticalBeritaAdapter adapter;
    private TextView tvSearchResult, tvSearchRefresh;
    private View v;
    private ListBeritaViewModel request;
    private SwipeRefreshLayout refreshLayoutList;
    private static final String TAG = "ListBeritaFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_list_berita, container, false);

            tvSearchResult = v.findViewById(R.id.tv_list_berita_result);
            tvSearchRefresh = v.findViewById(R.id.tv_refreshHelper);
            //Show Progress Bar Loading
            refreshLayoutList = v.findViewById(R.id.container_rv_listberita);
            refreshLayoutList.setRefreshing(true);

            //Request Data from API
            fetchData();

            //Attach recyclerView
            RecyclerView recyclerView = v.findViewById(R.id.rv_listberita);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            //Set Listener Refresh
            refreshLayoutList.setOnRefreshListener(() -> fetchData());

            //Attach Adapter to recyclerView
            adapter = new VerticalBeritaAdapter();
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickCallback(article ->
                    Navigation.findNavController(v).navigate(ListBeritaFragmentDirections.actionListBeritaFragmentToDetailBeritaFragment(article)));

        }
        return v;
    }

    private void fetchData(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String savedLang = sharedPref.getString("language", "English");
        String keyword = getArguments().getString("searchKeywords");
        String category = getArguments().getString("category");
        request = new ListBeritaViewModel();
        request.setRequestDataCallback(this);
        request.setArticle(category, keyword, savedLang);
        request.getArticles().observe(getViewLifecycleOwner(), articles -> {
            adapter.setData(articles);
            adapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onSuccessRetrieve(int results){
        refreshLayoutList.setRefreshing(false);
        String keyword = getArguments().getString("searchKeywords");
        String category = getArguments().getString("category");
        String result;
        if ((keyword.length() > 0) &&(category.length() >0)){
            result = "Showing "+results+" top results for '" +keyword+ "' from '" +category+"'";
        } else  if (category.length()>0){
            result = "Showing "+results+" top results from category '" + category +"'";
        } else  {
            result = "Showing "+results+" top results for '" +getArguments().getString("searchKeywords") +"'";
        }
        tvSearchResult.setText(result);
        Log.d(TAG, "onSuccessRetrieve: Callback");
    }
    @Override
    public void onFailureRetrieve(String errorMsg){
        refreshLayoutList.setRefreshing(false);
        tvSearchRefresh.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    };
}

class ListBeritaViewModel extends ViewModel {

    private ListBerita callback;
    private static final String TAG = "ListBeritaViewModel";
    //API
    private static final String API_KEY = "38b8efbd1980491babcbc35f8fc096bb";
    private MutableLiveData<ArrayList<Article>> listArticle = new MutableLiveData<>();

    void setArticle(String category, String keywords, String savedLang) {
        // request API
        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<Article> listItems = new ArrayList<>();
        String url;
        String language = MyUtils.getLanguageFormat(savedLang);

        if (category.length()==0)
            url = "http://newsapi.org/v2/everything?q="+ keywords +"&language="+ language +"&apiKey="+API_KEY;
        else
            url = "http://newsapi.org/v2/top-headlines?q="+ keywords + "&category=" + category +"&language="+ language + "&apiKey="+API_KEY;

        Log.d(TAG, "URL "+url);
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
    void setRequestDataCallback(ListBerita callback){
        this.callback = callback;
    }
}
interface ListBerita {
    void onSuccessRetrieve(int results);
    void onFailureRetrieve(String errorMsg);
}