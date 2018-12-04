package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Main extends ApplicationAdapter {
    private static final int N_ENEMIES = 20;

    int SZ = 10;
    int ROWS = 40;
    int COLS = 40;
    Sprite[][] sprites = new Sprite[ROWS][COLS];
    Sprite[][] drawed = new Sprite[ROWS][ROWS];

    Array<Sprite> enemies;

    private SpriteBatch batch;

    @Override
    public void create() {
        enemies = new Array<>();
        this.batch = new SpriteBatch();

        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                sprites[i][j] = new Sprite(createTexture(Color.RED));
            }
        }

        for (int i = 0; i < N_ENEMIES; i++) {
            Sprite enemy = new Sprite(createTexture(Color.RED));
            enemy.setPosition(20*SZ,20*SZ);
            enemies.add(enemy);

        }

        Gdx.graphics.setContinuousRendering(false);

        tickEnemy();
    }

    private void tickEnemy() {
        new Thread(() -> {
            while(true){
                Gdx.app.postRunnable( ()->{

                    for (int i = 0; i < N_ENEMIES; i++) {
                        Sprite enemy = enemies.get(i);
                        moveEnemy(enemy);
                    }

                });
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void moveEnemy(Sprite enemy) {
        int random = MathUtils.random(0, 3);
        if( enemy.getX()/SZ < 2 ||enemy.getX()/SZ>COLS-2 )return;
        if( enemy.getY()/SZ < 2 ||enemy.getY()/SZ>ROWS-2 )return;

        if( random == 0 ){
            if( drawed[(int) (enemy.getY()/SZ)][(int)(enemy.getY()+SZ)/SZ] != null) return;
            enemy.setPosition(enemy.getX(),enemy.getY()+SZ);
        } else if( random == 1 ){
            if( drawed[(int) (enemy.getY()+SZ)/SZ][(int)enemy.getX()/SZ] != null) return;
            enemy.setPosition(enemy.getX()+SZ,enemy.getY());
        } else if( random == 2 ){
            if( drawed[(int) (enemy.getY()-SZ)/SZ][(int)enemy.getX()/SZ] != null) return;
            enemy.setPosition(enemy.getX(),enemy.getY()-SZ);
        } else{
            if( drawed[(int) (enemy.getY())/SZ][(int)(enemy.getX()-SZ)/SZ] != null ) return;
            enemy.setPosition(enemy.getX()-SZ,enemy.getY());
        }
    }

    private Texture createTexture(Color color) {
        Pixmap pixmap = new Pixmap(SZ,SZ, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.drawRectangle(0,0,SZ,SZ);
        return new Texture(pixmap);
    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);


        handleClick();
        //handleEnemyMove();

        batch.begin();
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if(drawed[i][j] != null ) drawed[i][j].draw(batch);
            }
        }

        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if( Gdx.input.getX()>=i*SZ && Gdx.input.getX()<(i+1)*SZ
                        && (Gdx.graphics.getHeight()-Gdx.input.getY())>=j*SZ && (Gdx.graphics.getHeight()-Gdx.input.getY())<(j+1)*SZ){
                    batch.setColor(Color.YELLOW);
                    batch.draw(sprites[i][j],i*SZ,j*SZ);
                    batch.setColor(Color.BLACK);
                }

            }
        }

        for (Sprite e : enemies) e.draw(batch);

        batch.end();
    }

    private void handleClick() {
        if( Gdx.input.isTouched() ){
            createCell(Gdx.input.getX()/SZ, (Gdx.graphics.getHeight()-Gdx.input.getY())/SZ);
        }
    }

    private void createCell(int cellX, int cellY) {
        if( drawed[cellY][cellX] != null ) return;
        Gdx.app.log("DEBUG","Create at cell "+cellX+" "+cellY);
        Sprite sprite = new Sprite(createTexture(Color.GREEN));
        sprite.setPosition(cellX*SZ,cellY*SZ);
        drawed[cellY][cellX] = sprite;
    }
}
