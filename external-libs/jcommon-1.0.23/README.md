THE JCOMMON CLASS LIBRARY: Version 1.0.23
=========================================

24 July 2014

(C)opyright, 2000-2014, by Object Refinery Limited and Contributors.

-----------------
1.  INTRODUCTION
-----------------
JCommon is a free general purpose Java class library that is used in
several projects including JFreeChart and Pentaho Reporting.

*****
SPECIAL NOTICE:  BOTH JFREECHART AND PENTAHO REPORTING ARE MOVING TOWARDS
ELIMINATING THEIR DEPENDENCY ON JCOMMON.  AFTER THIS HAPPENS, JCOMMON WILL
MOST LIKELY BE "RETIRED".
*****

JCommon is licensed, free of charge, under the terms of the GNU Lesser
General Public Licence, version 2.1 or later.  A copy of the licence is 
included in the download.

The sources for this library are kept in a repo at GitHub:

    https://github.com/jfree/jcommon

If you have any comments, suggestions or bugs to report, please post a
message in the JCommon forum.

-----------------
2.  CONTRIBUTORS
-----------------
Thanks to the following developers who have contributed code to this
class library:  Anthony Boulestreau, Jeremy Bowman, J. David
Eisenberg, Paul English, Brian Fischer, Hans-Jurgen Greiner, Martin Hoeller,
Ryan Hirrlinger, Bill Kelemen, Arik Levin, Achilleus Mantzios, Thomas Meier,
Thomas Morgner, Krzysztof Paz, Andrzej Porebski, Nabuo Tamemasa, Mark Watson
and Hari.

---------------
3.  WHAT'S NEW
---------------
Changes in each version are listed here:

1.0.23: (24-Jul-2014) Reorganised directories to align to Maven standard.
                      Updated pom file.  Fixed Javadoc warnings for JDK 8.

1.0.22: (28-Feb-2014) Fix for endless loop in TextUtilities.createTextBlock().

1.0.21: (24-Oct-2013) Modified drawRotatedString() to call drawAlignedString()
                      when the rotation angle is 0.0.  This provides benefits
                      downstream for JFreeChart, JFreeSVG and OrsonPDF.

1.0.20: (01-Sep-2013) Fixed TextFragment.draw() to respect TextAnchor argument.

1.0.19: (30-Aug-2013) Updated Licences.java so that complete licence text is 
                      loaded on demand only (bug #1118).

1.0.18: (23-Sep-2012) Fixed PaintList.equals() bug, fixed line break issue in
                      TextUtilities;

1.0.17: (17-Oct-2011) Updated SerialUtilities to support serialisation of
                      Composite instances;

1.0.16: (17-Apr-2009) New release in conjunction with JFreeChart 1.0.13;

1.0.15: (19-Dec-2008) New ResourceBundleWrapper class to enable applets to
                      bypass codeBase lookup.  Updated Maven specs.

1.0.14: (10-Sep-2008) Workaround added to PngEncoder, bug fix in ShapeList.

1.0.13: (05-Jun-2008) Bug fixes for TextBox and ShapeUtilities.

1.0.12: (02-Nov-2007) Made the resource-loading and classloader selection more
        robust in cases where the jars were distributed over several
        classloaders.

1.0.11: (19-Oct-2007) BugFix in the KeyedComboBoxModel causing
        NullPointerExceptions on MacOS-X; Make sure that all resource-streams
        that get opened also get safely closed in case of errors.

1.0.10: (21-May-2007) BugFix in the PackageSorter, Window positioning is now
        multi-screen aware.

1.0.9 : (23-Mar-2007) Serialization fix in TextFragment.

1.0.8 : (11-Dec-2006) Minor bugfixes in the synchronization of the Booting code
        and the FastStack.

1.0.7 : (03-Dec-2006) Cosmetic fixes for the logging of the PackageManager,
        ObjectTable implementation allows faster cloning of its contents,
        minor improvements to the GUI classes.

1.0.6 : (26-Sep-2006) BugFixes in the TextLayoutUtilities, SpreadSheetDate and
        ResourceBundleSupport classes.

1.0.5 : (29-Jun-2006) BugFixes in the boot code (synchronization added) and
        WaitingImageObserver (possible deadlock fixed). ObjectUtilities
		did not handle all cases where a ClassLoader could be null.

1.0.4 : (30-Apr-2006) Date- and NumberCellRenderer did not handle null-values
        properly.

1.0.3 : (17-Apr-2006) More improvements in the boot process for dependent
        library hierarchies.

1.0.1 : (10-Feb-2006) This release adds new boot capabilities and fixes
        some bugs in the XML classes. This version is required for
        JFreeReport and it is fully backward compatible with the 1.0.x
        branch.

1.0.0 : (14-Nov-2005) This is the official 1.0.0 release.  All future
        releases in the 1.0.x series will maintain backward compatibility
        with the 1.0.0 API.

1.0.0-rc1 : (01-Jun-2005) : Update to coincide with JFreeReport 0.8.5-5.
        JavaDoc update and classloader handling changed.

1.0.0-pre3 : (15-Apr-2005) : Some minor changes in the ResourceBundleSupport
        to allow the explicit definition of a locale.

1.0.0-pre2 : (04-Mar-2005) : Update to coincide with JFreeReport 0.8.5.
        Separated the xml-support packages into an own jar file.
        JFreeChart does not use these classes (except in experimental
        non-release code). Therefore, JFreeChart needs both libs to
        build, but only the jcommon.jar library to run.

        The a more detailed list of changed please have a look at the
        CHANGELOG.txt file.

1.0.0-pre1 : (29-Nov-2004) : Update to coincide with JFreeChart 1.0.0-pre1.

0.9.7 : (13-Oct-2004) Update to join JFreeReport with latest JFreeChart.

0.9.6 : (10-Sep-2004) Update to coincide with JFreeChart 0.9.21.

0.9.5 : (7-Jun-2004) Update to coincide with JFreeChart 0.9.20.

0.9.4 : (28-May-2004) Update to coincide with JFreeChart 0.9.19.

0.9.3 : (15-Apr-2004) Update to coincide with JFreeChart 0.9.18.

0.9.2 : (26-Mar-2004) Update to coincide with JFreeChart 0.9.17.

0.9.1 : (9-Jan-2004) Update to coincide with JFreeChart 0.9.16.

0.9.0 : (28-Nov-2003) Update to coincide with JFreeChart 0.9.15.

0.8.9 : (17-Nov-2003) Update to coincide with JFreeChart 0.9.14.

0.8.8 : (26-Sep-2003) Update to coincide with JFreeChart 0.9.13.

0.8.7 : (11-Sep-2003) Update to coincide with JFreeChart 0.9.12.

0.8.6 : (8-Aug-2003) Update to coincide with JFreeChart 0.9.11.

0.8.5 : (25-Jul-2003) Transferred some support classes from JFreeChart.

0.8.3 : (16-Jun-2003) XML Parser: error locations are printed in the
        parse exceptions to make debugging xml files easier.

0.8.2a: (04-Jun-2003) xml parser: configuration interface modified.
	+ Bugfixes ...

0.8.2 : (26-May-2003) xml parser class factory bug fix

0.8.1 : (09-May-2003) Added support for the xml parser and imported
        some base classes from the JFreeReport project.

0.8.0 : (24-Apr-2003) Renamed all packages from com.jrefinery.*
        to org.jfree.*.

0.7.3 : (11-Apr-2003) Added serialization for SerialDate and
        SpreadsheetDate classes.  Added a SerialUtilities class.
        Removed palette classes (now in JFreeChart).  Added an
        attribute to control whether or not a workaround is used for
        drawing rotated text.

0.7.2 : (6-Feb-2003) Bug fixes and Javadoc updates, incorporated an
        Ant script to recompile the source files and generate the
        Javadocs.

0.7.1 : (18-Oct-2002) Bug fixes and Javadoc updates.

0.7.0 : (4-Sep-2002) Moved package (com.jrefinery.data) to JFreeChart
        project. Bug fixes and Javadoc updates.

0.6.4 : (27-Jun-2002) Added logo to about box. Minor bug fixes (plus
        JUnit tests) and code tidy up.

0.6.3 : (14-Jun-2002) Bug fixes and Javadoc updates.

0.6.2 : (7-Jun-2002) Added GanttSeriesCollection and supporting
        classes.  Added Polish resource bundle.  Minor bug fixes.

0.6.1 : (5-Apr-2002) Added MeterDataset interface and
        DefaultMeterDataset class. Resource bundles for French, German
        and Spanish languages. Reinstated the Week class.  Minor bugfixes.

0.6.0 : (22-Mar-2002) Changes to the API for the TimePeriod classes,
        to improve methods that convert to java.util.Date.  New
        DefaultHighLowDataset class.  New ResourceBundles for items
        that require localisation.

0.5.6 : (6-Mar-2002) Bug fix for combined datasets.  Additional
        methods in the TimePeriod class to handle different
        timezones. Updated About box classes moved to new package
        com.jrefinery.ui.about.  Renamed Files.java -->
        FileUtilities.java and SerialDates.java -->
        SerialDateUtilities.java.  Added new domain name
        (http://www.object-refinery.com) in the source headers.

0.5.5 : (15-Feb-2002) Fixed bugs in the constructors for the
        TimePeriod subclasses.  Reversed the order of the parameters
        in the Month(int, int) constructor.  Added methods to
        Datasets.java to handle stacked data ranges.  Fixed bug in
        CombinedDataset.

0.5.4 : (8-Feb-2002) New WindDataset interface and DefaultWindDataset
        class.  Bug fix for DefaultCategoryDataset.

0.5.3 : (25-Jan-2002) Bug fixes, some minor API changes.

0.5.2 : (10-Dec-2001) Added new combination datasets by Bill Kelemen.
        Added contributors table to the AboutFrame class.

0.5.1 : (27-Nov-2001) AboutPanel and AboutFrame classes added.

0.5.0 : (21-Nov-2001) PieDataset and DefaultPieDataset classes added.

0.4.2 : (16-Nov-2001) New classes in the com.jrefinery.data.* package,
        plus some new JUnit test cases.