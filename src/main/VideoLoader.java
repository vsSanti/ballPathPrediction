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
    public MatParaBuffImg matParaImagem;

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

        this.matParaImagem = new MatParaBuffImg();
    }

    private void loadVideo() {
        String videoEscolhido = "2N" + extension;
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

        if (frameAtual > 0 && (frameAtual % 2) == 0 && fazerAlteracaoDesenho) {

            if (!coordenadas.contains(tratamentoImagem.tratarFrameAtual(matParaImagem.getMat().clone()))) {
                coordenadas.add(tratamentoImagem.tratarFrameAtual(matParaImagem.getMat().clone()));
            }
            System.out.println("first step " + video.get(Videoio.CAP_PROP_POS_FRAMES));

            if (coordenadas.size() >= 3) {
                BufferedImage img = tratamentoImagem.desenhaGrafico(matParaImagem.getImage(matParaImagem.getMat()), coordenadas);
                System.out.println("second step" + video.get(Videoio.CAP_PROP_POS_FRAMES));
                fazerAlteracaoDesenho = false;

                video.read(matParaImagem.getMat());
                return img;
            }
        } else if (!fazerAlteracaoDesenho) {
            System.out.println("third step" + video.get(Videoio.CAP_PROP_POS_FRAMES));

            video.read(matParaImagem.getMat());
            BufferedImage img = matParaImagem.getImage(matParaImagem.getMat());
            return img;
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
