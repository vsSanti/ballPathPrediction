package main;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 *
 * @author Gabriel
 */
public class VideoLoader {

    private final VideoCapture video;
    private final String fileDir;
    private final String extension;
    
    private final double totalDeFrames;
    private double frameAtual;

    public MatParaImagem matParaImagem;
    

    public VideoLoader() {
        this.video = new VideoCapture();
        this.fileDir = System.getProperty("user.dir") + File.separator + "videos" + File.separator;
        this.extension = ".mp4";

        loadVideo();
        
        totalDeFrames = video.get(Videoio.CAP_PROP_FRAME_COUNT);
        System.out.println("Total de frames: " + totalDeFrames);
        
        this.matParaImagem = new MatParaImagem(totalDeFrames);
    }

    private void loadVideo() {
        String first_test = "4N" + extension;

        String caminhoCompleto = fileDir + first_test;

        if (video.open(caminhoCompleto)) {
            System.out.println("Video carrecado com sucesso.");
        } else {
            System.out.println("Video falhou.");
        }
    }

    public BufferedImage grabFrame() {
        frameAtual = video.get(Videoio.CAP_PROP_POS_FRAMES);
        System.out.println("Frame atual: " + frameAtual);
        
        video.read(matParaImagem.getMat());
        return matParaImagem.getImage(matParaImagem.getMat());
    }
    
    public double getTotalFrames() {
        return totalDeFrames;
    }
    
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
}
