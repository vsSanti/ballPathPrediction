package main;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Gabriel
 */
public class VideoLoader {

    private VideoCapture video;
    private String fileDir;
    private String extension;

    public MatParaImagem matParaImagem;

    public VideoLoader() {
        this.matParaImagem = new MatParaImagem();
        this.video = new VideoCapture();
        this.fileDir = System.getProperty("user.dir") + File.separator + "videos" + File.separator;
        this.extension = ".MOV";

        loadVideo();
    }

    private void loadVideo() {
        String first_test = "MVI_2702" + extension;

        String caminhoCompleto = fileDir + first_test;

        if (video.open(caminhoCompleto)) {
            System.out.println("Success");
        } else {
            System.out.println("Failure");
        }
    }

    public BufferedImage grabFrame() {
        video.read(matParaImagem.mat);
        return matParaImagem.getImage(matParaImagem.mat);
    }
}
