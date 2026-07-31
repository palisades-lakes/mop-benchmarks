package mop.java.scripts.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.BigFloat;
import mop.java.prng.Generator;

/** <pre>
 * jy --source 12 src/scripts/java/xfp/java/scripts/BFProfile.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-06-19
 */
@SuppressWarnings("unused")
public final class BFProfile {

  public static final void main (final String[] args) {
    final int dim = (64*1024*1024) + 1;
    final int trys = 128;
    for (final Generator g : Common.generators(dim)) {
      System.out.println();
      System.out.println(g.name());
      final double[] x0 = (double[]) g.next();
      final double[] x1 = (double[]) g.next();
      final long t = System.nanoTime();
      for (int i=0;i<trys;i++) {
        BigFloat a = BigFloat.valueOf(0L);
        for (int j=0;j<dim;j++) { a = a.addProduct(x0[j],x1[j]); }
        a = BigFloat.valueOf(0L);
        for (int j=0;j<dim;j++) { a = a.add2(x0[j]).add2(x1[j]); }
        a = BigFloat.valueOf(0L);
        for (int j=0;j<dim;j++) { a = a.addL1(x0[j],x1[j]); }
        a = BigFloat.valueOf(0L);
        for (int j=0;j<dim;j++) { a = a.addL2(x0[j],x1[j]); }
        a = BigFloat.valueOf(0L);
        for (int j=0;j<dim;j++) { a = a.add(x0[j]).add(x1[j]); }
        final double z0 = a.doubleValue();
        if (0.0 != z0) {
          System.out.println(Double.toHexString(0.0)
            + " != " + Double.toHexString(z0)
            //+ "\n" + Double.toHexString(z2)
            ); }
      }
      System.out.printf("total secs: %8.2f\n",
                        ( System.nanoTime()-t)*1.0e-9); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
