package API;

import java.util.List;


import Model.BookingDetailsResponse;
import Model.BookingRequest;
import Model.BookingResponse;
import Model.PaymentRequest;
import Model.ProgrammeDetail;
import Model.ProgrammeModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // 這裡的網址要對應你 Swagger 看到的後半段

    //節目清單
    @GET("api/SeatsApi/apihome")
    Call<List<ProgrammeModel>> getProgrammes();

    //節目詳細資料
    @GET("api/SeatsApi/detail/{id}")
    Call<ProgrammeDetail> getProgrammeDetail(@Path("id") String id);

    //確認付款
    @POST("api/SeatsApi/confirm")
    Call<BookingResponse> confirmBooking(@Body BookingRequest request);
    //支付方式
    @POST("api/SeatsApi/payment")
    Call<BookingResponse> processPayment(@Body PaymentRequest request);
    //訂單總攬
    @GET("api/OrdersApi/OrdersIndex")
    Call<List<BookingDetailsResponse>> GetOrdersIndex() ;


}
