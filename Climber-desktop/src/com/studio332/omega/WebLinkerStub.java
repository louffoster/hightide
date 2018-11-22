package com.studio332.omega;


public class WebLinkerStub implements WebLinker {
   public void openBrowser( final String url ) {
      System.err.println("LINK TO "+url);
   }

   @Override
   public void  openFile(byte[] data, String name) {
      // TODO Auto-generated method stub
      
   }
}
