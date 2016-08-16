import com.pitchbook.pulsarapi.clients.responses.baseapi.ApiResponse;
import com.pitchbook.pulsarapi.interfaces.ApiEntity;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Importer<T extends ApiEntity.Savable> {

    Logger logger = LoggerFactory.getLogger(Importer.class);

    private final PulsarApiClient client = new PulsarApiClient();
    private List<T> entityDtoList = new ArrayList<>();
    protected List<String> notSaveEntity = new ArrayList<>();
    protected String errorFilePath;
    protected String dataFilePath;
    protected char DELIMITER = ';';
    protected String NEW_LINE_SEPARATOR = "\n";


    public Importer(String errorFilePath, String dataFilePath) {
        this.errorFilePath = errorFilePath;
        this.dataFilePath = dataFilePath;
    }

    protected abstract List<T> loadData();
    protected abstract String getErrorData(T entity);

    public void importFile(){
        entityDtoList = loadData();
        saveAll();

        if (notSaveEntity.isEmpty()) {
            logger.info("All entity saved");
        } else {
            try {
                Path out = Paths.get(errorFilePath);
                Files.write(out, notSaveEntity, StandardCharsets.UTF_8);
                logger.info("Could not save some entity. Not saved entity write in file:"  + errorFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAll() {
        entityDtoList.forEach(savable -> {
            ApiResponse apiResponse = client.saveByApi(savable);
            if (!apiResponse.isOk()) {
                String errorData = getErrorData(savable);
                String errorMsg = apiResponse.getErrorResponse().getErrorDescription();
                errorData = errorData + DELIMITER + errorMsg + NEW_LINE_SEPARATOR;
                notSaveEntity.add(errorData);
                logger.error("Error API response: " + errorMsg);
            }
        });
    }

    protected CSVFormat getDefaultFormat(String[] headers) {
        return CSVFormat.DEFAULT.withHeader(headers)
                .withFirstRecordAsHeader()
                .withDelimiter(DELIMITER);
    }

//    public String getHeader(String[] headers) {
//        StringBuilder sb = new StringBuilder();
//        for (String header : headers) {
//            sb.append(header)
//                    .append(DELIMITER);
//        }
//        sb.append(NEW_LINE_SEPARATOR);
//        return sb.toString();
//    }

}
