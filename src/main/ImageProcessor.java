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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

<<<<<<< HEAD:src/main/TratarImagem.java
// ----------------------------------------
// Trabalho por Gabriel Corrêa Ferreira, Vinícius da Silva Santiago, e Mateus Souza
// Matrículas 78218, 78208, e 71293
// ----------------------------------------

public class TratarImagem {
=======
public class ImageProcessor {
>>>>>>> gabiwel:src/main/ImageProcessor.java

    public Point getBallCoordinates(Mat image) {
        Point coord = new Point();

        double[] foundCircle = processImage(image).get(0, 0);

        coord.x = (int) foundCircle[0];
        coord.y = (int) foundCircle[1];

        return coord;
    }

    public Mat processImage(Mat image) {
        Mat aux;
        Mat hsv = new Mat();
        Mat circles = new Mat();

        Imgproc.medianBlur(image, image, 17);
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        
        aux = new Mat(hsv.rows(), hsv.cols(), CvType.CV_8UC3);
        Core.inRange(hsv, new Scalar(0, 150, 0), new Scalar(115, 255, 115), aux);

        Imgproc.HoughCircles(aux, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 350, 20, 10, 20, 40);

        return circles;
    }

    public BufferedImage drawPrediction(BufferedImage img, ArrayList<Point> coord) {

        double[] x = new double[3];
        double[] y = new double[3];

        x[0] = coord.get(0).x;
        y[0] = coord.get(0).y;

        int valorMeio = coord.size() / 2;
        //System.out.println("valorMeio = " + valorMeio);
        x[1] = coord.get(valorMeio).x;
        y[1] = coord.get(valorMeio).y;

        x[2] = coord.get(coord.size() - 1).x;
        y[2] = coord.get(coord.size() - 1).y;

        System.out.println("\n x: " + Arrays.toString(x) + " | y: " + Arrays.toString(y));

        try {
            PolynomialFunctionNewtonForm fNewton = new DividedDifferenceInterpolator().interpolate(x, y);

            for (int i = 2; i < 848; i++) {
                double valor = fNewton.value(i);
                if (valor < img.getHeight() && valor > 0) {
                    img.setRGB(i, (int) valor, corVerde().getRGB());
                    img.setRGB(i + 1, (int) valor, corVerde().getRGB());
                    img.setRGB(i, (int) valor + 1, corVerde().getRGB());
                }
            }

            for (int i = 0; i < coord.size(); i++) {
                int var = -4;
                for (int j = var; j < Math.abs(var); j++) {
                    for (int k = var; k < Math.abs(var); k++) {
                        img.setRGB((int) coord.get(i).x + k, (int) coord.get(i).y + j, corVermelho().getRGB());
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return img;
    }

    public void displayMat(Mat mat) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        BufferedImage convertedMat = convertMat2BuffImg(mat);

        ImageIcon icon = new ImageIcon(convertedMat);
        iniNewJFrame(frame, label, icon);
    }

    public BufferedImage convertMat2BuffImg(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        
        if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());

        return image;
    }

    public void iniNewJFrame(JFrame frame, JLabel label, ImageIcon icon) {
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

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
