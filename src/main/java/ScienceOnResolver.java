
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ScienceOnResolver {

    private static final String target="RESOLVER";
    private static final String clientID="Your ClientId";
    private static final String accessToken="Your AccessToken";

    public static void main(String[] arg) throws Exception{

        String thesisTitle="Association of p53 Expression with Metabolic Features of Stage I Non-Small Cell Lung Cancer";
        String query=URLEncoder.encode(thesisTitle);

        /** 검색할 쿼리를 입력하여 링크리졸버 api에 request를 요청하고 response를 받는다. */
        String response=ScienceOnResolver.getResolverResponse(query);

        System.out.println(response);
    }
    /**
     * @brief ScienceON 링크리졸버 API사용 함수
     * @return String:요청을 받은 xml값
     * @param thesisTitle:정보를 원하는 논문 명
     */
    public static String getResolverResponse(String thesisTitle){
        String target_URL="https://apigateway.kisti.re.kr/openapicall.do?" +
                "client_id=" +clientID+
                "&token=" +accessToken+
                "&version=1.0" +
                "&action=resolver" +
                "&target=" +target+
                "&atitle="+thesisTitle;


        String response=getResponse(target_URL);
        return response;
    }

    /**
     * @brief 서버로 request요청을 보내고 그에 맞는 response를 받는
     * @return String:요청을 받은 xml or json값
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