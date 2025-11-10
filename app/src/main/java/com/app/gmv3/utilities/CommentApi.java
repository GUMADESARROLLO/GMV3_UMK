package com.app.gmv3.utilities;


import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommentApi {
    @POST("remove_comments")
    Call<ResponseBody> deleteComment(@Body Map<String, String> body);
}
