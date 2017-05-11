package ramBot;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class ApiAiClient {

	AIDataService dataService;
	
	public ApiAiClient(String APIKey) {
		AIConfiguration configuration = new AIConfiguration(APIKey);
		dataService = new AIDataService(configuration);
	}
	
	public String getResponce(String query) {
		 try {
	          AIRequest request = new AIRequest(query);

	          AIResponse response = dataService.request(request);

	          if (response.getStatus().getCode() == 200) {
	            return response.getResult().getFulfillment().getSpeech();
	          } else {
	            System.err.println(response.getStatus().getErrorDetails());
	            return "";
	          }
	        } catch (Exception ex) {
	          ex.printStackTrace();
	          return "";
	        }
	}

}
