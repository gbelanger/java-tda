     * This is important since the variance of the rates is a function of 
import cern.colt.list.DoubleArrayList;
import cern.jet.random.Exponential;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import cern.jet.stat.Descriptive;
import hep.aida.IAnalysisFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import hep.aida.ref.histogram.Histogram1D;
import org.apache.log4j.Logger;
