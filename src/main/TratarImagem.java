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
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Gabriel
 */
public class TratarImagem {

    public TratarImagem() {
    }

    public Point tratarFrameAtual(Mat imagem) {
        Point coord = new Point();
        Mat aux = new Mat();

        aux = getSaturation(imagem);

        //mostrarMat(imagem);
        coord = getBallCoordinates(aux);

        return coord;
    }

    public Point getBallCoordinates(Mat image) {
        Point coord = new Point();
        Mat circles = new Mat();

        //mostrarMat(image);
        Imgproc.HoughCircles(image, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 350, 20, 10, 30, 40);

        System.out.println("circles row: " + circles.rows() + " | cols: " + circles.cols());

        double[] aux = circles.get(0, 0);

        for (int i = 0; i < aux.length; i++) {
            System.out.println("aux " + i + ": " + aux[i]);
        }

        if (aux[0] != 0) {
            coord.x = (int) aux[0];
        }
        if (aux[1] != 0) {
            coord.y = (int) aux[1];
        }

        System.out.println("Circulo escolhido X: " + aux[0] + " | Y: " + aux[1]);
        return coord;
    }

    public Mat getSaturation(Mat image) {
        Imgproc.medianBlur(image, image, 17);
        Mat hsv = new Mat();

        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        Mat mask = new Mat(hsv.rows(), hsv.cols(), CvType.CV_8UC3);

        Core.inRange(hsv, new Scalar(0, 150, 0), new Scalar(115, 255, 115), mask);

        //mostrarMat(mask);
        return mask;
    }

    public BufferedImage desenhaGrafico(BufferedImage img, ArrayList<Point> coord) {

        for (int a = 2; a <= coord.size(); a++) {

            double[] x = new double[a];
            double[] y = new double[a];

            for (int i = 0; i < a; i++) {
                if (coord.get(i).x != 0) {
                    x[i] = coord.get(i).x;
                    y[i] = coord.get(i).y;
                }
            }

            //System.out.println("\n x: " + Arrays.toString(x) + " | y: " + Arrays.toString(y));
            try {
                PolynomialFunctionNewtonForm fNewton = new DividedDifferenceInterpolator().interpolate(x, y);

                for (int i = 0; i < 850; i++) {
                    double valor = fNewton.value(i);
                    if (valor < img.getHeight() && valor > 0) {
                        img.setRGB(i, (int) valor, selecionaCor(a).getRGB());
                        img.setRGB(i + 1, (int) valor, selecionaCor(a).getRGB());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
