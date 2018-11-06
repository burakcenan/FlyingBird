package com.example.burak.FlyingBird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	public SpriteBatch batch;
	Texture background;
	float birdX;
	float birdY;
	float backgroundX;
	float backgroundY;
	int gameState = 0;
	float velocity = 0;
	float gravity =0.1f;

	int numberOfEnemy = 15;
	float[] enemyX= new float[numberOfEnemy];
	float[] enemyOffset= new float[numberOfEnemy];
	float enemyDistance;
	Random random;

	Circle birdCircle;
	Circle[] enemyCircles;

    int score = 0;
    int maxScore = 0;
    int scoredEnemy = 0;
    int level = 1;

    BitmapFont font;
    BitmapFont fontGameOver;

	TextureAtlas birdAtlas;
	Animation<TextureRegion> birdAnimation;
	float birdAnimTime = 0;
	Boolean animBool = false;
	int animSayac = 0;

	TextureAtlas enemyAtlas;
	Animation<TextureRegion> enemyAnimation;

	Preferences prefs;
	float enemyAnimTime = 0;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");
		birdAtlas = new TextureAtlas(Gdx.files.internal("bird.atlas"));
		enemyAtlas = new TextureAtlas(Gdx.files.internal("enemy.atlas"));
		birdAnimation = new Animation(0.1f,birdAtlas.getRegions());
		enemyAnimation = new Animation(0.2f,enemyAtlas.getRegions());

		birdX = Gdx.graphics.getWidth()/4;
		birdY = Gdx.graphics.getHeight()/3;
		backgroundX = 0;

		birdCircle = new Circle();
		enemyCircles = new Circle[numberOfEnemy];


		enemyDistance = Gdx.graphics.getWidth()/(5 * level);
		random = new Random();

		font= new BitmapFont();
		font.setColor(Color.BLACK);
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
		font.getData().setScale(2);

		fontGameOver= new BitmapFont();
		fontGameOver.setColor(Color.WHITE);
		fontGameOver.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
		fontGameOver.getData().setScale(2);

		for(int i=0;i<numberOfEnemy;i++){
			enemyX[i] = Gdx.graphics.getWidth() + i * enemyDistance;
			enemyOffset[i] = (random.nextFloat()- 0.5f) * Gdx.graphics.getHeight()/2;
			enemyCircles[i]= new Circle();
		}try {
			prefs = Gdx.app.getPreferences("My Preferences");
			maxScore = prefs.getInteger("maxScore", 0);
			level = prefs.getInteger("level", 1);
		}catch (Exception e){
			System.out.println("BRK"+e.getLocalizedMessage());
			level=1;
			maxScore=0;
		}

	}

	@Override
	public void render () {
		animSayac++;
		if (animSayac > 50){
	    animBool = false;
		animSayac=0;
		}
		birdAnimTime += Gdx.graphics.getDeltaTime();
		enemyAnimTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		batch.draw(background,backgroundX,backgroundY,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.draw(background,backgroundX+1.77f*Gdx.graphics.getHeight(),backgroundY,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1)
		{
            for(int i=0 ; i < numberOfEnemy ; i++){
                batch.draw(enemyAnimation.getKeyFrame(enemyAnimTime,true),enemyX[i],Gdx.graphics.getHeight()/2+enemyOffset[i],-Gdx.graphics.getWidth()/20,Gdx.graphics.getHeight()/20);
                enemyCircles[i].set(enemyX[i]-Gdx.graphics.getWidth()/40,Gdx.graphics.getHeight()/2+enemyOffset[i]+Gdx.graphics.getHeight()/40,Gdx.graphics.getHeight()/40);
				if (enemyX[scoredEnemy] < birdX){
				    if (score % 15 == 0 && score != 0){
				        level++;
                    }
					score++;
					if (scoredEnemy<numberOfEnemy-1){
						scoredEnemy++;
					}else{
						scoredEnemy=0;
					}
				}

				if(enemyX[i] < -Gdx.graphics.getWidth()/20){
					enemyX[i] = enemyX[i] + numberOfEnemy * enemyDistance;
                    enemyOffset[i] = (random.nextFloat()- 0.5f) * Gdx.graphics.getHeight()/1.5f;
				}
				else{
					enemyX[i] = enemyX[i] -5-level;
				}
				if(backgroundX <- 1.77f*Gdx.graphics.getHeight()){
					backgroundX = 0;
				}

			}
			backgroundX -= 250 * Gdx.graphics.getDeltaTime();


			if (birdY>0) {
				velocity += gravity;
				birdY -= velocity;
			}

			else{
				gameState = 2;
			}

			if (Gdx.input.justTouched() && birdY<Gdx.graphics.getHeight()*0.9f) {
				velocity = -Gdx.graphics.getHeight() / 150;
				animBool = true;
			}

		}
		else if(gameState==0) {
			if(Gdx.input.justTouched())
			{
				gameState = 1;
			}
		}else if(gameState==2){
            if (maxScore<score){

                maxScore = score;
                prefs.putInteger("maxScore",maxScore);
                prefs.putInteger("level",level);
				prefs.flush();
            }
			fontGameOver.draw(batch,"Tap to Play Again",birdX+Gdx.graphics.getWidth()/20,birdY+Gdx.graphics.getHeight()/8.5f);

			if(Gdx.input.justTouched())
			{
				gameState = 1; 
				birdX = Gdx.graphics.getWidth()/4;
				birdY = Gdx.graphics.getHeight()/3;

				for(int i=0;i<numberOfEnemy;i++){
					enemyX[i] = Gdx.graphics.getWidth() + i * enemyDistance;
					enemyOffset[i] = (random.nextFloat()- 0.5f) * Gdx.graphics.getHeight();

					enemyCircles[i]= new Circle();
				}
				velocity=0;
				score = 0;
				level = 1;
				scoredEnemy=0;
			}
		}
		font.draw(batch,"Score: " + score,Gdx.graphics.getWidth()/50,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/40);
		font.draw(batch,"Level: " + level,Gdx.graphics.getWidth()*0.9f,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/40);
        font.draw(batch,"Max Score: " + maxScore,Gdx.graphics.getWidth()/2.4f,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/40);
        batch.draw(birdAnimation.getKeyFrame(birdAnimTime,animBool),birdX,birdY,Gdx.graphics.getWidth()/20,Gdx.graphics.getWidth()/18);
        batch.end();
		birdCircle.set(birdX+Gdx.graphics.getWidth()/40,birdY+Gdx.graphics.getHeight()/24,Gdx.graphics.getWidth()/40);
		for(int i=0;i<numberOfEnemy;i++)
		{
			if(Intersector.overlaps(birdCircle,enemyCircles[i])) {
				gameState = 2;

			}
		}



	}
	@Override
	public void dispose ()
    {
		batch.dispose();
		birdAtlas.dispose();
		enemyAtlas.dispose();
		fontGameOver.dispose();
		font.dispose();
	}

}
