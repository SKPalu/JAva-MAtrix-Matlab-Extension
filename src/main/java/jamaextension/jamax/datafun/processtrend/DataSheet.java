package jamaextension.jamax.datafun.processtrend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jamaextension.jamax.Cell;
import jamaextension.jamax.Indices;
import jamaextension.jamax.Matrix;
import jamaextension.jamax.Tuple;
import jamaextension.jamax.constants.Dimension;

public class DataSheet
{

    private Matrix data;
    private String sheetName;
    private List<String> rowIdName;
    private List<String> colIdName;
    private boolean vector;
    private String rowTitle = "RowTitle";
    private List<String> auxiliaryIdName;
    private Dimension mainIdName = Dimension.ROW;

    public DataSheet(Tuple<String,Matrix> dataTuple)
    {
        this(dataTuple, null);
    }

    public DataSheet(Tuple<String,Matrix> dataTuple, List<String> rowId)
    {
        this(dataTuple, rowId, null);
    }

    public DataSheet(Tuple<String,Matrix> dataTuple, List<String> rowId, List<String> colId)
    {
        if (dataTuple == null)
        {
            throw new IllegalArgumentException("Null \"Tuple\" object input found.");
        }
        
        this.sheetName = dataTuple.x;
        this.data = dataTuple.y;
        if (data == null || data.isNull())
        {
            throw new IllegalArgumentException("Null \"data\" in the input argument \"Tuple\" object found.");
        }
        this.vector = data.isVector();
        //this.data = data;

        setRowIdName(rowId);
        setColIdName(colId);
    }

    
    
    /**
     * @return the sheetName
     */
    public String getSheetName()
    {
        return sheetName;
    }

    public Dimension getMainIdName()
    {
        return mainIdName;
    }

    public void setMainIdName(Dimension mainIdName)
    {
        if (mainIdName == null)
        {
            return;
        }
        this.mainIdName = mainIdName;
    }

    public List<String> getAuxiliaryIdName()
    {
        return auxiliaryIdName;
    }

    public void setAuxiliaryIdName(List<String> auxiliaryIdName)
    {
        if (auxiliaryIdName == null || auxiliaryIdName.isEmpty())
        {
            return;
        }
        int siz = auxiliaryIdName.size();
        if (mainIdName == Dimension.ROW)
        {
            if (siz != this.data.getRowDimension())
            {
                throw new IllegalArgumentException("Inconsistent row sizes found.");
            }
        }
        else
        {
            if (siz != this.data.getColumnDimension())
            {
                throw new IllegalArgumentException("Inconsistent column sizes found.");
            }
        }
        this.auxiliaryIdName = auxiliaryIdName;
    }

    public List<String> getRowIdName()
    {
        return rowIdName;
    }

    public void setRowIdName(List<String> rowIdName)
    {
        int row = data.getRowDimension();
        // int col = data.getColumnDimension();

        boolean emptyRow = rowIdName == null || rowIdName.isEmpty();
        if (emptyRow)
        {// Fill-in
            this.rowIdName = new ArrayList<String>();
            for (int i = 0; i < row; i++)
            {
                this.rowIdName.add("row_" + i);
            }
        }
        else
        {
            if (rowIdName.size() != row)
            {
                throw new IllegalArgumentException("Number of \"data\" rows (= " + row
                        + ") doesn't match number of elements in rowIds (= " + rowIdName.size() + ").");
            }
            else
            {
                this.rowIdName = rowIdName;
            }
        }
        // this.rowIdName = rowIdName;
    }

    public List<String> getColIdName()
    {
        return colIdName;
    }

    public void setColIdName(List<String> colIdName)
    {
        int col = data.getColumnDimension();
        boolean emptyCol = colIdName == null || colIdName.isEmpty();
        if (emptyCol)
        {
            this.colIdName = new ArrayList<String>();
            for (int j = 0; j < col; j++)
            {
                this.colIdName.add("col_" + j);
            }
        }
        else
        {
            if (colIdName.size() != col)
            {
                throw new IllegalArgumentException("Number of \"data\" columns (= " + col
                        + ") doesn't match number of elements in colIds (= " + colIdName.size() + ").");
            }
            else
            {
                this.colIdName = colIdName;
            }
        }
        // this.colIdName = colIdName;
    }

    public String getRowTitle()
    {
        return rowTitle;
    }

    public void setRowTitle(String rowTitle)
    {
        this.rowTitle = rowTitle;
    }

    public Matrix getData()
    {
        return data;
    }

    public boolean isVector()
    {
        return vector;
    }

    public Indices getDataSize()
    {
        return this.data.sizeIndices();
    }

    public Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> generateWindowedSegments(Dimension direction,
            int slideWin, int overLap)
    {

        if (direction == null)
        {
            direction = Dimension.COL;
        }

        Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> result = null;

        if (direction == Dimension.COL)
        {
            result = rowWiseSegmentation(slideWin, overLap);
        }
        else
        {
            result = columnWiseSegmentation(slideWin, overLap);
        }

        return result;
    }

    protected Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> rowWiseSegmentation(int slideWin, int overLap)
    {
        MovingWindowMatrix MWM = null;
        Matrix X = this.data;
        int M = X.getRowDimension();

        Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> segmentsMap = new LinkedHashMap<String, Tuple<Tuple<Matrix, Indices>, Matrix>>();

        for (int i = 0; i < M; i++)
        {
            String rowIdStr = rowIdName.get(i);
            Matrix rowI = X.getRowAt(i);
            MWM = new MovingWindowMatrix(rowI, slideWin, overLap);
            Matrix Tmat = MWM.getWindowedData();
            Indices Tind = MWM.getWindowedIndices();
            Tuple<Matrix, Indices> ts = new Tuple<Matrix, Indices>(Tmat, Tind);
            Tuple<Tuple<Matrix, Indices>, Matrix> tp = new Tuple<Tuple<Matrix, Indices>, Matrix>(ts, rowI);
            segmentsMap.put(rowIdStr, tp);
        }
        return segmentsMap;
    }

    protected Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> columnWiseSegmentation(int slideWin, int overLap)
    {
        MovingWindowMatrix MWM = null;
        Matrix X = this.data;
        int N = X.getColumnDimension();

        Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> segmentsMap = new LinkedHashMap<String, Tuple<Tuple<Matrix, Indices>, Matrix>>();

        for (int j = 0; j < N; j++)
        {
            String colIdStr = colIdName.get(j);
            Matrix colI = X.getColumnAt(j);
            MWM = new MovingWindowMatrix(colI, slideWin, overLap);
            Matrix Tmat = MWM.getWindowedData();
            Indices Tind = MWM.getWindowedIndices();
            Tuple<Matrix, Indices> ts = new Tuple<Matrix, Indices>(Tmat, Tind);
            Tuple<Tuple<Matrix, Indices>, Matrix> tp = new Tuple<Tuple<Matrix, Indices>, Matrix>(ts, colI);
            segmentsMap.put(colIdStr, tp);
        }
        return segmentsMap;
    }

    public void printSheet()
    {
        printSheet("");
    }

    public void printSheet(String name)
    {
        printSheet(name, 2);
    }

    public void printSheet(String name, int decplaces)
    {
        Cell dataCell = this.data.toCellString(decplaces);
        // rowTitle = "RowTitle";
        if (rowTitle == null || "".equals(rowTitle))
        {
            rowTitle = "RowTitle";
        }

        List<String> titleColIdName = new ArrayList<String>();
        titleColIdName.add(rowTitle);
        titleColIdName.addAll(colIdName);
        Cell headers = Cell.listToCell(titleColIdName).toRowVector();

        Cell rowCells = Cell.listToCell(rowIdName).toColVector();

        dataCell = rowCells.mergeH(dataCell);

        dataCell = headers.mergeVerti(dataCell);

        if (name == null || "".equals(name.trim()))
        {
            name = "DataSheet";
        }
        dataCell.printCell(name);
    }

    public Tuple<List<String>, List<Object[]>> getHeaderBody()
    {
        Cell dataCell = this.data.toCellString(0);
        // rowTitle = "RowTitle";
        if (rowTitle == null || "".equals(rowTitle))
        {
            rowTitle = "RowTitle";
        }

        List<String> titleColIdName = new ArrayList<String>();
        titleColIdName.add(rowTitle);
        titleColIdName.addAll(colIdName);
        Cell headers = Cell.listToCell(titleColIdName).toRowVector();

        Cell rowCells = Cell.listToCell(rowIdName).toColVector();

        dataCell = rowCells.mergeH(dataCell);

        //dataCell = headers.mergeVerti(dataCell);

        List<Object[]> objArrList = dataCell.toObjArray();//new ArrayList<>();
        List<String> headerList = headers.toStringList();

        Tuple<List<String>, List<Object[]>> TP = new Tuple<List<String>, List<Object[]>>(headerList, objArrList);
        return TP;
    }

    static void example1()
    {
        int nr = 2;
        int nc = 30;
        Matrix X = Matrix.rand(nr, nc).scale(20).plus(1).round();
        X.printInLabel("X", 0);
        
        Tuple<String,Matrix> tupleX = new Tuple<String, Matrix>("NoName", X);

        DataSheet sheet = new DataSheet(tupleX);
        sheet.printSheet();
        int slideWin = 10;
        int overLap = 5;
        Dimension direction = Dimension.COL;
        Map<String, Tuple<Tuple<Matrix, Indices>, Matrix>> segments = sheet.generateWindowedSegments(direction,
                slideWin, overLap);

        for (Map.Entry<String, Tuple<Tuple<Matrix, Indices>, Matrix>> entry : segments.entrySet())
        {
            String key = entry.getKey();
            Tuple<Tuple<Matrix, Indices>, Matrix> TPall = entry.getValue();
            Tuple<Matrix, Indices> TP = TPall.x;
            Matrix Segs = TP.x;
            Indices Inds = TP.y;
            // System.out.println("" + key);
            Segs.printInLabel("Segs-" + key, 0);
            Inds.printInLabel("Inds-" + key);
            System.out.println("=========================================================\n\n\n");
        }

        System.out.println("Completed");
    }

    public static void main(String[] args)
    {
        example1();
    }

}
