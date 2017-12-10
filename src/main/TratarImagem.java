package main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.commons.math3.analysis.interpolation.DividedDifferenceInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
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
    
    private BufferedImage imagemFinal;

    //private JFrame frame = new JFrame();
    //private JLabel label = new JLabel();
    public TratarImagem(ArrayList<Mat> listaMats, BufferedImage imagemFinal) {
        System.out.println("tratamneto de imagem");

        this.listaMats = listaMats;
        this.imagemFinal = imagemFinal;

        coordX = new ArrayList<>();
        coordY = new ArrayList<>();

        iniciarTratamento();
        imagemFinal = desenhaGrafico(imagemFinal);
        mostrarImagem(imagemFinal);
    }
    
    
    public BufferedImage desenhaGrafico(BufferedImage img) {

        double[] x = new double[coordX.size()];
        double[] y = new double[coordY.size()];
       
        for (int i = 0; i < coordX.size(); i++) {
            x[i] = coordX.get(i);
            y[i] = coordY.get(i);
        }
        
        
        System.out.println("\n x: " + Arrays.toString(x) + " | y: " + Arrays.toString(y));

        PolynomialFunctionNewtonForm fNewton = new DividedDifferenceInterpolator().interpolate(x, y);

        for (int i = 0; i < 1920; i++) {
            double valor = fNewton.value(i);
            if (valor < img.getHeight() && valor > 0) {
                img.setRGB(i, (int) valor, corVermelho().getRGB());
                img.setRGB(i + 1, (int) valor, corVermelho().getRGB());
            }
        }

        return img;
    }
    
    public Color corVermelho() {
        return new Color(255, 0, 0);
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

            //Imgproc.cvtColor(saturation, image, Imgproc.color_);
            //mostrarImagem(image);

            Mat circles = new Mat();
            
            Imgproc.HoughCircles(saturation, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 500, 20, 50, 60, 80);

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
    
    public void mostrarImagem(BufferedImage img) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        ImageIcon icon = new ImageIcon(img);

        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(), img.getHeight());
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
