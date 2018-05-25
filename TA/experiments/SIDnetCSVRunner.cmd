echo off

echo Configuring SIDnet environment ...
if not "%SIDNETSWANSDIR%"=="" goto skipenv
set SIDNETSWANSDIR=D:\My Documents\NWU\Research Activity\Java Programming\SIDnet-SWANS
set SIDNETDIR=%SIDNETSWANSDIR%\src
set SIDNETLIBS=%SIDNETSWANSDIR%\libs
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\build\classes;
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\BCEL\org\apache\bcel-5.2\bcel-5.2.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\bsh.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\checkstyle-all.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\jargs.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\jython.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\log4j.jar
set CLASSPATH=%CLASSPATH%;%SIDNETDIR%\apache-log4j-1.2.15\log4j-1.2.15.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\swing-layout-1.0.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\jaxb-ri-20070917\lib\jaxb-api.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\jaxb-ri-20070917\lib\jaxb-impl.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\jaxb-ri-20070917\lib\jsr173_1.0_api.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\opencsv-1.8\deploy\opencsv-1.8.jar
:skipenv

echo on
echo Launching SIDnet's batching utility
java sidnet.batch.SIDnetCSVRunner %1 %2 %3 %4 %5