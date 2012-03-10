/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

/**
 *
 * @author Chris
 */
public class Move {
    private int a;
    private int b;
    private int aaa;
    private int bbb;
    private int [][]S;
    private double utility;
    private int jump;
    private int EXPERT_TYPE;
    public boolean wasEvaluated = false;
    
    public boolean dangerous;
    public int piece_difference;
    public boolean promotion;
    
    private int RW; // the player who made this move
    public Move combo_move = null;  
    public Move(){
        
    }
    public Move(int[][]S, int a, int b, int aaa, int bbb, int RW, int jump, boolean promotion){ 
        this.S = new int [8][8]; //the state of the board (once this move has been made)
        this.S = S.clone();
        this.a = a; //the source is a and b
        this.b = b;
        this.aaa = aaa; //  the destination is aaa bbb
        this.bbb = bbb;
        this.jump = jump;  //is this move a jump, or part of a combo jump
        this.RW = RW; 
        this.EXPERT_TYPE = Checkers.EXPERT_TYPE[RW];
        this.utility = -99998;
        this.promotion = promotion;
        
        this.wasEvaluated = false;
    }
    
    /* Experts: 
            1. number of pieces
            2. number of kings
     *      3. Vertical positioning *in enemy territory?
     *      4. Defensive positioning (on the sides)
     *      5. Aggressiveness (takes a piece?)
     *      6. Defensiveness (minimize number of enemy neighbors + maximize friendly neighbors)
     *      
     * 
     * 
     * */
     
    
    
    
    
    public double evalUtility(){  
        int [][] SS = this.getS(); 
        double u = 0;
        int total_kings_us =0, total_kings_them=0, total_pieces_us=0, total_pieces_them=0, a_average_us=0,  b_average_us=0,  a_average_them=0,  b_average_them =0;
        if(this.EXPERT_TYPE!=0){
        for(int i = 0; i<8 ; i++)
            for(int j = 0; j<8; j++){ 
                if(SS[i][j] == RW + 3){
                    total_kings_us++;
                    total_pieces_us++;
                    a_average_us +=i;
                    b_average_us+=j; 
                }
                if(SS[i][j] == (1-RW) + 3){ 
                    total_kings_them++;
                    total_pieces_them++;
                    a_average_them +=i;
                    b_average_them+=j;
                }
                if(SS[i][j] == RW+1){ 
                    total_pieces_us++;
                    a_average_us +=i;
                    b_average_us+=j;
                }
                if(SS[i][j] == (1-RW)+1){  
                    total_pieces_them++;
                    a_average_them+=i;
                    b_average_them+=j;
                }
               }
         this.piece_difference = total_pieces_us - total_pieces_them; 
         }
        u = 0;
        double v = 0;
        double alpha = 0;

        switch(this.EXPERT_TYPE){
                    /*expert 0 is dumb. all his moves are equally bad. */ 
                      case 0: break; 
                    /* Greedy player */
                      case 1: u = M1(); break; 
                    /* El Presidente */
                      case 2: u = M2(total_kings_us - total_kings_them);   break; 
                     
                      case 12: u = M12(total_kings_us - total_kings_them);  break;  
                    /* sidewinder */
                      case 3:  u = M3(); break; 
                  //If this move is going onto a side, it is a good move.
                      case 4:  u = M4(); break; 
                          
                      case 34: u = M34(); break;
                          
                      case 1234: alpha = .4; u = M34() + alpha * M12(total_kings_us - total_kings_them) ;  break;
                      /* warlord */
                      case 5:  u = M5();  break; 
                      /* stay out of trouble*/
                      case 6:  u = M6();  break;
                      case 56:  alpha = .7; u = M5() + (alpha * M6());  break;
                          
                      case 123456:
                          alpha = .4; u = (alpha * M34()) + M12(total_kings_us - total_kings_them) ; //1234
                          alpha = .7; v = M5() + (alpha * M6()); //56
                          alpha = .9;
                          u =  (alpha * u)  + (v);
                          return u;
                          
      /*expert 1 */   case 7: 
                        if(this.promotion)
                            u+=10; 
                        u += jump*jump;
                       // u = u + (total_pieces_us - total_pieces_them); 
                //        
                        u = u + (total_kings_us - total_kings_them);  
                //        a_average_them /= total_pieces_them;
                //        b_average_them /= total_pieces_them;
                //        a_average_us /= total_pieces_us;
                //        b_average_us /= total_pieces_us;
                        
                        // if(((aaa == 8) && (RW==1)) || ((aaa == 0) && (RW == 0)))
                        //     u+=2; //became a god
                         if((bbb)== 8 || (bbb)==0)
                             if(SS[aaa][bbb] == RW +3)
                                 u+=3; //on the left/right, even better if youre a king
                             else
                                 u+=1;
                        if(total_pieces_them < 1)
                            u+=1000 ; //the killing blow 
                        if(total_pieces_us < 1)
                            u-=1000; //avoid this at all costs       
                        break;
        }
        if(total_pieces_them < 1)
              u+=1000 ; //the killing blow 
         if(total_pieces_us < 1)
              u-=1000; //avoid this at all costs     
       return u;
    }
    public double M1(){
         double u = 5 * this.piece_difference; 
         return u;
    }
    public double M2(int difference_in_kings){
        double u = 0;
        u = difference_in_kings; 
        if(this.promotion)
                       u+=5;  
        return u;
    }
    public double M3(){
        double u = 0;
        if((this.bbb)== 8 || (this.bbb)==0)
             if(this.getS()[aaa][bbb] == RW + 3)
                 u+=7; //sides is better if youre a king
             else
                 u+=3; 
        return u;  
        
    }
    public double M4(){
         double u = 0;
         if((this.aaa)== 8 || (this.aaa)==0)
             if(this.getS()[aaa][bbb] == RW + 3)
                 u+=5; //corner is better if youre a king
             else
                 u+=3; 
          return u;
    }
    public double M5(){
        double u = 0;
        u = this.jump * this.jump;
        return u;
    }
    public double M6(){
        double u = 0;
        if(this.dangerous) 
               u =  -15 + this.piece_difference; 
         else 
               u =   this.piece_difference;
        return u;
    }
    public double M12(int king_difference){
        double u = 0;
        double alpha = .5;
        u = M1() + (alpha * M2(king_difference)); 
        return u;
 
    }
    public double M34(){
        double u = 0;
        double alpha = .8;
         u = (alpha * M3()) + M4(); 
         return u;
    }
    public void setUtility(double u){
        this.wasEvaluated = true;
        this.utility = u;
    }
    public int getJump(){
        return this.jump;
    }
    public double getUtility(){
        if(this.wasEvaluated == false){
            this.wasEvaluated = true;
            this.utility = evalUtility();
        }
        return this.utility;
    }
    public int [][] getS(){
        return this.S;
    }
    public int [] getSource(){
        int [] source = new int [2];
        source[0] = this.a;
        source[1] = this.b;
        return source;
    }
     public int [] getDestination(){
        int [] destination = new int [2];
        destination[0] = this.aaa;
        destination[1] = this.bbb;
        return destination;
    }
}
