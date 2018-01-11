# slcodeanalysis
A .NET, .NET Core, VB6, COM project dependency analyzer written in Scala. It runs on Windows, Linux and Osx

# Prerequisits
* [GraphViz](https://graphviz.gitlab.io/download/)
* [Java 8+](https://www.oracle.com/java/index.html)

Make sure that you can run the commands `dot` and `java` with success in your windows powershell prompt, commandline prompt or in your shell..
To do that you probably need to modify your systems `PATH` variable and add the bin folders of java and graphviz.

# Installing
Download the zip package from the releases tab in github. Extract the zip package to the folder of choice on your system.
Add the bin folder to the `PATH` variable so that you can run the `slcodeanalysis` command in your commandline prompt or shell.

# Running
Just type `slcodeanalysis` in your commandline prompt or shell. You will be presented with the options documentuation bellow. 
```
Usage: slcodeanalyzer [options]

  -d, --directory <directory>
                           directory is the directory that holds the files that should be analyzed
  -o, --output <file>      the output file to write to. A single dash '-' means stdout
  -s, --settings <file>    a file that contains the settings for how to process the graph
  -p, --thread-pool-size <size>
                           the size of the thread pool
  -r, --root <root name>   show only this root and all the dependencies recursively
  -f, --format <output format>
                           the output format of the diagram. 'dot' is default. Only 'dot' is supported right now
```

*Example:*
```
> slcodeanalysis -d mydirectory -o test.dot
```

When you have executed the analyzer you will end up with a dot file. This dot file can then be fed in to GraphViz for creating a visual representation
of the project dependency graph. Ex:
```
dot -Tsvg -o test.svg test.dot
```
This command will generate an svg file rendering of the graph.

# Customizing The Output
TBD
