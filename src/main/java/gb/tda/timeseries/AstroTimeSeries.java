package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class AstroTimeSeries extends AbstractAstroTimeSeries {

    private static Logger logger = Logger.getLogger(AstroTimeSeries.class);

    AstroTimeSeries(IAstroTimeSeries ts) {
        super(ts);
    }

    AstroTimeSeries(ICountsTimeSeries countsTimeSeries) {
        super(countsTimeSeries);
    }

    AstroTimeSeries(IRatesTimeSeries ratesTimeSeries) {
        super(ratesTimeSeries);
    }

}
