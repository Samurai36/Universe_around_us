package viktor.khlebnikov.geekgrains.android1.universearoundus.ui.picture

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

interface PictureOfTheDayAPI {

    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("date") date: LocalDate,
        @Query("api_key") apiKey: String
    ): Call<PODServerResponseData>
//
//    @GET("planetary/apod")
//    fun getPictureOfTheDay(
//        @Query("api_key") apiKey: String
//    ): Call<PODServerResponseData>

}
