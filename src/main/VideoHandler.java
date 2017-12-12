package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 *
 * @author Gabriel
 */
public class VideoHandler {

    private final VideoCapture video;
    private final String fileDir;
    private final String extension;

    private final double totalDeFrames;
    private double frameAtual;

    private final ImageProcessor tratamentoImagem;
    private final ArrayList<Point> coordenadas;

    public VideoHandler() {
        this.video = new VideoCapture();
        this.fileDir = System.getProperty("user.dir") + File.separator + "videos" + File.separator;
        this.extension = ".mp4";

        this.tratamentoImagem = new ImageProcessor();
        this.coordenadas = new ArrayList<>();

        loadVideo();

        totalDeFrames = video.get(Videoio.CAP_PROP_FRAME_COUNT);
        System.out.println("Total de frames: " + totalDeFrames);
    }

    private void loadVideo() {
        String videoEscolhido = "5N" + extension;
        String caminhoCompleto = fileDir + videoEscolhido;

        if (video.open(caminhoCompleto)) {
            System.out.println("Video carregado com sucesso.");
        } else {
            System.out.println("Video falhou.");
        }
    }

    public BufferedImage grabFrame() {
        frameAtual = video.get(Videoio.CAP_PROP_POS_FRAMES);
        Mat frame = new Mat();
        video.read(frame);
        
        coordenadas.add(tratamentoImagem.getBallCoordinates(frame.clone()));
        BufferedImage img = tratamentoImagem.drawPrediction(tratamentoImagem.convertMat2BuffImg(frame), coordenadas);

        return img;
    }

    public double getFrameAtual() {
        return frameAtual;
    }

    public double getTotalFrames() {
        return totalDeFrames;
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
