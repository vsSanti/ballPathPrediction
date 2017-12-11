package main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Gabriel
 */
public class MatParaBuffImg {

    private Mat matClasse = new Mat();
    private BufferedImage img;
    private byte[] dat;

    private BufferedImage imagemFinal;

    public MatParaBuffImg() {
    }

    public MatParaBuffImg(Mat mat) {
        getSpace(mat);
    }

    private void getSpace(Mat mat) {
        this.matClasse = mat;
        int w = mat.cols(), h = mat.rows();

        System.out.println("mat.cols: " + mat.cols() + " | mat.rows(): " + mat.rows());

        if (dat == null || dat.length != w * h * 3) {
            dat = new byte[w * h * 3];
        }

        try {
            if (img == null || img.getWidth() != w || img.getHeight() != h || img.getType() != BufferedImage.TYPE_3BYTE_BGR) {
                img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR); // normaliza as cores
    }

    public BufferedImage getImage(Mat mat) {
        System.out.println("mat.cols GTIMAGE: " + mat.cols() + " | mat.rows(): " + mat.rows());
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
        return matClasse;
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
