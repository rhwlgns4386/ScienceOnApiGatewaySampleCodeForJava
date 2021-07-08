import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContentRecommend {

    private static final String target="RECOMMEND";
    private static final String clientID="Your ClientId";
    private static final String accessToken="Your AccessToken";


    public static void main(String[] arg) throws Exception{

        String recomType="merge";
        String cn="NART95824392";
        String uid="cchh7549";

        /** 추천을 할려는 유형,cn,uid를 입력 받아 콘텐츠 추천 api에 request를 요청하고 response를 받는다. */
        String response=ContentRecommend.getRecommendItem(recomType,cn,uid);

        System.out.println(response);
    }

    /**
     * @brief 추천 api를 사용하기 위한 함수
     * @return String:요청을 받은 xml or json값
     * @param recomType:추천 유형(user,item,content,merge)
     * @param cn:선택한 콘텐츠 ID
     * @param uid:로그인 이용자 ID
     */
    private static String getRecommendItem(String recomType,String cn,String uid) {
        String target_URL="https://apigateway.kisti.re.kr/openapicall.do?" +
                "client_id=" +clientID+
                "&token="+accessToken+"" +
                "&version=1.0" +
                "&action=browse" +
                "&target=" +target+
                "&recomType="+recomType+
                "&cn="+cn+
                "&uid="+uid;

        /** api요청을 보냅니다. */
        String response = getResponse(target_URL);

        return response;
    }

    /**
     * @brief 서버로 request요청을 보내고 그에 맞는 response를 받는
     * @return String:요청을 받은 xml값
     * @param target_URL:요청을 보낼 url
     */
    private static String getResponse(String target_URL) {
        try {
            URL url = new URL(target_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET"); // optional default is GET
            int responseCode=con.getResponseCode();
            BufferedReader in;
            if(responseCode!=HttpURLConnection.HTTP_OK){
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            else{
                in= new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            String inputLine;
            StringBuffer response= new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            return  response.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}