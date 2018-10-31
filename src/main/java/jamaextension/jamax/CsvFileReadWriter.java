package jamaextension.jamax;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import com.opencsv.CSVReader;

import jamaextension.jamax.Cell;
import jamaextension.jamax.ConditionalRuleException;
 

public final class CsvFileReadWriter
{

    private CsvFileReadWriter()
    {
    }

    public static void csvWriter(String CSV_FILE_OUT, List<Object[]> someObjects, List<String> headers)
            throws IOException
    {
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator('\n').withEscape('\\')
                .withQuoteMode(QuoteMode.MINIMAL).withQuote('"');
        Writer out = null;
        try
        {
            out = new OutputStreamWriter(new FileOutputStream(CSV_FILE_OUT), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        CSVPrinter printer = csvFormat.withHeader(headers.toArray(new String[headers.size()])).print(out);// .withHeader();//("header1",
                                                                                                          // "header2",
                                                                                                          // "header3",
                                                                                                          // "header4",
                                                                                                          //"header5").print(out);
        for (Object[] record : someObjects)
        {
            printer.printRecord(Arrays.asList(record));
            // printer.printRecord(record.prop1(), record.prop2(),
            // record.prop3(), record.prop4(), record.prop5());
        }

        try
        {
            out.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Cell readOpenCsvToCell(String fname)
    {
        CSVReader reader = null;
        try
        {
            reader = new CSVReader(new FileReader(fname));
        }
        catch (FileNotFoundException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        List<String[]> listStr = null;

        try
        {
            listStr = reader.readAll();
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String[] nextLine = null;
        List<String[]> lineArr = listStr;// new ArrayList<>();
        int len = 0;
        int count = 0;

        /*
         * try { // boolean first = false; nextLine = reader.readNext(); while
         * (nextLine != null) {
         * 
         * count++; if (count == 1) { len = nextLine.length; Object[] strCopy =
         * new String[len]; System.arraycopy(nextLine, 0, strCopy, 0, len);
         * lineArr.add(strCopy); nextLine = reader.readNext(); continue; }
         * 
         * if (len != nextLine.length) { throw new
         * ConditionalRuleException("Csv rows have inconsistent lengths."); }
         * 
         * Object[] strCopy = new String[len]; System.arraycopy(nextLine, 0,
         * strCopy, 0, len); lineArr.add(strCopy); // nextLine[] is an array of
         * values from the line // System.out.println(nextLine[0] + nextLine[1]
         * + "etc...");
         * 
         * nextLine = reader.readNext(); if (nextLine == null) {
         * System.out.println("count = " + count); } } } catch (IOException e) {
         * // TODO Auto-generated catch block e.printStackTrace(); }
         */

        int siz = lineArr.size();
        len = lineArr.get(0).length;
        Cell CC = new Cell(siz, len);

        for (int i = 0; i < siz; i++)
        {
            Object[] obj = lineArr.get(i);
            // CC.setRowAt(i, obj);
            CC.setMatrix(i, i, 0, len - 1, new Cell(obj));
        }

        return CC;
    }

    public static Cell readAppacheCommonCsvIntoCell(String fileName)
    {

        FileReader fileReader = null;

        CSVParser csvFileParser = null;

        // Create the CSVFormat object with the header mapping

        CSVFormat csvFileFormat = CSVFormat.DEFAULT;// .withHeader(FILE_HEADER_MAPPING);

        List<Object[]> rowIList = new ArrayList<Object[]>();
        int col = 0;

        try
        {
            // Create a new list of student to be filled by CSV file data

            // List<Object[]> rowObj = new ArrayList<Object[]>();

            // initialize FileReader object

            fileReader = new FileReader(fileName);

            // initialize CSVParser object

            csvFileParser = new CSVParser(fileReader, csvFileFormat);

            // Get a list of CSV file records

            List<CSVRecord> csvRecords = csvFileParser.getRecords();

            // Read the CSV file records starting from the second record to skip
            // the header

            for (int i = 0; i < csvRecords.size(); i++)
            {

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
        }//

        int siz = rowIList.size();
        int len = rowIList.get(0).length;
        Cell CC = new Cell(siz, len);

        for (int i = 0; i < siz; i++)
        {
            Object[] obj = rowIList.get(i);
            // CC.setRowAt(i, obj);
            CC.setMatrix(i, i, 0, len - 1, new Cell(obj));
        }

        return CC;

    }

}
/*
 * public class CsvFileReadWriter {
 * 
 * 
 * 
 * }
 */
