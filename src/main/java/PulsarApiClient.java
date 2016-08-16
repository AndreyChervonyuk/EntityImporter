import com.pitchbook.pulsarapi.clients.ApiClient;
import com.pitchbook.pulsarapi.clients.PAPIClient;
import com.pitchbook.pulsarapi.clients.responses.baseapi.ApiResponse;
import com.pitchbook.pulsarapi.interfaces.ApiEntity;

public class PulsarApiClient {
    private ApiClient client;

    private String rtsApiToken;
    private String apiLink;

    public PulsarApiClient() {
        client = getApiClient();
    }

    private ApiClient getApiClient() {
        if (null == client || client.isClosed()) {
            client = PAPIClient.APIClientBuilder
                    .builder(apiLink, rtsApiToken)
                    .includeNullFieldsIntoJson()
                    .creationMode()
                    .retryOnFail(0)
                    .build();
        }
        return client;
    }

    public<T extends ApiEntity.Savable> ApiResponse saveByApi(T entity) {
        return client.save(entity, "EntityImporter");
    }
}
