/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jca.codegenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

/**
 * A SimpleTemplate.
 * 
 * @author Jeff Zhang
 * @version $Revision: $
 */
public class SimpleTemplate implements Template
{
   /** template file */
   
   private URL input;
   
   /** template string */
   String templateText = null;
   
   /**
    * SimpleTemplate
    * @param input template string
    */
   public SimpleTemplate(String input)
   {
      this.templateText = input;
   }
   
   /**
    * SimpleTemplate
    * @param input template string
    */
   public SimpleTemplate(URL input)
   {
      this.input = input;
   }
   
   /**
    * Processes the template
    * @param varMap variable map
    * @param out the writer to output the text to.
    */
   @Override
   public void process(Map<String, String> varMap, Writer out)
   {
      try
      {
         if (templateText == null)
         {
            templateText = readFileIntoString(input);
         }
         String replacedString = replace(varMap);
         out.write(replacedString);
         out.flush();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      //System.out.println(templateString);
   }

   /**
    * Replace string in the template text
    * @param varMap variable map
    * @return replaced string
    */
   public String replace(Map<String, String> varMap)
   {
      StringBuilder newString = new StringBuilder();

      int p = 0;
      int p0 = 0;
      while (true)
      {
         p = templateText.indexOf("${", p);
         if (p == -1)
         {
            newString.append(templateText.substring(p0, templateText.length()));
            break;
         }
         else
         {
            newString.append(templateText.substring(p0, p));
         }
         p0 = p;
         p = templateText.indexOf("}", p);
         if (p != -1)
         {
            String varName = templateText.substring(p0 + 2, p).trim();
            if (varMap.containsKey(varName))
            {
               newString.append(varMap.get(varName));
               p0 = p + 1;
            }
         }
      }
      return newString.toString();
   }

   /**
    *  Reads the contents of a file into a string variable.
    * 
    * @param input
    * @return
    * @throws IOException
    */
   private String readFileIntoString(URL input) throws IOException
   {
      
      InputStream stream = null;
      InputStreamReader reader = null;
      try
      {
         stream = input.openStream();
         reader = new InputStreamReader(stream);
         return readStreamIntoString(reader);
      }
      finally
      {
         if (reader != null)
            reader.close();
         if (stream != null)
            stream.close();
      }
   }

   /**
    *  Reads the contents of a stream into a string variable.
    * 
    * @param reader
    * @return
    * @throws IOException
    */
   private String readStreamIntoString(Reader reader) throws IOException
   {
      StringBuilder s = new StringBuilder();
      char a[] = new char[0x10000];
      while (true)
      {
         int l = reader.read(a);
         if (l == -1)
            break;
         if (l <= 0)
            throw new IOException();
         s.append(a, 0, l);
      }
      return s.toString();
   }
}