import static java.lang.Math.*;

public class Main
{
    static int[] z = new int[9];
    static double[] x = new double[15];
    static double[][] z1 = new double[9][15];

    public static void main(String[] args)
    {
        CalculateArray(z); CalculateArray(x); CalculateArray(z1, z, x);
    }

    static void CalculateArray(int[] array)
    {
        for (int i = 0, v = 23; i < array.length; i++, v -= 2)
            array[i] = v;

        System.out.println("Array z:");

        for (int num: array)
            System.out.print(num + " ");

        System.out.println("\n");
    }

    static void CalculateArray(double[] array)
    {
        for (int i = 0; i < array.length; i++)
            array[i] = GetRandomNumber(-7.0, 11.0);

        System.out.println("Array x:");

        for (double num: array)
            System.out.printf("%5.2f ", num);

        System.out.println("\n");
    }

    static void CalculateArray(double[][] array, int[] z, double[] x)
    {
        for (int i = 0; i < z.length; i++)
            for (int j = 0; j < x.length; j++)
                switch (z[i])
                {
                    case 17:
                        array[i][j] = log(sqrt(pow(sin(x[j]), 2)));
                        break;
                    case 9, 11, 13, 15:
                        array[i][j] = asin(1.0/pow(E, pow(E, tan(cbrt(x[j])))));
                        break;
                    default:
                        array[i][j] = log(pow(E, tan(2.0/3.0 * (cos(x[j]) - 1))));
                }
        DisplayArray2D(array);
    }

    static void DisplayArray2D(double[][] array)
    {
        System.out.println("Array z1:");

        for (double[] arr : array)
        {
            for (double num : arr)
                System.out.printf("%5.2f ", num);
            System.out.println();
        }
        System.out.println("\n");
    }

    static double GetRandomNumber(double min, double max)
    {
        return random() * (max - min) + min;
    }
}