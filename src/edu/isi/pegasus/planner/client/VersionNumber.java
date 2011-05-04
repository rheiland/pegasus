/*
 * This file or a portion of this file is licensed under the terms of
 * the Globus Toolkit Public License, found in file GTPL, or at
 * http://www.globus.org/toolkit/download/license.html. This notice must
 * appear in redistributions of this file, with or without modification.
 *
 * Redistributions of this Software, with or without modification, must
 * reproduce the GTPL in: (1) the Software, or (2) the Documentation or
 * some other similar material which is provided with the Software (if
 * any).
 *
 * Copyright 1999-2004 University of Chicago and The University of
 * Southern California. All rights reserved.
 */

package edu.isi.pegasus.planner.client;

import java.io.*;
import edu.isi.pegasus.common.util.Version;
import gnu.getopt.*;

/**
 * This class just prints the current version number on stdout.
 *
 * @author Jens-S. Vöckler
 * @author Yong Zhao
 * @version $Revision$
 */
public class VersionNumber
{
  /**
   * application's own name.
   */
  private String m_application = null;

  /**
   * ctor: Constructs a new instance with the given application name.
   * @param appName is the name of the application
   */
  public VersionNumber( String appName )
  {
    m_application = appName;
  }

  /**
   * Prints the usage string.
   */
  public void showUsage()
  {
    String linefeed = System.getProperty( "line.separator", "\r\n" );

    System.out.println(
"$Id$" + linefeed +
"PEGASUS version " + Version.instance().toString() + linefeed );

    System.out.println( "Usage: " + m_application + " [-f | -V | -m]" );
    System.out.println( linefeed +
"Options:" + linefeed +
" -V|--version   print version information about itself and exit." + linefeed +
"    --verbose   increases the verbosity level (ignored)." + linefeed +
" -f|--full      also shows the internal built time stamp." + linefeed +
" -l|--long      alias for --full." + linefeed +
" -m|--match     matches internal version with installation." + linefeed +
" -q|--quiet     in match mode, no news are good news, use exit code." + linefeed +
linefeed +
"The following exit codes are produced:" + linefeed +
" 0  :-)  Success" + linefeed +
" 1  :-|  Detected a mis-match in match mode. Your installation is bad!" + linefeed +
" 2  :-(  Runtime error detected, please read the message." + linefeed +
" 3  8-O  Fatal error merits a program abortion." + linefeed );
  }

  /**
   * Creates a set of options.
   * @return the assembled long option list
   */
  protected LongOpt[] generateValidOptions()
  {
    LongOpt[] lo = new LongOpt[8];

    lo[0] = new LongOpt( "version", LongOpt.NO_ARGUMENT, null, 'V' );
    lo[1] = new LongOpt( "help", LongOpt.NO_ARGUMENT, null, 'h' );
    lo[2] = new LongOpt( "verbose", LongOpt.NO_ARGUMENT, null, 1 );

    lo[3] = new LongOpt( "full", LongOpt.NO_ARGUMENT, null, 'f' );
    lo[4] = new LongOpt( "long", LongOpt.NO_ARGUMENT, null, 'l' );
    lo[5] = new LongOpt( "build", LongOpt.NO_ARGUMENT, null, 'f' );
    lo[6] = new LongOpt( "match", LongOpt.NO_ARGUMENT, null, 'm' );
    lo[7] = new LongOpt( "quiet", LongOpt.NO_ARGUMENT, null, 'q' );


    return lo;
  }

  /**
   * Print the version information onto stdout. 
   *
   * @param v is the version information class. 
   * @param build if true, also show build information with version.
   */
  public static void showVersion( Version v, boolean build )
  {
    System.out.print( v.toString() );
    if ( build ) System.out.print( '-' + v.determinePlatform() +
				   '-' + v.determineBuilt() );
    System.out.println();
  }

  public static void main( String args[] )
  {
    int result = 0;
    VersionNumber me = null;

    try {
      me = new VersionNumber("pegasus-version");
      Getopt opts = new Getopt( me.m_application, args,
				"Vflhmq",
				me.generateValidOptions() );
      opts.setOpterr(false);
      String installed = null;
      String internal = null;
      boolean build = false;
      boolean quiet = false;
      Version v = Version.instance();

      int option = 0;
      while ( (option = opts.getopt()) != -1 ) {
	switch ( option ) {
	case 1:
	  break;

	case 'V':
	  System.out.println( "$Id$" );
	  System.out.println( "PEGASUS version " + v.toString() );
	  return;

	case 'l':
	case 'f':
	  build = true;
	  break;

	case 'm':
	  installed = v.determineInstalled();
	  internal  = v.determineBuilt() + " " + v.determinePlatform();
	  if ( ! quiet ) {
	    System.out.println( "Compiled into PEGASUS: " + internal );
	    System.out.println( "Installation provides: " + installed );
	  }

	  if ( v.matches() ) {
	    if ( ! quiet ) System.out.println( "OK: Internal version matches installation." );
	    // see pegasus-devel 2011-03-29: A user should not have to run both,
	    // first "pegasus-version -fm" and then "pegasus-version". The version
	    // info should also be returned by "pegasus-version -fm". 
	    build = true; // successful -m should imply -f
	    System.out.print( "Complete version info: " );
	  } else {
	    System.out.println( "ERROR: Internal version does not match installed version!" );
	    System.out.println( "Your installation is suspicious, please correct!" );
	    System.exit(1);
	  }
	  break;

	case 'q':
	  quiet = true;
	  break;

	case 'h':
	default:
	  me.showUsage();
	  return;
	}
      }

      // action
      showVersion(v,build); 

    } catch ( RuntimeException rte ) {
      System.err.println( "ERROR: " + rte.getMessage() );
      result = 2;

    } catch( Exception e ) {
      e.printStackTrace();
      System.err.println( "FATAL: " + e.getMessage() );
      result = 3;
    }

    if ( result != 0 ) System.exit(result);
  }
}

