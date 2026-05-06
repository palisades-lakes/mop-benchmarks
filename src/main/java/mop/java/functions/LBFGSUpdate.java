package mop.java.functions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.carrotsearch.hppc.DoubleArrayDeque;
import com.carrotsearch.hppc.DoubleDeque;
import com.carrotsearch.hppc.cursors.DoubleCursor;

/** A linear function that maps a gradient to the L-BFGS search
 * direction.
 * <p>
 * This is an integrated implementation,
 * maintaining queues of past changes in position and gradient,
 * and using those in the algorithm from Nocedal-Wright 2nd ed.
 * algorithm 7.4, p 178.
 * <p>
 * <b>WARNING:</b> !!!mutable!!! !!!Not thread safe!!!
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-07
 */

public final class LBFGSUpdate implements Function {

  /** (Co)Domain. */
  private final Dn _domain;

  /** Maximum history length. */
  private final int _maxHistory;

  // Note: dx, dg and dxdg need to be updated together.

  /** Changes in sample position. */
  private final Deque<Vektor> _dx;

  /** Changes in gradient. */
  private final Deque<Vektor> _dg;

  /** cache dot products between corresponding gradient and
   * position change vectors.
   */

  private final DoubleDeque _dxdg;

  private final double[] _tmp;

  public double _scale;

  /** Current history length. */
  private final int history () { return _dg.size(); }

  //--------------------------------------------------------------
  // methods
  //--------------------------------------------------------------

  public final boolean isFull () {
    return history() >= domain().dimension(); }

  public final void clear () {
    _dg.clear(); _dx.clear(); _dxdg.clear(); _scale = 1.0; }

  public final void update (final Vektor dxi,
                            final Vektor dgi,
                            // dot product often already computed
                            final double dxdgi) {
    assert history() <= _maxHistory;
    if (history() == _maxHistory) {
      _dx.removeFirst();
      _dg.removeFirst();
      _dxdg.removeFirst(); }

    _dx.addLast(dxi);
    _dg.addLast(dgi);
    _dxdg.addLast(dxdgi);

    final double dg2last = _dg.getLast().l2norm2();
    // dg2last == 0 implies no change in gradient?
    if (dg2last > 0.0) { _scale = _dxdg.getLast()/dg2last; } }

  //--------------------------------------------------------------
  // Function methods
  //--------------------------------------------------------------

  @Override
  public final Domain domain() { return _domain; }

  @Override
  public final Domain codomain () { return _domain; }

  // Note: should not change state of this Function
  // changes tmp array, which could be allocated here...
  @Override
  public final Vektor value (final Vektor g) {
    final int m = history();
    if (0 == m) { return g; }

    final double[] dc = g.coordinates();
    final Iterator<Vektor> skDown = _dx.descendingIterator();
    final Iterator<Vektor> ykDown = _dg.descendingIterator();
    final Iterator<DoubleCursor> skykDown =
      _dxdg.descendingIterator();
    for (int j=m-1;j>=0;j--) {
      final Vektor skj = skDown.next();
      final Vektor ykj = ykDown.next();
      final double skykj = skykDown.next().value;
      final double t = skj.dot(dc) / skykj;
      _tmp[j] = t;
      ykj.axpy(-t,dc); }

    Doubles.scale1(-_scale,dc);

    final Iterator<Vektor> skUp = _dx.iterator();
    final Iterator<Vektor> ykUp = _dg.iterator();
    final Iterator<DoubleCursor> skykUp = _dxdg.iterator();
    for (int j=0;j<m;j++) {
      final Vektor skj = skUp.next();
      final Vektor ykj = ykUp.next();
      final double skykj = skykUp.next().value;
      final double tauj = _tmp[j];
      final double t = ykj.dot(dc) / skykj;
      skj.axpy(-t-tauj,dc); }

    return Vektor.make(dc); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private LBFGSUpdate (final int dim,
                       final int mem) {
    this._domain = Dn.get(dim);
    this._maxHistory = mem;
    this._dg = new ArrayDeque<>(mem);
    this._dx = new ArrayDeque<>(mem);
    this._dxdg = new DoubleArrayDeque(mem);
    this._scale = 1.0;
    this._tmp = new double[mem]; }

  //--------------------------------------------------------------

  public static final LBFGSUpdate make (final int dim,
                                        final int mem) {
    assert (dim >= mem) :
      "Memory (" + mem + ") > dim (" + dim +")";
    //assert (mem >= 3) : "Memory (" + mem + ") must be >=3";
    return new LBFGSUpdate(dim,mem); }

  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
