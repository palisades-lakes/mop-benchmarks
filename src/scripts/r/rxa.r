# nzqr-jmh
# palisades dot lakes at gmail dot com
# version 2019-04-05
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
prefix <- 'Sums-20190404-192600'
#-----------------------------------------------------------------
runtime <- read.runtimes(prefix=prefix)
summary(runtime)
#-----------------------------------------------------------------
accuracy <- read.accuracy(benchmarks=c('Dot','L2','Sum'))
summary(accuracy)
#-----------------------------------------------------------------
data <- merge(
  x=runtime,
  y=accuracy,
  by=c('benchmark','algorithm','dim'))
cols <- c('benchmark','algorithm','dim','ms','residual50','abs.residual50')
data <- data[,cols]
summary(data)
#-----------------------------------------------------------------
source('src/scripts/r/functions.r')

runtime.accuracy.plot(
  data=data, 
  y='abs.residual50',
  ylabel='median absolute residual',
  prefix='rxa-median-absolute-residual',
  scales='fixed')  

runtime.accuracy.plot(
  data=data, 
  y='abs.residual50',
  ylabel='median absolute residual',
  prefix='rxa-median-absolute-residual-free',
  scales='free')  
