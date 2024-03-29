package com.example.dincerkizildere.fixmovie.function;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dincerkizildere.fixmovie.R;
import com.example.dincerkizildere.fixmovie.data.APIUser;
import com.example.dincerkizildere.fixmovie.data.FilmAdapter;
import com.example.dincerkizildere.fixmovie.detail.Language;
import com.example.dincerkizildere.fixmovie.detail.NowPlayingModel;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NowPlayingFragment extends Fragment {

    private Context context;
    private Unbinder unbinder;

    @BindView(R.id.rv_now_playing)
    RecyclerView rv_now_playing;

   private FilmAdapter adapter;

   private Call<NowPlayingModel> apiCall;
   private APIUser apiUser= new APIUser();

   public NowPlayingFragment(){

   }
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       View view=inflater.inflate(R.layout.fragment_now_playing,container,false);
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
       if (apiCall!=null) apiCall.cancel();
   }



   private void setupList(){
       adapter=new FilmAdapter();
       rv_now_playing.setLayoutManager(new LinearLayoutManager(context));
       rv_now_playing.setAdapter(adapter);
   }


   private void loadData(){
       apiCall=apiUser.getService().getNowPlayingMovie(Language.getCountry());
       apiCall.enqueue(new Callback<NowPlayingModel>() {
           @Override
           public void onResponse(Call<NowPlayingModel> call, Response<NowPlayingModel> response) {
               if (response.isSuccessful()){
                   adapter.repleaceAll(response.body().getResults());
               }
               else loadFailed();
           }



           @Override
           public void onFailure(Call<NowPlayingModel> call, Throwable t) {
            loadFailed();
           }
       });
   }
    private void loadFailed() {
        Toast.makeText(context,R.string.err_load_failed, Toast.LENGTH_SHORT).show();
    }
}
