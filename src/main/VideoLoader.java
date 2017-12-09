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
    
    MatParaImagem matParaImagem = new MatParaImagem();

    public VideoLoader() {
        this.video = new VideoCapture();
        this.fileDir = System.getProperty("user.dir");

        loadVideo();
    }

    private void loadVideo() {
        String first_test = File.separator + "videos" + File.separator + "MVI_2702.MOV";
        String second_test = File.separator + "teste_2.mp4";
        
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
