package com.application.flygokuzin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying = false;
    private SurfaceHolder holder;
    private PlayerActivity player;
    private List<ObstacleActivity> obstacles;
    private List<ObstacleActivity> obstaclesParaAdicionar;
    private List<ObstacleActivity> obstaclesParaRemover;
    private float sensorX;
    private int score = 0;
    private int recorde = 0;
    private long ultimoIncrementoScore = System.currentTimeMillis();
    private Paint textPaint;
    private boolean playerCriado = false;
    private boolean gameOverMostrado = false;
    private Canvas canvas;
    private ParallaxActivity layer1, layer2, layer3;
    private int dificuldade = 1;
    private float velocidadeObstaculos = 1.0f;
    private static final float MIN_DISTANCIA_X = 85f;

    // Construtor da GameView: inicializa listas e configurações iniciais do jogo
    public GameView(Context context) {
        super(context);
        holder = getHolder();
        obstacles = new ArrayList<>();

        obstaclesParaAdicionar = new ArrayList<>();
        obstaclesParaRemover = new ArrayList<>();

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setFakeBoldText(true);

        recorde = carregarRecorde();

        // Cria o player e o fundo após a view ser inicializada
        post(this::criarPlayer);
    }

    // Inicializa o player e o fundo parallax
    private void criarPlayer() {
        if (getWidth() == 0 || getHeight() == 0) {
            post(this::criarPlayer);
            return;
        }

        Bitmap raw = BitmapFactory.decodeResource(getResources(), R.drawable.bg_far2);
        Bitmap bgFar = Bitmap.createScaledBitmap(raw, getWidth(), getHeight(), false);

        layer1 = new ParallaxActivity(bgFar, 1.0f, getHeight());

        int centerX = getWidth() / 2;
        int centerY = getHeight() - 300;

        Bitmap[] direita = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.goku1_d),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku2_d),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku3_d)
        };

        Bitmap[] esquerda = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.goku1_e),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku2_e),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku3_e)
        };

        player = new PlayerActivity(centerX, centerY, 60, direita, esquerda);
        player.setCanvasWidth(getWidth());
        playerCriado = true;
    }

    // Recebe valor do acelerômetro para controlar o movimento
    public void setSensorX(float sensorX) {
        this.sensorX = sensorX;
    }

    // Loop principal do jogo
    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    // Atualiza o estado do jogo: movimentação, colisões, score, obstáculos
    private void update() {
        if (!playerCriado || gameOverMostrado) return;

        long agora = System.currentTimeMillis();
        if (agora - ultimoIncrementoScore >= 100) {
            score++;
            ultimoIncrementoScore = agora;

            // Aumenta a velocidade do jogo conforme o score aumenta
            float novaVelocidade = 1.0f + (score / 7000.0f);
            velocidadeObstaculos = Math.min(novaVelocidade, 4f);

            for (ObstacleActivity obs : obstacles) {
                obs.aumentarVelocidade(velocidadeObstaculos);
            }
        }

        if (layer1 != null) layer1.update();

        player.update(sensorX);

        synchronized (obstacles) {
            for (ObstacleActivity obs : obstacles) {
                obs.update();

                if (obs.isOffScreen()) {
                    obstaclesParaRemover.add(obs);
                } else if (obs.colideCom(player)) {
                    gameOverMostrado = true;
                    isPlaying = false;

                    // Atualiza recorde se o score atual for maior
                    if (score > recorde) {
                        salvarRecorde(score);
                        recorde = score;
                    }

                    post(() -> {
                        pause();
                        Context context = getContext();
                        if (context instanceof Activity) {
                            ((MainActivity) context).tocarSomDeMorte();
                            ((MainActivity) context).mostrarGameOver(score);
                        }
                    });

                    return;
                }
            }

            // Adiciona novos obstáculos de forma aleatória conforme dificuldade
            double chanceObstaculo = 0.025 + (dificuldade * 0.01);
            if (Math.random() < chanceObstaculo) {
                int tentativas = 0;
                boolean criado = false;

                while (tentativas < 10 && !criado) {
                    float x = (float) (Math.random() * getWidth());

                    if (posicaoEhValida(x)) {
                        ObstacleActivity novoObstaculo = new ObstacleActivity(getContext(), x, getHeight());
                        novoObstaculo.aumentarVelocidade(velocidadeObstaculos);
                        obstaclesParaAdicionar.add(novoObstaculo);
                        criado = true;
                    }
                    tentativas++;
                }
            }

            obstacles.removeAll(obstaclesParaRemover);
            obstacles.addAll(obstaclesParaAdicionar);
            obstaclesParaRemover.clear();
            obstaclesParaAdicionar.clear();
        }
    }

    // Garante que o obstáculo não seja criado muito próximo de outro
    private boolean posicaoEhValida(float novaPosicaoX) {
        for (ObstacleActivity obs : obstacles) {
            float distanciaX = Math.abs(obs.getX() - novaPosicaoX);
            if (distanciaX < MIN_DISTANCIA_X) {
                return false;
            }
        }
        return novaPosicaoX > 50 && novaPosicaoX < (getWidth() - 50);
    }

    // Responsável por desenhar todos os elementos do jogo na tela
    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();

            // Fundo parallax
            if (layer1 != null) layer1.draw(canvas);

            // Desenha o personagem
            if (playerCriado && player != null) {
                player.draw(canvas);
            }

            // Desenha os obstáculos
            synchronized (obstacles) {
                for (ObstacleActivity obs : obstacles) {
                    obs.draw(canvas);
                }
            }

            // Desenha score e recorde na tela
            canvas.drawText("Score: " + score, 50, 100, textPaint);
            canvas.drawText("Recorde: " + recorde, 50, 180, textPaint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    // Controla a velocidade do loop do jogo (aproximadamente 60 FPS)
    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Inicia o loop do jogo
    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Pausa o loop do jogo
    public void pause() {
        isPlaying = false;
        try {
            if (gameThread != null && gameThread.isAlive()) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // LÓGICA DO RECORDE:

    // Salva o novo recorde no SharedPreferences
    private void salvarRecorde(int novoRecorde) {
        Context context = getContext();
        context.getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .edit()
                .putInt("recorde", novoRecorde)
                .apply();
    }

    // Recupera o recorde salvo anteriormente
    private int carregarRecorde() {
        Context context = getContext();
        return context.getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .getInt("recorde", 0);
    }
}
