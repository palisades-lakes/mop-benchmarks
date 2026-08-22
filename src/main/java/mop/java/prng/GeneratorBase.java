package mop.java.prng;

/** Base class for generators, providing <code>name</code> method.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */


public abstract class GeneratorBase implements Generator {

  private final String _name;
  @Override
  public final String name () { return _name; }

  public GeneratorBase (final String name) {
    super(); _name = name;  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

