/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

/**
 *
 * @author Oliver
 */
public class PacketStamp 
{
  //PacketStamp() : ant(0), node(0), Pr(-1), lambda(-1) { }

  void init(PacketStamp s) {
	  /*Antenna ant;
	  if (s.ant != null)
		  ant = s.ant.copy();
	  else
		  ant = 0;
	  
	  //Antenna *ant = (s->ant) ? s->ant->copy(): 0;
	  stamp(s.node, ant, s.Pr, s.lambda);*/
  }

  void stamp(/*MobileNode n, Antenna a,*/ double xmitPr, double lam) {
    //ant = a;
    //node = n;
    Pr = xmitPr;
    lambda = lam;
  }

  //inline Antenna  getAntenna() {return ant;}
  //inline MobileNode * getNode() {return node;}
  double getTxPr() {return Pr;}
  double getLambda() {return lambda;}

  /* WILD HACK: The following two variables are a wild hack.
     They will go away in the next release...
     They're used by the mac-802_11 object to determine
     capture.  This will be moved into the net-if family of 
     objects in the future. */
  double RxPr;			// power with which pkt is received
  double CPThresh;		// capture threshold for recving interface

  //protected Antenna       *ant;
  //protected MobileNode	*node;
  protected double        Pr;		// power pkt sent with
  protected double        lambda;         // wavelength of signal
};
