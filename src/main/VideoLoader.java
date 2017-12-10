package main;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
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

    private ArrayList<Mat> matsParaTratar;
    public MatParaBuffImg matParaImagem;

    public VideoLoader() {
        this.matsParaTratar = new ArrayList<>();
        this.video = new VideoCapture();
        this.fileDir = System.getProperty("user.dir") + File.separator + "videos" + File.separator;
        this.extension = ".mp4";

        loadVideo();

        totalDeFrames = video.get(Videoio.CAP_PROP_FRAME_COUNT);
        System.out.println("Total de frames: " + totalDeFrames);

        this.matParaImagem = new MatParaBuffImg();
    }

    private void loadVideo() {
        String videoEscolhido = "4N" + extension;
        String caminhoCompleto = fileDir + videoEscolhido;

        if (video.open(caminhoCompleto)) {
            System.out.println("Video carrecado com sucesso.");
        } else {
            System.out.println("Video falhou.");
        }
    }

    public BufferedImage grabFrame() {
        frameAtual = video.get(Videoio.CAP_PROP_POS_FRAMES);
        System.out.println("Frame atual: " + frameAtual);

        if (frameAtual > 0 && (frameAtual % 2) == 0) {
            matsParaTratar.add(matParaImagem.getMat().clone());

            System.out.println("Frames escolhidos: " + matsParaTratar.size());
        }

        video.read(matParaImagem.getMat());
        return matParaImagem.getImage(matParaImagem.getMat());
    }

    public BufferedImage getImagemFinal() {
        return matParaImagem.getImagemFinal();
    }

    public double getFrameAtual() {
        return frameAtual;
    }

    public ArrayList<Mat> getMatsParaTratar() {
        return matsParaTratar;
    }

    public double getTotalFrames() {
        return totalDeFrames;
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
