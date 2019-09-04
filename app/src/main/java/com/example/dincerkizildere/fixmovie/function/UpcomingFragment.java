package com.example.dincerkizildere.fixmovie.function;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dincerkizildere.fixmovie.R;
import com.example.dincerkizildere.fixmovie.data.APIUser;
import com.example.dincerkizildere.fixmovie.data.FilmAdapter;
import com.example.dincerkizildere.fixmovie.detail.Language;
import com.example.dincerkizildere.fixmovie.detail.UpcomingModel;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingFragment extends Fragment {

    private Context context;
    private Unbinder unbinder;

    @BindView(R.id.rv_upcoming)
    RecyclerView rv_upcoming;

    private FilmAdapter adapter;

    private Call<UpcomingModel> apiCall;
    private APIUser apiUSer= new APIUser();

    public UpcomingFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_upcoming, container, false);
        context=view.getContext();

        unbinder=ButterKnife.bind(this,view);

        setupList();
        loadData();

        return view;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
        if (apiCall != null) apiCall.cancel();

    }

    private void setupList()
    {
        adapter=new FilmAdapter();
        rv_upcoming.setLayoutManager(new LinearLayoutManager(context));
        rv_upcoming.setAdapter(adapter);
    }

    private void loadData(){
        apiCall=apiUSer.getService().getUpcomingMovie(Language.getCountry());
        apiCall.enqueue(new Callback<UpcomingModel>() {
            @Override
            public void onResponse(Call<UpcomingModel> call, Response<UpcomingModel> response) {
                if(response.isSuccessful()){
                    adapter.repleaceAll(response.body().getResults());
                }else loadFailed();
            }

            @Override
            public void onFailure(Call<UpcomingModel> call, Throwable t) {
                loadFailed();
            }
        });
    }
    private void loadFailed(){
        Toast.makeText(context,R.string.err_load_failed, Toast.LENGTH_SHORT).show();
    }
}
