package com.example.administrator.wplayer.interfaces;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/12 0012.
 * com.example.administrator.wplayer.interfaces
 * 功能、作用：
 */

public interface RetrofitService {
    @GET("aa/bb/{cc}/dd")
    Call<String> getStringOne(@Path("cc") String cc);

    //baseUrl:https://route.showapi.com/928-1?
    //@Path：URL占位符，用于替换和动态更新,相应的参数必须使用相同的字符串被@Path进行注释
    @GET("/213-4?showapi_appid=35450&showapi_test_draft=false&showapi_sign=b01571f137e446649ee18e8f49f295d5")
    Call<String> getNetAudioLrc(@Query("musicid") String musicid);

    @GET("/213-4?showapi_appid=35450&showapi_test_draft=false&showapi_sign=b01571f137e446649ee18e8f49f295d5")
    Call<String> getStringForAudios(@Query("topid") String type);

    @GET("181-anim1?showapi_appid=24083&showapi_timestamp=20161107201150&showapi_sign=4b35f9175491d9e889224331d012119f")
    Call<String> getStringTwo(@Query("rand") String rand, @Query("num") String num, @Query("page") String page);

    //全路径
    @GET
    Call<String> getStringThree(@Url String url);

    //大文件下载
    @Streaming
    @GET
    Call<ResponseBody> downLoad(@Url String fileUrl);
}
