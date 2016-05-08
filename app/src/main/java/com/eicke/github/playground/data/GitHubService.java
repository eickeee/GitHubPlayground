package com.eicke.github.playground.data;

import com.eicke.github.playground.data.entities.Repository;
import com.eicke.github.playground.data.entities.UserSearchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubService {

    @GET("users/{user}/repos")
    Call<List<Repository>> listRepositories(@Path("user") String user);

    @GET("search/users")
    Call<UserSearchResponse> searchUsers(@Query("q") String user);

}
