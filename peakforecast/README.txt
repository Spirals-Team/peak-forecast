============================================================================
PeakForecast
Copyright (C) 2009-2012 INRIA, University of Lille 1

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Contact: contact-adam@lifl.fr
Author: Daniel Fouomene
Contributor(s): Romain Rouvoy, Lionel Seinturier

============================================================================

PeakForecast:
-----------------------


Compilation with Maven:
-----------------------
  mvn install

Execution with Maven:
---------------------
  1) Open a console for the server
     cd server
     mvn -Prun                      (standalone execution)
     mvn -Pexplorer                 (with FraSCAti Explorer)
     mvn -Pexplorer-fscript         (with FraSCAti Explorer and FScript plugin)
     mvn -Pfscript-console          (with FraSCAti FScript Console)
     mvn -Pfscript-console-explorer (with FraSCAti Explorer and FScript Console)
     mvn -Pexplorer-jdk6            (with FraSCAti Explorer and JDK6)

  2) Open another console for the client
     cd client
     mvn -Prun                      (standalone execution)
     mvn -Pexplorer                 (with FraSCAti Explorer)
     mvn -Pexplorer-fscript         (with FraSCAti Explorer and FScript plugin)
     mvn -Pfscript-console          (with FraSCAti FScript Console)
     mvn -Pfscript-console-explorer (with FraSCAti Explorer and FScript Console)
     mvn -Pexplorer-jdk6            (with FraSCAti Explorer and JDK6)

Compilation and execution with the FraSCAti script:
---------------------------------------------------
  1) Open a console and follow instructions in the 'server' directory
  2) Open another console and follow instructions in the 'client' directory