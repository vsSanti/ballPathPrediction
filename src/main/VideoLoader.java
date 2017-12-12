package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

// ----------------------------------------
// Trabalho por Gabriel Corrêa Ferreira, Vinícius da Silva Santiago, e Mateus Souza
// Matrículas 78218, 78208, e 71293
// ----------------------------------------

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
        coordenadas.add(tratamentoImagem.tratarFrameAtual(frame.clone()));

//        if (frameAtual > 0 && (frameAtual % 2) == 0 && fazerAlteracaoDesenho) {
        if (frameAtual > 0 && fazerAlteracaoDesenho) {
            System.out.println("\nFrame atual: " + frameAtual);
            if (coordenadas.size() > 2) {
                Point aux = tratamentoImagem.tratarFrameAtual(frame.clone());
                coordenadas.add(aux);

//                if (coordenadas.get(coordenadas.size() - 1).y > aux.y) {
//                    coordenadas.add(aux);
//
//                }
            } else {
//                coordenadas.add(tratamentoImagem.tratarFrameAtual(frame.clone()));
            }

            if (coordenadas.size() >= (int) (0.26 * totalDeFrames)) {
                BufferedImage img = tratamentoImagem.desenhaGrafico(ShowWindow.matToBufferedImage(frame), coordenadas);
                fazerAlteracaoDesenho = false;

                return img;
            }
        } else if (!fazerAlteracaoDesenho) {

            BufferedImage img = tratamentoImagem.desenhaGrafico(ShowWindow.matToBufferedImage(frame), coordenadas);
            return img;
        }

        return ShowWindow.matToBufferedImage(frame);
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
