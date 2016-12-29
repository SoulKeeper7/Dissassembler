
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
public class Dissassembler 
{
	
	public static void main(String[] args) 
	{
		
		if(!args[0].equals("dis"))
		{
			 System.out.print("Operataion not recognized:"+args[0]);	
			 System.exit(0);
		}
		
		if(!Files.exists(Paths.get(args[1])))
		{
			System.out.print("File not available"+args[1]);
			
			System.exit(0);
		}
	
		 byte[] data = null;
		 String opcodebits = "";
        String firstfivebits = "";
        String secondfivebits = "";
        String thirdfivebits = "";
        String fourthfivebits = "";
        String lastsixbits = "";
        List<String> opcodekey = new ArrayList<String>();;
     
        Map<String, String[]> opcode= new HashMap<String, String[]>();
        
		 try
		 {         
                try
                {
                	data= Files.readAllBytes(Paths.get(args[1]));           
                }
                catch(Exception e)
                {
                	System.out.println(e.getMessage());
                }   
                
	          try
	          {          	
	        	  opcodeKeyCollectionBuilding(opcodekey);	
	        	 // Object location = Dissassembler.class.getClassLoader().getResource("Resource/opcodekey.txt");
	        	  //System.out.println((Paths.get(location.toString())));  
	        	  //opcodekey = Files.readAllLines((Paths.get(location.toString()))); 
	          }
	          catch (Exception e)
	 		  {
	        	  System.out.println(e.getMessage());  
	 		  }

          
          for(int i =0;i<opcodekey.size();i++)
          {
          	String[] splited = opcodekey.get(i).split("\\s+");
          	String[] ListString = new String[]{splited[1],splited[2],splited[3]};          	
          	opcode.put(splited[0], ListString);          	
          }
        
          StringBuilder sb = new StringBuilder();
          
          for(byte b:data)
          {
          	
          String s1 = String.format("%1s", Integer.toBinaryString((b+256)%256));
          String S2 = String.format("%8s", s1).replace(' ', '0');
          sb.append(S2);           
          }
          
          int i =0;
          StringBuilder sb1 = new StringBuilder();          
          boolean breakreached = false;        
          int z = 600;
          for (int j = 0; j < sb.length(); j++)
          {
              char c = sb.charAt(j);  
              if (i ==36  && i!=0 && breakreached ==false)
              { 
                  sb1.append(c).append(" ");                  
                  lastsixbits = sb1.toString().substring((sb1.length() - 7), sb1.length()-1);                 
                  sb1.append(z).append(" ");                 
                  for (Map.Entry<String, String[]> items : opcode.entrySet())
                  {
                  	String Key = items.getKey();
                  	String[] values = items.getValue();
                  	
                      if (values[1].equals("000000")  && (values[2].equals(lastsixbits) && (!lastsixbits.equals("001101") ))) 
                      {
                      	
                      	 iType(firstfivebits, secondfivebits, thirdfivebits, fourthfivebits, sb1, Key,lastsixbits,opcodebits);                       
                           break;
                      }
                      else if (values[1].equals(opcodebits) && (!opcodebits.equals("000000")) && (!opcodebits.equals("000010")) && (!lastsixbits.equals("001101")))
                      {
                          if ((Key.equals("BGEZ") && (!secondfivebits.equals("00000"))) || (((Key.equals("BLTZ")) && (!(secondfivebits.equals("00001"))))))
                          {
                          	continue;
                          }
                          	rType(firstfivebits, secondfivebits, thirdfivebits, fourthfivebits, lastsixbits, sb1, Key);
                          	break;
                  		}
                      else if (opcodebits.equals("000010")&& values[1].equals(opcodebits))
                      {
                      	int x = Integer.parseInt((firstfivebits+ secondfivebits+ thirdfivebits+ fourthfivebits+ lastsixbits),2) *4;
                        sb1.append(Key).append(" ").append("#").append(x);
                        break;
                      }
                      else if (values[1].equals("000000")&& (lastsixbits.equals("001101") && (Key.equals("BREAK"))))
                      {                      	
                          sb1.append(Key);
                          breakreached = true;
                          break;
                      }                         
                  }
                      
	                sb1.append(System.getProperty("line.separator"));
	                i = 0;
	                z += 4;	               
	            }  
          	 else if ( i == 5&&breakreached ==false) 
              {
              	 sb1.append(c).append(" ");              	
              	 try
              	 {              		
              		 opcodebits = sb1.toString().substring((sb1.length() - 7), sb1.length()-1);
              	 }
              	 catch(Exception e)
              	 {
              		System.out.println(e.getMessage());  
              	 }
              	 
                 i += 2;
              }
              else if ((i == 11)&& i!=0&&breakreached ==false) 
              {
              	 sb1.append(c).append(" "); 
                  firstfivebits = sb1.toString().substring((sb1.length() - 6), sb1.length()-1);                 
                  i += 2;
              }
              else if ((i == 17)&& i!=0 && breakreached ==false) 
              {
              	 sb1.append(c).append(" ");
                  secondfivebits = sb1.toString().substring((sb1.length() - 6), sb1.length()-1);
                  i += 2;
              }
              else if ((i == 23)&& i!=0&& breakreached ==false) 
              {
              	 sb1.append(c).append(" ");
                  thirdfivebits = sb1.toString().substring((sb1.length() - 6), sb1.length()-1);
                  i += 2;
              }
              else if ((i == 29)&& i!=0&& breakreached ==false)
              {
              	 sb1.append(c).append(" ");
                  fourthfivebits = sb1.toString().substring((sb1.length() - 6),sb1.length()-1);
                  i += 2;
              } 
              else if (breakreached&& i!=0)
              {
                  sb1.append(c);
                  if ((i == 31)) 
                  {
                	  int dat = Integer.parseInt(sb1.toString().substring(sb1.length()-32,sb1.length()), 2) ;                	  
                      sb1.append(c).append(" ").append(z).append(" ").append(dat).append(System.getProperty("line.separator"));
                	  
                      z += 4;
                      i = 0;
                  }
                  else
                  {
                      i++;
                  }                  
              }
              else
              {              	
                  sb1.append(c);                   
                  i++;
              }              
      }  
          if(Files.exists(Paths.get(args[2])))
          {
        	  //System.out.println(sb1);
        	  BufferedWriter bwr = new BufferedWriter(new FileWriter(args[2]));
        	  bwr.write(sb1.toString());
 	  		 bwr.close();
          }
          else
		  {
        	  System.out.println(sb1);
	  		BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(args[2])));
	  		 bwr.write(sb1.toString());
	  		 bwr.close(); 
		  }       		  
                   
		 }
		 catch (Exception e)
		 {
			 System.out.println(e.getMessage());	
		 }
                
		 }

	private static void opcodeKeyCollectionBuilding(List<String> opcodekey)
	{
			opcodekey.add(0,"SW I 101011 222222");
			opcodekey.add(1,"J N 000010 011110");
			opcodekey.add(2,"BREAK R 000000 001101");			
			opcodekey.add(3,"XOR R 000000 100110");
			opcodekey.add(4,"OR R 000000 100101");
			opcodekey.add(5,"AND R 000000 100100");
			opcodekey.add(6,"SRA R 000000 000011");
			opcodekey.add(7,"SRL R 000000 000010");
			opcodekey.add(8,"SLL R 000000 000000");
			opcodekey.add(9,"SLTU R 000000 101011");
			opcodekey.add(10,"SLT R 000000 101010");
			opcodekey.add(11,"SUB R 000000 100010");
			opcodekey.add(12,"SUBU R 000000 100011");
			opcodekey.add(13,"ADD R 000000 100000");
			opcodekey.add(14,"ADDU R 000000 100001");
			opcodekey.add(15,"NOR R 000000 100111");
			opcodekey.add(16,"NOP R 000000 000000");
			opcodekey.add(17,"SLTI I 001010 222222");
			opcodekey.add(18,"SLTI I 001010 222222");
			opcodekey.add(19,"ADDIU I 001001 222222");
			opcodekey.add(20,"ADDI I 001000 222222");
			opcodekey.add(21,"BGEZ I 000001 222222");
			opcodekey.add(22,"BGTZ I 000111 222222");
			opcodekey.add(23,"BLEZ I 000110 222222");
			opcodekey.add(24,"BLTZ I 000001 222222");
			opcodekey.add(25,"BNE I 000101 222222");          		
			opcodekey.add(26,"BEQ I 000100 222222");
			opcodekey.add(27,"LW I 100011 222222");
	}

	private static void iType(String firstfivebits, String secondfivebits, String thirdfivebits, String fourthfivebits,
			StringBuilder sb1, String Key,String lastsixbits, String opcodebits) 
	{
		if(Integer.parseInt(opcodebits+ firstfivebits+secondfivebits+thirdfivebits+fourthfivebits+lastsixbits, 2) ==0)
		{
			Key = "NOP";
		}
		switch (Key) 
		 {
		     case "ADD":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "ADDU":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "SUB":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "SUBU":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "SLT":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "SLTU":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "SLL":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(secondfivebits, 2) + ("," + ("H" + Integer.parseInt(fourthfivebits, 2)))))))))));
		         break;
		     case "SRL":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(secondfivebits, 2) + ("," + Integer.parseInt(fourthfivebits, 2))))))))));
		         break;
		     case "SRA":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(secondfivebits, 2) + (","+ Integer.parseInt(fourthfivebits, 2))))))))));
		         break;
		     case "AND":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "OR":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "XOR":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "NOR":
		         sb1.append((Key + (" " + ("R" 
		                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
		                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
		         break;
		     case "NOP":
		         sb1.append("NOP");
		         break;
		 }
	}
	
	
	
	private static void rType(String firstfivebits, String secondfivebits, String thirdfivebits, String fourthfivebits,
			String lastsixbits, StringBuilder sb1, String Key)
	{
		String val = thirdfivebits.toString().substring((thirdfivebits.length() - 5),1);
		int immed;
		if( val.equals("1"))
		{
			immed = Integer.parseInt(thirdfivebits 
                  + fourthfivebits + lastsixbits, 2)-65536;
		}
		else
		{
			immed = Integer.parseInt(thirdfivebits 
                  + fourthfivebits + lastsixbits, 2);
		}
		
		
		switch (Key) 
		{
		        case "LW":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " 
		                            + (Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2) + ("(" + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ")")))))))))));
		            break;
		        case "SW":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " 
		                            + (Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2) + ("(" + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ")")))))))))));
		            break;
		        case "ADDI":
		        	
		            sb1.append((Key + " " + "R" 
		                            + Integer.parseInt(secondfivebits, 2) + "," + " " + "R" 
		                            + Integer.parseInt(firstfivebits, 2) + "," + " " + "#" + immed));
		            break;
		        case "ADDIU":
		        	
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("#" +immed ))))))))))));
		            break;
		        case "SLTI":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2)))))))))))));
		            break;
		        case "BEQ":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("R" 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2)))))))))))));
		            break;
		        case "BNE":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("R" 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2)))))))))))));
		            break;
		        case "BGEZ":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2))))))))))));
		            break;
		        case "BGTZ":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2))))))))))));
		            break;
		        case "BLEZ":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2))))))))))));
		            break;
		        case "BLTZ":
		            sb1.append((Key + (" " + ("R" 
		                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
		                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
		                                + (fourthfivebits + lastsixbits)), 2))))))))))));
		            break;
		    }
	}}
              		
 
          
		
	



