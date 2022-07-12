package gb.tda.tools;

/**
 * $Id: FilterFilesByType.java 85 2010-03-01 10:20:00Z oneyour $
 * 
 * This is an accompanying program for the article
 * http://www.1your.com/drupal/filterfilesinJava
 * 
 * Copyright (c) 2009 - 2010 www.1your.com.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of www.1your.com nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */ 
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

/**
 * A Java Program to search for files within a specified directory. Currently
 * three types of filtering are supported.
 * 1. Filter by extension (Type)
 * 2. Filter by size
 * 3. Filter by file name
 */
public class FilterFilesByType
{
    
    /**
     * Private Constructor as this is a utility class with only public
     * static methods
     */
    private FilterFilesByType()
    {}
    
    /**
     * This main method will demonstrate the functionality offered by this class.
     * 
     * It will do the following:
     * 1. Get the path to the directory where the searching will be done
     * 2. Perform the search
     * 3. List the search results back to the user
     */
    public static void main(String[] args)
    {
        
        // Note: Should validate the command line input below to make sure 
        // it is a valid and not empty.
        // This validation is not implemented in this program.
        
        // Get the containing the directory path
        String inputMessage = "Enter the name/path of the folder: ";
        String folderName = readCommandLineInput(inputMessage);

        inputMessage = "Enter the file extensions (comma seperated if more than one) :" +
        		"(The file search will find the files of these specified extensions)";
        String commaSepExtensions = readCommandLineInput(inputMessage);
        String[] filterExtensions = commaSepExtensions.split(",");
        
        FilenameFilter fileTypeFilter = createFileTypeFilter(filterExtensions);
        File[] files = FilterFilesByType.listFiles(folderName, fileTypeFilter);
                
        if (files != null)
        {
            // Print them out in the console
            System.out.println("List of filtered files are:");
            for (File file: files)
            {
                System.out.println(file);
            }
        }
        else
        {
            System.out.println("No files in folder: " + folderName);
        }
    }
    
    /********************* Utility Methods *********************/
    
    /**
     * Request and read input from the command line (System.in)
     * 
     * @param inputMessage
     *      The message to be displayed as a request to the user
     *      
     * @return
     *      The input from the command line
     *      
     * @throws IOException
     *      Any problems while reading the input
     */
    public static String readCommandLineInput(String inputMessage)
    {
        System.out.println(inputMessage);
        Scanner scanner = new Scanner(System.in);
        
        String inputLine = scanner.nextLine();
        return inputLine;
    }

    /********************* List File Methods *********************/
    
    /**
     * Find all the files within the specified directory using the 
     * supplied FilenameFilter as a filter
     * 
     * @param dirPath
     *      The path to the directory containing the files. 
     *      
     * @param filter
     *      This FilenameFilter will be used when listing the files
     *        
     * @return
     *      The files within the specified directory filtered using the 
     *      supplied FilenameFilter filter.
     */
    public static File[] listFiles(String dirPath, FilenameFilter filter)
    {
        File dir = new File(dirPath);
        if (dir.exists())
        {
            return dir.listFiles(filter);
        }
        return null;
    }

    /********************* Create Filter Methods *********************/

    /**
     * Creates a new FilenameFilter which will filter the files with the specified 
     * extensions.
     * 
     * @param extensions
     *      An array of file extensions that will be used for the filtering. 
     *      i.e. Any file with the specified extensions will remain after the filtering.
     *      
     * @return
     *      A FilenameFilter that will filter files with the specified file extensions
     */
    public static FilenameFilter createFileTypeFilter(final String[] extensions)
    {
        FilenameFilter fileNameFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                for (String extension: extensions)
                {
                    if (name.endsWith(extension.trim()))
                    {
                        return true;
                    }
                }
                
                return false;
            }
        };
        
        return fileNameFilter;
    }

} // End of class
