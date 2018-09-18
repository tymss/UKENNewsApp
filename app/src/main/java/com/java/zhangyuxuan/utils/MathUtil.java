package com.java.zhangyuxuan.utils;

public class MathUtil {
    public static double[] getSoftmax(int[] data)
    {
        int length = data.length;
        double[] result = new double[length];
        double sum = 0.;
        for(int i = 0; i < length; i++)
        {
            double exp = Math.exp(data[i]);
            result[i] = exp;
            sum += exp;
        }
        for(int i = 0; i < length; i++)
            result[i] = result[i] / sum;
        return result;
    }

    public static int getRussianNum(double[] prob, double num)
    {
        double sum = 0.;
        int length = prob.length;
        int hint = length - 1;
        for(int i = 0; i < length; i++)
        {
            if(num >= sum && num < sum + prob[i])
            {
                hint = i;
                break;
            }
            sum += prob[i];
        }
        return hint;
    }
}
