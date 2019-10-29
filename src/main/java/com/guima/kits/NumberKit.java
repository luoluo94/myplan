package com.guima.kits;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberKit
{
    /**
     * 获取一个百分数，四舍五入保留2位
     *
     * @param d1 分子
     * @param d2 分母
     * @return
     */
    public static Double getPercent(double d1, double d2)
    {
        if (d2 == 0)
            return 0d;
        else
            return new BigDecimal(d1 / d2 * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static Integer getIntPercent(double d1, double d2)
    {
        if (d2 == 0)
            return 0;
        else
            return new BigDecimal(d1 / d2 * 100).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public static void main(String[] args)
    {
        System.out.println(getIntPercent(1,10));

    }
}
