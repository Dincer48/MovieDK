package com.example.dincerkizildere.fixmovie.data;

import com.example.dincerkizildere.fixmovie.function.Trailer;

import java.util.List;

public interface OnGetTrailersCallback {
    void onSuccess(List<Trailer> trailers);

    void onError();
}