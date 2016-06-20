// $Id
// Pipe ls output into this program by 'ls | xargs java jparse'
// note that "." is not allowed in filenames based on processing method
// Creates group / attribute files based on dilineation by '_'


import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.System.*;

class jparse {
   private static final String STDIN_FILENAME = "-";
   private static final String REGEX = "\\w+([-'.:/]\\w+)*";
   private static final Pattern PATTERN = Pattern.compile(REGEX);

   
   private static void xref (String[] filenames) {
      listmap map = new listmap();  
      for (String filename: filenames) {
         //misc.trace ("filename", filename);
         int ismzXML = 0;  //this next section checks if file is mzXML
         if (filename != null) {  //else break later
            if (filename.contains(".mzXML")) { // check for mzXML
              misc.trace ("Processing " + filename);
              ismzXML = 1;
            }
            else {
               misc.trace("File: " + filename + " is not mzXML");
            }
         
            if (ismzXML == 1) {
               String basename[] = filename.split("\\."); //No '.' 
               String groups[] = basename[0].split("_");
               for (String group : groups){
                  //System.out.println(filename + " groups: " + group);
                  map.insert(group, filename);
               }
            
            
               map.printgroup();
               map.printattribute();
             
            }
         
        


         }else break; // 
      }


   
   
   }

   
   private static String[] fileread(String filename) {
      misc.trace("reading filenames from " + filename);
      String[] filelist = new String[2000];
      try {
         Scanner file = new Scanner (new File (filename));
      
         for (int i=0;file.hasNextLine();i++) {
            filelist[i]=file.nextLine();
            misc.trace("file #" + i + ": " + filelist[i]);
         }
      }catch (IOException error) {
         misc.warn (error.getMessage());
      }
      
      return filelist;
   }

   // Main function scans arguments to cross reference files.
   public static void main (String[] args) {
      //misc.trace("args length: " + args.length);
      if (args.length == 0) {
         misc.trace ("Please enter a filename");
         Scanner input = new Scanner(System.in);
         
         String filename = input.nextLine();
         xref (fileread(filename));
         
         //misc.die();
         //xref_filename (STDIN_FILENAME); // allow manual entry
      }else {
         
         xref (args);
         
      }
      exit (misc.exit_status);
   }

}

