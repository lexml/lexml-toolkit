@echo off
%~d0
cd %~p0
java -cp . LauncherBootstrap -executablename lexml-toolkit run %1 %2
