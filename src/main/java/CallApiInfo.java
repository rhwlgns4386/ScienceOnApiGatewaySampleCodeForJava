import Detailed.ResearcherDetailed;
import Search.SearchThesis;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;

public class CallApiInfo {

    public static void main(String[] args) throws Exception {

        String cn="322433";

        /** CallApiInfo의 실행을 위해 연구원상세보기 api를 호출합니다. */
        String response= ResearcherDetailed.select(cn);

        /**
         * XML를 Json으로 변경후 callAPIInfo까지 하여 논문 검색 api를 실행하는 함수
         * 자세한 내용은 ScienceOn API Gateway를 참조해주세요
         */
        callApiInfoForJson(response);

        /**
         * XML를 변환하지 않고 callAPIInfo까지 접근하여 논문 검색 api를 실행하는 함수
         * 자세한 내용은 ScienceOn API Gateway를 참조해주세요
         */
        callApiInfoForXml(response);
    }

    /**
     * @brief 연구원상세보기,연구기관상세보기의 response에서 CallApiInfo를 찾고 검색을 xml로 실행하느 함수
     * @param response:연구원상세보기,연구기관상세보기의 response를 입력하세요
     */
    private static void callApiInfoForXml(String response) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document document=builder.parse(new InputSource(new StringReader(response)));
        NodeList nodelist=document.getElementsByTagName("item");

        /** item태그의 Element들사이에서 metacode값이 callApiInfo를 찾고 논문 검색 aip를 실행합니다. */
        for (int i=0;i<nodelist.getLength();i++){
            Element el= (Element) nodelist.item(i);
            if(el.getAttribute("metaCode").equalsIgnoreCase("CallAPIInfo")){
                if(el.getElementsByTagName("item").item(0).getChildNodes().item(0).getNodeValue().equals("API-003-01")){
                    String query=URLEncoder.encode(el.getElementsByTagName("item").item(2)
                                    .getChildNodes().item(0).getNodeValue());
                    String searchResponse =SearchThesis.getSearchResults(query);
                    System.out.println(searchResponse);
                }
            }

        }
    }

    /**
     * @brief 연구원상세보기,연구기관상세보기의 response에서 CallApiInfo를 찾고 검색을 json으로 실행하는 함수
     * @param response:연구원상세보기,연구기관상세보기의 response를 입력하세요
     */
    private static void callApiInfoForJson(String response) {
        JSONObject metaData= (JSONObject) XML.toJSONObject(response).get("MetaData");
        JSONObject recordList= (JSONObject) metaData.get("recordList");
        JSONObject record= (JSONObject) recordList.get("record");
        JSONArray items= (JSONArray) record.get("item");

        /** item리스트 안에 있는 json데이터를 돌면서 CallAPIInfo를 찾고 논문 검색 api를 실행합니다. */
        for (Object obj:items){
            JSONObject item=(JSONObject)obj;
            if(item.has("item")){
                JSONArray callAPIInfo=(JSONArray) item.get("item");
                JSONObject temObject=(JSONObject) callAPIInfo.get(0);
                if (temObject.get("content").toString().equals("API-001-01")){
                    temObject= (JSONObject) callAPIInfo.get(2);
                    String query=URLEncoder.encode(temObject.get("content").toString());
                    String searchResponse = SearchThesis.getSearchResults(query);
                    System.out.println(searchResponse);
                }
            }
        }
    }
}
