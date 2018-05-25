echo off

echo Configuring SIDnet environment ...
if not "%SIDNETSWANSDIR%"=="" goto skipenv

rem --------- MODIFY THIS TO SUIT YOUR DIRECTORY STRUCTURE ------------------------------------
rem -----------------------------------------------------------------------------------------------------------
set SIDNETSWANSDIR=Q:\My Documents\Work\NWU\ResearchActivity\Java Programming\SIDnet-SWANS\Java\SIDnet-SWANS
rem -----------------------------------------------------------------------------------------------------------

set SIDNETDIR=%SIDNETSWANSDIR%\src\main\java
set DEPLOYMENTMODELSDIR=%SIDNETDIR%\sidnet\models\deployment\models
set DEPLOYMENTXMLDIR=%SIDNETDIR%\sidnet\models\deployment\store\
set MOBXMLDIR=%SIDNETDIR%\sidnet\models\senseable\mob\
set SIDNETLIBS=%SIDNETSWANSDIR%\libs

rem For NetBeans
rem set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\build\classes
rem For Eclipse
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\bin

set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\BCEL\org\apache\bcel-5.2\bcel-5.2.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\bsh.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\checkstyle-all.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\jargs.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\jython.jar
set CLASSPATH=%CLASSPATH%;%SIDNETSWANSDIR%\importedpackages\jist-swans-1.0.6\libs\log4j.jar
set CLASSPATH=%CLASSPATH%;%SIDNETDIR%\apache-log4j-1.2.15\log4j-1.2.15.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\swing-layout-1.0.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\jaxb-ri-2.1.8\lib\jaxb-api.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\jaxb-ri-2.1.8\lib\jaxb-impl.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\jaxb-ri-2.1.8\lib\jsr173_1.0_api.jar
set CLASSPATH=%CLASSPATH%;%SIDNETLIBS%\opencsv-1.8\deploy\opencsv-1.8.jar
set CLASSPATH=%CLASSPATH%;%DEPLOYMENTMODELSDIR%
goto end
:skipenv

echo SIDnet environment has been set previously.

:end

echo .
echo SIDnet-SWANS installation directory:
set SIDNETSWANSDIR

echo.
echo SIDnet-SWANS source path
set SIDNETDIR

echo.
echo SIDnet-SWANS libs path
set SIDNETLIBS

echo.
echo CLASSPATH:
set CLASSPATH

echo.
echo DONE!
