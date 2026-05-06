# nzqr-jmh
# palisades dot lakes at gmail dot com
# version 2019-05-02
#-----------------------------------------------------------------
if (file.exists('e:/porta/projects/nzqr-jmh')) {
  setwd('e:/porta/projects/nzqr-jmh')
  model <- '20ERCTO1WW' # P70
} else {
  setwd('c:/porta/projects/nzqr-jmh')
  model <- '20HRCTO1WW' # X1
}
source('src/scripts/r/functions.r')
#-----------------------------------------------------------------
#prefix <- 'Sums-20190410-213030'
#prefix <- 'Sums-20190414-144041'
#prefix <- 'PartialSums-20190430-212132'
prefix <- 'PartialSums-20190501-200322'
#-----------------------------------------------------------------
runtime <- read.runtimes(prefix=prefix)
summary(runtime)
#-----------------------------------------------------------------
options(warn=2)
#options(error=utils::dump.frames)
options(error = function() traceback(2))

runtime.plot(data=runtime,prefix=prefix,xscale=NULL,yscale=NULL) 
runtime.plot(data=runtime,prefix=paste(prefix,'log','log',sep='-')) 
runtime.plot(data=runtime,prefix=paste(prefix,'relative',sep='-'),
  xscale=NULL,yscale=NULL,
  ymin='relative.ms.min',y='relative.ms',ymax='relative.ms.max') 
