package com.example.dincerkizildere.fixmovie.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.dincerkizildere.fixmovie.BuildConfig;
import com.example.dincerkizildere.fixmovie.R;
import com.example.dincerkizildere.fixmovie.detail.ItemResult;

import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

import static com.example.dincerkizildere.fixmovie.database.DatabaseContact.CONTENT_URI;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    private Context mContext;
    private int mAppWidgetId;

    private Cursor list;

    public StackRemoteViewsFactory(Context applicationContext, Intent intent){
        mContext=applicationContext;
        mAppWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);


    }
    @Override
    public void onCreate() {
        list= mContext.getContentResolver().query(
                CONTENT_URI,
                null,null,null,null
        );

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return list.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        ItemResult item=getItem(i);
        RemoteViews rv=new RemoteViews(mContext.getPackageName(), R.layout.stack_widget_item);


        Bitmap bitmap=null;

        try{
            bitmap= Glide.with(mContext)
                    .asBitmap()
                    .load(BuildConfig.BASE_URL_IMG + "w500" + item.getBackdropPath())
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }

        rv.setImageViewBitmap(R.id.imageView, bitmap);

        Bundle extras=new Bundle();
        extras.putInt(StackWidget.EXTRA_ITEM, i );
        Intent fillInIntent=new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);


        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    private ItemResult getItem(int position) {
        if (!list.moveToPosition(position)) {
            throw new IllegalStateException("Position invalid!");
        }

        return new ItemResult(list);
    }
}
