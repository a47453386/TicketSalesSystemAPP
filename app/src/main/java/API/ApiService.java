package API;

import java.util.List;


import Model.BookingDetailsResponse;
import Model.BookingRequest;
import Model.BookingResponse;
import Model.FAQ;
import Model.LoginRequest;
import Model.LoginResponse;
import Model.Member;
import Model.MemberUserEdit;
import Model.PaymentRequest;
import Model.ProgrammeDetail;
import Model.ProgrammeModel;
import Model.PublicNotice;

import Model.Question;
import Model.QuestionDetail;
import Model.UserOrderDetail;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    // 這裡的網址要對應你 Swagger 看到的後半段

    //節目清單
    @GET("api/HomeApi/Home")
    Call<List<ProgrammeModel>> getProgrammes();
    //最新五筆公告
    @GET("api/HomeApi/FiveNews")
    Call<List<PublicNotice>> GetFiveNews();



    //常見問題
    @GET("api/HomeApi/FAQs")
    Call<List<FAQ>> GetFAQs();

    //我要發問
    @Multipart
    @POST("api/HomeApi/QuestionsCreate")
    Call<ResponseBody> QuestionsCreate(
            @Part("QuestionTitle") RequestBody title,
            @Part("QuestionDescription") RequestBody description,
            @Part("FAQTypeID") RequestBody typeId,
            @Part MultipartBody.Part upload // 🚩 這裡對應 C# 的 IFormFile? upload
    );



    //登入
    @POST("api/HomeApi/Login")
    Call<LoginResponse> login(@Body LoginRequest request);




    //會員資料
    @GET("api/MemberApi/MembersDetails/{id}")
    Call<Member> GetMemberDetails(@Path("id") String id);

    //會員基本資料更新
    // 獲取目前資料填充編輯框
    @GET("api/MemberApi/GetProfile/{id}")
    Call<MemberUserEdit> GetProfile(@Path("id") String id);

    // 送出修改資料
    @POST("api/MemberApi/UpdateProfile")
    Call<ResponseBody> UpdateProfile(@Body MemberUserEdit model);





    //訂單總攬
    @GET("api/OrdersApi/api/OrdersIndex")
    Call<List<BookingDetailsResponse>> GetOrdersIndex() ;

    //訂單詳情
    @GET("api/OrdersApi/OrdersDetail/{id}")
    Call<UserOrderDetail> getOrderDetail(@Path("id") String id) ;




    //全部公告
    @GET("api/PublicNoticeApi/AllNews")
    Call<List<PublicNotice>> GetAllNews();




    //問題清單
    @GET("api/QuestionApi/GetMyQuestions/")
    Call<List<QuestionDetail>> GetMyQuestions();

    //問題詳細資料
    @GET("api/QuestionApi/GetQuestionsDetail/{id}")
    Call<QuestionDetail> getQuestionDetail(@Path("id") String id);















    //節目詳細資料
    @GET("api/SeatsApi/detail/{id}")
    Call<ProgrammeDetail> getProgrammeDetail(@Path("id") String id);

    //確認付款
    @POST("api/SeatsApi/confirm")
    Call<BookingResponse> confirmBooking(@Body BookingRequest request);
    //支付方式
    @POST("api/SeatsApi/payment")
    Call<BookingResponse> processPayment(@Body PaymentRequest request);





}
