#!/bin/bash
java -classpath Climber/libs/gdx.jar:Climber/libs/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 Climber-android/raw/game/ . game_atlas

java -classpath Climber/libs/gdx.jar:Climber/libs/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 Climber-android/raw/map/ . map_atlas

echo -n "Moving assets into place..."
mv game_atlas* Climber-android/assets/data/
mv map_atlas.png Climber-android/assets/maps/tiles.png
rm map_atlas.atlas


echo "DONE!"
