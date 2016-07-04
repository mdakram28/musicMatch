package com.akbros.musicmatch;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SCService {

    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    public void getTracks(Callback<List<Track>> cb);

    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    public void searchTracks(@Query("q") String q, Callback<List<Track>> cb);

}