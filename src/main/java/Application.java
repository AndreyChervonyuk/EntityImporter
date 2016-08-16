
public class Application {

    protected static String errorFilePath;
    protected static String dataFilePath;

    public static void main(String[] args) {
        Importer businessEntityImporter = new BusinessEntityImporter(errorFilePath, dataFilePath);
        businessEntityImporter.importFile();
    }
}
