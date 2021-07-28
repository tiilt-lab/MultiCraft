# MultiCraft Java Setup Instructions
### Development Environemnt Setup
1. Clone repository.
2. Open folder using IntelliJ.
3. Ensure Project SDK is 1.8 (File > Project Structure > Project SDK).
   
    a. If not, download a JDK (Add JDK > Download JDK... > Version: 1.8)
4. Project should build without errors.

### Exporting JAR
1. IntelliJ should recognize pom.xml and begin Maven setup.
2. Add a new Maven Run Configuration (Add Configuration... > Add New Configuration > Maven)
3. Use "compile assembly:single" for command line and Run.
5. Exported JAR should appear in target directory.