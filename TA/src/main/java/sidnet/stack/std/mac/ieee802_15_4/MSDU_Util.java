/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

/**
 *
 * @author Oliver
 */
// ------------ HEADER DEFINITIONS -----------------

// Not in NS-2 to be used with MSDU_Payload
class MSDU_Util
{   
    public static void storeAt(byte[] target, int index, Object obj)
    {
          if (obj instanceof Byte)
          {
              byte byteVal = ((Byte)obj).byteValue();
              target[index] = byteVal;
          }
          if (obj instanceof Short)
          {
              short shortVal = ((Short)obj).shortValue();
              target[index] = (byte)((shortVal << 8) >> 8);
              target[index+1] = (byte)(shortVal >> 8);
          }
          
          if (obj instanceof MACenum)
          {
              MACenum val = (MACenum)obj;
              target[index] = (byte)val.getByteVal();
          }
    }
//    
//    public static Object loadFrom(byte[] source, int index, Class clazz)
//    {
//        if (clazz.getName().equals("MACenum"))
//        {
//            return MACenum.retrieveFor(source[index]);
//        }
//        if (clazz.getName().equals("Short"))
//        {
//            byte lb, ub;
//            short val;
//            ub = source[index];
//            lb = source[index+1];
//            val = (short)lb;
//            val += ((short)ub) << 8;
//            return val;
//        }
//        return null;
//    }
}