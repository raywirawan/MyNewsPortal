package com.example.mynewsportal.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynewsportal.R;
import com.example.mynewsportal.activity.AddFollowActivity;
import com.example.mynewsportal.adapter.HorizontalBeritaAdapter;
import com.example.mynewsportal.models.Article;
import com.example.mynewsportal.utils.MyUtils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;


public class DashboardFragment extends Fragment implements Dashboard{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View v;
    private DashboardViewModel request;
    private HorizontalBeritaAdapter[] adapters;
    private TextView[] textViews;
    private ProgressBar[] pbs;
    private Set<String> followingSet;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v == null){
            v = inflater.inflate(R.layout.fragment_dashboard, container, false);

            pbs = new ProgressBar[5];
            pbs[0] = v.findViewById(R.id.pb_dashboard);
            pbs[1] = v.findViewById(R.id.pb_dashboard_1);
            pbs[2] = v.findViewById(R.id.pb_dashboard_2);
            pbs[3] = v.findViewById(R.id.pb_dashboard_3);
            pbs[4] = v.findViewById(R.id.pb_dashboard_4);

            textViews = new TextView[5];
            textViews[0] = v.findViewById(R.id.tv_following);
            textViews[1] = v.findViewById(R.id.tv_following_1);
            textViews[2] = v.findViewById(R.id.tv_following_2);
            textViews[3] = v.findViewById(R.id.tv_following_3);
            textViews[4] = v.findViewById(R.id.tv_following_4);

            //Instance recyclerViews and adapters;
            RecyclerView[] recyclerViews = new RecyclerView[5];
            recyclerViews[0] = v.findViewById(R.id.rv_dashboard_listberita);
            recyclerViews[1] = v.findViewById(R.id.rv_dashboard_listberita_1);
            recyclerViews[2] = v.findViewById(R.id.rv_dashboard_listberita_2);
            recyclerViews[3] = v.findViewById(R.id.rv_dashboard_listberita_3);
            recyclerViews[4] = v.findViewById(R.id.rv_dashboard_listberita_4);

            adapters = new HorizontalBeritaAdapter[5];
            for (int i = 0; i < adapters.length; i++) {
                adapters[i] = new HorizontalBeritaAdapter();
                adapters[i].notifyDataSetChanged();
            }

            //get saved following
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            Set<String> defaultData = new HashSet<>();
            defaultData.add("Technology");
            followingSet = sharedPref.getStringSet("following", defaultData);
            //load data from API
            fetchData();

            //Attach Adapter to recyclerView
            for (int i = 0; i < followingSet.size(); i++) {
                recyclerViews[i].setAdapter(adapters[i]);
                adapters[i].setOnItemClickCallback(article ->
                        Navigation.findNavController(v).navigate(DashboardFragmentDirections.actionDashboardFragmentToDetailBeritaFragment(article)));
            }
            //Listener floating action button
            ExtendedFloatingActionButton fab = v.findViewById(R.id.fab_tambah);
            fab.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), AddFollowActivity.class);
                startActivity(intent);
            });
        }
        return v;
    }
    private void fetchData(){
        //get saved language
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String savedLang = sharedPref.getString("language", "English");
        int i = 0;
        for (String keyword : followingSet) {
            textViews[i].setVisibility(View.VISIBLE);
            textViews[i].setText("Here's some articles about "+keyword);
            pbs[i].setVisibility(View.VISIBLE);
            request = new DashboardViewModel();
            request.setDashboardCallback(this);
            request.setArticle(keyword, savedLang);
            final int y = i;
            request.getArticles().observe(getViewLifecycleOwner(), articles -> {
                adapters[y].setData(articles);
                adapters[y].notifyDataSetChanged();
                if (articles!=null){
                    pbs[y].setVisibility(View.GONE);
                }
            });
            i++;
        }
    }

    @Override
    public void onSuccessRetrieve(int results) {
//        pb.setVisibility(View.GONE);
    }

    @Override
    public void onFailureRetrieve(String errorMsg) {
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
//        pb.setVisibility(View.GONE);
    }
}

class DashboardViewModel extends ViewModel {

    private Dashboard callback;
    private static final String TAG = "DashboardViewModel";
    //API
    private static final String API_KEY = "38b8efbd1980491babcbc35f8fc096bb";
    private MutableLiveData<ArrayList<Article>> listArticle = new MutableLiveData<>();


    void setArticle(String arguments, String lang) {
        String category = "entertainment";
        String language = MyUtils.getLanguageFormat(lang);
        String country = "us";
        String url;
        String endpoint = "everything";
        // request API
        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<Article> listItems = new ArrayList<>();

        url = "http://newsapi.org/v2/"+endpoint+"?q=" + arguments +"&language="+ language + "&apiKey=" + API_KEY;


        Log.d(TAG, "RequestURL: "+url);

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