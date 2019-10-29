package com.guima.base.kits;

import com.jfinal.render.Render;

public class ByteArrRender extends Render
{
    private final byte[] fileByte;

    public ByteArrRender(byte[] fileByte)
    {
        this.fileByte = fileByte;
    }

    @Override
    public void render()
    {
        try
        {
            response.setCharacterEncoding("UTF-8");
            response.reset();
            response.getOutputStream().write(fileByte);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
