package com.guima.base.kits;

import com.jfinal.render.Render;
import com.guima.kits.ExcelKit;

import java.io.IOException;
import java.io.OutputStream;

public class PoiRender extends Render
{
    private final ExcelKit excel;

    public PoiRender(ExcelKit excel)
    {
        this.excel = excel;
    }

    @Override
    public void render()
    {
        try
        {
            response.setCharacterEncoding("UTF-8");
            OutputStream stream = excel.toStream(response,request);
            stream.flush();
            stream.close();
            excel.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
