// $Id: listmap.java,v 1.17 2015-12-04 10:59:44-08 - - $

import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.System.*;

class listmap implements Iterable<String> {

   private class node {
      String key;
      String[] members = new String[300]; // lazy implementation 
      node link;
      public String getKey() {
         return key;
      }
   }
   
   private node head = null;
   public String eol = System.getProperty("line.separator");
   
   public listmap() {
      // Not needed, since head defaults to null anyway.
   }

   public void insert (String key, String filename) {
      //misc.trace ("insert", key);

      node prev = null;
      node curr = head;
      int cmp = 1;
      while ( curr != null) {
        cmp = curr.key.compareTo(key); //returns 0 if equal
        if (cmp >= 0) break;
        prev = curr;
        curr = curr.link;
      }
      // do insert
      if (cmp !=0 ) {
         node tmp = new node();
         tmp.key = key;
         tmp.members[0] = filename;
         tmp.link = curr;
         if (prev == null) head = tmp;
            else prev.link = tmp;
      }
      if (cmp == 0) {
         for (int i = 0; ; i++) { //break exit, sorry
            if (curr.members[i] == null) {
              curr.members[i] = filename;
              break;
            }
         }
      }
   }

   public void printgroup (){
      if (head == null) {
         return;
      } else {
      
         Writer writer = null;
         try {
            writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("group.group"), "utf-8"));
            //node prev = null;
            node curr = head;
            while (curr.link != null) {
               writer.write("GROUP_" + curr.key + "=");
               for (String file : curr.members){
                  if (file == null) break;
                  writer.write(file + ";");
               }
               writer.write(eol);
               curr = curr.link;
            }
            writer.write("GROUP_" + curr.key + "=");
            for (String file : curr.members){
               if (file == null) break;
               writer.write(file + ";");
            }
            writer.write(eol);
         }catch (IOException ex) {
         
         }finally {
            try {writer.close();} catch (Exception ex) {}
         }
      }
   }
   
   public void printattribute() {
      if (head == null) {
        return;
      } else {
         String sponge = "Sponges=";
         String bacteria = "Strain=";
         String media = "Media Type=";
         String blank = "Blank=";
         String date = "Date=";
         String crude = "Crude=";
         String pfxn = "Prefractions=";
		 String nci = "NCI extract=";
		 String noattrib = "No Attribute found=";
		 String pc = "Pure compound=";
         
         node curr = head;
         int hasnextnode = 0;
         if (curr != null) hasnextnode =1;
         boolean hasattribute = false;
		 
         while (hasnextnode != 0) {
            hasattribute = false;
			if (Pattern.matches("\\d{5}", curr.key)){
               sponge = sponge.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(sponge);
            }
            
            if (Pattern.matches("\\w\\d{3}+\\w{2}+\\d{3}\\w{2}+", 
                                             curr.key)){ //old coll. no scheme
               bacteria = bacteria.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(bacteria);
            }
			
			if (Pattern.matches("\\d{4}+\\w{2}+\\d{1}+\\w{1}+\\d{1}+\\w{2}+", 
                                             curr.key)){  //new coll. no scheme
               bacteria = bacteria.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(bacteria);
            }
            
			if (Pattern.matches("[cC]\\d{6}", curr.key)){
               sponge = sponge.concat(curr.key + ";");
			   nci = nci.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(nci);
			   //misc.trace(sponge);
            }
			
            if (Pattern.matches("(?i)M\\d", 
                              curr.key)){   //media type reported as M#
               media = media.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(media);
            }
            
            if (Pattern.matches("(?i)MeOH\\d*?|meoh\\d*?|blank\\d*?",
                                       curr.key)){
               blank = blank.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(blank);
            }
            
            if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", curr.key)){
               date = date.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(date);
            }
            
            if (Pattern.matches("(?i)F|W|TPE", curr.key)){
               crude = crude.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace(crude);
            }
            
            if (Pattern.matches("(?i)F\\d.*?", curr.key)){
               pfxn = pfxn.concat(curr.key + ";");
			   hasattribute = true;
               //misc.trace (pfxn);
            }
			if (Pattern.matches("(?i)PC\\d.*?", curr.key)){
			   pc = pc.concat(curr.key + ";");
			   hasattribute = true;
			}
            
			if (!hasattribute){
			   noattrib = noattrib.concat(curr.key + ";");
			}
            
            curr = curr.link;
            hasnextnode = 0;
            if (curr != null) hasnextnode = 1;
            
         }
         
         Writer writer = null;
         try {
            writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream("attribute.group"), 
                                                       "utf-8"));
            //node prev = null;
            //String eol = System.getProperty("line.separator");
            writer.write(sponge + eol + bacteria + eol + nci + eol + 
			             media + eol + blank + eol + date + eol + crude
						 + eol + pfxn + eol + pc + eol + noattrib);
            
         }catch (IOException ex) {
         
         }finally {
            try {writer.close();} catch (Exception ex) {}
         }
            
      }
   
   }
   
   
   public Iterator<String> iterator() {
      return new iterator();
   }


   private class iterator
           implements Iterator<String> {
      node curr = head;

      public boolean hasNext() {
         return curr != null;
      }

      public String next() {
         if (curr == null) throw new NoSuchElementException();
         String next = curr.key;
         curr = curr.link;
         return next;
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

   }

}
