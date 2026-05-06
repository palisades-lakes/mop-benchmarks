package mop.java.functions;

/** Base class for real valued functions.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-07
 */

public abstract class Functional implements Function {

  //--------------------------------------------------------------
  // Function methods
  //--------------------------------------------------------------

  @Override
  public final Domain codomain () { return Dn.D1; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  public Functional () { }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

