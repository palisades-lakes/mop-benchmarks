# nzqr-jmh
# palisades dot lakes at gmail dot com
# version 2019-04-08
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
accuracy <- read.accuracy(benchmarks=c('Dot','L2','Sum'))
summary(accuracy)
#-----------------------------------------------------------------
accuracy.plot(
  data=accuracy, 
  ymin='abs.residual05',y='abs.residual50',ymax='abs.residual95',
  ylabel='absolute-residuals',
  prefix='residuals-absolute',
  scales='fixed')  

accuracy.plot(
  data=accuracy, 
  ymin='abs.residual05',y='abs.residual50',ymax='abs.residual95',
  ylabel='absolute-residuals',
  prefix='residuals-absolute-free',
  scales='free_y')  

accuracy.plot(
  data=accuracy, 
  ymin='residual05',y='residual50',ymax='residual95',
  ylabel='residuals',
  prefix='residuals',
  scales='fixed') 

accuracy.plot(
  data=accuracy, 
  ymin='residual05',y='residual50',ymax='residual95',
  ylabel='residuals',
  prefix='residuals-free',
  scales='free_y') 

accuracy.plot(
  data=accuracy, 
  ymin='fresidual05',y='fresidual50',ymax='fresidual95',
  ylabel='fractional residual',
  prefix='residuals-fractional',
  scales='fixed') 

accuracy.plot(
  data=accuracy, 
  ymin='fresidual05',y='fresidual50',ymax='fresidual95',
  ylabel='fractional residual',
  prefix='residuals-fractional-free',
  scales='free_y') 

accuracy.plot(
  data=accuracy, 
  ymin='abs.fresidual05',y='abs.fresidual50',ymax='abs.fresidual95',
  ylabel='absolute fractional residual',
  prefix='residuals-fractional-absolute',
  scales='fixed') 

accuracy.plot(
  data=accuracy, 
  ymin='abs.fresidual05',y='abs.fresidual50',ymax='abs.fresidual95',
  ylabel='absolute fractional residual',
  prefix='residuals-fractional-absolute-free',
  scales='free_y') 
