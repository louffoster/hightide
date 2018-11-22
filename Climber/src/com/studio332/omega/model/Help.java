package com.studio332.omega.model;

public class Help {
   private final String callout;
   private final String title;
   private final String text;
   
   public Help( String co, String title, String txt) {
      this.callout = co.toUpperCase();
      this.title = title;
      this.text = txt;
   }

   public String getCallout() {
      return callout;
   }

   public String getTitle() {
      return title;
   }

   public String getText() {
      return text;
   }
}
