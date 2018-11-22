package com.studio332.omega.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.studio332.omega.Climber;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.model.Settings;
import com.studio332.omega.model.ClimberGame.State;
import com.studio332.omega.util.SoundManager;   


public class Cliff extends Group {
   private ClimberGame model;
   private TiledMap tiledMap;
   private OrthogonalTiledMapRenderer tileMapRenderer;
   private OrthographicCamera camera;
   private Ike ike;
   private final float scale;
   private final float centerIkeX;
   private final float maxCameraX;
   private final float minCameraX;
   private  float maxCameraY;
   private final float minCameraY;
   private final float minCliffX;
   private final float maxCliffX;
   private CliffListener listener;
   private RisingWater water;
   private boolean waterThreat = false;
   
   private static float CLIMBER_CENTER_Y = 250f;
   
   public Cliff(float w, float h, float leftPnlW, float rightPnlW, ClimberGame model) {
      super();
      
      this.model = model;
      
      // set the dimensions of this actor. necessary to calculate extents later
      setWidth(w);
      setHeight(h);
           
      this.tiledMap = new TmxMapLoader(). load(
            "maps/"+Settings.instance().getCurrentMap().getFile());
      this.scale = Gdx.graphics.getWidth()/Climber.TGT_WIDTH;
      this.tileMapRenderer = new OrthogonalTiledMapRenderer(this.tiledMap, this.scale);
      this.camera = new OrthographicCamera();
    
      // add the ike and water level actors
      this.water = new RisingWater( );
      this.water.setWidth(w);
      this.water.setHeight(h);
      this.ike = new Ike( model );
      this.ike.setPosition( (Climber.TGT_WIDTH-this.ike.getWidth())/2, 0f);
      addActor(this.ike);
      addActor(this.water);
      
      // full height COUNTING the blank row at top for stars
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      float fullH = tl.getHeight()*tl.getTileHeight()*this.tileMapRenderer.getUnitScale();
            
      // save some limits for panning & moving climber
      this.maxCameraX = getFullWidth()-Gdx.graphics.getWidth()/2 + rightPnlW*this.scale;
      this.minCameraX =  Gdx.graphics.getWidth()/2 - leftPnlW*this.scale;
      this.maxCameraY = fullH - Gdx.graphics.getHeight()/2;
      
      // these are in real screen coords. no scaling
      this.minCliffX =  leftPnlW;
      this.maxCliffX =  Climber.TGT_WIDTH - rightPnlW - this.ike.getWidth();
      this.centerIkeX = this.ike.getX();   
      
      // center of cliff and bottom along bottom of view
      this.camera.setToOrtho(false);
      this.camera.position.set(getFullWidth()/2, Gdx.graphics.getHeight()/2, 0 );      
      this.camera.update();
      this.minCameraY = this.camera.position.y;
   }
   
   @Override
   public void setPosition(float x, float y) {
      this.water.setPosition(x, y);
      super.setPosition(x, y);
   }
   
   public void setListener( CliffListener l ) {
      this.listener = l;
   }
   
   public boolean isWaterThreat() {
      return this.waterThreat;
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      if ( this.model.getState().equals(State.ENDED)) {
         return;
      }
      
      batch.end();
      this.tileMapRenderer.setView(camera);
      this.tileMapRenderer.render();
      
      batch.begin();
      this.ike.draw(batch, parentAlpha);
            
      // calculate water level and draw it
      this.waterThreat = false;
      float mapBottomY = this.camera.position.y -  Gdx.graphics.getHeight()/ 2;
      float waterPercent = this.model.getWaterLevelPercent() / 100.0f;
      float waterH = waterPercent*getFullHeight();
      float waterB = waterH-mapBottomY;// this is in terms of scaled map coords
      if ( waterB > 0 ) {
         this.water.draw(batch, waterB/this.scale); // convert to screen coords by inverse scale
         if ( waterPercent > 0.05f ) {
            this.waterThreat = true;
         }
      } 
      
      if ( this.ike.isDead() == false ) {
         // make ike fall and scream
         if ( this.model.getState().equals(State.DEAD) ) {
            this.ike.kill();
            SoundManager.instance().playSound(SoundManager.SCREAM);
            this.ike.addAction(moveTo(this.ike.getX(), -250, 1.00f, Interpolation.circleIn));
         }
         
         // enforce drowning!
         if ( this.model.hasTank() == false ) {
            // percentProgress is to the top of his head!
            if ( waterPercent > getPercentProgress() ) {
               if ( this.ike.isDead() == false ) {
                  this.ike.kill();
                  this.ike.addAction(moveTo(this.ike.getX(), -200, 5f));
                  if (this.listener != null ) {
                     this.listener.drowned();
                  }
               }
            }
         }
         
         // find winner
         if ( getPercentProgress() >= 1.0f ) {
            this.ike.addAction(fadeOut(1f));
            if ( this.listener != null ) {
               this.listener.reachedTop();
            }
         }
      }
   }

   /**
    * Climb left-right and enforce obstacles
    * @param rate
    */
   public boolean sidle(float sidleX ) {
      if ( sidleX == 0 ) {
         return true;
      }
      
     // dont let ike climb over shit he shoudn't
     float deltaX = enforceObstaclesX( sidleX );
     boolean fullMove = (deltaX == sidleX);
      
      // if ike is off center, see if this moves him closer to center
      if ( this.ike.getX() < this.centerIkeX && deltaX > 0 ) {
         float newX = this.ike.getX() + deltaX;
         if ( newX > this.centerIkeX ) {
            deltaX = newX - this.centerIkeX;
            this.ike.setPosition(this.centerIkeX, this.ike.getY() );
         } else {
            this.ike.setPosition(newX, this.ike.getY() );
            return fullMove;
         }
      } else if ( this.ike.getX() > this.centerIkeX && deltaX < 0 ) {
         float newX = this.ike.getX() + deltaX;
         if ( newX < this.centerIkeX ) {
            deltaX = newX - this.centerIkeX;
            this.ike.setPosition(this.centerIkeX, this.ike.getY() );
         } else {
            this.ike.setPosition(newX, this.ike.getY() );
            return fullMove;
         }
      }
      
      // move the camera til it meets the edges of the map
      Vector3 v = new Vector3( Math.round(deltaX*this.scale), 0f, 0f);
      this.camera.position.add(v);
      if ( camera.position.x <  this.minCameraX ) {
         deltaX = this.camera.position.x - this.minCameraX;
         this.camera.position.x = Math.round(this.minCameraX);
      } else if ( this.camera.position.x > this.maxCameraX ) {
         deltaX = this.camera.position.x - this.maxCameraX;
         camera.position.x = Math.round(this.maxCameraX);
      } else {
         deltaX = 0f;
      }
      this.camera.update();
      
      // done if delta has been consumed
      if ( deltaX == 0f) {
         return fullMove;
      }
      
      // apply remainder to ike
      float newX = this.ike.getX() + deltaX;
      if ( newX < this.minCliffX ) {
         newX = this.minCliffX;
      } else if (newX > this.maxCliffX ) {
         newX = this.maxCliffX;
      }
      
      if ( this.ike.getX() != newX ) {
         this.ike.setPosition(newX, this.ike.getY() );
      }
      
      return fullMove;
   }
   
   /**
    * Collision detection in the X direction -- needs to handle both left and righ bonks
    * @param deltaX
    * @return
    */
   private float enforceObstaclesX( final float deltaX ) {
      // save original delta
      float actualDeltaX = deltaX;
      
      // find rows that ike is in
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      float mapBottomY = this.camera.position.y -  Gdx.graphics.getHeight()/ 2;
      float ikeBottomY = (ike.getY()+ike.getHeight()*0.3f)*this.scale;
      int bottomRow = (int)( (mapBottomY+ikeBottomY) / (tl.getTileHeight()*this.scale) );
      float ikeTopY = (ike.getY()+ike.getHeight()*0.7f)*this.scale;
      int topRow = (int)( (mapBottomY+ikeTopY) / (tl.getTileHeight()*this.scale) );
      topRow = Math.min(tl.getHeight()-1, topRow);
     
      // x coord at edge of ike based on direction to be traveled
      float ikeXOffset = this.ike.getX() - this.centerIkeX;
      float ikeCenterX = this.camera.position.x + ikeXOffset*scale;
      float ikeEdgeX = ikeCenterX + this.ike.getWidth()*0.6f*this.scale;
      if ( deltaX < 0 ) {
         ikeEdgeX = ikeCenterX - +this.ike.getWidth()*0.6f*this.scale;
      }     
      
      // project curr position to next and convert to map column
      float projectedX = ikeEdgeX + deltaX;
      int projectedCol = (int)( projectedX / (tl.getTileWidth()*scale) );
      
      projectedCol = Math.min(tl.getWidth()-1, projectedCol);
      projectedCol = Math.max(0, projectedCol);
      
      // did he bump into a rock?
      if ( !isClimbable(bottomRow, projectedCol) || !isClimbable(topRow, projectedCol) ) {
         float hitX = projectedCol * tl.getTileWidth()*scale;
         float d = projectedX - hitX;
         actualDeltaX = actualDeltaX - d;
         actualDeltaX = Math.max(0, actualDeltaX);
      }
      return actualDeltaX;
   }
   
   public Ike getIke() {
      return this.ike;
   }
   
   /**
    * Climb the mountain at the specified rate & time. Detect top.
    * @param delta Amount climbed
    * @param deltaS Delta seconds for this climb update
    * @return distance actually climbed
    */
   public boolean climb( float delta, float deltaS ) {
      if ( delta == 0 ) {
         return true;
      }
      
      // limit the amount actually cimbed by obstacles
      float deltaY = enforceObstaclesY( delta );
      boolean fullMove = ( delta == deltaY);

      // if ike hasn't reached his static position on screen
      // (up from bottm a bit), move his image first. Any leftover
      // delY will be used to move the camera up the cliff
      if ( this.ike.getY() < CLIMBER_CENTER_Y && deltaY > 0) {
         float newY = this.ike.getY()+deltaY;
         if ( newY > CLIMBER_CENTER_Y ) {
            deltaY = newY - CLIMBER_CENTER_Y;
            this.ike.addAction( moveTo(this.ike.getX(), CLIMBER_CENTER_Y, deltaS));
         } else {
            this.ike.addAction( moveTo(this.ike.getX(), newY, deltaS));
            deltaY = 0;
         }
      } else if (  this.ike.getY() <= CLIMBER_CENTER_Y && deltaY < 0) {
         float newY = this.ike.getY()+deltaY;
         if ( newY > 0 ) {
            this.ike.addAction( moveTo(this.ike.getX(), newY, deltaS));
            deltaY = 0;
         } else {
            deltaY = newY;
            this.ike.addAction( moveTo(this.ike.getX(), 0, deltaS));
         }
      }
      
      // move the camera til it meets the edges of the map
      Vector3 v = new Vector3(0f, Math.round(deltaY * this.scale), 0f);
      this.camera.position.add(v);
      if (camera.position.y < this.minCameraY) {
         deltaY = this.camera.position.y - this.minCameraY;
         this.camera.position.y = Math.round(this.minCameraY);
         
      } else if ( camera.position.y > this.maxCameraY ) {
         deltaY = this.camera.position.y - this.maxCameraY;
         camera.position.y = Math.round(this.maxCameraY);
      } else {
         deltaY = 0;
      }
      this.camera.update();
      
      // still y left, ike is climbing past mid screen to top
      if ( deltaY > 0 && this.ike.getY() < getFullHeight() ) {
         float newY = this.ike.getY()+deltaY;
         this.ike.addAction( moveTo(this.ike.getX(), newY, deltaS));
      }
        
      return fullMove;
   }
   
   /**
    * Collision detection in the Y direction
    * @param deltaY
    * @return
    */
   private float enforceObstaclesY( final float deltaY ) {
      // save original delta
      float actualDeltaY = deltaY;
      
      // find columns that ike is in
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      float ikeCenterX = this.camera.position.x+(this.ike.getX() - this.centerIkeX)*this.scale;
      float ikeW = (this.ike.getWidth()*0.5f-20f)*this.scale;//  (this.ike.getWidth()*0.1f*this.scale)/2f;
      float rightX = ikeCenterX + ikeW;
      float leftX = ikeCenterX - ikeW;
      int leftCol = (int)( leftX / (tl.getTileWidth()*this.scale) );
      int rightCol = (int)( rightX / (tl.getTileWidth()*this.scale) );
      
      // y coord at edge of ike based on direction to be traveled
      float bottY = this.camera.position.y - Gdx.graphics.getHeight()/2; 
      float ikeY = this.ike.getY() + this.ike.getHeight()*0.9f+25f;
      float ikeEdgeY = bottY+ikeY*this.scale; 
      if ( deltaY < 0 ) {
         ikeEdgeY = bottY + this.ike.getY()*this.scale;
      }
      
      // project curr position to next and convert to map row
      float projectedY = ikeEdgeY + deltaY;
      int projectedRow = (int)( projectedY / (tl.getTileHeight()*this.scale) );
      projectedRow = Math.min(tl.getHeight()-1, projectedRow);
      projectedRow = Math.max(0, projectedRow);
      
      // get the tile ids at these row/col positions
      if ( !isClimbable(projectedRow, leftCol) || !isClimbable(projectedRow, rightCol) ) {
         float hitY = projectedRow * tl.getTileHeight()*scale;
         float d = projectedY - hitY;
         actualDeltaY = actualDeltaY - d;
         actualDeltaY = Math.max(0, actualDeltaY);
      }
      return actualDeltaY;
   }
   
   public int getBottomRow() {
      float bottomY = this.camera.position.y -Gdx.graphics.getHeight()/2;
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      int bottomRow = (int)( bottomY / (tl.getTileHeight()*scale) );
      return bottomRow; 
   }
   
   public boolean isClimbable( int row, int col ) {
      MapLayers layers = this.tiledMap.getLayers();
      TiledMapTileLayer obstacles = (TiledMapTileLayer)layers.get("obstacles");
      Cell cell = obstacles.getCell(col,row );
      return (cell == null);
   }
   
   public float getFullWidth() {
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      return tl.getWidth()*tl.getTileWidth()*this.tileMapRenderer.getUnitScale();
   }
   
   public float getFullHeight() {
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      return (tl.getHeight()-1)*tl.getTileHeight()*this.tileMapRenderer.getUnitScale();
   }
   
   public int getMaxRows () {
      TiledMapTileLayer tl =  (TiledMapTileLayer)this.tiledMap.getLayers().get(0);
      return tl.getHeight()-1;
   }  
   
   public float getIkeX() {
      float ikeXOffset = this.ike.getX() - this.centerIkeX - this.ike.getWidth()/2;
      return this.camera.position.x + ikeXOffset*this.scale;
   }
   
   public float getIkeY() {
      float mapBottomY = this.camera.position.y -  Gdx.graphics.getHeight()/ 2;
      float ikeMidY = ike.getY()*this.scale;
      return mapBottomY+ikeMidY;
   }
   
   public float getIkeHeight() {
      return this.ike.getHeight();
   }

   public float getPercentProgress() {
      float mapBottomY = this.camera.position.y -  Gdx.graphics.getHeight()/ 2;
      float ikeY = (this.ike.getY()+ike.getHeight()*0.9f)*this.scale;
      return (mapBottomY+ikeY) / getFullHeight();
   }
   
   /**
    * Listener for ikes status on cliff. Lets others know
    * when he hs made it to the top or drowned in the rising water
    * 
    * @author lfoster
    *
    */
   public interface CliffListener {
      public void reachedTop();
      public void drowned();
   }
}
