package main;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Gabriel
 */
public class TratarImagem {

    private ArrayList<Mat> listaMats;
    private ArrayList<Double> coordX, coordY;

    //private JFrame frame = new JFrame();
    //private JLabel label = new JLabel();
    public TratarImagem(ArrayList<Mat> listaMats) {
        System.out.println("tratamneto de imagem");

        this.listaMats = listaMats;

        coordX = new ArrayList<>();
        coordY = new ArrayList<>();

        //iniciarTratamento();
    }

    public void iniciarTratamento() {

        System.out.println("listaMats: " + listaMats.size());

        for (int matAtual = 0; matAtual < listaMats.size(); matAtual++) {
            System.out.println("matAtual: " + matAtual);
            Mat image = listaMats.get(matAtual);
            //mostrarImagem(image);

            Mat hue = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
            Mat saturation = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
            Mat value = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
            Mat binario = new Mat(image.rows(), image.cols(), CvType.CV_8UC3);

            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

            for (int i = 0; i < image.rows(); i++) {
                for (int j = 0; j < image.cols(); j++) {
                    double temp[] = image.get(i, j);
                    hue.put(i, j, temp[0]);
                    saturation.put(i, j, temp[1]);
                    value.put(i, j, temp[2]);
                }
            }

            for (int i = 0; i < hue.rows(); i++) {  // binarizacao
                for (int j = 0; j < hue.cols(); j++) {
                    double temp[] = hue.get(i, j);
                    if (temp[0] >= 1 && temp[0] <= 13) {
                        double t[] = image.get(i, j);
                        binario.put(i, j, t);
                    } else {
                        double data[] = {0, 0, 0};
                        binario.put(i, j, data);
                    }
                }
            }

            Imgproc.cvtColor(binario, image, Imgproc.COLOR_BGR2GRAY);
            //mostrarImagem(image);

            Mat circles = new Mat();
            Imgproc.HoughCircles(image, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 10, 200, 10, 100, 500);

            int maiorCirculo = 0;
            double maiorCirculoX = 0;
            double maiorCirculoY = 0;
            for (int j = 0; j < circles.rows(); j++) {
                for (int k = 0; k < circles.cols(); k++) {
                    double[] aux = circles.get(j, k);

                    if (aux[2] > maiorCirculo) {
                        maiorCirculoX = aux[0];
                        maiorCirculoY = aux[1];
                    }
                }
            }

            coordX.add(maiorCirculoX);
            coordY.add(maiorCirculoY);
            System.out.println("Maior circulo X: " + maiorCirculoX + " | Y: " + maiorCirculoY);
        }

    }

    public ArrayList<Double> getCoordX() {
        return coordX;
    }

    public ArrayList<Double> getCoordY() {
        return coordY;
    }

    public void mostrarImagem(Mat mat) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        BufferedImage convertedMat = converterMat(mat);

        ImageIcon icon = new ImageIcon(convertedMat);

        frame.setLayout(new FlowLayout());
        frame.setSize(convertedMat.getWidth(), convertedMat.getHeight());
        label.setIcon(icon);
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public BufferedImage converterMat(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        System.out.println("channels:" + mat.channels());
        if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());

        return image;
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
