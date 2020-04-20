import java.io.*; 
import java.util.*; 

/* 
 *  The program reads an input file "data.txt"  that has entries of the form 
 *  0	0.000000	I	536	98.190	92.170	92.170
 *  4	133.333330	P	152	98.190	92.170	92.170
 * 	1	33.333330	B	136	98.190	92.170	92.170
 *
 * The file is read line-by-line, values are parsed and assigned to variables,
 * values are  displayed, and then written to a file with name "output.txt"  
 */

class ReadFileWriteFile {  
	public static void main (String[] args) { 
		
		BufferedReader bis = null; 
		String currentLine = null; 
		PrintStream pout = null;
		
		try {  
			
			/*
			 * Open input file as a BufferedReader
			 */ 
			File fin = new File("movietrace.data"); 
			FileReader fis = new FileReader(fin);  
			bis = new BufferedReader(fis);  
			
			/*
			 * Open file for output 
			 */
			FileOutputStream fout =  new FileOutputStream("output.txt");
			pout = new PrintStream (fout);
			
			/*
			 *  Read file line-by-line until the end of the file 
			 */
			 
			 //Parameters for computing average I, B and P frame size.
			 int iSize = 0;
			 int bSize = 0;
			 int pSize = 0;
			 
			 int iFrames = 0;
			 int bFrames = 0;
			 int pFrames = 0; 



			while ( (currentLine = bis.readLine()) != null) { 
				
				/*
				 *  Parse line and break up into elements 
				 */
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); //Sequence Number
				String col2 = st.nextToken(); //Time
				String col3  = st.nextToken(); //Frame type
				String col4 = st.nextToken();  //Size 
				
				/*
				 *  Convert each element to desired data type 
				 */
				int SeqNo 	= Integer.parseInt(col1);
				float Ftime 	= Float.parseFloat(col2);
				String Ftype 	= col3;
				int Fsize 	= Integer.parseInt(col4);
				
				
				/*
				 *  Display content of file 
				 */
				// System.out.println("SeqNo:  " + SeqNo); 
				// System.out.println("Frame time:   " + Ftime); 
				// System.out.println("Frame type:        " + Ftype); 
				// System.out.println("Frame size:       " + Fsize + "\n");

				//Determine total size/frames of each frame type 
				if (Ftype.equals("I"))
				{
					iSize += Fsize;
					iFrames++; 
				}
				else if (Ftype.equals("B"))
				{
					bSize += Fsize;
					bFrames++; 
				}
				else if (Ftype.equals("P"))
				{
					pSize += Fsize;
					pFrames++; 
				}
				else
				{
					pout.println("Unknown Frame\n"); 
				}

				/*
				 *  Write line to output file 
				 */
				pout.println(SeqNo+ "\t"+  Ftime + "\t" + Ftype + "\t" + Fsize); 
				
			}

			/*
			*  Display Average Frame Sizes
			*/
			System.out.println("Average I Frame:  " + (float)(iSize/iFrames));
			System.out.println("Average B Frame:  " + (float)(bSize/bFrames)); 
			System.out.println("Average P Frame:  " + (float)(pSize/pFrames));  
			
			
		} catch (IOException e) {  
			// catch io errors from FileInputStream or readLine()  
			System.out.println("IOException: " + e.getMessage());  
		} finally {  
			// Close files   
			if (bis != null) { 
				try { 
					bis.close(); 
					pout.close();
				} catch (IOException e) { 
					System.out.println("IOException: " +  e.getMessage());  
				} 
			} 
		} 
	}  
}

