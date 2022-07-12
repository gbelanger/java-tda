#!/bin/bash

if [ $# != 3 ]
then
    echo "Usage: combinePlots.sh totalDuration lenghOfSegments alpha"
    exit
fi

duration="$1"
lengthOfSegment="$2"
alpha="$3"
nSegments=`calc.pl $duration/$lengthOfSegment`
#echo $duration
#echo $lengthOfSegment
#echo $nSegments
nHeadLines=14
outputFile="resultsFor-"${duration}"-withSegmentsOf-"${lengthOfSegment}".qdp"
if [ -f $outputFile ] 
then
  rm $outputFile
fi

resultDir="redNoise-${alpha}-${duration}-${lengthOfSegment}"
if [ -d $resultDir ]
then
    rm -r $resultDir
fi
mkdir $resultDir


echo Combining:

fullPsd=`ls redNoiseLeak-full-psd-alpha-${alpha}-${duration}*`
echo $fullPsd

cat $fullPsd > $outputFile
echo "NO NO" >> $outputFile
echo "LAB 2 POS 4e-6 2 LS 4 LINE 0 1 \"" >> $outputFile
echo "LAB 3 VPOS 0.55 0.73 \"\ga="$alpha", T\dseg\u="$lengthOfSegment" s\" JUST LEFT" >> $outputFile
echo "LAB 4 VPOS 0.55 0.68 \"Average of 100 short event lists\" CO 2 JUST LEFT" >> $outputFile
echo "LAB 5 VPOS 0.55 0.65 \"Average of "$nSegments" segments\" CO 3 JUST LEFT" >> $outputFile
echo "LAB 6 VPOS 0.55 0.62 \"Complete event list\" CO 1 JUST LEFT" >> $outputFile


mv $fullPsd $resultDir

for file in redNoiseLeak-short-psd-alpha-${alpha}-${nSegments}-*Of-${lengthOfSegment}s.qdp
do
  echo $file
  nLines=`wc $file | awk '{print $1}'`
  nDataLines=`calc.pl $nLines - $nHeadLines`
  tail -$nLines $file | tail -$nDataLines >> $outputFile
  echo "NO NO" >> $outputFile
  mv $file $resultDir
done

psFile="resultsFor-"${duration}"-withSegmentsOf-"${lengthOfSegment}".ps" 
echo "HARD "$psFile"/cps" >> $outputFile
mv $outputFile $resultDir
lcFile="redNoiseLeak-full-lc-alpha-"${alpha}"-"${duration}"-binOf-1000.0s.qdp"
mv $lcFile $resultDir
echo Done

cd $resultDir
qdp $outputFile
cd ..
