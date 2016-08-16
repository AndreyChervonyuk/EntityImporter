import com.pitchbook.pulsarapi.entities.BusinessEntityDto;
import com.pitchbook.pulsarapi.entities.WorkflowDto;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class BusinessEntityImporter extends Importer<BusinessEntityDto> {

    private static final String[] IMPORT_FILE_HEADER_MAPPING = {"id", "name"};
    private static final String[] NOT_IMPORT_ENTITY = {"MS ID", "Entity Name"};

    public BusinessEntityImporter(String errorFilePath, String dataFilePath) {
        super(errorFilePath, dataFilePath);
    }

    public List<BusinessEntityDto> loadData() {
        List<BusinessEntityDto> businessEntityDtoList = new ArrayList<>();
        try (FileReader fileReader = new FileReader(dataFilePath)) {
            CSVParser records = getDefaultFormat(IMPORT_FILE_HEADER_MAPPING)
                    .parse(fileReader);

            BusinessEntityDto businessEntityDto;
            BusinessEntityDto.SimpleCompanyDto companyDto;
            WorkflowDto workflowDto;

            for (CSVRecord record : records) {
                businessEntityDto = new BusinessEntityDto();
                companyDto = new BusinessEntityDto.SimpleCompanyDto("FS");
                workflowDto = new WorkflowDto();
                workflowDto.setTargetDate("1/1/2016");
                businessEntityDto.setMorningstarId(record.get("id"));
                businessEntityDto.setEntityName(record.get("name"));
                businessEntityDto.setCompany(companyDto);
                businessEntityDto.setActivityLog(workflowDto);
                businessEntityDtoList.add(businessEntityDto);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return businessEntityDtoList;
    }

    @Override
    public String getErrorData(BusinessEntityDto entity) {
        StringBuilder sb = new StringBuilder();
        return sb.append(entity.getMorningstarId())
                .append(DELIMITER)
                .append(entity.getEntityName())
                .append(NEW_LINE_SEPARATOR)
                .toString();
    }
}
