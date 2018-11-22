package com.studio332.omega.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.studio332.omega.Climber;

public class AbstractScreen  implements Screen  {
   protected final Stage stage;
   protected final Climber game;
   
   public AbstractScreen(Climber game) {
      this.game = game;
      this.stage = new Stage(Climber.TGT_WIDTH, Climber.TGT_HEIGHT, false) {
         @Override
         public boolean keyDown(int keyCode) {
             if (keyCode == Keys.BACK || keyCode == Keys.ESCAPE) {
                if ( backClicked() ) {
                   return true;
                } else {
                   System.err.println("EXIT GAME NOWWWW =============================================");
                   Gdx.app.exit();
                }
             }
             return true;
         }
     };
     Gdx.input.setInputProcessor(this.stage);
     Gdx.input.setCatchBackKey(true);
   }
   
   protected boolean backClicked() {
      // return false to indicate it was not handled
      // by the screen
      return false;
   }

   protected String getName() {
      return getClass().getSimpleName();
   }

   @Override
   public void render(float delta) {
      Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      
      // update and draw the stage actors
      stage.act( delta );
      stage.draw();
   }

   @Override
   public void resize(int width, int height) {
   }
   
   public Stage getStage() {
      return this.stage;
   }

   @Override
   public void show() {
      // let this screen pocess events (like touch)
      Gdx.input.setInputProcessor( this.stage );
   }

   @Override
   public void hide() {
   }

   @Override
   public void pause() {
   }

   @Override
   public void resume() {
   }

   @Override
   public void dispose() {
      this.stage.dispose();
   }
}
