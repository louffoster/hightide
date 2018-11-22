package com.studio332.omega;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class WebLinkerImpl implements WebLinker {
   private Context appContext;
   public WebLinkerImpl( Context appCtx) {
      this.appContext = appCtx;
   }
   public void openBrowser( final String url ) {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      this.appContext.startActivity(i);
   }
   
   @Override
   public void openFile(byte[] data, String name) {
      try { 
         FileOutputStream fOut = this.appContext.openFileOutput(name, Context.MODE_WORLD_READABLE);
         fOut.write(data);
         fOut.close();
      } catch ( Exception e) {
         e.printStackTrace();
      }
      
      File out = this.appContext.getFileStreamPath(name);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.fromFile(out), "application/pdf");
      //intent.setDataAndType(Uri.fromFile(out), "text/html");
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      this.appContext.startActivity(intent);

   }
}
