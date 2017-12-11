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
import org.opencv.core.Point;
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

    private TratarImagem tratamentoImagem;
    private ArrayList<Point> coordenadas;

    private boolean fazerAlteracaoDesenho;

    public VideoLoader() {
        this.video = new VideoCapture();
        this.fileDir = System.getProperty("user.dir") + File.separator + "videos" + File.separator;
        this.extension = ".mp4";

        this.tratamentoImagem = new TratarImagem();
        this.matsParaTratar = new ArrayList<>();
        this.coordenadas = new ArrayList<>();
        this.fazerAlteracaoDesenho = true;

        loadVideo();

        totalDeFrames = video.get(Videoio.CAP_PROP_FRAME_COUNT);
        System.out.println("Total de frames: " + totalDeFrames);
    }

    private void loadVideo() {
        String videoEscolhido = "7N" + extension;
        String caminhoCompleto = fileDir + videoEscolhido;

        if (video.open(caminhoCompleto)) {
            System.out.println("Video carrecado com sucesso.");
        } else {
            System.out.println("Video falhou.");
        }
    }

    public BufferedImage grabFrame() {
        frameAtual = video.get(Videoio.CAP_PROP_POS_FRAMES);
        System.out.println("\nFrame atual: " + frameAtual);

        Mat teste = new Mat();
        video.read(teste);

        if (frameAtual > 0 && (frameAtual % 2) == 0 && fazerAlteracaoDesenho) {

            coordenadas.add(tratamentoImagem.tratarFrameAtual(teste.clone()));

            if (coordenadas.size() >= 5) {
                BufferedImage img = tratamentoImagem.desenhaGrafico(ShowWindow.matToBufferedImage(teste), coordenadas);
                fazerAlteracaoDesenho = false;

                return img;
            }
        } else if (!fazerAlteracaoDesenho) {

            BufferedImage img = tratamentoImagem.desenhaGrafico(ShowWindow.matToBufferedImage(teste), coordenadas);
            return img;
        }

        return ShowWindow.matToBufferedImage(teste);
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
