package com.guima.kits;

import com.guima.base.kits.MetaInfo;
import com.jfinal.plugin.activerecord.Model;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class ExcelKit
{
    private final String fileName;
    private HSSFWorkbook excel;

    public ExcelKit(String fileName)
    {
        this.fileName = fileName;
        this.excel = new HSSFWorkbook();
    }

    public <M extends Model> void build(List<MetaInfo> tableMetas, List<String> ignore, List<M> datas)
    {
        this.build("查询结果", tableMetas, ignore, datas);
    }

    public <M extends Model> void build(String sheetName, List<MetaInfo> tableMetas, List<String> ignore, List<M> datas)
    {
        this.excel = new HSSFWorkbook();
        HSSFSheet sheet = excel.createSheet(sheetName);

        HSSFRow title = sheet.createRow(0);
        int cellIdx = 0;
        for (MetaInfo info : tableMetas)
        {
            if (ignore.size() > 0 && ignore.contains(info.baseFiledName)) continue;
            HSSFCell cell = title.createCell(cellIdx);
            cell.setCellValue(info.getRemark());
            cellIdx++;
        }

        int rowIdx = 1;
        for (M data : datas)
        {
            HSSFRow dataRow = sheet.createRow(rowIdx);
            rowIdx++;
            cellIdx = 0;
            for (MetaInfo info : tableMetas)
            {
                String fieldName = info.baseFiledName;
                if (ignore.size() > 0 && ignore.contains(fieldName)) continue;
                String cellValue = Kit.strTrim(data.getStr(fieldName));
                HSSFCell dataCell = dataRow.createCell(cellIdx);
                dataCell.setCellValue(cellValue);
                cellIdx++;
            }
        }
    }

    public void build(String sheetName, List<ExcelRow> rows)
    {
        this.excel = new HSSFWorkbook();
        if (rows.isEmpty())
            buildEmptyExcel();
        else
        {
            Set<String> columns = rows.get(0).getColumn();
            HSSFSheet sheet = excel.createSheet(sheetName);
            HSSFRow title = sheet.createRow(0);
            int cellIdx = 0;
            for (String column : columns)
            {
                HSSFCell cell = title.createCell(cellIdx);
                cell.setCellValue(column);
                cellIdx++;
            }

            int rowIdx = 1;
            for (ExcelRow row : rows)
            {
                HSSFRow dataRow = sheet.createRow(rowIdx);
                rowIdx++;
                cellIdx = 0;
                for (String cell : row.getCells())
                {
                    HSSFCell dataCell = dataRow.createCell(cellIdx);
                    dataCell.setCellValue(cell);
                    cellIdx++;
                }
            }
        }
    }

    public void buildEmptyExcel()
    {
        excel.createSheet("无查询结果");

    }

    public void toFile(String path) throws IOException
    {
        excel.write(new File(path + "\\" + fileName + ".xls"));
        excel.close();
    }

    public OutputStream toStream(HttpServletResponse response, HttpServletRequest request) throws IOException
    {
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=" + Kit.urlEncode(fileName,request) + ".xls");
        response.setContentType("application/msexcel");
        excel.write(output);
        return output;
    }

    public void close() throws IOException
    {
        this.excel.close();
    }

    public class ExcelRow
    {
        private Map<String, String> rows = new LinkedHashMap<>();

        public void add(String column, String cell)
        {
            this.rows.put(column, cell);
        }

        public Set<String> getColumn()
        {
            return rows.keySet();
        }

        public Collection<String> getCells()
        {
            return rows.values();
        }
    }
}
