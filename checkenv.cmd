echo off
echo [checkenv] Checking required evnironment for SIDnet-SWANS v1.0

rem echo ---   
rem echo [checkenv] Checking JAVA run-time environment ...
rem if "%JAVA_HOME%"=="" goto notfoundJavaHome
rem    echo            JAVA_HOME
rem                       printenv JAVA_HOME 
rem    echo            Java JRE version
rem		java -version
rem :notfoundJavaHome
rem    echo            JAVA_HOME variable not set. Make sure you point it to a Java 1.5 or later version
    


echo [checkenv] Checking SIDNETSWANSDIR
if "%SIDNETSWANSDIR%"=="" goto notfoundSidnetSwansDir
    echo            OK
:notfoundSidnetSwansDir
    echo            SIDNETSWANSDIR not set. It must point to the root directory SIDnet-SWANS. Update and use the 'setenv.cmd'

echo [checkenv] Checking SIDNETDIR
if "%SIDNETDIR%"=="" goto notfoundSidnetSwansDir
    echo            OK
:notfoundSidnetSwansDir
    echo            SIDNETDIR not set. It must point to the root of the source files, which should be SIDNETSWANSDIR\src directory SIDnet-SWANS. Update and use the 'setenv.cmd'

echo [checkenv] Modify appropriatelly the included 'setenv.cmd' to point to the required libraries and you should be all set. Remember to execute the 'setenv.cmd' before launching scripts. Netbeans/Eclipse - you'll need to indicate the libraries manually

