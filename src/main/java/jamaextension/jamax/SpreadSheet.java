package jamaextension.jamax;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SpreadSheet
{

    private Cell data;
    private String sheetName;
    private List<String> columnNames;
    private static boolean verbose = false;

    public SpreadSheet(String sheetName, Cell data)
    {
        this(sheetName, data, null);
    }

    public SpreadSheet(String sheetName, Cell data, List<String> columnNames)
    {
        if (sheetName == null || "".equals(sheetName.trim()))
        {
            throw new ConditionalRuleException("First \"String\" input argument must be non-null or non-empty.");
        }
        this.sheetName = sheetName;
        if (data == null || data.isNull())
        {
            throw new ConditionalRuleException("Second \"Cell\" input argument must be non-null or non-empty.");
        }
        this.data = data;
        int N = this.data.getColumnDimension();

        if (columnNames == null || columnNames.isEmpty())
        {
            this.columnNames = Cell.genCellSequence("Col", N).toStringList();
        }
        else
        {
            if (columnNames.size() != N)
            {
                throw new ConditionalRuleException(
                        "Third \"List\" input argument must have the same number of column-names as the Cell data.");
            }
            this.columnNames = columnNames;
        }
    }

    public int getWidth()
    {
        return this.data.getColumnDimension();
    }

    public int getHeight()
    {
        return this.data.getRowDimension();
    }

    public Cell getData()
    {
        return data;
    }

    public static void setVerbose(boolean verb)
    {
        verbose = verb;
    }

    public SpreadSheet geSubSpredSheet(int from, int to)
    {
        int[] idxArr = Indices.linspace(from, to).getRowPackedCopy();

        SpreadSheet sheet = this.geSubSpredSheet(idxArr);

        return sheet;
    }

    public SpreadSheet geSubSpredSheet(int[] idxArr)
    {
        // int[] idxArr = Indices.linspace(from, to).getRowPackedCopy();
        Cell newSubData = this.data.getColumns(idxArr);
        Cell newColNames = Cell.listToCell(this.columnNames);
        List<String> newColumnNames = newColNames.getElements(idxArr).toStringList();
        String newSheetName = this.sheetName + "";
        SpreadSheet sheet = new SpreadSheet(newSheetName, newSubData, newColumnNames);

        return sheet;
    }

    public String getSheetName()
    {
        return sheetName;
    }

    public List<String> getColumnNames()
    {
        return columnNames;
    }

    public void printSheet()
    {
        Cell headers = Cell.listToCell(this.columnNames).toRowVector();
        Cell headerData = headers.mergeV(this.data);
        headerData.printCell(this.sheetName);
    }

    public void printHeaders()
    {
        Cell.listToCell(this.columnNames).toRowVector().printCell(this.sheetName);
    }

    public void writeCsv(String fname)
    {
        List<String> headers = this.columnNames;
        List<Object[]> objAgg = data.toObjArray();

        String CSV_FILE_OUT = fname;// basedir +
                                    // "ProcessedData\\SelectedActorsTimeSeriesWikiviews.csv";
        try
        {
            FileReadWriteUtil.csvWriter(CSV_FILE_OUT, objAgg, headers);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////
    public static SpreadSheet readCsv(String fileName)
    {
        return readCsv(fileName, true);
    }

    @SuppressWarnings("resource")
    public static SpreadSheet readCsv(String fileName, boolean hasHeaders)
    {

        FileReader fileReader = null;

        CSVParser csvFileParser = null;

        // Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT;

        List<Object[]> rowIList = new ArrayList<Object[]>();
        int col = 0;

        try
        {
            fileReader = new FileReader(fileName);

            // initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);

            // Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords();

            // Read the CSV file records
            for (int i = 0; i < csvRecords.size(); i++)
            {

                if (verbose)
                {
                    if (i % 50 == 0)
                    {
                        System.out.println("Record #" + (i + 1));
                    }
                }
                CSVRecord record = csvRecords.get(i);
                if (i == 0)
                {
                    col = record.size();
                    Object[] objArr = new String[col];
                    for (int j = 0; j < col; j++)
                    {
                        String ele = record.get(j);
                        objArr[j] = ele;
                    }
                    rowIList.add(objArr);
                    continue;
                }

                // Create a new student object and fill his data
                if (col != record.size())
                {
                    throw new ConditionalRuleException("Csv rows have inconsistent lengths.");
                }

                Object[] objArr = new String[col];
                for (int j = 0; j < col; j++)
                {
                    String ele = record.get(j);
                    objArr[j] = ele;
                }
                rowIList.add(objArr);
            }

        }
        catch (Exception e)
        {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fileReader.close();
                csvFileParser.close();
            }
            catch (IOException e)
            {
                System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        } //

        int siz = rowIList.size();
        int len = rowIList.get(0).length;
        Cell CC = new Cell(siz, len);

        for (int i = 0; i < siz; i++)
        {
            Object[] obj = rowIList.get(i);
            CC.setMatrix(i, i, 0, len - 1, new Cell(obj));
        }

        Cell headers = null;
        if (hasHeaders)
        {
            headers = CC.getRowAt(0);
            CC = CC.getRows(1, siz - 1);
        }
        else
        {
            headers = Cell.genCellSequence("Col", len);
        }
        List<String> headersList = headers.toStringList();
        File fn = new File(fileName);
        String sheetFileName = fn.getName();
        sheetFileName = sheetFileName.split("\\.")[0];

        return new SpreadSheet("Read-" + sheetFileName, CC, headersList);

    }

    ////////////////////////////////////////////////////////////////

    static void testSheet()
    {
        Cell data = Matrix.rand(10, 8).scale(20).toIndices().toCell();
        SpreadSheet SS = new SpreadSheet("Data", data);
        SS.printSheet();

    }

    public static void main(String[] args)
    {

        // testSheet();
        String fn = "sione.csv";
        String first = fn.split("\\.")[0];

        System.out.println("first = " + first);
    }

}
