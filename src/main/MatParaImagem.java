package main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    
    private BufferedImage imagemFinal;
    
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
        
        imagemFinal = img;
        return img;
    }
    
    public BufferedImage getImagemFinal() {
        return imagemFinal;
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
