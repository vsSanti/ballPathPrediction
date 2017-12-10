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
    private ArrayList<Integer> coordX, coordY;

    private Mat hue, saturation, value, binario;

    private JFrame frame = new JFrame();
    private JLabel label = new JLabel();

    int histograma[] = new int[256];

    public TratarImagem(ArrayList<Mat> listaMats) {
        System.out.println("tratamneto de imagem");

        this.listaMats = listaMats;

        coordX = new ArrayList<>();
        coordY = new ArrayList<>();

        iniciarTratamento();
    }

    public void iniciarTratamento() {

        for (int matAtual = 0; matAtual < listaMats.size(); matAtual++) {
            Mat image = listaMats.get(matAtual);

            hue = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
            saturation = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
            value = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
            binario = new Mat(image.rows(), image.cols(), CvType.CV_8UC3);

            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
            mostrarImagem(image);
            
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
                    if (temp[0] >= 15 && temp[0] <= 20) {
                        double t[] = image.get(i, j);
                        binario.put(i, j, t);
                    } else {
                        double data[] = {0, 0, 0};
                        binario.put(i, j, data);
                    }
                }
            }

            for (int i = 0; i < image.rows(); i++) {
                for (int j = 0; j < image.cols(); j++) {
                    double temp[] = image.get(i, j);
                    histograma[(int) temp[0]]++;
                }
            }

            /*for (int i = 0; i < 256; i++) {
                System.out.println(histograma[i]);
            }*/
            mostrarImagem(binario);
        }

    }

    public void mostrarImagem(Mat mat) {

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
        if (mat.channels() > 1) {
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
