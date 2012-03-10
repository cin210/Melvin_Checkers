/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
/**
 *
 * @author Chris
 */
public class Checkers {
    
    static boolean PLAYER_1_RANDOM = false; //set player 1 to only make random moves
    static boolean TOURNAMENT = true;
    static boolean PRINT_GAME = false;  

    static int [] WINS = new int[2];  
    static int [] MOVES = new int[2];  
    static int [] MOVES_WHEN_WON = new int[2];  
    static final int NUMBER_OF_GAMES = 1;  
    static final int MAX_DEPTH = 14; //minimum depth you want move searches to go  
    static final int MAX_MOVES = 150;    
    static int [] EXPERT_TYPE = new int[2];
    public static void main(String[] args) {
        int[][] S = new int [8][8];
        boolean stepThrough = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        // TODO code application logic here
        if(args.length == 0)
            S = initialize();
        else if(args[0].equalsIgnoreCase("pick")){
            stepThrough = true;
            S = pickPieces(input); 
            System.out.println("This is the setup you have specified:");
            printBoard(S);

        } else if(args[0].equalsIgnoreCase("versus")){
            stepThrough = true;
            TOURNAMENT = true;
            S = initialize(); 
            System.out.println("Ready to Play:");
            printBoard(S);

        }
        
        EXPERT_TYPE[0] = 0;
        EXPERT_TYPE[1] = 123456;
        if(PLAYER_1_RANDOM) 
            EXPERT_TYPE[0] = 0;
        for(int numGames = 0; numGames < NUMBER_OF_GAMES; numGames++){ 
                MOVES[0] = 0;
                MOVES[1] = 0; 
        if(NUMBER_OF_GAMES > 1)
            S = initialize();
        int BW = 0; //Game starts as player 0
                    System.out.printf("Player %d moves first \n", BW+1);

        int F = 0; //firstmove - depleted after a player takes a full turn (NOT when they calculate moves)
        if(!stepThrough)
             for(int i = 0; i< MAX_MOVES; i++){  
                MOVES[BW]++;
                S = expand(S, BW, F);
                BW = ((BW + 1) % 2);
                F--;
                if(S == null){ 
                    break;
                } 
            }
        if(stepThrough && !TOURNAMENT){
          System.out.println("Please keep hitting enter to step through moves. Type DONE to exit");
          try {  
                 while(!input.readLine().equals("DONE")){ 
                     S = expand(S, BW, F);
                     BW = ((BW + 1) % 2);
                     F--;
                     if(S == null){
                          System.out.println("Tie Game"); 
                          break;
                     }
                 }
                 }
          catch (IOException ioe) {
                     System.out.println("Something went horribly wrong reading the input!");
                     System.exit(1);
                  }
        }
        else if (stepThrough && TOURNAMENT){ 
          try {  
                    
                 while(!input.readLine().equals("DONE")){ 
                     S = expand(S, BW, F);
                     printBoard(S);
                     BW = ((BW + 1) % 2);
                     F--;
                     if(S == null){
                          System.out.println("Tie Game"); 
                          break;
                     }
                 }
                 }
          catch (IOException ioe) {
                     System.out.println("Something went horribly wrong reading the input!");
                     System.exit(1);
                  }
        }
        }
                System.out.println("series: " + NUMBER_OF_GAMES);
        System.out.println("total wins player 1: " + WINS[0]);
        System.out.println("total wins player 2: " + WINS[1]);
        
        System.out.println("avg wins player 1: " + (WINS[0]/1000.0));
        System.out.println("avg wins player 2: " + (WINS[1]/1000.0));
        
        System.out.println("total moves player 1: " + MOVES[0]);
        System.out.println("total moves player 2: " + MOVES[1]);
        
        
        System.out.println("total moves player 1: " + MOVES_WHEN_WON[0]/max(1,WINS[0]));
        System.out.println("total moves player 2: " + MOVES_WHEN_WON[1]/max(1,WINS[1]));
                MOVES[0] = 0;
                MOVES[1] = 0;
         
    }
    
    public static Move inputMove(BufferedReader s, int[][] S, LinkedList<Move> moves){ 
        Move print_move = new Move();
        for(int i = 0; i < moves.size(); i++){
            print_move = moves.get(i); 
            System.out.printf("Move %d: %d,%d %d,%d \n", i,print_move.getSource()[0],print_move.getSource()[1],print_move.getDestination()[0],print_move.getDestination()[1]  );
            
                    }
        System.out.println("Enter where you want to move (ex: 'sourceY,sourceX destinationX, destinationY'): ");
        String next;
        int BW = 1; 
        Move move = new Move();
        boolean validMove = false;
            try {
                 next = s.readLine();
                 while(!(validMove)){
                    int movenum = next.charAt(0)-48; 
                    if( 0 >  movenum || movenum > moves.size()){
                        System.out.printf("That move isn't an option!!"); 
                    } else {
                    validMove = true;  
                    move = moves.get(movenum);
                    System.out.printf("Picked move %d  \n", movenum); 
                    return move
                            ;
                    }
                 next = s.readLine();
            } 
                  } catch (IOException ioe) {
                     System.out.println("Something went horribly wrong reading the coordinates!");
                     System.exit(1);
                  }
            
            
        
        
        return move;
    }
    public static int[][] pickPieces(BufferedReader s){ 
        int[][]S = new int[8][8];
        String next;

        for(int BW = 1; BW<3; BW++){
        System.out.println("You have selected to input your own board. Please enter pairs of coordinates for player" + BW);
        
       
            System.out.printf("Enter pairs of coordinates in the form 'A,B'. Ex: 0,0 is top left, or type DONE \n", BW);
            try {
                 next = s.readLine();
                 while(!(next).equals("DONE")){
                    int a = next.charAt(0)-48;
                    int b = next.charAt(2)-48; 
                    if((a+b)%2 == 0){
                        System.out.printf("Make sure your coordinates are valid!!"); 
                    }
                    else{
                    System.out.printf("Added piece for player %d at %d, %d. ", BW, a, b);
                    S[a][b] = BW;
                    System.out.printf("Add another Checker for Player %d or type DONE \n", BW);
                    }
                 next = s.readLine();
            } 
                  } catch (IOException ioe) {
                     System.out.println("Something went horribly wrong reading the coordinates!");
                     System.exit(1);
                  }
            
            
        }
        
        return S;
    }
    public static void printBoard(int[][]S){
        
        for(int j = 0; j<8; j++){
            for(int i = 0; i<8; i++)
               System.out.print(S[j][i]+ " ");
        System.out.println();
        
        
    }
}
     
    public static LinkedList<Move> getMoves(LinkedList<Move> moves, int[][]S, int RW, int jump){
        //System.out.println("getting moves for player " + (RW+1));
        if(RW==0)
            for(int a = 0; a<8; a++){
                for(int b = 0; b<8; b++){
                    if((a + b) % 2 == 1){
                        if((S[a][b] == 1 + RW) || (S[a][b] == 3 + RW)){ 
                           moves = exploitMoves(S, a, b, jump, moves, RW);
                        }
                    }
                }
            }
        else
            for(int a = 7; a>=0; a--){
               for(int b = 7; b>=0; b--){
                   if((a + b) % 2 == 1){
                       if((S[a][b] == 1 + RW) || (S[a][b] == 3 + RW)){ 
                           moves = exploitMoves(S, a, b, jump, moves, RW);
                    }
                }
            }
        }
        return moves;
    }
    public static int[][] expand(int[][] S, int RW, int firstmove){
        int [] destination; // initialize empty coordinates for the coming move
        int [] source;//initialize empty coordinates where the piece that will move is
        LinkedList<Move> moves = new LinkedList<Move>();
        Move move;
       // System.out.println("It is Player " + (RW+1) + "'s turn");
        int jump = 0;
        
        int[][]testBoard = new int[8][8];
         for(int i = 0; i<8; i++)
             for(int j = 0; j < 8; j++)
                 testBoard[i][j] = S[i][j]; 
         
        moves = getMoves(moves, testBoard, RW, jump);
        
            
        if(jump==0){
            if(!(moves.isEmpty())){
                //System.out.print("\n");  

                if(PRINT_GAME) 
                    System.out.printf("Player %d can make %d different moves. \n", (RW+1), moves.size() );  
                if(firstmove > 0 || (RW == 0 && PLAYER_1_RANDOM) ){
                   
                    Random generator = new Random();
                    move = moves.get(generator.nextInt(moves.size())); 
                }else if (TOURNAMENT && RW ==0){
                     LinkedList jumpMoves = onlyJumpMoves(moves);
                    if((jumpMoves.size()>0))
                        moves = jumpMoves;
                    move = inputMove( (new BufferedReader(new InputStreamReader(System.in))) , S,  moves);
                    
                }
                else 
                    move = evalMoves(moves, RW, MAX_DEPTH);  
                destination = move.getDestination();
                source = move.getSource();
                if(PRINT_GAME) 
                    System.out.printf("Player %d moves from %d, %d to %d, %d \n",(RW+1), source[0], source[1], destination[0], destination[1]);
                S = move.getS(); 
                
                while(move.combo_move != null){ 
                     move = move.combo_move;
                     destination = move.getDestination();
                     source = move.getSource();
                     S = move.getS(); 
                     if(PRINT_GAME) System.out.printf("Player %d jumps again from %d, %d to %d, %d \n",(RW+1), source[0], source[1], destination[0], destination[1]);
                }
                if(PRINT_GAME) printBoard(S);
                
                
                
                firstmove++;
            }
            else{ 
                
                //printBoard(S);
                S = null;
                System.out.println("Player " + ((1-RW)+1) + " wins!!!! in "+  MOVES[1-RW] +" moves \n"); 
                WINS[1-RW]++;  
                MOVES_WHEN_WON[1-RW] += MOVES[1-RW];
                MOVES[0] = 0;
                MOVES[1] = 0;
            }
        }
        return S;
        
    }
    
    public static LinkedList onlyJumpMoves(LinkedList moves){
        LinkedList jumpMoves = new LinkedList<Move>();
        Move move = (Move) moves.getFirst();
        for(int i =0; i<moves.size(); i++){
            move =(Move)moves.get(i);
            if(move.getJump() > 0)
                jumpMoves.add(move); 
        }
        return jumpMoves;
    }
    
    //Returns the move you should choose next based on the previous 
    public static Move evalMoves(LinkedList<Move> moves, int RW, int depth){  
        Move move = new Move();
        LinkedList onlyJumps = onlyJumpMoves(moves);  
        double alpha = -99999;
        double beta  =  99999;
        
        if(!onlyJumps.isEmpty()){ 
          //  if(PRINT_GAME) 
          //      System.out.println("because of forced jumps, Player " +(RW + 1)+" can make " +onlyJumps.size() + " moves.");
            moves = onlyJumps;  
        }
//        if(PRINT_GAME)
//            for(int i = 0; i< moves.size(); i++){
//                 System.out.println("Move "  + i + " from " + moves.get(i).getSource()[0] + "," + moves.get(i).getSource()[1] + " to " + moves.get(i).getDestination()[0] + "," + moves.get(i).getDestination()[1] + " has a utility of " + (moves.get(i)).getUtility());
//                 if( moves.get(i).dangerous) 
//                    System.out.println("this move is dangerous.");  
//            }
        if(moves.size() == 1)
            move = moves.getFirst();
        else{
        double v = maxValue(moves, RW, depth, alpha, beta);
         // if(PRINT_GAME) System.out.println("v is: " + v);
//          if(PRINT_GAME)
//            for(int i = 0; i< moves.size(); i++){
//                 //System.out.println("Move "  + i + " from " + moves.get(i).getSource()[0] + "," + moves.get(i).getSource()[1] + " to " + moves.get(i).getDestination()[0] + "," + moves.get(i).getDestination()[1] + " has a utility of " + (moves.get(i)).getUtility());
//                 if( moves.get(i).dangerous) 
//                    System.out.print(" (this move is dangerous.)");  
//            }
        for(int i = 0; i < moves.size(); i++){ 
            Move tempMove = (Move)moves.get(i);
            if(tempMove.getUtility() == v){ 
                move = tempMove; //take the first move in the list that has the maximum v value, assume its the move you want 
                break;
            }
        }
        }
       //if(PRINT_GAME) 
       //     System.out.printf("chose move with utility of %f \n", move.getUtility());
        
        return move;
    }
    public static double min(double a, double b){
        if(a > b)
            return b;
        else 
            return a;
    }
    public static double max(double a, double b){
        if(a > b)
            return a;
        else 
            return b;
    }
    //original depth search functions
    public static double maxValue(LinkedList moves, int RW, int depth){ 
          LinkedList<Move> successor_moves = new LinkedList<Move>(); 
          if(moves.size() == 1)
                return ((Move)(moves.getFirst())).evalUtility(); 
           
        double v = -99999; 
        
        for(int i = 0; i <  moves.size(); i++){ 
            Move tempMove = (Move) moves.get(i); 
            if(depth>0 || isDangerous(tempMove, RW)){ 
                successor_moves = getMoves( successor_moves, tempMove.getS(), (1-RW), 0);
                if(!successor_moves.isEmpty())
                    tempMove.setUtility(max(v,minValue(successor_moves, (RW+1)%2, (depth-1))));
            }
            else
                return tempMove.getUtility();
            if(tempMove.getUtility() > v)
                v = tempMove.getUtility();
        } 
        return v;
    }
    public static double minValue(LinkedList moves, int RW, int depth){ 
        LinkedList<Move> successor_moves = new LinkedList<Move>();
          if(moves.size() == 1)
                ((Move)(moves.getFirst())).getUtility(); 
        double v = 999999;
        for(int i = 0; i <  moves.size(); i++){ 
            Move tempMove = (Move) moves.get(i);
            if(depth>0 || isDangerous(tempMove, RW)){ 
                 successor_moves = getMoves( successor_moves, tempMove.getS(), (RW+1)%2, 0);
                 if(!successor_moves.isEmpty())
                    tempMove.setUtility(min(v,maxValue(successor_moves, (RW+1)%2,(depth-1)))); 
            }        
            else
                return tempMove.getUtility();
            if(tempMove.getUtility() < v)
                v = tempMove.getUtility();
        }
         return v;
    }
    //5-argument Alpha-beta functions
    public static double maxValue(LinkedList moves, int RW, int depth, double alpha, double beta){  
           if(moves.size() == 1 && !((Move)moves.getFirst()).dangerous){ 
                //if(PRINT_GAME) System.out.println("state: v = " + ((Move)(moves.getFirst())).getUtility() + " depth: "+ depth + " alpha: + " + alpha + " beta: "+ beta);
                return ((Move)(moves.getFirst())).getUtility();   
            }
        LinkedList<Move> successor_moves = new LinkedList<Move>();   
        double v = -99999;  
        
        for(int i = 0; i <  moves.size(); i++){ 
            Move tempMove = (Move) moves.get(i);    
            if(depth > 1 || tempMove.dangerous){ 
                successor_moves = getMoves( successor_moves, tempMove.getS(), (1-RW), 0);
                if(!successor_moves.isEmpty()){
                    LinkedList successor_moves_jumps = onlyJumpMoves(successor_moves);
                    if( (successor_moves_jumps).size() > 0)
                        successor_moves = successor_moves_jumps; 
                    double min = -1 * minValue(successor_moves, (1-RW), (depth-1), alpha, beta);
                    if(v < min) {
                        v = min;
                        tempMove.setUtility(v);
                    } 
                    else
                        tempMove.setUtility(min); 
                        

                }
            }
            else  
                 v =  tempMove.getUtility() ;
            if(v >= beta) 
                return v;  
            alpha = max(v, alpha); 
            
        } 
        return alpha;
    }
    public static double minValue(LinkedList moves, int RW, int depth, double alpha, double beta){  
         if(moves.size() == 1 && !((Move)moves.getFirst()).dangerous){ 
            //if(PRINT_GAME) System.out.println("state: v = " + ((Move)(moves.getFirst())).getUtility() + " depth: "+ depth + " alpha: + " + alpha + " beta: "+ beta);
            return ((Move)(moves.getFirst())).getUtility();   
        }
//        if(moves.size() == 1 || (depth <= 0 && !((Move)moves.getFirst()).dangerous))
//                return ((Move)(moves.getFirst())).getUtility();
//        
        LinkedList<Move> successor_moves = new LinkedList<Move>();  
        double v = 99999;
        
        for(int i = 0; i <  moves.size(); i++){ 
            Move tempMove = (Move) moves.get(i);   
            if(depth> 1 || tempMove.dangerous){ 
                 successor_moves = getMoves( successor_moves, tempMove.getS(), (1-RW), 0);
                 if(!successor_moves.isEmpty()){
                    LinkedList successor_moves_jumps = onlyJumpMoves(successor_moves); 
                    if( (successor_moves_jumps).size() > 0)
                        successor_moves = successor_moves_jumps; 
                    double max = -1* maxValue(successor_moves,(1-RW), (depth-1), alpha, beta);
                    if(v > max) {
                        v = max;
                        tempMove.setUtility(v);
                    } 
                    else
                        tempMove.setUtility(max); 

                 }
            } 
            else  
                 v =  tempMove.getUtility();
            if(v <= alpha) 
                 return v; 
            beta = min(beta,v);
            } 
         return beta;
    }  
    //  Test if the moving piece is putting itself in a dangerous position 
    //     aka it will be captured next turn
    //     or if it is putting a friend at risk by moving
    public static boolean isDangerous(Move move, int RW){
        int [] location = move.getDestination(); 
        int [] source = move.getSource();
        int [][] SS = move.getS();
        int [][] neighbor = new int[4][2]; 
        int [][] oldneighbor = new int[4][2];
        
        
        boolean isDangerous = false;
        
        for(int FB =0; FB<2; FB++)
            for(int d = 0; d<2; d++)
                neighbor[2*FB + d] = Checkers.neighbor(location[0], location[1], d, RW, FB);
        
         for(int FB =0; FB<2; FB++)
            for(int d = 0; d<2; d++)
                oldneighbor[2*FB + d] = Checkers.neighbor(source[0], source[1], d, RW, FB);
        
        for(int n = 0; n<4; n++) {
            if(neighbor[n][0] != -1 && neighbor[(n+2)%4][0] != -1){
               // System.out.print(n);
            if((SS[neighbor[n][0]][neighbor[n][1]] == ((1-RW)+3) || SS[neighbor[n][0]][neighbor[n][1]] == ((1-RW)+1)) && SS[neighbor[(n+2)%4][0]][neighbor[Math.abs((n+2)%4)][1]] == 0){
                //if(PRINT_GAME) System.out.printf("stranger danger! you have moved to %d,%d but there is an enemy at %d,%d and noone at %d,%d.  n = %d \n",location[0], location[1],neighbor[n][0], neighbor[n][1],neighbor[(n+2)%4][0],neighbor[(n+2)%4][1], n );
                isDangerous =  true;
                        }
           
            }
        }
         for(int n = 0; n<0; n++) {
             
            if(oldneighbor[n][0] != -1 && oldneighbor[(n+2)%4][0] != -1){ 
              if(SS[oldneighbor[n][0]][oldneighbor[n][1]] == (RW+3) || SS[oldneighbor[n][0]][oldneighbor[n][1]] == (RW+1)) {
                int [][] oldneighborneighbor = new int[4][2];
                for(int FB =0; FB<2; FB++)
                   for(int d = 0; d<2; d++)
                     oldneighborneighbor[2*FB + d] = Checkers.neighbor(oldneighbor[n][0], oldneighbor[n][1], d, RW, FB);
        
                if((SS[oldneighborneighbor[(n+2)%4][0]][oldneighborneighbor[Math.abs((n+2)%4)][1]] == (1-RW)+1)||(SS[oldneighborneighbor[(n+2)%4][0]][oldneighborneighbor[Math.abs((n+2)%4)][1]] == (1-RW)+3)){
                //  System.out.printf("you left a friend in danger! you have moved to %d,%d but there is an enemy at %d,%d and noone at %d,%d.  n = %d \n",location[0], location[1],oldneighbor[n][0], oldneighbor[n][1],oldneighbor[(n+2)%4][0],oldneighbor[(n+2)%4][1], n );
                 isDangerous = true;
                           }
              }
           }
        }
         
        return isDangerous;
    }
    
    public static int [] neighbor(int a, int b, int d, int RW, int FB){
        int [] neighbor = new int [2];
        neighbor[0] = -1;
        neighbor[1] = -1;
        int neighborColor = (RW + FB ) % 2; 
        if (d == 0){
            if(neighborColor > 0){
                if (((a+1) <= 7) && ((b+1) <= 7)){  
                    neighbor[0] = a+1;
                    neighbor[1] = b+1;
                    return neighbor;
                }
                else return neighbor;
            }else if (((a-1) >= 0) && ((b-1) >= 0)){  
                    neighbor[0] = a-1;
                    neighbor[1] = b-1;
                    return neighbor;
                }
                else return neighbor;
        }
        else if (d == 1){
            if(neighborColor > 0){
                if (((a+1) <= 7) && ((b-1) >= 0)){  
                    neighbor[0] = a+1;
                    neighbor[1] = b-1;
                    return neighbor;
                }
                else return neighbor;
            }else if (((a-1) >= 0) && ((b+1) <= 7)){  
                    neighbor[0] = a-1;
                    neighbor[1] = b+1;
                    return neighbor;
                }
                else return neighbor;
        }
        return neighbor;
            
       
    }
    public static LinkedList<Move> listUpdate(int[][] S, int a, int b, int aaa, int bbb, LinkedList<Move> moves, int RW, int jump, boolean promo){
        Move move =  new Move(S, a, b, aaa, bbb, RW, jump, promo);  
        move.dangerous = isDangerous(move, RW); 
        if(moves.isEmpty())
            moves.add(move);
        else
            moves.addLast(move);
        return moves;
    }    
    
    public static LinkedList<Move> checkMove(int [][]S, int a, int b, int aa, int bb, int d, int jump, LinkedList moves, int RW, int FB){
        int [][] SS;
        boolean promotion = false;
        int [] neighbor = new int [2]; //holds coordinates of neighboring destination
        if ((S[aa][bb]==0) && jump==0){  /* empty spot to move and no Jump occurred*/
            SS = simulateMove(S,a,b,aa,bb,RW);  
            moves = listUpdate(SS,a,b,aa,bb,moves,RW,jump,promotion);
        }
        else if ( (S[aa][bb]== 2-RW) || (S[aa][bb]==4-RW) ){
          neighbor = neighbor(aa,bb, d, RW, FB); 
          if ((neighbor[0] != -1) && (S[neighbor[0]][neighbor[1]]==0) ){// neighbor empty & inside the board             
             if (jump == 0){// jump is obliged
               //  moves.clear(); //reinitialize list-of-moves
             } 
                 jump += 1; 
                 moves = makeJump(S,a,b,aa,bb,neighbor[0],neighbor[1], jump, moves, RW, FB); 
         }
        }
        return moves;
    }  
            
     public static LinkedList<Move> makeJump(int[][] S, int  a, int b, int aa, int bb, int aaa, int bbb, int jump, LinkedList moves, int RW, int FB){
         int [][] SS = new int[8][8];
         boolean promotion = false;
         for(int i = 0; i<8; i++)
             for(int j = 0; j < 8; j++)
                 SS[i][j] = S[i][j];  
         
         int piece = S[a][b] ;
         SS[a][b] = 0; // empty where the piece is
         SS[aa][bb] = 0; //empty where the piece thats getting jumped is
         if((aaa == (7*(1-RW)) ) && (piece == 1+RW)){
                 promotion = true; //a piece became a king
                 SS[aaa][bbb] = 3 + RW; 
          }
         else 
              SS[aaa][bbb] = piece; //The new piece goes here. its not a king.
         moves = listUpdate(SS,a,b,aaa,bbb,moves, RW, jump, promotion);
         if(!promotion){ //if it didnt become a king, check if it can jump again
              LinkedList continue_jump_moves = new LinkedList<Move>();
              int depth = 0; //you are just extending one move into multiple jumps, not analyzing multiple back and forth plays
              continue_jump_moves = exploitMoves(SS, aaa, bbb, jump, continue_jump_moves, RW);
              if(!continue_jump_moves.isEmpty())
                  if(RW == 1 || (RW == 0 && !PLAYER_1_RANDOM)){ 
                      Move parent_move = (Move)moves.getLast(); //the parent will have been the last to have been added to moves by listUpdate 5 lines ago ^
                      parent_move.combo_move = evalMoves(continue_jump_moves, RW, depth);  
                      parent_move.setUtility(parent_move.getUtility() + parent_move.combo_move.getUtility());
                            
                 } else{
                      Random generator = new Random();
                      Move tempMove = (Move)(continue_jump_moves.get(generator.nextInt(continue_jump_moves.size())));
                      Move parent_move = (Move)moves.getLast();
                      parent_move.combo_move = tempMove;  
                      parent_move.setUtility(parent_move.getUtility() + tempMove.getUtility());
                  }
         }
         return moves;
     }       
     
    public static int [][] simulateMove(int [][] S, int a, int b, int aa, int bb, int RW){
         int [][] SS = new int[8][8];
         for(int i = 0; i<8; i++)
             for(int j = 0; j < 8; j++)
                 SS[i][j] = S[i][j]; 
           
         int temp = S[a][b];
         SS[a][b] = 0;
         
         if ((aa ==(7*(1-RW)))&&( temp == 1+RW))
             SS[aa][bb] = 3 + RW; 
         else
             SS[aa][bb] = temp;
         
         return SS;
    }
    
    public static LinkedList<Move> exploitMoves(int[][] S, int a, int b, int jump, LinkedList moves, int RW){
        int [] n = new int [2];
        int FB; 
        if(S[a][b] == 3 + RW){ //check backward king moves  
                 FB = 0; 
                 for(int d = 0; d<2; d++){
                     n = neighbor(a, b, d, RW, FB);
                     if(n[0] != -1)
                        moves = checkMove(S, a, b, n[0], n[1], d, jump, moves, RW, FB); 
                 }
             
            }
        if((S[a][b] == 1 + RW )||(S[a][b] == 3 + RW)){  
                 FB = 1; 
                 for(int d = 0; d<2; d++){
                     n = neighbor(a, b, d, RW, FB);
                     if(n[0] != -1)
                        moves = checkMove(S, a, b, n[0], n[1], d, jump, moves, RW, FB); 
                 }
             
            }
        
        return moves;
    }
    
    public static int[][] initialize(){
        int[][] board = new int[8][8];
        for(int j = 0; j<8; j++){
            for(int i = 0; i <3; i++){
                if((i+j)% 2 == 1)
                    board[i][j] = 1;
                
            }
            for(int i = 5; i <8; i++){
                if((i+j)% 2 == 1)
                    board[i][j] = 2;
            }
        }
        return board;
}
    
}
