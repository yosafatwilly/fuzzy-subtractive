
import java.util.ArrayList;

/*
    Author : Yosafat Willy Christian
 */
public class FuzzySubtractive {

    public double r;
    public double acceptRatio;
    public double rejectRatio;
    public double q;
    public double[] XMin;
    public double[] XMax;
    public double data[][];
    ArrayList<double[]> Center;
    double[][] u;
    int C = 0;

    public FuzzySubtractive(double r, double acceptRatio, double rejectRatio, double q, double[] XMin, double[] XMax, double[][] data) {
        this.r = r;
        this.acceptRatio = acceptRatio;
        this.rejectRatio = rejectRatio;
        this.q = q;
        this.XMin = XMin;
        this.XMax = XMax;
        this.data = data;
        //3
        double[][] dataNorm;
        dataNorm = normMinMax(data, XMin, XMax);

        //4
        double[] D = new double[dataNorm.length];
        for (int i = 0; i < dataNorm.length; i++) {
            double[] T = dataNorm[i];
            double dT = 0;
            for (int j = 0; j < dataNorm.length; j++) {
                double temp = 0;
                for (int k = 0; k < dataNorm[0].length; k++) {
                    temp += Math.pow(((T[k] - dataNorm[j][k]) / r), 2);
                }
                dT += Math.exp((-4 * temp));
            }
            D[i] = dT;
        }

        //5
        double max = 0;
        double tmp = 0;
        double M = 0;
        int h = 0;
        for (int i = 0; i < D.length; i++) {
            max = Math.max(D[i], max);
            if (tmp != max) {
                h = i;
            }

            tmp = max;
        }
        M = D[h];

        //6
        //  a
        Center = new ArrayList<>();
        //  b
        double[] V = dataNorm[h];
        //  c

        //  d
        int Kondisi = 1;
        //  e
        double Z = M;
        //  f
        double rasio = 0;

        int iterasi = 1;
        do {
            System.out.println("Iterasi : " + iterasi);
            //  o
            Kondisi = 0;
            //  o
            rasio = Z / M;

            if (rasio > acceptRatio) {
                Kondisi = 1;
            } else {
                if (rasio > rejectRatio) {
                    double Md = -1;
                    for (int i = 0; i < C; i++) {
                        double Sd = 0;
                        for (int k = 0; k < dataNorm[0].length; k++) {
                            Sd += Math.pow((V[k] - Center.get(i)[k]) / r, 2);
                        }
                        if (Md < 0 || Sd < Md) {
                            Md = Sd;
                        }
                    }
                    double Smd = Math.sqrt(Md); //Mds
                    if (rasio + Smd >= 1) {
                        Kondisi = 1;
                    } else if (rasio + Smd < 1) { // kondisi = 2
                        Kondisi = 2;
                    }
                }
            }
            if (Kondisi == 1) {
                Center.add(V);
                C++;
                double[] DC = new double[dataNorm.length];
                for (int j = 0; j < dataNorm.length; j++) {
                    double S = 0;
                    for (int k = 0; k < dataNorm[0].length; k++) {
                        S += Math.pow(((V[k] - dataNorm[j][k]) / (r * q)), 2);
                    }
                    DC[j] = M * Math.exp((-4 * S));
                }
                //D = D - DC ;
                for (int i = 0; i < dataNorm.length; i++) {
                    D[i] = D[i] - DC[i];
                    if (D[i] <= 0) {
                        D[i] = 0;
                    }
                }
                max = 0;
                h = 0;
                for (int i = 0; i < D.length; i++) {
                    max = Math.max(D[i], max);
                    if (tmp != max) {
                        h = i;
                    }
                    tmp = max;
                }
                Z = max;
                V = dataNorm[h];
            }

            if (Kondisi == 2) {
                D[h] = 0;
                max = 0;
                h = 0;
                for (int i = 0; i < D.length; i++) {
                    max = Math.max(D[i], max);
                    if (tmp != max) {
                        h = i;
                    }
                    tmp = max;
                }
                Z = max;
                V = dataNorm[h];
            }
            iterasi++;
        } while (Kondisi != 0 && Z != 0);

        for (int i = 0; i < Center.size(); i++) {
            for (int j = 0; j < Center.get(0).length; j++) {
                Center.get(i)[j] = Center.get(i)[j] * (XMax[j] - XMin[j]) + XMin[j];
            }
        }

        double[] sigmaCluster = new double[dataNorm[0].length];
        for (int i = 0; i < dataNorm[0].length; i++) {
            sigmaCluster[i] = r * (XMax[i] - XMin[i]) / Math.sqrt(8);
        }

        u = new double[dataNorm.length][Center.size()];
        for (int i = 0; i < dataNorm.length; i++) {
            for (int k = 0; k < Center.size(); k++) {
                double temp = 0;
                for (int j = 0; j < dataNorm[0].length; j++) {
                    temp += Math.pow(((data[i][j] - Center.get(k)[j]) / (Math.sqrt(2) * sigmaCluster[j])), 2);
                }
                u[i][k] = Math.exp(-temp);
            }
        }
    }

    public double[][] normMinMax(double[][] data, double[] XMin, double[] XMax) {
        double[][] dataNorm = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                dataNorm[i][j] = (data[i][j] - XMin[j]) / (XMax[j] - XMin[j]);
            }
        }
        return dataNorm;
    }

    public static void main(String[] args) {
        double[][] data = {
            {15000000, 25000000, 42, 5000000},
            {20000000, 26420000, 72, 5230000},
            {17820000, 22052000, 35, 5200000},
            {16205000, 18500000, 12, 4250000},
            {8000000, 15200000, 5, 3500000},
            {14260000, 19640000, 15, 4023000},
            {7025000, 15230000, 19, 5000000},
            {25032000, 34000000, 28, 8000000},
            {24320100, 35100000, 39, 12500000},
            {25602100, 38200000, 43, 13250000},
            {19872000, 28000000, 27, 10500000},
            {19000000, 25000200, 41, 6350000},
            {16540200, 30000200, 29, 7525000},
            {28920000, 41000000, 58, 15620000},
            {15870200, 26750000, 19, 4025000},
            {26840320, 39000200, 47, 13025000},
            {24601200, 38450000, 64, 11000250},
            {21650000, 37525000, 60, 9850000},
            {18602000, 30500000, 74, 11230000},
            {35024000, 52000000, 73, 18230000},
            {39024300, 52050000, 26, 15725000},
            {27500000, 36500000, 6, 10560000},
            {32500500, 45600000, 10, 16583000},
            {27963000, 40250000, 38, 13670000},
            {37250020, 51000000, 68, 18530000},
            {16523000, 26750000, 9, 8500000},
            {25690000, 39565000, 48, 15250000},
            {34500000, 51065000, 37, 21500000},
            {9850000, 1350000, 13, 2000000},
            {16950000, 24580000, 18, 4500000}
        };

        double[] XMin = {0, 0, 0, 0};
        double[] XMax = {50000000, 70000000, 120, 50000000};
        FuzzySubtractive f = new FuzzySubtractive(0.3, 0.5, 0.15, 1.25, XMin, XMax, data);

        System.out.println("Jumlah Cluster  : "+f.C);
        for (int i = 0; i < f.Center.size(); i++) {
            System.out.print("Pusat Cluster " + (i + 1) + " : ");
            for (int j = 0; j < f.Center.get(0).length; j++) {
                System.out.printf("%-14.2f", f.Center.get(i)[j]);
            }
            System.out.println();
        }

        for (int i = 0; i < f.data.length; i++) {
            System.out.print("Derajat Keanggotaan " + (i + 1) + " : ");
            for (int j = 0; j < f.Center.size(); j++) {
                System.out.printf("%10.3f", f.u[i][j]);
            }
            System.out.println();
        }

        for (int i = 0; i < f.u.length; i++) {
            double max = 0, tmp = 0;
            int h = 0;
            for (int k = 0; k < f.C; k++) {
                max = Math.max(f.u[i][k], max);
                if (tmp != max) {
                    h = k+1;
                }
                tmp = max;
            }
            System.out.println("Data ke " + (i + 1) + " berada di cluster " + h);
        }
    }
}
