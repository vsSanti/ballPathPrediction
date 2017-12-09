package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.apache.commons.math3.analysis.interpolation.DividedDifferenceInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Gabriel
 */
public class MatParaImagem {

    private Mat mat = new Mat();
    private BufferedImage img;
    private byte[] dat;
    
    private double quantidadeTotalFrames;
    private double frameAtual;

    public MatParaImagem(double quantidadeTotalFrames) {
        this.quantidadeTotalFrames = quantidadeTotalFrames;
    }

    public MatParaImagem(Mat mat) {
        getSpace(mat);
    }

    private void getSpace(Mat mat) {
        this.mat = mat;
        int w = mat.cols(), h = mat.rows();

        if (dat == null || dat.length != w * h * 3) {
            dat = new byte[w * h * 3];
        }

        try {
            if (img == null || img.getWidth() != w || img.getHeight() != h || img.getType() != BufferedImage.TYPE_3BYTE_BGR) {
                img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
            }
        } catch (IllegalArgumentException e) {
            //System.out.println("Fim do vídeo.");
        }

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR); // normaliza as cores
    }

    public BufferedImage getImage(Mat mat) {
        getSpace(mat);
        mat.get(0, 0, dat);

        img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), dat);
        
      

        return desenhaGrafico(img);
    }

    public BufferedImage desenhaGrafico(BufferedImage img) {

//        gera um gráfico qualquer
        int n = 3;
        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = i * 10;
        }

//        testar com diferentes valores aqui
        y[0] = 8;
        y[1] = 13;
        y[2] = 17.5;

        PolynomialFunctionNewtonForm fNewton = new DividedDifferenceInterpolator().interpolate(x, y);

        for (int i = 0; i < 200; i++) {
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

    public Mat getMat() {
        return mat;
    }

    public double getHeight() {
        return img.getHeight();
    }

    public double getWidth() {
        return img.getWidth();
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
