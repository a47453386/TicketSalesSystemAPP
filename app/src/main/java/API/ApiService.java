package API;

import java.util.List;



import Model.ProgrammeModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // 這裡的網址要對應你 Swagger 看到的後半段
    @GET("api/SeatsApi/api/home")
    Call<List<ProgrammeModel>> getProgrammes();
}
