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

    public TratarImagem(ArrayList<Mat> listaMats, BufferedImage imagemFinal) {
        System.out.println("tratamneto de imagem");

        this.listaMats = listaMats;
        this.imagemFinal = imagemFinal;

        coordX = new ArrayList<>();
        coordY = new ArrayList<>();

        iniciarTratamento();
        imagemFinal = desenhaGrafico(imagemFinal);
        mostrarBufferedImage(imagemFinal);
    }

    public void iniciarTratamento() {

        System.out.println("listaMats: " + listaMats.size());

        for (int matAtual = 0; matAtual < listaMats.size(); matAtual++) {
            System.out.println("matAtual: " + matAtual);
            Mat image = listaMats.get(matAtual);

            image = getSaturation(image);
            image = getBinary(image);

            //mostrarMat(image);
            System.out.println(image.toString());

            getBallCoordinates(image);
        }
    }

    public void getBallCoordinates(Mat image) {
        Mat circles = new Mat();

        Imgproc.HoughCircles(image, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 350, 20, 20, 30, 50);

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

        if (maiorCirculoX != 0) {
            coordX.add(maiorCirculoX);
        }
        if (maiorCirculoY != 0) {
            coordY.add(maiorCirculoY);
        }

        System.out.println("Circulo escolhido X: " + maiorCirculoX + " | Y: " + maiorCirculoY);
    }

    public Mat getSaturation(Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
        Mat saturation = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);

        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                double temp[] = image.get(i, j);
                saturation.put(i, j, temp[1]);
            }
        }

        return saturation;
    }

    public Mat getBinary(Mat image) {
        Mat binarized = new Mat();

        Imgproc.threshold(image, binarized, 135, 255, Imgproc.THRESH_BINARY);

        return binarized;
    }

    public BufferedImage desenhaGrafico(BufferedImage img) {

        for (int a = 2; a <= coordX.size(); a++) {

            double[] x = new double[a];
            double[] y = new double[a];

            for (int i = 0; i < a; i++) {
                if (coordX.get(i) != 0) {
                    x[i] = coordX.get(i);
                    y[i] = coordY.get(i);
                }
            }

            System.out.println("\n x: " + Arrays.toString(x) + " | y: " + Arrays.toString(y));

            PolynomialFunctionNewtonForm fNewton = new DividedDifferenceInterpolator().interpolate(x, y);

            for (int i = 0; i < 850; i++) {
                double valor = fNewton.value(i);
                if (valor < img.getHeight() && valor > 0) {
                    img.setRGB(i, (int) valor, selecionaCor(a).getRGB());
                    img.setRGB(i + 1, (int) valor, selecionaCor(a).getRGB());
                }
            }

        }
        return img;
    }

    public void mostrarBufferedImage(BufferedImage bi) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        ImageIcon icon = new ImageIcon(bi);
        criarFrames(frame, label, icon);
    }

    public void mostrarMat(Mat mat) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        BufferedImage convertedMat = converterMat(mat);

        ImageIcon icon = new ImageIcon(convertedMat);
        criarFrames(frame, label, icon);
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

    public void criarFrames(JFrame frame, JLabel label, ImageIcon icon) {
        frame.setLayout(new FlowLayout());
        frame.setSize(icon.getIconWidth(), icon.getIconHeight());
        label.setIcon(icon);
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Color corVermelho() {
        return new Color(255, 0, 0);
    }

    public Color corAmarelo() {
        return new Color(255, 255, 0);
    }

    public Color corVerde() {
        return new Color(0, 255, 0);
    }

    public Color corRosa() {
        return new Color(255, 0, 255);
    }

    public Color selecionaCor(int n) {
        Color color = corVermelho();
        switch (n) {
            case 2:
                color = corVermelho();
                break;
            case 3:
                color = corAmarelo();
                break;
            case 4:
                color = corVerde();
                break;
            case 5:
                color = corRosa();
                break;
        }
        return color;
    }

    public ArrayList<Double> getCoordX() {
        return coordX;
    }

    public ArrayList<Double> getCoordY() {
        return coordY;
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
