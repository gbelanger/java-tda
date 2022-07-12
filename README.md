# The _gb.tda.eventlist_ package

This package contains java objects to represent and work with event lists.

The main objects are:
- IEventFileReader.java is an interface that defines a method for reading event files.
- EventList.java is the representation of an event list
- AsciiEventFileReader.java to read event lists in ascii format
- FitsEventFileReader.java to read event lists in fits format
- EventFileReader.java to loop on all specific file readers
- EventListWriter.java to write event lists
- EventListSelector.java to perform various selections

The exception handlers are:
- EventListException.java
- EventFileException.java
- EventFileFormatException.java
- AsciiEventFileException.java
- AsciiEventFileFormatException.java
- FitsEventFileException.java
- FitsEventFileFormatException.java

Unit testing classes include:
- AsciiEventFileReaderTest.java
- EventFileReaderTest.java
- EventListSelectorTest.java
- EventListTest.java
- EventListWriterTest.java
- FitsEventFileReaderTest.java# The _gb.tda.timeseries_ package

# The _gb.tda.timeseries_ package

This package contains classes to work with time series.

## Basic time series

At the base of the class hierarchy we have:

- the interface `ITimeSeries`,
- the abstract class `AbstractTimeSeries`, and
- the concrete class `BasicTimeSeries`.

The interface `ITimeSeries` defines the public methods that _every_ time series object must implement. These include methods to access the time axis (`x`) and the intensity axis (`y`) in the most general, least restrictive way. The time axis only requires to have times and a time unit (e.g., seconds or days). The intensity axis can be any quantity.

The abstract class `AbstractTimeSeries` implements all the public methods that return the values of the time series's properties, all the internal (package-private) setters that define them, as well as the constructors.

The concrete class `BasicTimeSeries` defines only constructors that call the parent class to allow the creation of the simplest form of a time series. An example of such a time series could be the number of people living on a particular street over the course of a year. The times when the census (counting) is made can be irregularly spaced, the number of people have no uncertainties (a person is either living there or not), and each measurement is a point estimate without any extent in time (on the day we counted).

A `BasicTimeSeries` can have uncertainties, while being a set of time-ordered point estimates, like for example, measurements of your weight, each with an uncertainty defined by the accuracy of the scale, distributed irregularly in time based on when you recorded measurements. 

The only requirement of a `BasicTimeSeries` is that it has time-stamped intensities.

## Binned time series

The next level in the hierarchy is where are have defined:

- the interface `IBinnedTimeSeries`,
- the abstract class `AbstractBinnedTimeSeries`, and
- the concrete class `BinnedTimeSeries`.

The interface `IBinnedTimeSeries` defines additional public methods that provide access to properties of binned times series objects. These include methods related to the bins on the time axis.

The abstract class `AbstractBinnedTimeSeries` implements the handling of all the logic related to constructing, defining, handling, and providing access to bin-specific properties. These include the details of bin edges, widths, and various statistics about bins, as well as details of gaps, sampling function, and statistics of the gaps.

The concrete class `BinnedTimeSeries` simply provides public constructors that call the parent constructors.

A binned time series has bins, which are time periods of finite duration during which the measurement was made. These intensities can be without uncertainties, like the number of cars that pass in front of the door in each hour of the day, or with uncertainties, like average of 3 temperature measurements performed within a 10-minute window throughout the day. The fundamental characteristic of a binned time series is that the measurements are always given within a time bin within which we have no information on the time distribution details.

If we counted 10 people passing in front of the door in an hour, we do not have access to the details of _when_ they walked by within at hour. Similarly, once we have computed the average temperature within a 10-minute window and discarded those measurements, we only have a record of the average temperature for that 10-minute period.

Binned time series require special handling of the bin edges and gaps (the sampling function). Intensities can be either absolute quantities that are counted and cannot be divided, like a certain number of discrete things or events like people or sunny days; or they can be a density or rate, like breaths per minute or counts per second. Any of these can but don't need to have uncertainties.

## Counts and rates time series

The third level is for binned time series in counts or rates. Here we have defined:

- the interface `ICountsTimeSeries` with 
- its corresponding concrete class `CountsTimeSeries`, as well as
- the interface `IRatesTimeSeries` with 
- its corresponding concrete class `RatesTimeSeries`.

The two pairs of interfaces and concrete classes that both extend `BinnedTimeSeries` provide methods to work with time series in counts and in rates because it is usually the case that we can go from counts to rate or vice versa.

However, a `CountsTimeSeries` will provide access to information about what will be referred to as _equivalent rates_, and a `RatesTimeSeries` will provide access to what will be referred to as _equivalent counts_. This is used to distinguish and emphasize the fact that they are computed from the primary data and are thus secondary in their value compared to the primary data used to define the object. There is a natural mapping between the methods that give access to intensity information in the parent class to those accessing counts and rates.

## Astrophysics and astronomy time series

The fourth level has the astronomy related time series interface and classes:

- the interface `IAstroTimeSeries`,
- the abstract class `AbstractAstroTimeSeries`, and
- the concrete class `AstroTimeSeries`.

They define a set of properties and methods specific to astronomy, things like the telescope, instrument, target name and coordinates, etc. To allow for the most general framework, `AbstractAstroTimeSeries` implements, in addition to `IAstroTimeSeries`, also the two interfaces in the previous levels, `ICountsTimeSeries` and `IRatesTimeSeries`. This is specifically required to handle high-energy time series of event data in X-rays and Gamma-rays, together with any other kind of astronomical time series, like energy flux density, we find in optical and infrared astronomy.

## Class hierarchy
- `ITimeSeries`
- `AbstractTimeSeries` implements `ITimeSeries`
- `BasicTimeSeries` extends `AbstractTimeSeries`
  - `IBinnedTimeSeries` extends `ITimeSeries`
  - `AbstractBinnedTimeSeries` extends `AbstractTimeSeries` implements `IBinnedTimeSeries`
  - `BinnedTimeSeries` extends `AbstractBinnedTimeSeries`
      - `ICountsTimeSeries` extends `IBinnedTimeSeries`
      - `CountsTimeSeries` extends `BinnedTimeSeries` implements `ICountsTimeSeries`
      - `IRatesTimeSeries` extends `IBinnedTimeSeries`
      - `RatesTimeSeries` extends `BinnedTimeSeries` implements `IRatesTimeSeries`
          - `IAstroTimeSeries` extends `IBinnedTimeSeries`
          - `AbstractAstroTimeSeries` extends `BinnedTimeSeries` implements `IAstroTimeSeries` `ICountsTimeSeries` `IRatesTimeSeries`
          - `AstroTimeSeries` extends `AbstractAstroTimeSeries`
